package com.log.application


import groovy.json.JsonSlurper


import com.log.controller.Configurator;
import com.log.controller.ResolveLogServer


class LogMain {

	
	
	
	static parseconfig(def configFileName){
		
		// Read the configuration file into a map called "global".
		// This map is shared with all other threads in order to provide
		// a centralised configuration store.
		
		
		
		
		try
		{
			Configurator.globalconfig = new JsonSlurper().parse(new FileReader(configFileName))
		
			Configurator.globalconfig.configuration_file = configFileName
		
			println "Configuration: ${Configurator.globalconfig}"
		}
		catch(Exception e)
		{
			println "Error: Unable to load configuration"
			e.printStackTrace()
			System.exit(1)
		}
		
	}
	
	static void waitformore(def server){
		
		def CLEANUP_REQUIRED = true
		Runtime.runtime.addShutdownHook {
		  println "Shutting down..."
		  if(null != server)
			  server.stopServer()
		  if( CLEANUP_REQUIRED ) {
		
		  }
		}
		(1..10).each {
		  sleep( 1000 )
		}
		CLEANUP_REQUIRED = false
		
	}
	static void  startAppServer(){
		
		ResolveLogServer server=new ResolveLogServer()
		server.init()
		server.startServer()
		
		
		waitformore(server)
		
		
		
	}
	
	static main(args) {
	
		def configFileName
		
		
		if(args.size() != 1)
		{
			println "Usage: LogMain.groovy <configuration file>"
			System.exit(1)
		}
		
		
		configFileName=args[0]
		
		parseconfig(configFileName)
		
		//leavecodetest()
		//integrate()
		
		//browsertest();
		startAppServer()
	}

}
