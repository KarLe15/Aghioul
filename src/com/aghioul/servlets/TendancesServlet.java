package com.aghioul.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.analytics.Tendances;

@WebServlet("/TendancesServlet")
public class TendancesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public TendancesServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
		response.getWriter().println(Tendances.getTendances().toJson());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
