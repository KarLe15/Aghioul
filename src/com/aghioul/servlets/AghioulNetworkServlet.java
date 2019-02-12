package com.aghioul.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.analytics.AghioulNetwork;

/**
 * Servlet implementation class AghioulNetworkServlet
 */
@WebServlet("/AghioulNetworkServlet")
public class AghioulNetworkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AghioulNetworkServlet() {
        super();
    }

	/**
	 * en GET, renvoie le cercle formé par mes followers & followings, et leurs followers & followings
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	String username = request.getParameter("username");
		response.getWriter().println(AghioulNetwork.getNeighborhood("lol", username).toJson());;
	}

	/**
	 * en POST, renvoie
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
		response.getWriter();
	}

}
