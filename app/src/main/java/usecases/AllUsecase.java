package usecases;

import repositories.DataService;

// theoretically in the servlet this usecase class should be called.
// but the logic created here is still too simple so the code in this usecase class
// will be just like boilerplate code.
public class AllUsecase {
    private DataService service;
    public void register(){}
    public void login(){}
    public void editProfile(){}
    public void addEvent(){}
    public void deleteEvent(){}
    public void editEvent(){}
    public void checkEventsList(){}
    public void inviteParticipants(){};
    public void respondToInvitation() {}
    public boolean authenticate(){
        return false;
    }
    public void notifyParticipants() {}
    // public void findUser(); // by ID or not
    // public void findEventByID();
    // block user if too many time failed.
}
