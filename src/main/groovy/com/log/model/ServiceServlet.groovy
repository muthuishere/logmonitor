package com.log.model

import com.log.controller.Responder
import javax.servlet.http.HttpServlet
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ServiceServlet extends HttpServlet {
 
    private static final long serialVersionUID = 1L;
 
	Responder responder=null
	
   public ServiceServlet(){
	    responder=new Responder()
		
	   }
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response)
throws ServletException, IOException
{
// Set response content type
response.setContentType("text/xml");

String path=request.getPathInfo()
String[] servicedata=path.split("/")

//println servicedata.length

for ( e in servicedata ) {
	println e
}


String actionname=servicedata.getAt(1)

String reportname=""


def result = ""

	
println("Action : $actionname")
switch ( actionname ) {
	
		
	case "getservers":
	result = responder.getservers(request)

	break;
	
	
	
	case "updateserver":
	result = responder.updateserver(request)

	break;
	
	
//
//		
//		case "logpoll":
//		result = responder.logpoll(request)
//		
//		break;
//		
//	
//		
//		case "getsysteminfo":
//		result = responder.getsysteminfo(request)
//		
//		break;
//		
//		
//		
//		case "addsysteminfo":
//		result = responder.addsysteminfo(request)
//		
//		break;
//		
//		case "startmonitor":
//		result = responder.startmonitor(request)
//		
//		break;
		
		case "downloadexcel":
		response.setHeader("Content-Disposition",
			"attachment; filename="+request.getParameter("repname")+".csv");
		
		result = request.getParameter("csv")
		
		break;		
		
		
		

	default:
		result = "<reply><status code='1' error='true' description='Invalid Service specified'/></reply>"
}



if(result.equals("")){
	result = "<reply><status code='1' error='true' description='Authentication Overruled'/></reply>"
	
}
// Actual logic goes here.
PrintWriter out = response.getWriter();
out.println(result);
}



    /***************************************************
     * URL: /jsonservlet
     * doPost(): receives JSON data, parse it, map it and send back as JSON
     ****************************************************/
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
 
       doGet( request, response);
    }
}
