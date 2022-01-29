package repositories;

import java.util.List;

import entities.Event;
import entities.User;


/**
 * This <code>DataRepository</code> interface is the one that {@link usecases.Services} will interact with
 * instead of a concrete database implementation, this way we can swap out Postgre and use MySql, or cloud service
 * , or even txt file
 * @see repositories.PostgreAdapter
 */
public interface DataRepository {
    /**
    * This is called in {@link usecases.Services} when they want to save a new User to their Repositories
    * 
    * Password should be hashed 
    @param u an <code>User</code> object which include all username,email,password (hashed) 
    @return a <code>boolean</code> specifying whether we have successfully add the user in the database.
    */
    public boolean createUser(User u);
    /**
    * This is called in {@link usecases.Services} when admin want to delete a new User from their Repositories
    * @param u an <code>User</code> object which only need to include username.
    @return a <code>boolean</code> specifying whether we have successfully delete the user from the database.
    */
    public boolean deleteUser(User u);

    /**
    * This is called in {@link usecases.Services} when they want to edit a new User in their Repositories.
    Bro what are we gonna edit with just username,email,password?
    * @param u an <code>User</code> object 
    @return a <code>boolean</code> specifying whether we have successfully edit the user in the database.
    */
    public boolean editUser(User u);

    /**
    * This is called in {@link usecases.Services} when they want to add a new Event in their Repositories.
    it will also trigger {@link addParticipant} as we need to add participants(including the host) 
    * @param event an <code>Event</code> object 
    @return an <code>int</code> specifying the event ID we just created.
    */
    public int addEvent(Event event);
    
    /**
    * This is called in {@link usecases.Services} when they want to delete Event from their Repositories.
    * @param eventID an <code>int</code> specifying the id of the event we want to delete 
    @return a <code>boolean</code> specifying whether we have successfully delete the from the database.
    */
    public boolean deleteEvent(int eventID);
    
    /**
    * This is called in {@link usecases.Services} when they want to edit an Event in their Repositories.
    * @param event an <code>Event</code> object which must include an eventID for specifying the event they want
    to change,and the rest of the attribute is for changing the event
    @return a <code>boolean</code> specifying whether we have successfully edited the database.
    */
    public boolean editEvent(Event event);

    /**
    * This is called in {@link usecases.Services} when they want to find all events associated with a user.
    * @param u a <code>User</code> object which must include a userID and a username for finding all related event.
    I feel like this is a bad design decision, should it just include userID or username?
    @return a <code>List</code> of <code>Event</code> in which either the user participate or host 
    */
    public List<Event> findEventsFromUser(User u);
    /**
    * This is called in {@link usecases.Services} when they want add a participant to an event.
    * @param eid a <code>int</code>
    can also add host of the event as a participant
    @return a <code>boolean</code> indicating whether the participant has been added to the event
    */
    public boolean addParticipant(int eid,int uid);

    /**
    * This is called in {@link usecases.Services} when the user need verifying before being actually put in the repositories.
    They will be put in a temporary database first.
    * @param u a <code>User</code> including email,username,hashed password
    @return a <code>boolean</code> indicating whether the user has been added to the temporary database. Should be false if there are duplicate tuple of
    (username,email)
    */
    public boolean addUserToPending(User u);


    /**
    * This is called in {@link usecases.Services} when a user has been verified and we need to remove similar usernames, which are not verified, from tmp repo.
    * @param username a <code>String</code> showing the name we need to remove
    @return an <code>int</code> indicating number of entries removed from the tmp repo.
    */
    public int deleteUserFromPending(String username);


    /**
    * This is called in {@link usecases.Services} when a user has been verified and we need to get the user out from tmp repo.
    * @param username a <code>String</code> showing the name of the verified user 
      @param email a <code>String</code> showing the email of the verified user 
    @return a <code>User</code> which should be collected from the tmp repo.should return null if there is no such user.
    */
    public User findUserInPending(String username,String email);



    public boolean checkIfUsernameExist(String username);


    /**
    @return a <code>String</code> showing the hashed password, should be null if the username does not exist in the repo
    */
    public String showHashedPassword(String username);

    /**
    @return a <code>User</code> object,which only include username,email,uid and <strong>NOT</strong> a list of events.
    Should be null if the eid does not exist in the repo.
    */
    public User findOwnerOfEvent(int eid);

    /**
    @return a fully populated <code>Event</code> object.
    
    Should be null if the eid does not exist in the repo.
    */
    public Event findEventByID(int eid);

    /**
    @return an <code>User</code> object including username,email,userID
    Should be null if the name does not exist in the repo.
    */
    public User findUserByName(String name);

    
    /**
    @return a {@code List} of <code>User</code> object including username,email,userID
    */
    public List<User> findAllUser();
    
    /** this is called when admin wants to delete a specific user.
     * @param username : the name of the user the admin wants to delete
     * @return int: the number of User deleted. Return 0 if username does not exist.
     * In normal case it should return 1 because the username is unique.
    */
    public int deleteUser(String username);

}