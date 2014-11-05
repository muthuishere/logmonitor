package com.log.beans

import groovy.transform.ToString

@ToString
class Log {

	def name
	def filename
	def host
	
	
	@Override
	public boolean equals(Object o) {


		Log logObject = (Log) o;

		if (name != null ? !name.equals(logObject.name) : logObject.name != null) return false;
		if (filename != null ? !filename.equals(logObject.filename) : logObject.filename != null) return false;
		
		if (host != null ? !host.equals(logObject.host) : logObject.host != null) return false;
		
		return true;
	}
	
}
