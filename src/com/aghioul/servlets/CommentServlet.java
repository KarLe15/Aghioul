package com.aghioul.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Comments;

@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CommentServlet() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * en GET, renvoyer les commentaires d'un message donné
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String messageId = request.getParameter("messageId");
		String key = request.getParameter("key");
		//
		
		response.getWriter().println(Comments.getComments(key, messageId));
	}

	/*
     * (non-Javadoc)
     * en POST, publier un commentaire sur un message donné
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String messageId = request.getParameter("messageId");
		String key = request.getParameter("key");
		String text = request.getParameter("text");
		
		response.getWriter().println(Comments.addComment(key, messageId, text).toJson());
	}

}
