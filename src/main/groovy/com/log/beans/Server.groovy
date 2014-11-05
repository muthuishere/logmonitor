package com.log.beans

import groovy.transform.ToString

@ToString
class Server {

	def host
	def port
	def user
	def password
	def proxyhost
	def proxyport
	def proxyuser
	def proxypwd
	def name
	def group=""
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Server server = (Server) o;

		if (host != null ? !host.equals(server.host) : server.host != null) return false;
		

		return true;
	}
}