package com.aghioul.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Message;
import com.aghioul.services.Search;

@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SearchServlet() {
        super();
    }


    public static void main(String[] args) {
        System.out.println(Message.getByUser("wgYuLTIx5Or2SNv4T6uSOxzto0eicUdu", "karle15").toJson());
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String username = request.getParameter("user");
		String key = request.getParameter("key");
		String query = request.getParameter("query");
		
		if(username != null) {
			//si le nom d'utilisateur est spécifié, envoyer les wesh de l'utilisateur
			response.getWriter().println(Search.searchUser(key, username).toJson());
			return;
		}
		else if (query != null){
			//faire appel au moteur de recherche avec la query
			response.getWriter().println(Search.search(key, query).toJson());
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  	
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "aghioul - methode non supportee");
	}

}
