package com.log.model

import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage

public class ChatWebSocket implements OnTextMessage {
private Connection connection;
private Set users;

public ChatWebSocket(Set users ) {
	this.users = users;
}
public void onMessage( String data) {
	
	println "Value of data in onMessage function is : " + data
	for (ChatWebSocket user : users) {
		try {
			
			user.connection.sendMessage(data);
			
		} catch (Exception e) {
		}
	}

}
@Override
public void onOpen(Connection connection) {
	this.connection = connection;
	users.add(this);
}
@Override
public void onClose(int closeCode, String message) {
	users.remove(this);
      }
}	