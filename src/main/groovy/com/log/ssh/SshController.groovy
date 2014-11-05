package com.log.ssh



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.log.beans.CommandInfo
import com.log.beans.Server
import com.log.helpers.StringHelper
import com.log.exceptions.ServiceException

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;



public class SshController {



    boolean connected = false;

    Session session = null;
    SecureChannel secureChannel = null;
    SshMessageListener sshMessageListener = null;
    ByteArrayOutputStream baos = null;
    Server connectionInfo = null;
    boolean terminateCmd = false;
    boolean executing = false;

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
        return connectionInfo;
    }

    public void setConnectionInfo(Server connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
    Channel channel = null;

    public SshController(Server connectionInfo, SshMessageListener sshMessageListener) throws ServiceException {

      //  println("connectionInfo  " + connectionInfo.toString());
       // println("connectionInfo password " + connectionInfo.getPassword());
        secureChannel = new SecureChannel();
        this.sshMessageListener = sshMessageListener;

        this.connectionInfo = connectionInfo;
        if (null == connectionInfo || null == sshMessageListener || null == connectionInfo.host) {

            throw new ServiceException("Invalid Constructor arguments for SSH Controller ");
        }

    }

    public void getSession() {
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
			
            session = secureChannel.getSession(connectionInfo.user,connectionInfo.password,connectionInfo.host,connectionInfo.port);

            if (null != connectionInfo.proxyhost && "" != connectionInfo.proxyhost) {

                //Connect to gateway and set it as proxy
                Session gateway = connectToGateway(connectionInfo);
                session.setProxy(new SecureProxy(gateway));

            }

            session.connect();
            sendConnectionMessage("Successfully connected  to " + connectionInfo.toString());

            connected = true;
        } catch (JSchException ex) {

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

        sshMessageListener.onReceiveConnectionMessage(connectionInfo, msg);
    }

    public void disconnect() {

        connected = false;
        if (null != channel) {
            sendConnectionMessage("Disconnecting from channel" + connectionInfo.getOnlyProfileName());
            channel.disconnect();
        }
        if (null != session) {
            sendConnectionMessage("Disconnecting from session" + connectionInfo.getOnlyProfileName());
            session.disconnect();
            session = null;
        }

    }

    public void closeChannel() {
        connected = false;

        if (null != channel) {
            sendConnectionMessage("Closing channel" + connectionInfo.getOnlyProfileName());
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
            throw new ServiceException("Exception While connecting to Server " + ex.toString(), ex);
        }

    }

    public void printStringcode(String s) {

        for (int i = 0; i < s.length(); i++) {
            System.out.println(s.charAt(i) + "-" + ((int) s.charAt(i)));
        }

    }

    public void sendExecuteMsg(CommandInfo commandInfo, String res, boolean flgCompleted) {

       
        if (null == res) {
            return;
        }

//            System.out.println("--------------------------");
//            printStringcode(res);
//            
//            System.out.println("+++++++++++++++++++++++++++++++");
        String lines[] = res.split("\\r?\\n");
        if (lines.length > 0) {

            StringBuilder formattedResponse = new StringBuilder();

           // println(lines.length);

            for (int y = 0; y < lines.length; y++) {
                //println(lines[y]);
                if (y > 0) {
                    formattedResponse.append(StringHelper.NEW_LINE);
                }

                String curLine = lines[y];//StringHelper.removeUnicodeAndEscapeChars(lines[y]);
               // printStringcode(curLine);

                formattedResponse.append("[").append(connectionInfo.getHost()).append("]");
                formattedResponse.append(curLine);

            }

            commandInfo.setResponse(formattedResponse.toString());
            println("Command Response sending " + formattedResponse.toString());
        }
      //  println("Sending back");
        sshMessageListener.onReceiveExecuteMessage(commandInfo, flgCompleted);

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

            commandInfo.setConnectionInfo(connectionInfo);

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
                        println("Sending via b Output" + res);
                        sendExecuteMsg(commandInfo, res, false);
                        res = "";
                    }

                }

                while (inp.available() > 0) {
                    int i = inp.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }

                    res = res + new String(tmp, 0, i);
                    println("Output" + res);
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
                sendExecuteMsg(commandInfo, res, false);
                res = "";
            }
            println("completeres " + completeres);

            terminateCmd = false;
            executing = false;
            sendExecuteMsg(commandInfo, completeres, true);

            return;

        } catch (JSchException ex) {
            //sendExecuteMsg( commandInfo, "Exception While Executing Command in Server " + ex.toString(),true);
            throw new ServiceException("Exception While Executing Command in Server " + ex.toString(), ex);
        } catch (IOException ex) {
            //sendExecuteMsg( commandInfo, "Exception While Executing Command in Server " + ex.toString(),true);
            throw new ServiceException("IOException ", ex);

        }
    }

}
