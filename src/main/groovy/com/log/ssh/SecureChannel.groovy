package com.log.ssh


import com.jcraft.jsch.*;




public class SecureChannel extends JSch {

    public SecureChannel() {
        super();
    }

    public Session getSession(String username, String pwd, String host, int port) throws JSchException {

        Session session = getSession(username, host, port);

        session.setPassword(pwd);

        // Additional SSH options.  See your ssh_config manual for
        // more options.  Set options according to your requirements.
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("Compression", "yes");
        config.put("ConnectionAttempts", "2");

        session.setConfig(config);

        return session;

    }

   
	

}
