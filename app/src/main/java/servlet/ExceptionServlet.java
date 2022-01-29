package servlet;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionServlet extends HttpServlet{

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processError(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processError(request, response);
	}
	protected void doDelete(HttpServletRequest request,
	HttpServletResponse response) throws ServletException, IOException {
processError(request, response);
}
protected void doPut(HttpServletRequest request,
	HttpServletResponse response) throws ServletException, IOException {
processError(request, response);
}

	private void processError(HttpServletRequest request,
			HttpServletResponse response) throws IOException, IOException {
		// Analyze the servlet exception
		Throwable throwable = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request
				.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) request
				.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}
		
		// Set response content type
	    //   response.setContentType("t");
	 
	      PrintWriter out = response.getWriter();
	      if(statusCode != 500){
	    	  out.write("Status Code:"+statusCode+"\n");
	    	  out.write("<strong>Requested URI</strong>:"+requestUri);
	      }else{
			out.write("Servlet Name:"+servletName+"\n");
	    	  out.write("Exception Name:"+throwable.getClass().getName()+"\n");
	    	  out.write("Requested URI:"+requestUri+"\n");
	    	  out.write("Exception Message:"+throwable.getMessage()+"\n");
	      }
	}
}
