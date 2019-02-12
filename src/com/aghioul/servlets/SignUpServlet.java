package com.aghioul.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Authentification;
import com.aghioul.tools.ErrorAghioul;
import com.aghioul.tools.Utilisateur;

import java.io.IOException;

public class SignUpServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	@Override
	/*
	 * (non-Javadoc)
	 * cette méthode n'est pas supportée
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "aghioul - methode non supportee");
    }

    @Override
    /*
     * (non-Javadoc)
     * en POST, permet d'inscrire un nouvel utilisateur
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	//recuperer les parametres
    	String nom = (String) request.getParameter("nom");
    	String prenom = (String) request.getParameter("prenom");
    	String password = (String) request.getParameter("password");
    	String username = (String) request.getParameter("username");
    	String sexe = (String) request.getParameter("sexe");
    	String mail = (String) request.getParameter("email");

    	if(nom == null || prenom == null || password == null || username == null || sexe == null || mail == null) {
    		response.getWriter().println(ErrorAghioul.serviceRefused("missing arguments", ErrorAghioul.SERVICE_ERROR));
    		return;
    	}
    	
    	Utilisateur u = new Utilisateur(mail, nom, prenom, password, username, sexe);
    	
    	response.getWriter().println(Authentification.signup(u).toJson());
    }
}
