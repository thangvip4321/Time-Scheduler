package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
public class UserListServlet extends HttpServlet{
    private static final long serialVersionUID = 123L;
	private ObjectMapper oMapper = new ObjectMapper();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		try {
			Context initContext = new InitialContext();
			// List<ObjectNode> 
			
			Context envContext = (Context) initContext.lookup("java:comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/UserDB");
			Connection conn = ds.getConnection();
			Connection conn2 = Utils.getSource();
			Statement statement = conn.createStatement();
			String sql = "select sname, matrnr from student";
			ResultSet rs = statement.executeQuery(sql);
			
			int count = 1;
			response.setContentType("application/json");
			while (rs.next()) {
				Student v1 = new Student(rs.getString("sname"),rs.getInt("matrnr"));
				// String str= oMapper.writeValueAsString(v1);
				oMapper.writeValue(writer, v1);
				writer.flush();
				// writer.println(rs.getString("sname") + rs.getString("matrnr"));
				
			}
		} catch (NamingException ex) {
			System.err.println(ex);
		} catch (SQLException ex) {
			System.err.println(ex);
		}
	}
	private class Student{
		public Student(String string, int int1) {
			name=string;
			matriculationNumber=int1;
		}
		public String name;
		public int matriculationNumber;
	}
}
