package com.aghioul.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Message;
import com.aghioul.tools.ErrorAghioul;

@WebServlet("/PublishServlet")
public class PublishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public PublishServlet() {
        super();
    }

    /*
	 * (non-Javadoc)
	 * cette methode n'est pas supportée
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "aghioul - methode non supportee");
	}
	
	/*
	 * (non-Javadoc)
	 * en POST, permet de publier un wesh sur le réseau
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = request.getParameter("message");
		String key = request.getParameter("key");
		if(message == null)
			response.getWriter().println(ErrorAghioul.serviceRefused("missing parameter", ErrorAghioul.SERVICE_ERROR));
		else
			response.getWriter().println(Message.publish(key, message).toJson());
	}

}
