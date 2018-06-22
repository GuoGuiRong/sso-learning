<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	String userName = (String)session.getAttribute("user");
	if(userName!=null){
	
	
%>
	你已经登录<a herf="/success.jsp">进入</a>,或者<a href="login?action=logout">注销</a>
<%
	return;
	}
%>
	<form action="login" method="post">
		用户名：<input type="text" name="userName"/><br>
		密码：<input type="text" name="password" /><br>
		自动登录<input type="checkbox" name="autoLogin"/><br>
		<input type="submit"/><br>
	</form>
</body>
</html>