package com.log.beans

import com.jcraft.jsch.Channel
import com.jcraft.jsch.Session
import com.log.controller.Configurator

class ShellOutputStream extends OutputStream {

	private volatile String data = "";
	private String sessionId;
	private Server server;
	
	ShellOutputStream(String sessionId, Server server) {
		this.sessionId = sessionId
		this.server = server
	}

	@Override
	public synchronized void write(int b) throws IOException {
		data += Byte.toString((byte)b);
		checkSendData()
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		data += new String(b, off, len);
		checkSendData()
	}
	
	@Override
	public void close() throws IOException {
		sendData();
	}
	
	@Override
	public void flush() throws IOException {
		sendData()
	}
	
	private void checkSendData() {
		if(data.length() > 50) {
			sendData();
		}
	}
	
	private synchronized void sendData() {
		print data
		Configurator.worker_shell_lbq.put([  "response": data,"action": "UPDATE","sessionId":sessionId, "server":server])
		data = ""		
	}
}
