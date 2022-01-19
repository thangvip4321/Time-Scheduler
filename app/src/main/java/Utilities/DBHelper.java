package Utilities;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class DBHelper {
    // Ok we can still use the initialContext outside of the servlet function
    public static JdbcTemplate getConnection() throws NamingException, SQLException{
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/UserDB");
        JdbcTemplate conn = new JdbcTemplate(ds);
        return conn;
    }
}
