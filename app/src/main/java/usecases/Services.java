package usecases;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.springframework.security.crypto.bcrypt.BCrypt;

import Utilities.FormValidator;
import Utilities.JwtHelper;
import Utilities.MailHelper;
import entities.Event;
import entities.User;
import static gradle_tish_embedded.App.reminder;
import io.jsonwebtoken.security.SignatureException;
import repositories.DataRepository;
import java.lang.IllegalArgumentException;
import java.time.Instant;

// theoretically in the servlet this usecase class should be called.
// but the logic created here is still too simple so the code in this usecase class
// will be just like boilerplate code.

/**
    * This {@link Services} class contains all business logic code for this Time Scheduler, from login,register,... to add task,delete task,...
    This code should be changed the least, as changing the code means changing the core logic of our business, thus avoid using any framework-specific 
    code here as we will be tightly coupled with that framework in the future.
*/
public class Services {
    private DataRepository repo;
    /** 
     * Create a {@link Services} instance with a {@link DataRepository} object to perform any CRUD operation 
     * @param repo this represent how these usecase will perform CRUD.
     */
    public Services(DataRepository repo) {
        this.repo = repo;
    }

    /** 
     * perform check on registration information, to avoid somebody spamming our server with fake account.
     * throw specific exception when the info is not legitimate
     * @param username
     * @param password
     * @param email
     * @return boolean
     */
    public boolean checkValidRegistration(String username,String password,String email){
        if(repo.checkIfUsernameExist(username)){
            throw new IllegalArgumentException("username has already existed");
        }
        boolean req1 = FormValidator.validatePassword(password);
        boolean req2 = FormValidator.validateEmail(email);
        if(!req1){
            throw new IllegalArgumentException("Password not strong enough, must have a lowercase,uppercase, and a symbol");
        }
        if(!req2){
            throw new IllegalArgumentException("hmm something is wrong with this email format");
        }

        //  i need to learn a bit more about elliptic curves and bcrypt and stuff
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User tmpUser = new User(username,hashedPassword,email);
        boolean addSuccessfully = repo.addUserToPending(tmpUser);
        if(!addSuccessfully)
            throw new IllegalArgumentException("somebody has already registered with this username and email");

        JwtHelper jwt = new JwtHelper();
        jwt.put("username", username);
        jwt.put("email", email);
        String token = jwt.createToken();
        MailHelper.sendVerificationMail(token,email);
        return true;
        // if user click on the link, send request to /confirm servlet with the key
    }
    
    /** this is called after the potential user confirm their registration
     * @param token a JWT token attached to the confirmation link.
     * @throws IllegalAccessException
     */
    public void registerAfterReceivingConfirmationMail(String token) throws IllegalAccessException{
        JwtHelper jwt = new JwtHelper();
        try {
            // this looks ugly
           Map<String, Object> userInfo =jwt.parseToken(token);
           String username = (String) userInfo.get("username");
           String email = (String) userInfo.get("email");
            // consider adding checking null in username, but should we do that? 

           User tmpUser = repo.findUserInPending(username,email);
           if(tmpUser==null){
            throw new IllegalArgumentException("looks like somebody has already created an account with this username");
           }
           repo.createUser(tmpUser);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("the confirm link you sent is invalid");
        }
    }
    
    /** this is called to check the validity of user's credential when logging into the system.
     * @param username
     * @param password
     * @return User
     * @throws IllegalAccessError if the username or password is not correct
     */
    public User login(String username,String password){
        String hashed = repo.showHashedPassword(username);
        User u = repo.findUserByName(username);
        if(hashed==null){throw  new IllegalAccessError("username does not exist");}
        boolean isPasswordCorrect=  BCrypt.checkpw(password, hashed);
        if (!isPasswordCorrect) {
            throw  new IllegalAccessError("password incorrect");
        }
        return u;
    }
    
