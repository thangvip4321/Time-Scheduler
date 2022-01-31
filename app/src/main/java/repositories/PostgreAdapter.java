package repositories;


import java.sql.SQLException;
import java.sql.Timestamp;

import javax.naming.NamingException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import Utilities.DBHelper;
import entities.Event;
import entities.User;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgreAdapter implements DataRepository {
    private JdbcTemplate conn;
    
    public PostgreAdapter() {
        try {
            conn = DBHelper.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * @param u
     * @return boolean
     */
    @Override
    public boolean createUser(User u) {
		conn.update("Insert into users (username,email,password) VALUES (?,?,?)",u.username,u.email,u.password);
        return true;
    }

    
    /** 
     * @param u
     * @return boolean
     */
    @Override
    public boolean deleteUser(User u) {
        // delete user and also delete all entry in userevent
        int uid = conn.queryForObject("SELECT * FROM users WHERE username=?", new Object[]{u.username}, new int[]{Types.VARCHAR}, int.class);
        conn.update("Delete from users where username=?",u.username);
        conn.update("Delete from userevent where uid=?", uid);
        return true;
    }

    
    /** 
     * @param u
     * @return boolean
     */
    @Override
    public boolean editUser(User u) {
        // TODO Auto-generated method stub
        conn.update("UPDATE users set (eventname,start_time)=(?,?) from events where eid=?",u.userID);
        return true;

    }

    
    /** 
     * @param event
     * @return int
     */
    @Override
    public int addEvent(Event event) {
        int eid = conn.queryForObject("Insert into events (eventname,organizer,event_location,start_time,end_time,priority) VALUES (?,?,?,?,?,?) RETURNING eid;"
        ,int.class,event.eventName,event.organizer,event.location,Timestamp.from(event.startFrom),Timestamp.from(event.endAt),event.priority);
        return eid;
    }

    
    /** 
     * @param event
     * @return boolean
     */
    @Override
    public boolean deleteEvent(int eventID) {
        // TODO Auto-generated method stub
        conn.update("Delete from userevent where eid=?",eventID);
        conn.update("Delete from events where eid=?",eventID);
        return true;
    }

    
    /** 
     * @param event
     * @return boolean
     */
    @Override
    public boolean editEvent(Event event) {
        conn.update("UPDATE events set eventname=?,start_time=?,end_time=?,event_location=? where eid=?",
                    event.eventName,Timestamp.from(event.startFrom),Timestamp.from(event.endAt),event.location,event.eventID);
        return true;
    }

    
    /** 
     * @param u
     * @return List<Event>
     */
    @Override
    public List<Event> findEventsFromUser(User u) {
        List<Event> evList = new ArrayList<Event>();
        SqlRowSet rs= 
        conn.queryForRowSet("SELECT * FROM events e WHERE e.eid in (SELECT eid FROM userevent where uid=?)",u.userID);
        while (rs.next()) {
            int eventID = rs.getInt("eid");
            evList.add(createEventFromRowSet(rs));
        }

        rs = conn.queryForRowSet("SELECT * FROM events e WHERE organizer=?",u.username);
        while (rs.next()) {
            int eventID = rs.getInt("eid");
            evList.add(createEventFromRowSet(rs));
        }
        return evList;
    }
    
    /** 
     * @param eid
     * @return List<String>
     */
    private List<String> findParticipants(int eid){
        String[] participantList =  conn.queryForList(
                    "SELECT username FROM users natural join userevent where eid=?",eid)
                .stream().map(userMap -> userMap.get("username")).toArray(String[]::new);
        return Arrays.asList(participantList);
    }

    
    /** 
     * @param eid
     * @return Event
     */
    public Event findEventByID(int eid) {
        SqlRowSet rs = conn.queryForRowSet("SELECT * FROM events WHERE eid= ?", eid);
        Event e = null;
        while (rs.next()) {
            e =createEventFromRowSet(rs);
        }
        return e;
    }
    private Event createEventFromRowSet(SqlRowSet rs){
        int eid = rs.getInt("eid");
        return new Event(eid,rs.getString("eventname"),
        rs.getString("organizer"),rs.getTimestamp("start_time").toInstant()
        ,rs.getTimestamp("end_time").toInstant(),rs.getString("event_location")
        ,rs.getString("priority"),findParticipants(eid));
    }
    
    /** 
     * @param name
     * @return User
     */
    public User findUserByName(String  name) {
        SqlRowSet rs= conn.queryForRowSet("SELECT uid,username,email FROM users WHERE username=?",name);
        User tmpUser = null;
        while(rs.next()){
            tmpUser = new User(rs.getString("username"),rs.getString("email"),rs.getInt("uid"));
        }
        return tmpUser;
    }

    
    /** 
     * @param eid
     * @param uid
     * @return boolean
     */
    @Override
    public boolean addParticipant(int eid,int uid) {
        conn.update("INSERT INTO userevent (uid,eid) VALUES (?,?)", uid,eid);
        return true;
    }

    
    /** 
     * @param u
     * @return boolean
     */
    @Override
    public boolean addUserToPending(User u) {
        try {
            conn.update("Insert into pendingusers (username,email,password) VALUES (?,?,?)",u.username,u.email,u.password);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    

    @Override
    public int deleteUserFromPending(String username) {
        return conn.update("Delete from pendingusers where username=?",username);
    }

    
    /** 
     * @param username
     * @return boolean
     */
    @Override
    public boolean checkIfUsernameExist(String username) {
        User u =findUserByName(username);
        return !(u==null);
    }

    
    /** 
     * @param username
     * @return String
     */
    @Override
    public String showHashedPassword(String username) {
        SqlRowSet rs =  conn.queryForRowSet("SELECT password from users where username=?", username);
        if(!rs.next()){System.out.println("Nothing in db"); return null;}
        return rs.getString("password");
    }

    
    /** 
     * @param eid
     * @return User
     */
    @Override
    public User findOwnerOfEvent(int eid) {
        Event e = findEventByID(eid);
        if(e == null) return null;
        System.out.println();
        return findUserByName(e.organizer);
    }


    @Override
    public User findUserInPending(String username, String email) {
        SqlRowSet rs= conn.queryForRowSet("SELECT password,username,email FROM pendingusers WHERE username=? AND email = ?",username,email);
        User tmpUser = null;
        while(rs.next()){
            tmpUser = new User(rs.getString("username"),rs.getString("password"),rs.getString("email"));
        }
        return tmpUser;
    }


    @Override
    public List<User> findAllUser() {
        List<User> userList = new ArrayList<User>();
        SqlRowSet rs= 
        conn.queryForRowSet("SELECT username,email,uid FROM users");
        while (rs.next()) {
            int eventID = rs.getInt("eid");
            userList.add(new User(rs.getString("username"), rs.getString("email"), rs.getInt("uid")));
        }
        return userList;
    }


    @Override
    public int deleteUser(String username) {
        return conn.update("Delete from users where username=?",username);
    }

}
