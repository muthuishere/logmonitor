package com.log.model

import com.log.beans.LogSession
import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage
import com.log.controller.Configurator

/*
 * Logmsg socket
 * 		
 * 		On Receiving connect message with session id
 * 		  create a socket object with session id
 * 
 */
public class PollerSocket implements OnTextMessage {
private Connection connection;
private def sessionid;


public PollerSocket(def sessionid ) {
	
	this.sessionid=sessionid
}


def sendMessagetoClient(String data){
	
	
	try{
		
	
		this.connection.sendMessage(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
}
public void onMessage( String data) {
	
	println "Value of data in onMessage function is : " + data
	



}
@Override
public void onOpen(Connection connection) {
	this.connection = connection;
	//Add current object in configurar
//	logsessions.add(this);
	Configurator.addSocket(this)
}
@Override
public void onClose(int closeCode, String message) {
	//remove socket object for user
	//logsessions.remove(this);
	Configurator.killsocket(this)
      }
}	