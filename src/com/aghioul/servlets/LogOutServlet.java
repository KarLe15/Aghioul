package com.aghioul.servlets;

import com.aghioul.services.Authentification;
import com.aghioul.tools.ErrorAghioul;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogOutServlet")
public class LogOutServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String key = request.getParameter("key");

        if(username == null || key == null) {
            response.getWriter().println(ErrorAghioul.serviceRefused("invalid fields", ErrorAghioul.SERVICE_ERROR).toJson());
        }
        response.getWriter().println(Authentification.logout(username,key).toJson());
    }
}
