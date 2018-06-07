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
      
      过程原理： 用户发送请求到web服务器，该请求会被正在监听的Connector连接器接收（通过端口号以及协议信息来定位使用哪一个Connector连接器），并把该请求交给Service下的Engine来处理，并等待Engine处理的结果。Engine获得请求后会根据请求的主机信息（主机名）来匹配相应的Host主机，Host主机会根据请求的路径匹配对应的Context，Context web应用匹配上之后就构建request、response请求对象，调用指定的Servlet来处理请求。请求处理完成后会将response对象返回给Host主机，Host主机将response对象返回给Engine引擎，Engine再将response对象返回给Connector链接器，最后Connector连接器将response返回给浏览器。
  
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
        
        b.配置server.xml
         
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
        
        c.测试
        
            http://www.ggr.org:8080/
        
            http://www.clj.org:8080/
        
        d.备注：
        
         开始下载新的tomcat7测试，发现每次启动的都是固定的某个tomcat，
          原来每个tomcat的默认启动未知是调用了系统环境变量CATALINA_HOME里面的tomcat路径，我们只要重新添加一个新变量CATALINA_HOME2只想新的tomcat，
          然后修改tomcat包下的bin目录里面的catalina.bat,startup.bat,shutdown.bat里面的%CATALINA_HOME%为%CATALINA_HOME2%便可
          当然还需要添加一个新环境变量CATALINA_BASE2 值和CATALINA_HOME一样，后续步骤和上面一样。
        
        
  
  - 基于端口号的虚拟主机 
  
     * 原理：通过使用不同的链接器来链接
     
     * 步骤：
     
       a. 修改server.xml配置文件如下：
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
       
       b. 测试
                
            http://www.ggr.org:8080/
            http://www.ggr.org:8888/
       
