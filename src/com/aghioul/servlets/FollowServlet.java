package com.aghioul.servlets;

import com.aghioul.services.Friends;
import com.aghioul.tools.ErrorAghioul;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import java.io.IOException;

@WebServlet(name = "FollowServlet")
public class FollowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * en GET, permet de récupérer les abonnés/abonnements d'un utilisateur
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        
		String key = request.getParameter("key");;
		String followings = request.getParameter("followings");
		String followers = request.getParameter("followers");
		
		if(followers != null) {
			response.getWriter().println(Friends.getFollowers(key, followers).toJson());
		}
		else if (followings != null){
			response.getWriter().println(Friends.getFollowings(key, followings).toJson());
		}
    }

	/*
	 * (non-Javadoc)
	 * en POST, permet de s'abonner à un utilisateur
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        
        String key = request.getParameter("key");
        String user = request.getParameter("user");
        String follow = request.getParameter("follow");
        String idUser = request.getParameter("userId");
        String idfollow = request.getParameter("followId");

        Document ret = new Document();
        if(key == null) {
        	response.getWriter().println(ErrorAghioul.serviceRefused("missing API key", ErrorAghioul.SERVICE_ERROR).toString());
        	return;
        }
        	
        if(user != null && follow != null) {
            ret = Friends.follow(key,user,follow);
        }else if(idUser != null && idfollow != null){
            ret = Friends.follow(key,Integer.parseInt(idUser),Integer.parseInt(idfollow));
        }else{
            response.getWriter().println(ErrorAghioul.serviceRefused("invalid fields", ErrorAghioul.SERVICE_ERROR).toJson());
            return;
        }



        response.getWriter().println(ret.toString());
    }
}
