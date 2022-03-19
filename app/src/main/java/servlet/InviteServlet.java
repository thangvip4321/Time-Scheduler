package servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utilities.Factory;
import Utilities.JwtHelper;
import entities.Event;
import entities.User;

public class InviteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String token = req.getParameter("token");
        Map<String,Object> invitationDetails =  new JwtHelper().parseToken(token);
        int eventID= (int) invitationDetails.get("eventID");
        String username = (String) invitationDetails.get("username");
        Factory.servicesFactory().afterAcceptInvitation(new Event(eventID), new User(username));
    }
}
