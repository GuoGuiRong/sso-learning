# sso-learning
### 单点登录专题学习

- Tip1: 用Tomcat配置虚拟主机

    - 虚拟主机的定义: 
        一台物理机上面搭建多个web站点，每个站点独立运行，互不干扰，这些站点就是"虚拟主机"

    - 目的：
	    在一台计算机上创建多个WEB站点，并为每个WEB站点设置不同的主目录和虚拟子目录，每个WEB站点作为各自独立的网站分配给不同的公司或部门。 

    - 好处：
	    多个公司或部门的网站就可以共用同一台计算机，而用户感觉每个公司和部门都有各自独立的网站。多个没有实力在Internet上架设自己专用服务器的中小公司可以联合租用一台WEB服务器，对外提供各自的WEB服务而互不影响。 
    
    - 原理：
        WEB服务器上的每个WEB站点必须设置有不同的标识信息
        WEB浏览器发出的连接和请求信息中包含WEB站点的标识信息
    
    - WEB站点的标识信息：
        IP地址、端口号、主机名

    所以理论上也存在3种不同的虚拟主机配置方案。


  - 浏览器访问WEB资源的过程分析
    * URL
    格式： http://主机名（或IP地址）：端口号/目录名称/资源名称
    
      举例： http://www.ggr.com/book/java.html 
   
      思考： http://www.ggr.com/book/java.html中的www.ggr.com是代表一台计算机的地址，还是代表一个网站的地址呢？网站的地址与计算机的地址有什么关系和区别呢？一个计算机地址上对应多个网站地址，它们是一对多的关系！
    
    * 通过URL访问WEB资源的过程分析
    
      ![URL访问WEB资源的过程分析](http://oqp19rq4p.bkt.clouddn.com/%E6%B5%8F%E8%A7%88%E5%99%A8%E8%AE%BF%E9%97%AE%E8%BF%87%E7%A8%8B.png) 
      
    * 过程原理： 
    
        用户发送请求到web服务器，该请求会被正在监听的Connector连接器接收（通过端口号以及协议信息来定位使用哪一个Connector连接器），并把该请求交给Service下的Engine来处理，并等待Engine处理的结果。Engine获得请求后会根据请求的主机信息（主机名）来匹配相应的Host主机，Host主机会根据请求的路径匹配对应的Context，Context web应用匹配上之后就构建request、response请求对象，调用指定的Servlet来处理请求。请求处理完成后会将response对象返回给Host主机，Host主机将response对象返回给Engine引擎，Engine再将response对象返回给Connector链接器，最后Connector连接器将response返回给浏览器。
  
  - 基于主机名的虚拟主机 
    
    * 原理：
    
        多个域名解析到同一个IP地址，在WEB服务器里添加多个站点，每个站点设定一个主机名。HTTP协议请求里包含了主机名信息，当WEB服务器收到访问请求时，就可以根据不同的主机名来访问不同的网站。
  
    * 步骤：
    
        a、配置域名与Ip的映射管理（对于本地局域网我们使用在host文件中添加；对于大型网络或者外网网络则需要配置DNS服务器中Ip地址与域名的映射关系）
        
        在C:\Windows\System32\drivers\etc\hosts文件末尾添加：
       
        ```
          127.0.0.1 www.ggr.org
          127.0.0.1 www.clj.org
        ```
        
        b、配置server.xml
         
        ```xml
          <Host name="www.ggr.org"  appBase="C:/Users/GuiRunning/Desktop/app1" unpackWARs="true" autoDeploy="true">
                <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                    prefix="localhost_access_log." suffix=".txt"
                    pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
          
          <Host name="www.clj.org"  appBase="C:/Users/GuiRunning/Desktop/app2" unpackWARs="true" autoDeploy="true">
              <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                 prefix="localhost_access_log." suffix=".txt"
                 pattern="%h %l %u %t &quot;%r&quot; %s %b" />
           </Host>
        ```
        
        c、测试
        
            http://www.ggr.org:8080/
        
            http://www.clj.org:8080/
        
        d.备注：
        
         开始下载新的tomcat7测试，发现每次启动的都是固定的某个tomcat，
         原来每个tomcat的默认启动未知是调用了系统环境变量CATALINA_HOME里面的tomcat路径，我们只要重新添加一个新变量CATALINA_HOME2指向新的tomcat存放路径，
         然后修改新的tomcat包下的bin目录里面的catalina.bat,startup.bat,shutdown.bat里面的%CATALINA_HOME%为%CATALINA_HOME2%便可
         同时还需要添加一个新环境变量CATALINA_BASE2 值和CATALINA_HOME一样，后续步骤和上面一样。
        
        
  
  - 基于端口号的虚拟主机 
  
     * 原理：通过使用不同的链接器来链接
     
     * 步骤：
     
       a、修改server.xml配置文件如下：
       ```xml
        <Service name="Catalina">
            <Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
            <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
            <Engine name="Catalina" defaultHost="localhost">
              <Realm className="org.apache.catalina.realm.LockOutRealm">
                <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
              </Realm>
                <Host name="www.ggr.org"  appBase="C:/Users/GuiRunning/Desktop/app1" unpackWARs="true" autoDeploy="true">
                    <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                    prefix="localhost_access_log." suffix=".txt"
                    pattern="%h %l %u %t &quot;%r&quot; %s %b" />
                </Host>
            </Engine>
          </Service>
          
          <Service name="Catalina2">
            <Connector port="8888" protocol="HTTP/1.1"  connectionTimeout="20000" redirectPort="8443" />
            <Connector port="8010" protocol="AJP/1.3" redirectPort="8443" />
            <Engine name="Catalina2" defaultHost="localhost">
              <Realm className="org.apache.catalina.realm.LockOutRealm">
                <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
              </Realm>
                <Host name="www.ggr.org"  appBase="C:/Users/GuiRunning/Desktop/app2" unpackWARs="true" autoDeploy="true">
                    <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                    prefix="localhost_access_log." suffix=".txt"
                    pattern="%h %l %u %t &quot;%r&quot; %s %b" />
                </Host>
            </Engine>
          </Service>
       ```
       
       b、测试
                
            http://www.ggr.org:8080/
            http://www.ggr.org:8888/
            
            
 # 基于Cookie的单点登录实现
 
  - 说明：Http是一种无状态的协议，所以我们需要额外地使用一些特殊的手段来维护状态。Session/Cookie就是我们用来维护客户端和服务器端状态的一种手段。
 通过使用Cookie携带JESSONID的方式来标志每一个会话，从而实现客户端和服务器端状态的管理是这种手段的基本原理。其中Cookie包括了key，value，过期时间，path，域，安全等几个重要属性
 通过Cookie我们可以实现同意域名下的服务集群的单点登录。通过设置Cookie的域为某个域名，然后将服务放在这个域名的多个子域名下，就可以实现Cookie共享，
 通过设置Cookie的path保证Cookie在作用的请求路径的范围，通过设置过期时间来保证某些数据的阶段性有效。
 
 
- 思路：用户每次访问的时候会先进入登陆拦截器，登录拦截器会检查用户是否已经登录，如果没有登录，就会尝试从请求中拿到Cookie然后从Cookie中获取用户信息进行自动登录。
如果Cookie中没有用户信息，就直接将请求转发到登陆页面进行登录。由于设置了Cookie的域名和路径都囊括了整个服务集群，所以理论上这些集群是共享一个Cookie的。这个时候任何一个服务节点
登录成功，其他服务节点进入的时候就会拿到新更新的Cookie数据进行自动登录。这就实现了我们需要的单点登录。

     
     
- 代码(每次登陆，我们可以回写一个Cookie保存用户的信息)，在我们的登陆Servlet中设置
```java
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


```


当用户关闭浏览器后再次进入的时候，浏览器会把之前的在同一个域名下面的Cookie携带过去。这个时候我们只需要尝试获取这个放了用户信息的Cookie
然后解析出用户信息进行自动登录就可以了。

```java
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

```

同时我在Tomcat中有如下配置server.xml：

```xml
<?xml version='1.0' encoding='utf-8'?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- Note:  A "Server" is not itself a "Container", so you may not
     define subcomponents such as "Valves" at this level.
     Documentation at /docs/config/server.html
 -->
<Server port="8005" shutdown="SHUTDOWN">
  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <!-- Security listener. Documentation at /docs/config/listeners.html
  <Listener className="org.apache.catalina.security.SecurityListener" />
  -->
  <!--APR library loader. Documentation at /docs/apr.html -->
  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
  <!--Initialize Jasper prior to webapps are loaded. Documentation at /docs/jasper-howto.html -->
  <Listener className="org.apache.catalina.core.JasperListener" />
  <!-- Prevent memory leaks due to use of particular java/javax APIs-->
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />

  <GlobalNamingResources>

    <Resource name="UserDatabase" auth="Container"
              type="org.apache.catalina.UserDatabase"
              description="User database that can be updated and saved"
              factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
              pathname="conf/tomcat-users.xml" />
  </GlobalNamingResources>
  <Service name="Catalina">

    <Connector port="8888" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
   
    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

    <Engine name="Catalina" >

      <Realm className="org.apache.catalina.realm.LockOutRealm">
       
        <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
               resourceName="UserDatabase"/>
      </Realm>
      <Host name="ggr.ggr.net"  appBase="webapps" unpackWARs="true" autoDeploy="true"></Host>  
      <Host name="clj.ggr.net"  appBase="webapps" unpackWARs="true" autoDeploy="true"> </Host>
      <Host name="localhost"  appBase="webapps"
            unpackWARs="true" autoDeploy="true">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="localhost_access_log." suffix=".txt"
               pattern="%h %l %u %t &quot;%r&quot; %s %b" />

      </Host>
    </Engine>
  </Service>
</Server>

```
最后一个简单的基于Cookie的单点登录就完成了。
这种方案有一个很明显的问题就是Cookie放在浏览器存在安全问题，而且很多主站的广告还可以通过Cookie进行用户跟踪（超级Cookie），即使目前
很多网站依然支持Cookie，但是这种涉及到会泄露用户个人隐私问题的技术本质上我们是不建议使用的。
