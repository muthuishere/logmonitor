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
	
	public static def updatebuffer(String sessionid,String msg){
		
		LogSession cursession=getLogSession(sessionid)
		cursession.buffer.append(msg)
		
	}
	public static def resetbuffer(String sessionid){
		
		LogSession cursession=getLogSession(sessionid)
		String res=cursession.buffer.toString();		
		cursession.buffer.delete(0,  res.length());
		
		return res;
		
		
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
		
		logSessions.each{logSession ->
			
			if(logSession.sessionid == sessionid){
			cursession=logSession;
			return;
			}
			
			
		}
		return cursession;
	}
	
	
	
}
