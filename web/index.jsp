<%-- 
    Document   : index
    Created on : Dec 29, 2010, 2:58:27 PM
    Author     : Zia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body style=" background-color: darkkhaki">
        <div align ="center" style=" margin-top: 200px">
            <form action="HomePage" method="post" enctype="multipart/form-data">
                <input type="hidden" name="documentURL" value="/5/1/8/2/7/146/"/>
                <table border="2">
                    <tr><td><b><i>Project ID: </i></b></td><td><input type="text" name="projectId" value="1"/></td></tr>                
                    <tr><td><b><i>File ID:</i></b></td><td> <input type="text" name="fileId" value="1"/></td></tr>
                    <tr><td><b><i>PDF File: (.pdf)</i></b></td><td> <input type="file" name="myFile" value =" Browse File"/></td></tr>
                </table><br/><br/>
                <input type="submit" value="Open SWF File" />
            </form>
        </div>
    </body>
</html>
