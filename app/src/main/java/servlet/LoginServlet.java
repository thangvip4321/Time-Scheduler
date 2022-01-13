package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import usecases.AllUsecase;

public class LoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
        String username = req.getReader().getParameter("username");
        boolean isCorrect = new AllUsecase().login(username, password);
        if(isCorrect) {
            resp.setStatus(200);
        }else {resp.setStatus(500);}
    }
}
