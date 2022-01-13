package repositories;

import entities.Event;
import entities.User;

public interface DataService {
    // this is a concept of what the actual Postgresql,MySQL connection can do
    // the implementation will be put in another file
    public void createUser(User u);
    public void deleteUser(User u);
    public void editUser(User u);
    public void addEvent(Event event);
    public void deleteEvent(Event event);
    public void editEvent(Event event);
    public void findEventsFromUser(User u);
    public void addParticipant(Event e,User participant);
    public void addUserToPending(User u);
    public void deleteUserFromPending(User u);
    public boolean checkIfUserExist(User u);
    // wait this interface should contain everything that the usecase use to interact with database, so leave it later.
}