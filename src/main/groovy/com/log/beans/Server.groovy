package com.log.beans

import groovy.transform.ToString

@ToString
class Server {

	def id
	def host
	def port
	def user
	def password
	def proxyhost
	def proxyport
	def proxyuser
	def proxypwd
	def name
	def servergroup=""
	def team=""
	def locked=false
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null ) return false;

		Server server = (Server) o;

		
		
		if (host != null ? !host.equals(server.host) : server.host != null) return false;
		if (user != null ? !user.equals(server.user) : server.user != null) return false;
		

		return true;
	}
	
	public String getOnlyProfileName(){
		
		return user + "@" +host;
	}
}
