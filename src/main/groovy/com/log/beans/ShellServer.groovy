package com.log.beans;

import com.log.controller.Configurator


public class ShellServer {
	private String sessionId
	private def shellSessions = []
	private boolean socketSession = true
	
	ShellServer(String sessionId) {
		this.sessionId = sessionId
		Configurator.addShellServer(sessionId, this)
	}
}