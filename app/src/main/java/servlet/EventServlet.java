package servlet;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utilities.Factory;
import Utilities.JsonHelper;
import entities.Event;
import entities.User;
import static gradle_tish_embedded.App.reminder;
import repositories.PostgreAdapter;
import reminder.QuartzReminder;

/** this is for all request that heads to the /event endpoints
 */
public class EventServlet extends HttpServlet {
    // actually i should instantiate reminder in the App, but when i created an initialContext there
    // i could not find the resource
    static{
        try {
            reminder = new QuartzReminder(new PostgreAdapter());
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    Logger logger = LoggerFactory.getLogger(EventServlet.class);
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        User currentUser = (User) req.getAttribute("currentUser");
        List<Event> evList = Factory.createService().checkEventsList(currentUser);
        String body =new ObjectMapper().writeValueAsString(evList);
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getWriter().write(body);
    }

    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // this is the endpoint for adding event
        User currentUser = (User) req.getAttribute("currentUser");
        Event eventToBeCreated = JsonHelper.extractEvent(req.getReader());
        if(!currentUser.username.equals(eventToBeCreated.organizer)){
            resp.setStatus(400);
            resp.getWriter().write("the user adding the event does not match the event organizer");
            return;
        }
        Event e = Factory.createService().addEvent(eventToBeCreated);
        JsonHelper.serialize(resp.getWriter(), e);
        resp.setStatus(200);
    }

    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        Event eventToBeChanged = JsonHelper.extractEvent(req.getReader());
        boolean canUpdateEvent =  Factory.createService().editEvent(eventToBeChanged, currentUser);
        if(!canUpdateEvent){
            resp.getWriter().println("you cannot edit this event");
            return;
        }
        resp.getWriter().println("Change was made, please reload");
        resp.setStatus(200);
    }

    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        int eventID=-1;
        try {
            eventID =   Integer.parseInt(req.getHeader("eventID"));
        } catch (Exception e) {
            throw new ServletException("the eventID parameter must be an integer");
        }
        boolean canDeleteEvent = Factory.createService().deleteEvent(eventID, currentUser);
        if(!canDeleteEvent){
            resp.getWriter().println("you cannot delete this event");
        }else{
            resp.getWriter().println("Change was made, please reload");
        }
        resp.setStatus(200);
    }
}
