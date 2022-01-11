package repositories;

public interface DataService {
    // this is a concept of what the actual Postgresql,MySQL connection can do
    // the implementation will be put in another file
    public void CreateUser();
    public void DeleteUser();
    public void EditUser();
    public void addEvent();
    public void deleteEvent();
    public void editEvent();
    public void listEventsWithUserID();
    public void addParticipant();
    // wait this interface should contain everything that the usecase use to interact with database, so leave it later.
}