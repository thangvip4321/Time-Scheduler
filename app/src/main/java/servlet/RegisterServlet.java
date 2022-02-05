package servlet;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.Factory;
import entities.User;
import io.jsonwebtoken.io.IOException;
import repositories.PostgreAdapter;
import usecases.Services;
public class RegisterServlet extends HttpServlet {
    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     * @throws java.io.IOException
     */
    // server will only validate the login at this point
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, java.io.IOException{
        PrintWriter writer = resp.getWriter();

        // this is a very big assumption that the json message is on only 1 line.
        // String jsonString = req.getReader().readLine();
        User u = new ObjectMapper().readerFor(User.class).readValue(req.getReader());
        Services allUsecase =new Services(new PostgreAdapter());
        boolean isCorrect= allUsecase.checkValidRegistration(u.username, u.password, u.email);
        if(isCorrect) {
            resp.setStatus(200);
            // resp.setHeader("token", new JwtHelper().put("username", u.name).put("issued date", new Date().getTime()).createToken());
            writer.println("check your mail pls");
            // send the jwt token
        }else {resp.setStatus(403);
            writer.println("email invalid or password is not strong, please try again");
        }
        writer.flush();

    }
    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     * @throws java.io.IOException
     */
    // this is what happen after the user click the link on the mail
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, java.io.IOException{
        String token = req.getParameter("token");
        // Map<String,Object> data = new JwtHelper().parseToken(token); 
        // String name = (String) data.get("username");
        // String email = (String) data.get("email");
        // User u = new User(name, email);

        Services allUsecase =Factory.createService();
        try {
            allUsecase.registerAfterReceivingConfirmationMail(token);
            resp.getWriter().println("thank you now you can close this window");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
