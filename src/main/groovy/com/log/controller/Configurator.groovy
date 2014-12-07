package com.log.controller

import com.log.beans.LogSession
import com.log.beans.RemoteFile
import java.util.concurrent.LinkedBlockingQueue

public class Configurator {

	
	public static def globalconfig=null
	public static boolean isUpdating=false
	
	public static logSessions=[];
	
	public static void log(def msg){
		
		println "${new Date()} [${Thread.currentThread().getName()}]: ${msg}"
	}
	
	
	public static def worker_lbq = new LinkedBlockingQueue()
	
	public static def updatebuffer(String sessionid,StringBuffer msg){
		
		println("================== Updating for ${sessionid} for ${msg} ========================")
		LogSession cursession=getLogSession(sessionid)
		
		cursession.buffer.append(msg)
		
	}
	public static def resetbuffer(String sessionid){
		
		println("================== Reset buffer for sessionid ${sessionid} ====================")
		LogSession cursession=getLogSession(sessionid)
		
		
		if(null == cursession?.buffer)
			return "";
			
		String res=cursession.buffer.toString();		
		cursession.buffer.delete(0,  res.length());
		
		return res;
		
		
	}

	
	public static def addSession(LogSession cursession){
		logSessions.add(cursession)
		
	}
	public static def closeremote(String sessionid,RemoteFile  remoteFile){
		
		LogSession cursession=getLogSession(sessionid)
		
		cursession.remotefiles.each {curremoteFile->
			
			if(curremoteFile.equals(remoteFile))
				curremoteFile.valid=false;
				
		}
		
		
	}
	

	public static def isSessionValid(String sessionid){
		
		LogSession cursession=getLogSession(sessionid)
		
		int invalidRemotes=0
		cursession.remotefiles.each {curremoteFile->
			
			if(curremoteFile.valid==false)
				invalidRemotes ++;
				
		}
		return (invalidRemotes != cursession.remotefiles.size)
		
		
	}
	
	public static LogSession getLogSession(sessionid){
		LogSession cursession=null;
		println " Searching for ${sessionid}"
		logSessions.each{logSession ->
		
			println " comparing ${logSession.sessionid } for  ${sessionid}"
			
			if(logSession.sessionid == sessionid){
			cursession=logSession;
			return;
			}
			
			
		}
		println " returning ${cursession} for  ${sessionid}"
		return cursession;
	}
	
	
	
}
