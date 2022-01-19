package usecases;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCrypt;

import Utilities.FormValidator;
import Utilities.JwtHelper;
import Utilities.MailHelper;
import entities.Event;
import entities.User;
import io.jsonwebtoken.security.SignatureException;
import repositories.DataRepository;


// theoretically in the servlet this usecase class should be called.
// but the logic created here is still too simple so the code in this usecase class
// will be just like boilerplate code.
public class Services {
    private DataRepository repo;
    public Services(DataRepository repo) {
        this.repo = repo;
    }

    public boolean checkValidRegistration(String username,String password,String email){
        repo.checkIfUsernameExist(username);
        boolean req1 = FormValidator.validatePassword(password);
        boolean req2 = FormValidator.validateEmail(email);
        if(!req1){
            System.out.println("Password not strong");
            return false;
        }
        if(!req2){
            System.out.println("invalid email address");
            return false;
        }

        //  i need to learn a bit more about elliptic curves and bcrypt and stuff
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User tmpUser = new User(username,hashedPassword,email);
        repo.addUserToPending(tmpUser);


        JwtHelper jwt = new JwtHelper();
        jwt.put("username", username);
        jwt.put("email", email);
        String token = jwt.createToken();
        MailHelper.sendVerificationMail(token,email);
        return true;
        // if user click on the link, send request to /confirm servlet with the key
    }

    public void registerAfterReceivingConfirmationMail(String token) throws IllegalAccessException{
        JwtHelper jwt = new JwtHelper();
        try {
           Map<String, Object> userInfo =jwt.parseToken(token);
           String username = (String) userInfo.get("username");
           String email = (String) userInfo.get("email");
           

           User tmpUser = repo.deleteUserFromPending(username);
           if(tmpUser==null){
            throw new IllegalAccessException("looks like somebody has already created an account with this username");

           }
           repo.createUser(tmpUser);
        } catch (SignatureException e) {
            throw new IllegalAccessException("the confirm link you sent is invalid");
        }
    }

    public boolean login(String username,String password){
        String hashed = repo.showHashedPassword(username);
        if(hashed==null){return false;}
        boolean isPasswordCorrect=  BCrypt.checkpw(password, hashed);
        return isPasswordCorrect;
    }

    // this should return a whole event object with eventID for further identification
    public Event addEvent(Event e){
        e.eventID = repo.addEvent(e);
        User[] participantList = (User[]) e.participantsList.stream().map(participantName -> new User(participantName)).toArray();
        inviteParticipants(e,participantList);
        return e;
    }
    public boolean deleteEvent(Event e,User requester){
        boolean isDeleted = false;
        User u = repo.findOwnerOfEvent(e.eventID);
        if(u.username==requester.username){
            isDeleted = repo.deleteEvent(e);
        }
        return isDeleted;

    }

    // this func need eventID to edit it
    public boolean editEvent(Event e,User requester){
        boolean isUpdated = false;
        User u = repo.findOwnerOfEvent(e.eventID);
        if(u.username==requester.username){
            isUpdated = repo.editEvent(e);
            if (isUpdated) {
                notifyParticipants("the event is changed, please reload your app", e);
            }
        }
        return isUpdated;
    }
    public List<Event> checkEventsList(User u){
        return repo.findEventsFromUser(u);
    }
    public void inviteParticipants(Event e,User[] participants){
        for (User user : participants) {
            MailHelper.sendInvitationMail(e,user);
        }
    };
    public void afterAcceptInvitation(Event e,User u) {
        e = repo.findEventByID(e.eventID);
        if(e==null){return;}
        u = repo.findUserByName(u.username);
        if(u==null){return;}
        repo.addParticipant(e.eventID,u.userID);
    }
    public void notifyParticipants(String content,Event e) {
        e = repo.findEventByID(e.eventID);
        String[] recipients = (String[]) e.participantsList.stream().map(name -> repo.findUserByName(name).email).toArray();
        MailHelper.sendMail(content,recipients);
    }
    
    
    // public boolean authenticate(){
        // return false;
    // }
    // public void editProfile(){}

    // public void findUser(); // by ID or not
    // public void findEventByID();
    // block user if too many time failed.
}
