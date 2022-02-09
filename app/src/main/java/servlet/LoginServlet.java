package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.Factory;
import Utilities.JsonHelper;
import Utilities.JwtHelper;
import entities.User;

public class LoginServlet extends HttpServlet {
    
    /** 
     * @param req an {@code HttpServletRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     * @param resp
     * @throws ServletException
     * @throws JsonProcessingException
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, JsonProcessingException, IOException{
        PrintWriter writer = resp.getWriter();

        // this is a very big assumption that the json message is on only 1 line.
        // String jsonString = req.getReader().readLine();
        User u = JsonHelper.extractUser(req.getReader());
        if (u.username.equals("admin")){
            if(u.password.equals("123")){
                resp.setStatus(200);
                // we will try to differentiate this admin user from any smartaleck who put admin as their username.
                resp.setHeader("token", new JwtHelper().put("username", "admin").put("userID", 0).createToken());
                writer.println("login successfully as admin");
                return;
            }
        }

        User result = Factory.createService().login(u.username, u.password);


        if(result != null) {
            resp.setStatus(200);
            resp.setHeader("token", new JwtHelper().put("username", result.username)
                                                   .put("issued date", new Date().getTime())
                                                   .put("userID",result.userID).createToken());
            
            writer.println("login successfully");
            // send the jwt token
        }else {resp.setStatus(400);
            writer.println("Incorrect login");
        }
        writer.flush();
    }
    
    /** 
     * @param req
     * @param resp
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // should be a server side rendering here but wtf am i doing
        resp.setStatus(200);
        resp.getWriter().println("please log in");
    }
}
