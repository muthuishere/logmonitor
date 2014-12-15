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
	throws ServletException, IOException {
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

		switch ( actionname ) {


			case "getservers":
				result = responder.getservers(request)

				break;



			case "updateserver":
				result = responder.updateserver(request)

				break;


			case "getappConfig":
				result = responder.getappConfig(request)

				break;
				
			case "gethosts":
			result = responder.gethosts(request)

			break;
				
				
			case "deleteserver":
				result = responder.deleteserver(request)

				break;
			
			case "getlogfiles":
				result = responder.getlogfiles(request)

				break;
				
			case "getlogfilesbyserver":
				result = responder.getlogfilesbyserver(request)
				break;
			


			case "updatelogfile":
				result = responder.updatelogfile(request)

				break;
				
			case "deletelogfile":
				result = responder.deletelogfile(request)

				break;


			case "startsession":
			result = responder.startsession(request)

			break;
				
			case "logpoll":
			result = responder.logpoll(request)

			break;
			
			case "download":
				response.setHeader("Content-Disposition",
				"attachment; filename="+request.getParameter("repname")+"." + request.getParameter("filetype"));

				println(request.getParameter("repname")+"." + request.getParameter("filetype"))
				result = request.getParameter("content")

			break;
			
			
			//Added to test the Websocket Chat implemenation
			case "WebSocketChat":
			
				result = responder.webSocketChat(request);
			
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
