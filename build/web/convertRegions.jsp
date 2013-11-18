<%-- 
    Document   : convertRegions
    Created on : Jul 29, 2013, 2:52:01 PM
    Author     : Zia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page language="java" import="java.util.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Convert Regions</title>
    </head>
    <body style=" background-color: lightpink">
        <div align ="center" style=" margin-top: 200px">
            <% String projectId = request.getAttribute("projectId").toString(); %>
            <% String fileId = request.getAttribute("fileId").toString(); %>
            <form action="ReadXML" method="post" > 
                <table border="2">
                    <tr><td><b><i>Project ID: </i></b></td><td><input type="text" name="projectId" value= <%= projectId %> /> </td></tr>                
                    <tr><td><b><i>File ID:</i></b></td><td> <input type="text" name="fileId" value=<%= fileId %> /></td></tr>
                    <tr><td>XML Content:</td><td><textarea type ="text" cols="35" rows="10" name="xmlContent"></textarea></td></tr> 
                </table><br/><br/>               
                <input type="submit" value="Convert to XML" />
            </form>
        </div>
    </body>
</html>
