package com.log.controller

import com.log.beans.LogSession
import com.log.beans.RemoteFile
import com.log.model.PollerSocket
import java.util.concurrent.LinkedBlockingQueue

public class Configurator {

	
	public static def globalconfig=null
	public static boolean isUpdating=false
	
	public static def logSessions=[:];
	public static def logMsgSockets=[:];
	
	public static void log(def msg){
		
		println "${new Date()} [${Thread.currentThread().getName()}]: ${msg}"
	}
	
	
	public static def addSocket(PollerSocket logMsgSocket){
		
		logMsgSockets.put(logMsgSocket.sessionid, logMsgSocket)
		
	}
	
	public static def sendmsgtosocket(String sessionid,String data){
		
		if(null != logMsgSockets.getAt(sessionid) ){
			
			logMsgSockets.getAt(sessionid).sendMessagetoClient(data)
			logMsgSockets.getAt(sessionid).lastfetchedTime=(new Date()).getTime();
		}
		else
			println "Session closed "	
		
	}
	
	public static def killsocket(String sessionid){
		
		if(null != logMsgSockets.getAt(sessionid))
			logMsgSockets.getAt(sessionid).remove()
		else
			println "Session killed ${sessionid} "
		
	}
	
	public static def killsocket(PollerSocket pollerSocket){
		
		if(null != logMsgSockets.getAt(pollerSocket.sessionid))
			logMsgSockets.getAt(pollerSocket.sessionid).remove()
		else
			println "Session killed ${pollerSocket.sessionid} "
		
	}
	
	public static def worker_lbq = new LinkedBlockingQueue()
	public static def worker_socket_lbq = new LinkedBlockingQueue()
	
	public static def updatebuffer(String sessionid,StringBuffer msg){
		
		println("================== Updating for ${sessionid} for ${msg} ========================")
		LogSession cursession=logSessions.getAt(sessionid)
		
		cursession.buffer.append(msg)
		
	}
	public static def resetbuffer(String sessionid){
		
		println("================== Reset buffer for sessionid ${sessionid} ====================")
		LogSession cursession=logSessions.getAt(sessionid)
		
		
		if(null == cursession?.buffer)
			return "";
			
		String res=cursession.buffer.toString();		
		cursession.buffer.delete(0,  res.length());
		cursession.lastfetchedTime=(new Date()).getTime();
		
		return res;
		
		
	}

	
	public static def addSession(LogSession cursession){
		cursession.lastfetchedTime=(new Date()).getTime();
		
		
		logSessions.put(cursession.sessionid, cursession)
		
	}
	
	
	
	public static boolean canterminate(String sessionid,RemoteFile  remoteFile){
		
		LogSession cursession=logSessions.getAt(sessionid)
		
		if(cursession == null){
			
			println("No session for $sessionid")
			return true;
		}
		long timediff=((new Date()).getTime()) - cursession.lastfetchedTime
		
		return (timediff > (globalconfig.poll_fetch_timeout * 1000))
		
		
	}
	
	public static def closeremote(String sessionid,RemoteFile  remoteFile){
		
		LogSession cursession=logSessions.getAt(sessionid)
		
		cursession.remotefiles.each {curremoteFile->
			
			if(curremoteFile.equals(remoteFile)){
				println ("***Invalidating remote file ${remoteFile}*****")
				curremoteFile.valid=false;
				}
				
				
		}
		
		
	}
	

	public static def isSessionValid(String sessionid){
		
		LogSession cursession=logSessions.getAt(sessionid)
		
		int invalidRemotes=0
		cursession.remotefiles.each {curremoteFile->
			
			if(curremoteFile.valid==false)
				invalidRemotes ++;
				
		}
		return (invalidRemotes != cursession.remotefiles.size)
		
		
	}
	

	
	
	
}
