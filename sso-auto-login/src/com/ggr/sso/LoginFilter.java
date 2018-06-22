package com.ggr.sso;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {

    /**
     * Default constructor. 
     */
    public LoginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;

		String user = (String)req.getSession().getAttribute("user");
		if(user!=null){
			chain.doFilter(request, response);
			return;
		}
		Cookie[] cookies = req.getCookies();
		if(cookies!=null && cookies.length>0){
			for(Cookie c:cookies){
				if(c.getName().equals("ggr")){
					String pwd = "123456";
				    String password = c.getValue().split("0")[1];
				    if(password.endsWith(pwd)){
				    	req.getSession().setAttribute("user", c.getName()+":"+c.getValue());
				    }	
				    break;
				}
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	private String loginPath;
	public void init(FilterConfig fConfig) throws ServletException {

		loginPath = (String)fConfig.getInitParameter("loginPath");
	}

}