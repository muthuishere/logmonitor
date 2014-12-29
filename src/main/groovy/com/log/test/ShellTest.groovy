package com.log.test;

import java.io.IOException;
import java.util.UUID;

import com.log.beans.Server;
import com.log.beans.ShellOutputStream;
import com.log.beans.ShellInputStream;
import com.log.ssh.ExecuteSSHController;


class ShellTest {
	public static void main(String[] args) throws IOException {
		String uuid = UUID.randomUUID().toString();
		ShellInputStream inStream = new ShellInputStream();
		ShellOutputStream outStream = new ShellOutputStream(uuid);
		Server server = new Server(user: "hutchuk", password: "Tata@1234", host: "localhost", port: 22);
		
		InputStream inputStream = new PipedInputStream();
		PipedOutputStream pin = new PipedOutputStream((PipedInputStream) inputStream);
		
		
		new ExecuteSSHController().startShell(server, inStream, outStream);
		while(true) {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			inStream.addBuffer("ls -ltr\r\n");
			/*pin.write(new String("ls\r\n").bytes);
			pin.flush();*/
		}
		
		
	}
}