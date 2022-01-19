package filter;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utilities.JwtHelper;
import entities.User;
import io.jsonwebtoken.security.SignatureException;

public class AuthenticateFilter extends HttpFilter{
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader("token");
        if(token==null){
            Logger.getGlobal().log(Level.INFO, "wtf"); 
            response.sendRedirect("/login");
            return;
        }
        try {
            Map<String,Object> userIdentifier = new JwtHelper().parseToken(token);
            String username = (String) userIdentifier.get("username");
            request.setAttribute("currentUser", new User(username));
            chain.doFilter(request, response);
        } catch (SignatureException e) {
            // the token is invalid or the token simply not exist
            response.sendRedirect("/login");
        }

    }
}
