package com.log.ssh



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.log.beans.CommandInfo
import com.log.beans.RemoteFile
import com.log.beans.Server
import com.log.helpers.StringHelper
import com.log.exceptions.ServiceException

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue

import com.log.controller.Configurator

public class ExecuteSSHController {



    boolean connected = false;

    Session session = null;
    SecureChannel secureChannel = null;

	
    ByteArrayOutputStream baos = null;
    Server serverInfo = null;
	RemoteFile remoteFile=null;
	String sessionid=null
	
    boolean terminateCmd = false;
    boolean executing = false;

	LinkedBlockingQueue processor_lbq = null;
	
    private String currentFolder = "";

    public boolean isExecuting() {
        return executing;
    }

    public void setExecuting(boolean executing) {
        this.executing = executing;
    }

    public void terminateCommand() {

        if (executing) {
            this.terminateCmd = true;
        }
    }

    public Server getConnectionInfo() {
        return serverInfo;
    }

    public void setConnectionInfo(Server connectionInfo) {
        this.serverInfo = connectionInfo;
    }
    Channel channel = null;

	public def start(LinkedBlockingQueue processor_lbq ,RemoteFile remoteFile,String sessionid){
		
		println(" Starting Execute controller");
		secureChannel=new SecureChannel()
		this.processor_lbq=processor_lbq
		this.remoteFile=remoteFile
		
		this.serverInfo = remoteFile.server;
		this.sessionid=sessionid;
		
		try{
		//Connect now get message and send it
			
			println(" Attempting to connect");
		connect()
		
		//execute tail command and send it to global
		
		CommandInfo commandInfo = new CommandInfo();
		
		commandInfo.cmd="tail -f " +remoteFile.filename
		
		println("Attepting to execute ${commandInfo.cmd}")
		
		execute(commandInfo);
		} catch (Exception ex) {
	
		ex.printStackTrace();
		def msg=ex.toString();
		
		
		
		
		
		
		
				if (ex.toString().contains("Invalid Credentials")) {
					processor_lbq.put([  "response": msg,"action": "LOCK_CREDENTIAL","remoteFile":remoteFile,"sessionid":sessionid])
				} else{
				
					processor_lbq.put([  "response": msg,"action": "CLOSE","remoteFile":remoteFile,"sessionid":sessionid])
				}
			}
		
	}
 

    public boolean isConnected() {
        return connected;
    }

    private Session connectToGateway(Server gateWayInfo) throws ServiceException {

        try {
            //Connect to gateway and set it as proxy
		//	String username, String pwd, String host, int port
            Session gateway = secureChannel.getSession(gateWayInfo.proxyuser,gateWayInfo.proxypwd,gateWayInfo.proxyhost,gateWayInfo.proxyport);
            gateway.connect();

            return gateway;

        } catch (JSchException ex) {

			ex.printStackTrace();		
            if (ex.toString().contains("Auth fail")) {
                throw new ServiceException("Invalid Credentials while connecting to gateway ", ex);
            } else {
                throw new ServiceException("Exception While connecting to gateway " + ex.toString(), ex);
            }
        }

    }

    public void connect() throws ServiceException {

        try {

           

            sendConnectionMessage("Attempting to connect  " );
			
            session = secureChannel.getSession(serverInfo.user,serverInfo.password,serverInfo.host,serverInfo.port);

            if (null != serverInfo.proxyhost && "" != serverInfo.proxyhost) {

                //Connect to gateway and set it as proxy
                Session gateway = connectToGateway(serverInfo);
                session.setProxy(new SecureProxy(gateway));

            }

            session.connect();
            sendConnectionMessage("Successfully connected  to " + serverInfo.toString());

            connected = true;
        } catch (JSchException ex) {

		ex.printStackTrace();
		
            if (ex.toString().contains("Auth fail")) {
               // sendConnectionMessage("Invalid Credentials while connecting to Server for " + connectionInfo.getOnlyProfileName());

                throw new ServiceException("Invalid Credentials while connecting to gateway ", ex);
            } else {
              //  sendConnectionMessage("Error While connecting to Server " + connectionInfo.getOnlyProfileName());

                throw new ServiceException("Exception While connecting to Server " + ex.toString(), ex);
            }

        }

    }

    public void sendConnectionMessage(String msg) {

      //  sshMessageListener.onReceiveConnectionMessage(serverInfo, msg);
		
		
		processor_lbq.put([  "response": msg,"action": "UPDATE","remoteFile":remoteFile,"sessionid":sessionid])
		
    }

    public void disconnect() {

        connected = false;
        if (null != channel) {
            sendConnectionMessage("Disconnecting from channel" + serverInfo.getOnlyProfileName());
            channel.disconnect();
        }
        if (null != session) {
            sendConnectionMessage("Disconnecting from session" + serverInfo.getOnlyProfileName());
            session.disconnect();
            session = null;
        }

    }

    public void closeChannel() {
        connected = false;

        if (null != channel) {
            sendConnectionMessage("Closing channel" + serverInfo.getOnlyProfileName());
            channel.disconnect();
        }

    }

