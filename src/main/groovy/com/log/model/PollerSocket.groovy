package com.log.model

import java.nio.ByteBuffer

import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage

import com.log.beans.ShellServer
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

def close(){
	
	try{
		println("Closing connection")
		
		this.connection.close();
		
	}catch(Throwable t){
		t.printStackTrace();
	}
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
	try {
		ShellServer shellServer = Configurator.shellServers.getAt(sessionid)
		if(shellServer != null) {
			shellServer.shellSessions.each { shellSession->
				if(data == "terminate") {
					println "Writing terminate signal to outstream"
					byte[] bytes = ByteBuffer.allocate(4).putInt(3).array();
					shellSession.inStream.addBuffer(bytes)
				} else {
					shellSession.inStream.addBuffer(data + "\r\n")
				}
			}
		} else {
			//add error to queue
		}
	} catch (Exception e) {
		e.printStackTrace()
		//add error to queue
	}



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