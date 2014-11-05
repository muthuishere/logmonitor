package com.log.ssh

import com.log.beans.CommandInfo
import com.log.beans.Server

public interface SshMessageListener {

	public void onReceiveExecuteMessage(CommandInfo commandInfo, boolean flgComplete);
	
		public void onReceiveConnectionMessage(Server connectionInfo, String msg);
}
