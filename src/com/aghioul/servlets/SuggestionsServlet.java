package com.aghioul.servlets;

import com.aghioul.analytics.FriendGraph;
import com.aghioul.tools.ErrorAghioul;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SuggestionsServlet")
public class SuggestionsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
        String key = request.getParameter("key");
        String username = request.getParameter("username");
        if(key != null){
            response.getWriter().println(FriendGraph.getSuggestions(key,username).toJson());
        }else{
            response.getWriter().println(ErrorAghioul.serviceRefused("missing key", ErrorAghioul.SERVICE_ERROR).toJson());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "aghioul - methode non supportee");
    }
}