    public void shell(final ByteArrayOutputStream out) throws ServiceException {

        try {
            channel = session.openChannel("shell");

            // Enable agent-forwarding.
            //((ChannelShell)channel).setAgentForwarding(true);
            channel.setInputStream(System.in);
            /*
             // a hack for MS-DOS prompt on Windows.
             channel.setInputStream(new FilterInputStream(System.in){
             public int read(byte[] b, int off, int len)throws IOException{
             return inp.read(b, off, (len>1024?1024:len));
             }
             });
             */

            channel.setOutputStream(out);

            /*
             // Choose the pty-type "vt102".
             ((ChannelShell)channel).setPtyType("vt102");
             */

            /*
             // Set environment variable "LANG" as "ja_JP.eucJP".
             ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
             */
            //channel.connect();
            channel.connect(3 * 1000);

        } catch (JSchException ex) {
		ex.printStackTrace();
            throw new ServiceException("Exception While connecting to Server " + ex.toString(), ex);
        }

    }

    public void printStringcode(String s) {

        for (int i = 0; i < s.length(); i++) {
            System.out.println(s.charAt(i) + "-" + ((int) s.charAt(i)));
        }

    }


    public boolean isDirectoryCommand(String cmd) {
        return (cmd.startsWith("cd "));
    }

    public boolean isContinuousCommand(String cmd) {
        return (cmd.startsWith("tail"));
    }

    public void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;

    }

    public void execute(final CommandInfo commandInfo) throws ServiceException {

        if (null == session) {
            throw new ServiceException("Connection not established");
        }

        try {
             println("Executing command" + commandInfo.getCmd());
            executing = true;
            terminateCmd = false;
            channel = session.openChannel("exec");

            commandInfo.setConnectionInfo(serverInfo);

            String cmd = commandInfo.getCmd();

            ((ChannelExec) channel).setPty(isContinuousCommand(commandInfo.getCmd()));

            if (!StringHelper.isEmpty(currentFolder)) {
                cmd = "cd  " + currentFolder + " &&" + cmd;
            }

            if (isDirectoryCommand(commandInfo.getCmd())) {
                cmd = cmd + " && pwd";
            }

            commandInfo.setModifiedcmd(cmd);

            ((ChannelExec) channel).setCommand(cmd);

            // X Forwarding
            // channel.setXForwarding(true);
            //channel.setInputStream(System.in);
            channel.setInputStream(null);

            //channel.setOutputStream(System.out);
            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            baos = new ByteArrayOutputStream();
            ((ChannelExec) channel).setErrStream(baos);

            InputStream inp = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            channel.connect();
            String res = "";
            String completeres = "";
            int count = 0;

            byte[] tmp = new byte[1024];

            while (true) {

                if (count > 5) {

                    if (!StringHelper.isEmpty(res)) {
                        commandInfo.setBufferedOutput(true);
                      //  println("Sending via b Output" + res);
						processor_lbq.put([  "response": res,"action": "UPDATE","remoteFile":remoteFile,"sessionid":sessionid])
                        res = "";
                    }

                }

                while (inp.available() > 0) {
                    int i = inp.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }

                    res = res + new String(tmp, 0, i);
                  //  println("Output" + res);
                    completeres = completeres + res;
                }

                if (isDirectoryCommand(commandInfo.getCmd())) {
                    println("changed folder[" + res.trim() + "]");

                    if (!StringHelper.isEmpty(res.trim())) {
                        currentFolder = res.trim();
                    }
                    //setCurrentFolder(res);
                }

                count++;

                if (terminateCmd) {
                    println("Sending terminate signal");
                    res = res + StringHelper.NEW_LINE + new String("Terminate signal received for " + commandInfo.getCmd());
                    completeres = completeres + res;
                    out.write(3);//Send terminate command signal
                    out.flush();
                    //channel.sendSignal(res);
                    terminateCmd = false;
                }

                if (channel.isClosed()) {
                    println("exit-status: " + channel.getExitStatus());
                    
                    //check for error
                    if(null != baos && baos.size() > 0){
                    
                        String str = baos.toString("UTF-8");
                    
                        if(!StringHelper.isEmpty(res))
                            res = res + StringHelper.NEW_LINE;    
                        
                        res=res + str;
                        completeres = completeres + res;
                        commandInfo.setErrMsg(str);
                        commandInfo.error=true
                    }
                    
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }

            }
            channel.disconnect();

            if (!StringHelper.isEmpty(res) && commandInfo.isBufferedOutput()) {
                println("Sending via Buffered Output" + res);
				
				res += " Channel disconnected"
              //  sendExecuteMsg(commandInfo, res, false);
				processor_lbq.put([  "response": res,"action": "CLOSE","remoteFile":remoteFile,"sessionid":sessionid])
				
                res = "";
            }
            println("completeres " + completeres);

            terminateCmd = false;
            executing = false;
           // sendExecuteMsg(commandInfo, completeres, true);

            return;

        } catch (JSchException ex) {
		ex.printStackTrace();
            //sendExecuteMsg( commandInfo, "Exception While Executing Command in Server " + ex.toString(),true);
            throw new ServiceException("Exception While Executing Command in Server " + ex.toString(), ex);
        } catch (IOException ioex) {
		ioex.printStackTrace();
            //sendExecuteMsg( commandInfo, "Exception While Executing Command in Server " + ex.toString(),true);
            throw new ServiceException("IOException ", ioex);

        }
    }

}
