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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean createUser(User u) {
		conn.update("Insert into users (username,email,password) VALUES (?,?,?)",u.username,u.email,u.password);
        return true;
    }

    @Override
    public boolean deleteUser(User u) {
        // delete user and also delete all entry in userevent
        int uid = conn.queryForObject("SELECT * FROM users WHERE username=?", new Object[]{u.username}, new int[]{Types.VARCHAR}, int.class);
        conn.update("Delete from users where username=?",u.username);
        conn.update("Delete from userevent where uid=?", uid);
        return true;
    }

    @Override
    public boolean editUser(User u) {
        // TODO Auto-generated method stub
        conn.update("UPDATE users set (eventname,event_date)=(?,?) from events where eid=?",u.userID);
        return true;

    }

    @Override
    public int addEvent(Event event) {
        int eid = conn.queryForObject("Insert into events (eventname,organizer,event_date) VALUES (?,?,?) RETURNING eid;"
        ,int.class,event.eventName,event.organizer,Timestamp.from(event.date));
        return eid;
    }

    @Override
    public boolean deleteEvent(Event event) {
        // TODO Auto-generated method stub
        conn.update("Delete from events where eid=?",event.eventID);
        conn.update("Delete from userevent where eid=?", event.eventID);
        return true;
    }

    @Override
    public boolean editEvent(Event event) {
        conn.update("UPDATE events set eventname=?,event_date=? where eid=?",event.eventName,Timestamp.from(event.date),event.eventID);
        return true;
    }

    @Override
    public List<Event> findEventsFromUser(User u) {
        List<Event> evList = new ArrayList<Event>();
        SqlRowSet rs= 
        conn.queryForRowSet("SELECT * FROM events e WHERE e.eid in (SELECT eid FROM userevent where uid=?)",u.userID);
        while (rs.next()) {
            int eventID = rs.getInt("eid");
            evList.add(new Event(eventID, rs.getString("eventname"), 
                rs.getString("organizer"), rs.getTimestamp("event_date").toInstant(), 
                rs.getInt("priority"), findParticipants(eventID)));
        }

        rs = conn.queryForRowSet("SELECT * FROM events e WHERE organizer=?",u.username);
        while (rs.next()) {
            int eventID = rs.getInt("eid");
            evList.add(new Event(eventID, rs.getString("eventname"), 
                rs.getString("organizer"), rs.getTimestamp("event_date").toInstant(), 
                rs.getInt("priority"), findParticipants(eventID)));
        }
        return evList;
    }
    private List<String> findParticipants(int eid){
        String[] participantList =  conn.queryForList(
                    "SELECT username FROM users natural join userevent where eid=?",eid)
                .stream().map(userMap -> userMap.get("username")).toArray(String[]::new);
        return Arrays.asList(participantList);
    }

    public Event findEventByID(int eid) {
        SqlRowSet rs = conn.queryForRowSet("SELECT * FROM events WHERE eid= ?", eid);
        Event e = null;
        while (rs.next()) {
            e =new Event(eid,rs.getString("eventname"),
            rs.getString("organizer"),
                        rs.getTimestamp("event_date").toInstant(),
                        rs.getInt("priority"),
                        findParticipants(eid));
        }
        return e;
    }

    public User findUserByName(String  name) {
        SqlRowSet rs= conn.queryForRowSet("SELECT uid,username,email FROM users WHERE username=?",name);
        User tmpUser = null;
        while(rs.next()){
            tmpUser = new User(rs.getString("username"),rs.getString("email"),rs.getInt("uid"));
        }
        return tmpUser;
    }

    @Override
    public boolean addParticipant(int eid,int uid) {
        conn.update("INSERT INTO userevent (uid,eid) VALUES (?,?)", uid,eid);
        return true;
    }

    @Override
    public boolean addUserToPending(User u) {
        // TODO Auto-generated method stub
        try {
            conn.update("Insert into pendingusers (username,email,password) VALUES (?,?,?)",u.username,u.email,u.password);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public User deleteUserFromPending(String username) {
        SqlRowSet rs= conn.queryForRowSet("SELECT password,username,email FROM pendingusers WHERE username=?",username);
        User tmpUser = null;
        while(rs.next()){
            tmpUser = new User(rs.getString("username"),rs.getString("password"),rs.getString("email"));
            conn.update("Delete from pendingusers where username=?",username);
        }
        return tmpUser;
    }

    @Override
    public boolean checkIfUsernameExist(String username) {
        User u =findUserByName(username);
        // return !(u==null);
        return (u!=null);
    }

    @Override
    public String showHashedPassword(String username) {
        SqlRowSet rs =  conn.queryForRowSet("SELECT password from users where username=?", username);
        if(!rs.next()){System.out.println("Nothing in db"); return null;}
        return rs.getString("password");
    }

    @Override
    public User findOwnerOfEvent(int eid) {
        Event e = findEventByID(eid);
        if(e == null) return null;
        System.out.println();
        return findUserByName(e.organizer);
    }
    
}
