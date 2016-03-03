<%@ page import="ru.nntu.elastic.HTMLHelper" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>ElasticSearch</title>
</head>

<body>
    <form method="post" action="index.jsp">
        <input name="search" align="bottom" style="width:80%" value="<%if (request.getParameter("search") != null) %><%= request.getParameter("search") %>"/>
        <input type="submit" value="Go"/>
    </form>

    <% if(request.getParameter("search") != null && !request.getParameter("search").trim().equals("")){%>
        <%=HTMLHelper.printDocument(request.getParameter("search"))%>




    <%}%>
</body>

</html>