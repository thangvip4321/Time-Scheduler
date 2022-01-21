package servlet;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import Utilities.Factory;
import Utilities.JsonHelper;
import entities.Event;
import entities.User;

public class EventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        User currentUser = (User) req.getAttribute("currentUser");
        List<Event> evList = Factory.servicesFactory().checkEventsList(currentUser);
        String body =new ObjectMapper().writeValueAsString(evList);
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getWriter().write(body);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // this is the endpoint for adding event
        User currentUser = (User) req.getAttribute("currentUser");
        Event eventToBeCreated = JsonHelper.extractEvent(req.getReader());
        System.out.println(eventToBeCreated.organizer+","+eventToBeCreated.organizer+",");
        System.out.println(currentUser.username.equals(eventToBeCreated.organizer));
        if(!currentUser.username.equals(eventToBeCreated.organizer)){
            resp.setStatus(400);
            resp.getWriter().write("the user adding the event does not match the event organizer");
            return;
        }
        Event e = Factory.servicesFactory().addEvent(eventToBeCreated);
        JsonHelper.serialize(resp.getWriter(), e);
        resp.setStatus(200);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        Event eventToBeChanged = JsonHelper.extractEvent(req.getReader());
        boolean canUpdateEvent =  Factory.servicesFactory().editEvent(eventToBeChanged, currentUser);
        if(!canUpdateEvent){
            resp.getWriter().println("you cannot edit this event as you are " + currentUser.username);
            return;
        }
        resp.getWriter().println("Change was made, please reload");
        resp.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        Event eventToBeChanged = JsonHelper.extractEvent(req.getReader());
        boolean canDeleteEvent = Factory.servicesFactory().deleteEvent(eventToBeChanged, currentUser);
        if(!canDeleteEvent){
            resp.getWriter().println("you cannot delete this event");
        }
        resp.getWriter().println("Change was made, please reload");
        resp.setStatus(200);
    }
}
