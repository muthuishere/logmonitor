package com.log.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue






import com.log.beans.CommandInfo;
import com.log.beans.LogSession
import com.log.beans.RemoteFile
import com.log.beans.Server;
import com.log.exceptions.ServiceException
import com.log.helpers.LogMessage
import com.log.model.DataStore
import com.log.ssh.ExecuteSSHController
import com.log.ssh.SshMessageListener






import java.util.concurrent.TimeUnit

class LogPoller  {


	DataStore dataStore=null;
	
	public LogPoller(){
		
	
		dataStore=new DataStore();
		dataStore.init(Configurator.globalconfig.dbfile)
		
		
		
		(1..Configurator.globalconfig.worker_thread_count).each
		{
			Thread.start { worker_thread( Configurator, dataStore) }
		}
		
		(1..Configurator.globalconfig.worker_thread_count).each
		{
			Thread.start { worker_thread_socket( Configurator, dataStore) }
		}
		
		(1..Configurator.globalconfig.worker_thread_count).each
		{
			Thread.start { worker_thread_shell( Configurator, dataStore) }
		}
		
		
	}

	
	def start(LogSession logSession,boolean useSocket){
		
	
		//Add session object to global configuration

		//Loop through remote files
		
		//for each file connect to server and execute command , pass parameter sessionid
		
		//Update the response in global configurator with sessionid	
		
		//Global config looks up those objects and update corresponding buffers
		
		

				
		try{
			def logpoller_lbq=Configurator.worker_lbq
			if(useSocket)
				logpoller_lbq=Configurator.worker_socket_lbq
		
			//Iterate all remote files
			
			//start in a Thread of sshcontroller
			logSession.remotefiles.each {remoteFile ->
				
				//println("Starting thread for ${remoteFile.}")
				
				Thread.start {
					
										new ExecuteSSHController().start( logpoller_lbq,remoteFile,logSession.sessionid)
					
					
									}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		
		
	}
	
	def worker_thread_shell(def globalconfig,DataStore dataStore) {
		while(true) {
			def req = globalconfig.worker_shell_lbq.take()
			
			try {
				switch(req.action) {
					case "UPDATE":
						while(1) {
							if(globalconfig.logMsgSockets.getAt(req.sessionId) != null) {
								break;
							}
							try {Thread.sleep(1000)} catch (Exception e) {}
						}
						StringBuffer response= new StringBuffer()
						response.append("<reply>")
						response.append("<server name='$req.server.host'>")
						response.append("<data>$req.response</data>")
						response.append("</server>")
						response.append("</reply>")
						if(globalconfig.shellServers.getAt(req.sessionId).socketSession) {
							globalconfig.sendmsgtosocket(req.sessionId, response.toString())
						} else {
							globalconfig.updatebuffer(sessionid,formattedResponse)
						}
					break
					case "CLOSE":
						globalconfig.closeSession(req.sessionId)
						globalconfig.killsocket(req.sessionId)
					break
					default:
						println "Wrong action $req"
					break
				}
			} catch (Exception exception) {
				exception.printStackTrace()
				req.error = "${exception.getCause().toString()} (${exception.getMessage()})"
			}
		}
	}

	
	//JsonSlurper reportapp =  new JsonSlurper()
	
	
	// The worker thread.  These threads listen for incoming requests
	// in the worker LinkedBlockingQueue, issue HTTP requests to the given
	// downstream systems and then post the HTTP reply to the temporary
	// LinkedBlockingQueue of the requester given in the request message.
	
	def worker_thread_socket(def globalconfig,DataStore dataStore)
	{
	  globalconfig.log("Worker Socket: Initialising for receiving messages")
	
	  while(true)
	  {
		def req_msg = globalconfig.worker_socket_lbq.take()
		def reply_msg = req_msg
		
			
		try
		{
		 
			
			def action=req_msg.action
			def res=req_msg.response
			RemoteFile remoteFile=req_msg.remoteFile
			def sessionid=req_msg.sessionid
			CommandInfo commandInfo=req_msg.commandInfo
			
			StringBuffer formattedResponse = new StringBuffer();
			
			if (null != res) {
				
			
	
			
	
				res.split("\\r?\\n").each{curLine ->
					
					if(curLine.trim().length() > 0){
					 formattedResponse.append("<span>[").append(remoteFile.server.host).append("]");
					 formattedResponse.append(curLine);
					 formattedResponse.append("</span><br/>");
					}
					 
				}
			
			}
			
			
			globalconfig.log("received message: sending to socket ${sessionid}")
			globalconfig.sendmsgtosocket(sessionid,formattedResponse.toString())
			
			
			
			if(action =="UPDATE" && null != commandInfo){
			
			commandInfo.terminate=globalconfig.canterminate(sessionid,remoteFile)
				
				if(commandInfo.terminate){
					println ("*************  The Session is supposed to terminate ***************************")
					globalconfig.killsocket(sessionid)
					
					}
					
			
			}
			else if(action =="CLOSE"){
				globalconfig.closeremote(sessionid,remoteFile)
				globalconfig.killsocket(sessionid)
				
				}
				
			else if(action =="LOCK_CREDENTIAL"){
				
				
				
				globalconfig.closeremote(sessionid,remoteFile)
				globalconfig.killsocket(sessionid)
				remoteFile.server.locked=true;
				
				dataStore.updateServerLock(remoteFile.server);
				
			}
			
				
			
			// get object
			//parse object
			
			//update message in globalconfig
			
		
		//  globalconfig.log("Worker: Took: ${took} ms")
		}
		catch(Exception e)
		{
		  e.printStackTrace()
	
		  reply_msg.error = "${e.getCause().toString()} (${e.getMessage()})"
		
		
		}
	
	
	  }
	}

	
	

	
	//JsonSlurper reportapp =  new JsonSlurper()
	
	
	// The worker thread.  These threads listen for incoming requests
	// in the worker LinkedBlockingQueue, issue HTTP requests to the given
	// downstream systems and then post the HTTP reply to the temporary
	// LinkedBlockingQueue of the requester given in the request message.
	
	def worker_thread(def globalconfig,DataStore dataStore)
	{
	  globalconfig.log("Worker: Initialising for receiving messages")
	
	  while(true)
	  {
		def req_msg = globalconfig.worker_lbq.take()
		def reply_msg = req_msg
		
			
		try
		{
		 
			
			def action=req_msg.action
			def res=req_msg.response
			RemoteFile remoteFile=req_msg.remoteFile
			def sessionid=req_msg.sessionid
			CommandInfo commandInfo=req_msg.commandInfo
			
			StringBuffer formattedResponse = new StringBuffer();
			
			if (null != res) {
				
			
	
			
	
				res.split("\\r?\\n").each{curLine ->
					
					if(curLine.trim().length() > 0){
					 formattedResponse.append("<span>[").append(remoteFile.server.host).append("]");
					 formattedResponse.append(curLine);
					 formattedResponse.append("</span><br/>");
					}
					 
				}
			
			}
			
			
			globalconfig.updatebuffer(sessionid,formattedResponse)
			
			
			
			if(action =="UPDATE" && null != commandInfo){				
			
			commandInfo.terminate=globalconfig.canterminate(sessionid,remoteFile)
				
				if(commandInfo.terminate)
					println ("*************  The Session is supposed to terminate ***************************")
			
			}
			else if(action =="CLOSE")
				globalconfig.closeremote(sessionid,remoteFile)			
			else if(action =="LOCK_CREDENTIAL"){
				globalconfig.closeremote(sessionid,remoteFile)
				remoteFile.server.locked=true;
				
				dataStore.updateServerLock(remoteFile.server);
				
			}
			
				
			
			// get object
			//parse object
			
			//update message in globalconfig
			
		
		//  globalconfig.log("Worker: Took: ${took} ms")
		}
		catch(Exception e)
		{
		  e.printStackTrace()
	
		  reply_msg.error = "${e.getCause().toString()} (${e.getMessage()})"
		
		
		}
	
	
	  }
	}



 
}
