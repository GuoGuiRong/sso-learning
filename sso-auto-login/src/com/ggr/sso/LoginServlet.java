package com.ggr.sso;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.xml.internal.bind.v2.runtime.Name;

import sun.awt.RepaintArea;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String userName = (String)request.getParameter("userName");
		String password = (String)request.getParameter("password");
		String autoLogin = (String)request.getParameter("autoLogin");
		String forwordPath = "/index.jsp";
		if("logout".equals(request.getParameter("action"))){
			request.getSession().invalidate();
			Cookie cookie = new Cookie("ggr", userName+":"+password);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setDomain("ggr.com");
			response.addCookie(cookie);
			request.getRequestDispatcher("/index.jsp").forward(request, response);	
			return;
		}
		if(userName==null || password==null){
			forwordPath = "/index.jsp";
		}else{
			String pwd = "123456";
			if(pwd.equals(password)){
				request.getSession().setAttribute("user", userName);
				if(autoLogin!=null){//如果设置了的话
					Cookie cookie = new Cookie("ggr", userName+":"+password);
					cookie.setMaxAge(24*60*60*14);
					cookie.setPath("/");
					cookie.setDomain("ggr.com");
					response.addCookie(cookie);
				}			
				forwordPath = "/success.jsp";
			}else{
				forwordPath = "/index.jsp";
			}
		}
		request.getRequestDispatcher(forwordPath).forward(request, response);	
	}

}
