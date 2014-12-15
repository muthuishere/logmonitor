package com.log.model

import java.util.concurrent.CopyOnWriteArraySet
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

class PollerSocketServlet extends WebSocketServlet {

	public PollerSocketServlet(){
		println "WebSocketChatServlet Initiated"
	}
	
	
	/**
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		// TODO Auto-generated method stub
		return null;
	}
	
			**/
	public final Set logsessions = new CopyOnWriteArraySet();
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}
	
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String arg1) {
		
		
		def sessionid=request.getParameter("sessionid")
		println "=================== Socket connected for $sessionid ======================="
		
		return new PollerSocket(sessionid);
	}

}
