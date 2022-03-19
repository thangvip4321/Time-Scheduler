package repositories;

import java.util.List;

import entities.Event;
import entities.User;

public interface DataRepository {
    // this is a concept of what the actual Postgresql,MySQL connection can do
    // the implementation will be put in another file
    public boolean createUser(User u);
    public boolean deleteUser(User u);
    public boolean editUser(User u);
    public int addEvent(Event event);
    public boolean deleteEvent(Event event);
    public boolean editEvent(Event event);
    public List<Event> findEventsFromUser(User u);
    public boolean addParticipant(int eid,int uid);
    public boolean addUserToPending(User u);
    // after deleting the user from pending database, return the user so that we can do something
    // e.g: add the user to a permanent database, or sth.
    // should return null if there is no such user
    public User deleteUserFromPending(String username);
    public boolean checkIfUsernameExist(String username);
    // wait this interface should contain everything that the usecase use to interact with database, so leave it later.
    public String showHashedPassword(String username);
    public User findOwnerOfEvent(int eid);
    public Event findEventByID(int eid);
    // public User findUserByID(int uid);
    public User findUserByName(String name);

}