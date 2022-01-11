package servlet;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Utils {
    // Ok we can still use the initialContext outside of the servlet function
    public static Connection getSource() throws NamingException, SQLException{
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/UserDB");
        Connection conn = ds.getConnection();
        return conn;
    }
}
