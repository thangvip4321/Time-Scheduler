package filter;

import java.io.IOException;
import java.util.Map;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utilities.JwtHelper;
import entities.User;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

/** 
 * Whenever a HTTP request want to get to a Servlet, it has to go through this authentication filter first
 * 
 * This filter will check for your JWT Token to check if you are actually logged in or not.
 * 
 * If you are then they will allow you to go through and reach the servlet.
 <p>
 * <strong> PS: </strong> this will filter all requests going to /event
 *  </p>
 * @author Nguyen Duc Thang
 */
public class AuthenticateUserFilter extends HttpFilter{
    Logger logger = LoggerFactory.getLogger(AuthenticateUserFilter.class);
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader("token");
        if(token==null){
            // logger.info("wtf");
            response.sendRedirect("/login");
            return;
        }
        try {
            Map<String,Object> userIdentifier = new JwtHelper().parseToken(token);
            String username = (String) userIdentifier.get("username");
            int userID = (int) userIdentifier.get("userID");
            request.setAttribute("currentUser", new User(username,userID));
            chain.doFilter(request, response);
        } catch (SignatureException e) {
            logger.error("The authentication token is not signed by our secret key",token);
            // the token is invalid or the token simply not exist
            response.sendError(403,"it seems like the token is invalid, please login again");
        } catch (MalformedJwtException e){
            logger.error("Wrong format on authentication token: {}",token);
        }

    }
}
