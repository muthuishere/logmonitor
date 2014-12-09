package com.log.helpers

public  class LogMessage {

	static def info(def obj){
		
		//Thread.start {
			printmsg("Logmessage INFO : ${new Date()}" + obj?.toString())
		//}
	}
	
	static def error(def obj){
		
		//Thread.start {
			printmsg("Logmessage Error : ${new Date()}" + obj?.toString())
		//}
	}
	
	private static def printmsg(final Object msg){
		  
			 println(msg?.toString());
		 
		
	}
}
