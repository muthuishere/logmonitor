package com.log.beans


public class CommandInfo {

	String sessionid;
     Server connectionInfo;
     String cmd;
     String modifiedcmd;

     String response;
     String errMsg;
     boolean bufferedOutput;
	 
	 boolean terminate=false;

    private boolean error;

}