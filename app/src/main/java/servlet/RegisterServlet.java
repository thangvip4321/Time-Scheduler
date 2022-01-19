package servlet;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.Factory;
import entities.User;
import io.jsonwebtoken.io.IOException;
import repositories.PostgreAdapter;
import usecases.Services;
public class RegisterServlet extends HttpServlet {
    // server will only validate the login at this point
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, java.io.IOException{
        PrintWriter writer = resp.getWriter();

        // this is a very big assumption that the json message is on only 1 line.
        // String jsonString = req.getReader().readLine();
        User u = new ObjectMapper().reader(User.class).readValue(req.getReader());
        Services allUsecase =new Services(new PostgreAdapter());
        boolean isCorrect= allUsecase.checkValidRegistration(u.username, u.password, u.email);
        if(isCorrect) {
            resp.setStatus(200);
            // resp.setHeader("token", new JwtHelper().put("username", u.name).put("issued date", new Date().getTime()).createToken());
            writer.println("check your mail pls");
            // send the jwt token
        }else {resp.setStatus(500);
            writer.println("email invalid or password is not strong, please try again");
        }
        writer.flush();

    }
    // this is what happen after the user click the link on the mail
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, java.io.IOException{
        String token = req.getParameter("token");
        // Map<String,Object> data = new JwtHelper().parseToken(token); 
        // String name = (String) data.get("username");
        // String email = (String) data.get("email");
        // User u = new User(name, email);

        Services allUsecase =Factory.servicesFactory();
        try {
            allUsecase.registerAfterReceivingConfirmationMail(token);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