    /** this is called when a user create an event.
     * @param event a fully populated {@link Event} object. All non-existent username in participant list will be ignored.Any field must not be null
     * @return {@link Event} a whole event object with eventID for further identification.
     * @throws IllegalArgumentException when user set event date in the past.
     * @throws ServletException
     */
    public Event addEvent(Event event) throws IllegalArgumentException, ServletException{
        if(event.startFrom.isBefore(Instant.now())){throw new IllegalArgumentException("cannot set event date in the past");}
        if(event.startFrom.isAfter(event.endAt)){throw new IllegalArgumentException("cannot set event end before event start");}
        if(!repo.checkIfUsernameExist(event.organizer)){throw new IllegalArgumentException("the organizer did not exist in our database");}
        var userStream=  event.participantsList.stream().filter(name -> (repo.checkIfUsernameExist(name)));
        event.eventID = repo.addEvent(event);
        // add reminder job to the scheduler
        try {
            switch (event.remindBefore) {
                case "1 week":
                    reminder.sendMailBefore1Week(event);
                    break;
                case "3 days":
                    reminder.sendMailBefore3Days(event);
                    break;
                case "1 day":
                    break;
                case "1 hour":
                    reminder.sendMailBefore1Hour(event);
                    break;
                case "30 minutes":
                    reminder.sendMailBefore30Min(event);
                    break;
                case "15 minutes":
                    reminder.sendMailBefore15Min(event);
                    break;
                case "10 minutes":
                    reminder.sendMailBefore10Min(event);
                    break;
                case "5 minutes":
                    reminder.sendMailBefore5Min(event);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new ServletException("reminder cannot store the schedule");
        }
        
        event.participantsList = userStream.collect(Collectors.toList());
        inviteParticipants(event);
        return event;
    }
    
    /** this is called when a user delete an event.
     * @param eid an integer representing the id of the event
     * @param requester a {@code User} object representing the user who want to delete the event

     * @return a {@code boolean} specify if the event is successfully deleted. Will return false if:
     * <ul>
     * <li>the user requesting to delete the event is not the owner of the event</li>
     * <li>Or the id does not correspond to any event</li>
     * </ul>
     */
    public boolean deleteEvent(int eid,User requester){
        boolean isDeleted = false;
        // delete job in the scheduler
        Event ev = repo.findEventByID(eid);
        if(ev==null) return false;
        User u = repo.findOwnerOfEvent(eid);
        if(u.username.equals(requester.username)){
            isDeleted = repo.deleteEvent(eid);
            if(isDeleted)
                notifyParticipants("the event with ID:"+ eid+ "has been deleted, please reload your app", ev);
        }
        //
        // ok what i should do here is to create an exception which is specific to our application
        // call it CoreLogicException and throw it whenever the user made any mistake in sending request to our server.
        // and let another servlet show it to user.
        return isDeleted;
    }

        /** this is called when a user delete an event.
     * @param e an Event object which need an eventID
     * @param requester a {@code User} object representing the user who want to edit the event

     * @return a {@code boolean} specify if the event is successfully edtied. Will return false if:
     * <ul>
     * <li>the user requesting to edited the event is not the owner of the event</li>
     * <li>Or the id does not correspond to any event</li>
     * </ul>
     */
    public boolean editEvent(Event e,User requester){
        if(e.startFrom.isBefore(Instant.now())){throw new IllegalArgumentException("cannot set event date in the past");}
        if(e.startFrom.isAfter(e.endAt)){throw new IllegalArgumentException("cannot set event end before event start");}
        // edit event in the scheduler
        boolean isUpdated = false;
        System.out.println(e.eventID);
        Event ev = repo.findEventByID(e.eventID);
        if(ev==null) throw new IllegalArgumentException("we cannot find the event you want to delete with the provided eventID");
        User u = repo.findOwnerOfEvent(e.eventID);
        if(u.username.equals(requester.username)){
            isUpdated = repo.editEvent(e);
            if (isUpdated) {
                notifyParticipants("the event with ID:"+ e.eventID+ "has been changed, please reload your app", e);
            }
        }
        return isUpdated;
    }
    
    /** this is called when a user wants to find all events they are involved in
     * @param u a User object, which must include a userID and username
     * @return a {@code List} of {@code Event} of the user. 
     * @throws IllegalArgumentException if username and userID is not in {@code u} 
     */
    public List<Event> checkEventsList(User u){
        if(u.username == null || u.userID==-1) throw new IllegalArgumentException("username and userID must both be provided");
        return repo.findEventsFromUser(u);
    }
    
    /** a function to invite other participants to an event
     * @param e an Event object
     * @param participants  a list of User object which only requires name
     */
    public void inviteParticipants(Event e){
        for (String username : e.participantsList) {
            User user = repo.findUserByName(username);
            if(user== null) return;
            MailHelper.sendInvitationMail(e,user);
        }
    };
    
    /** this is called after user accept the invitation.
     * @param e
     * @param u
     * @throws IllegalArgumentException if {@code eventID} is null or username does not exist
     */
    public void afterAcceptInvitation(Event e,User u) {
        e = repo.findEventByID(e.eventID);
        if(e==null){
            throw new IllegalArgumentException("eventID cannot be null");
        }
        u = repo.findUserByName(u.username);
        if(u==null){throw new IllegalArgumentException("no such user exist");}
        repo.addParticipant(e.eventID,u.userID);
    }
    
    /** 
     * send notification to the participant of the event
     * @param content a {@code String} describing the content of the email we want to send
     * @param e a fully populated {@code Event} 
     * 
     */
    public void notifyParticipants(String content,Event e) {
        String[] recipients =  e.participantsList.stream().map(name -> repo.findUserByName(name))
        .filter(user -> (user != null))
        .map(user ->user.email).toArray(String[]::new);
        MailHelper.sendMail("new update on your event",content,recipients);
    }

    /** 
     * a function for admin to see all users in the system
     * @return a List of User 
     * 
     */
    public List<User> seeAllUser() {
        return repo.findAllUser();
        // return null;    
    }

    /** 
     * a function for admin to delete a specific in the system
     * @param username the username of the user 
     * @return {@code boolean}: indicate whether the username is deleted or not
     * 
     */
    public boolean deleteUser(String username){

        return repo.deleteUser(username)==1;
    }   
}
