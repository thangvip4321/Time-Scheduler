package entities;

public class User {
    public String name;
    public String email;
    public int userID;
    public Event[] eventList;

    User(){
        
    }
    public void addEvent(Event e){};
    public void editEvent(Event e){};
    public void deleteEvent(Event e){};
    public void showEvents(){};

    // private void confirmMail() {
    //    MailService.sendMailWithOTP(otpNumber);
    //    CheckNumberOK
    // }

}
