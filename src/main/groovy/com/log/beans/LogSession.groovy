package com.log.beans

import java.util.ArrayList;

import groovy.transform.ToString

@ToString
class LogSession {

	def sessionid
	public StringBuffer buffer=new StringBuffer();
	def startTime
	Long lastfetchedTime=0l;
	
	ArrayList<RemoteFile> remotefiles=new ArrayList<RemoteFile>();
	

	public 	ArrayList<RemoteFile> getValidRemoteFiles(){
		
		ArrayList<RemoteFile> validremotefiles=new ArrayList<RemoteFile>();
		
		remotefiles.each{remotefile ->
			
			if(remotefile.valid)
				validremotefiles.add(remotefile)
		}
		return validremotefiles
	}
	
	public 	ArrayList<RemoteFile> getInValidRemoteFiles(){
		
		ArrayList<RemoteFile> inValidremotefiles=new ArrayList<RemoteFile>();
		
		remotefiles.each{remotefile ->
			
			if(!remotefile.valid)
				inValidremotefiles.add(remotefile)
		}
		
		return inValidremotefiles
	}
	@Override
	public boolean equals(Object o) {


		LogSession logObject = (LogSession) o;

		if (sessionid.equals(logObject.sessionid))
			 return true;
		
		
		
		return false;
	}
	
}
