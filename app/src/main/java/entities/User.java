package entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Just like Event class, this is just a placeholder class for storing Event entity when doing business logic code. Thus all properties are set to public.
 * 
 * <p> This class behaves similarly to a struct in C. The purpose of this class is just for other outside functions to change its state, that's all.
 * No methods, only attributes!
 * @see entities.Event
 */
public class User {
    public String username;
    public String email;
    public String password;
    @JsonProperty("events list") 
    public List<Event> eventList;
    public int userID;


    /**
 * <p>This is a default constructor for making User class.
 * 
 * <p> Those {@link JsonProperty}s are used for deserializing from JSON objects back to {@link User} object.
     * @param name   Name of the user
     *
     * @param email  email of the user. <strong>Reminder:</strong> this user class is just a placeholder, so
     * it is not responsible for verifying email's format. I wonder if this design decision is correct or not.
     * 
     * @param password password of the user. <strong>Reminder: </strong> this user class is just a placeholder, so
     * it is not responsible for verifying password's format.
     ** @param password password of the user. <strong>Reminder: </strong> this user class is just a placeholder, so
     * it is not responsible for verifying password's format. Also the password should ideally be hashed.
     * @param eventList list of events the user is hosting or participating. <strong>Reminder: </strong> this eventList absolutely 
     * does not reflect the actual event list of the user.
     * @param userID userID of the user. <strong>Reminder:</strong> , again this class is just a placeholder, so sometimes we dont
     * even need to specify this ID.
     * @exception IOException   if an input or output error is
     *                              detected when the servlet handles
     *                              the GET request
     *
     * @exception InvalidA  if the request for the GET
     *                                  could not be handled
     *
     */
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
