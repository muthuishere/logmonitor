package com.log.beans

import java.util.ArrayList;

import groovy.transform.ToString

@ToString
class Log {

	def id
	def name
	def filename
	def hosts
	def team
	
	
	def hostsmap
	
	@Override
	public boolean equals(Object o) {


		Log logObject = (Log) o;

		if (name != null ? !name.equals(logObject.name) : logObject.name != null) return false;
		if (filename != null ? !filename.equals(logObject.filename) : logObject.filename != null) return false;
		
		if (hosts != null ? !hosts.equals(logObject.hosts) : logObject.hosts != null) return false;
		if (team != null ? !team.equals(logObject.team) : logObject.team != null) return false;
		
		
		return true;
	}
	
}
