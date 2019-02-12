package com.aghioul.servlets;

import com.aghioul.services.Message;
import com.aghioul.tools.ErrorAghioul;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MessageServlet")
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MessageServlet() {
        super();
    }
    
    /*
     * (non-Javadoc)
     * en GET, renvoie la timeline
     * parametres : key, 
     * anteriorTo (timestamp) : le service renverra les 15 au plus derniers messages antérieurs à anteriorTo
     * posteriorTo (timestamp) : le service renverra les 15 au plus derniers messages postérieurs à posteriorTo
     * Si anteriorTo et posteriorTo non présents alors renvoie les 15 derniers messages à partir de maintenant
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String key = request.getParameter("key");
		String anteriorTo = request.getParameter("anteriorTo");
		String posteriorTo = request.getParameter("posteriorTo");
		
		if(key != null && anteriorTo != null) {
			Date date = new Date(Long.parseLong(anteriorTo));
			response.getWriter().println(Message.getTimeline(key, date, null).toJson());
		}
		else if(posteriorTo != null) {
			Date date = new Date(Long.parseLong(posteriorTo));
			response.getWriter().println(Message.getTimeline(key, null, date).toJson());
		}
		else if(anteriorTo == null){
			response.getWriter().println(Message.getTimeline(key, new Date(), null).toJson()); //retourner simplement les derniers messages
		}
		else {
			response.getWriter().println(ErrorAghioul.serviceRefused("missing key", ErrorAghioul.SERVICE_ERROR).toJson());
		}
	}

	/*
     * (non-Javadoc)
     * en POST, permet de liker, disliker ou partager
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String key = request.getParameter("key");
		String update = request.getParameter("update");
		String messageId = request.getParameter("messageId");
		if(key == null){
			response.getWriter().println(ErrorAghioul.serviceRefused("missing key", ErrorAghioul.SERVICE_ERROR).toJson());
		}else{
			if(messageId != null && update != null) {
				if(update.equals("like"))
					response.getWriter().println(Message.like(key, messageId).toJson());
				else if(update.equals("dislike"))
					response.getWriter().println(Message.dislike(key, messageId).toJson());
				else
					response.getWriter().println(Message.share(key, messageId).toJson());
			}	
		}
	}

}
