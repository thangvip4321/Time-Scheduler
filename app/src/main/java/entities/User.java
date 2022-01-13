package entities;

import java.util.List;

public class User {
    public String name;
    public String email;
    public String password; 
    public List<Event> eventList;

    User(String name,String password,String email,List<Event> evList){
        this.name = name;
        this.email = email;
        this.password = password;
        eventList = evList; 
    }
    public User(String name,String password,String email){
        this(name,password,email,null);
    }
    User(String name,String email){
        this(name,null,email);
    }
    User(String name){
        this(name,null);
    }


}
