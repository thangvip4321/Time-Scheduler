package servlet;

import java.io.IOException;
import java.util.List;

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
        List<User> userList =  Factory.createService().seeAllUser();
        JsonHelper.serialize(resp.getWriter(), userList);
    }

    
    /** 
     * delete a User
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getHeader("username");
        boolean isDeleted = Factory.createService().deleteUser(username);
        if(isDeleted)
        resp.getWriter().println("deleted successfully");
    }


    // @Override
    // // login to the system, the good thing is we can hardcode our admin password inside this! Should we?
    // protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //     User maybeSpecialUser = JsonHelper.extractUser(req.getReader());
    //     if (maybeSpecialUser.username.equals("admin")){
    //         if(maybeSpecialUser.password.equals("hippopotamus451")) return;
    //             // return JwtHelper.createTokenWithJsonClaim(claim);
                
    //     }
    // }

}
