package com.log.beans

import java.util.ArrayList;

import groovy.transform.ToString

@ToString
class RemoteFile {

	
	
	def filename
	
	Server server	
	boolean valid=true
	
	@Override
	public boolean equals(Object o) {


		RemoteFile logObject = (RemoteFile) o;

		if (server != null ? !server.equals(logObject.server) : logObject.server != null) return false;
		if (filename != null ? !filename.equals(logObject.filename) : logObject.filename != null) return false;
		
		
		return true;
	}
	
}
