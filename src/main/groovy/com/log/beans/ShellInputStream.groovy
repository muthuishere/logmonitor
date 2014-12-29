package com.log.beans;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.log.controller.Configurator;

public class ShellInputStream extends InputStream {
	
	private volatile byte[] buffer;
	private int pos;
	
	private boolean closed = false;

	
	public synchronized void addBuffer(String buffer) {
		addBuffer(buffer.getBytes());
	}
	
	public synchronized void addBuffer(byte[] buffer) {
		if(this.buffer != null)
			println "add data to buffer $pos and " + this.buffer.length + " new buffer " + buffer.length
		if(this.buffer == null || buffer.length == pos) {
			this.buffer = buffer
			pos = 0
		} else {
			byte[] tempBuffer = new byte[this.buffer.length + buffer.length];
			System.arraycopy(this.buffer, 0, tempBuffer, 0, this.buffer.length);
			System.arraycopy(buffer, 0, tempBuffer, this.buffer.length, buffer.length);
			this.buffer = tempBuffer;
		}
		System.out.println("New Buffer :: " + new String(this.buffer));
	}

	@Override
	public int read() throws IOException {
		return buffer!= null && buffer.length > pos ? buffer[pos++] & 0xFF : 0 ;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0 || buffer == null || buffer.length == pos) {
			return 0;
		}
		int availBufferLength  = buffer.length - pos;
		int reqBuffer = len - off;
		if(availBufferLength - reqBuffer < 0) {
			reqBuffer = availBufferLength
		} else {
			reqBuffer = len - off
		}
		try {
			System.arraycopy(buffer, pos, b, off, reqBuffer)
		} catch (Exception e) {
			e.printStackTrace()
		}
		pos += reqBuffer;
		return reqBuffer;
	}
	
	@Override
	public int available() throws IOException {
		return buffer!= null && buffer.length >= pos ? buffer.length - pos : -1 ;
	}
	
	@Override
	public void close() throws IOException {
		closed = true;
	}
}
