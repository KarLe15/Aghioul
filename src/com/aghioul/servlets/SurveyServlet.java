package com.aghioul.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aghioul.services.Survey;
import com.aghioul.tools.Saraha;

@WebServlet("/SurveyServlet")
public class SurveyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public SurveyServlet() {
        super();
    }


    /**
     * en GET, donne les servey
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
		String key = request.getParameter("key");
		response.getWriter().println(Survey.getSurveys(key).toJson());
	}


    /**
     *  en POST,        crée un servey          ou          permet de repondre a un servey
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
    	response.setContentType("application/json");
    	
		String key = request.getParameter("key");
		String title = request.getParameter("title");
		String question = request.getParameter("question"); //fournir le parametre question=1 pour voter à la reponse 1
		String q1 = request.getParameter("reponse1");
		String q2 = request.getParameter("reponse2");
		String surveyId = request.getParameter("surveyId");
		
		if(question != null && surveyId != null) {
			int q = Integer.valueOf(question);
			if(q >= 0 && q < 2) {
				response.getWriter().println(Survey.vote(key, surveyId, q).toJson());
				return;
			}
		}
		
		ArrayList<String> questions = new ArrayList<String>();
		questions.add(q1);
		questions.add(q2);
		response.getWriter().println(Survey.createSurvey(key, title, Saraha.NEVER, questions).toJson());
	}

}
