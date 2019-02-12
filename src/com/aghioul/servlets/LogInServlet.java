package com.aghioul.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Authentification;
import com.aghioul.tools.ErrorAghioul;


import java.io.IOException;

public class LogInServlet extends HttpServlet {
  
	private static final long serialVersionUID = 1L;
	
	/*
	 * (non-Javadoc)
	 * Cette méthode n'est pas supportée
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "aghioul - methode non supportee");
    }
    
	/*
	 * (non-Javadoc)
	 * en POST, permet de se connecter et récupérer une clé d'utilisateur
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	String username = (String) request.getParameter("username");
    	String password = (String) request.getParameter("password");
    	
    	if(username == null || password == null)
    		response.getWriter().println(ErrorAghioul.serviceRefused("invalid fields", ErrorAghioul.SERVICE_ERROR).toJson());
    	else
    		response.getWriter().println(Authentification.login(username, password).toJson());
    }
}
