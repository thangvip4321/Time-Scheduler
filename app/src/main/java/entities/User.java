package entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class User {
    public String username;
    public String email;
    public String password; 
    public List<Event> eventList;
    public int userID;
    @JsonCreator
    User(@JsonProperty("username")String name,@JsonProperty("password")String password,
    @JsonProperty("email")String email,@JsonProperty("events list")List<Event> evList,
    @JsonProperty("userID")int userID){
        this.username = name;
        this.email = email;
        this.password = password;
        eventList = evList;
        this.userID = userID;
    }
    public User(String name,String password,String email){
        this(name,password,email,null,-1);
    }
    public User(String name,String email,int id){
        this(name,null,email,null,id);
    }
    public User(String name,String email){
        this(name,null,email);
    }
    public User(String name){
        this(name,null);
    }
    public User(String name,int id){
        this(name,null,id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return username.equals(that.username) &&
            email.equals(that.email) &&
            password.equals(that.password) &&
            eventList.equals(that.eventList) &&
            userID==that.userID;
    }
}
