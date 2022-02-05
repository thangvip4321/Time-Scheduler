package Utilities;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;


/** 
    this is just a helper class for all things related to connecting to database
 */
public class DBHelper {
    
    /** 
     * @return JdbcTemplate
     * @throws NamingException
     * @throws SQLException
     */
    // Ok we can still use the initialContext outside of the servlet function
    public static JdbcTemplate getConnection() throws NamingException, SQLException{
        Context initContext = new InitialContext();
        System.err.println(initContext.getNameInNamespace());
        System.err.println(initContext.getEnvironment());
        // System.err.println(initContext.());

        Context envContext = (Context) initContext.lookup("java:comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/UserDB");
        JdbcTemplate conn = new JdbcTemplate(ds);
        return conn;
    }
}
