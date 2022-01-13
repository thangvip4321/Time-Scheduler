package usecases;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCrypt;

import entities.Event;
import entities.User;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.Signer;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import repositories.DataService;

import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

// theoretically in the servlet this usecase class should be called.
// but the logic created here is still too simple so the code in this usecase class
// will be just like boilerplate code.
public class AllUsecase {
    private DataService service;
    public boolean checkValidRegistration(String username,String password,String email){
        service.checkIfUserExist(username);
        boolean req1 = checkPasswordStrong(password);
        boolean req2 = checkRightFormatEmail(email);
        Map<String, Object> claim = new HashMap<String, Object>();
        JwtHelper jwt = new JwtHelper();
        jwt.put("username", username);
        jwt.put("email", email);
        String token = jwt.createToken();
        User tmpUser = new User(username,password,email);
        service.addUserToPending(tmpUser);
        MailHelper.sendVerificationMail(token,email);
        return true;
        // if user click on the link, send request to /confirm servlet with the key
    }

    public void registerUser(User u){
        service.deleteUserFromPending(u);
        service.createuser(u);
    }

    public boolean login(String username,String password){
        String hashed = service.showHashedPassword(username);
        boolean isPasswordCorrect=  BCrypt.checkpw(password, hashed);
        // hash password with appkey
        // check hashed with db 
        return isPasswordCorrect;
    }
    public void addEvent(Event e, User host){
        service.addEvent();
        service.addParticipant();
    }
    public void deleteEvent(Event e,User requester){
        service.checkIfUserOwnEvent(requester,e);
        service.deleteEvent();
    }
    public void editEvent(Event e,User requester){
        service.checkIfUserOwnEvent(requester,e);
        service.editEvent();
    }
    public Event[] checkEventsList(User u){
        service.findEventsWithUserID();
        return null;
    }
    public void inviteParticipants(Event e,User u,User[] participants){
        for (User user : participants) {
            sendInvitationMail(user);
        }
    };
    public void respondToInvitation(Event e,User u,boolean accept) {
        if(accept){
            service.addParticipant(e,u);
        }
        else{
            sendMailToHost("the user deny the invitation");
        }
    }
    public void notifyParticipants(Event e) {}
    
    
    // public boolean authenticate(){
        // return false;
    // }
    // public void editProfile(){}

    // public void findUser(); // by ID or not
    // public void findEventByID();
    // block user if too many time failed.
}
