package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utilities.Factory;
import Utilities.JsonHelper;
import entities.User;




// this is mostly for admin to administrate the users, too lazy to do this
public class AdminServlet extends HttpServlet {
    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    // get a list of user
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> userList =  Factory.servicesFactory().seeAllUser();
        JsonHelper.serialize(resp.getWriter(), userList);
    }

    
    /** 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getHeader("username");
        boolean isDeleted = Factory.servicesFactory().deleteUser(username);
        if(isDeleted)
        resp.getWriter().println("deleted successfully");
    }


}
