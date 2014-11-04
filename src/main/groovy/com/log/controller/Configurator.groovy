package com.log.controller

import java.util.concurrent.LinkedBlockingQueue

public class Configurator {

	//public static String dbname="C:\\muthu\\gitworkspace\\otlfetcher\\lat.db"
//	public static String leavecodes="'101','102'"
	public static def globalconfig=null
	public static boolean isUpdating=false
	public static def fetchDbInfo=[
		"status":"",
		"description":"",
		"lastupdated":""
		]
	
	public static void resetupdatestatus(){
		Configurator.fetchDbInfo=[
			"status":"",
			"description":"",
			"lastupdated":""
			]
		
	}
	
	public static void setupdatestatus(def status,def description,def lastupdated){
		Configurator.fetchDbInfo=[
			"status":status,
			"description":description,
			"lastupdated":lastupdated
			]
		
	}
	public static void log(def msg){
		
		println "${new Date()} [${Thread.currentThread().getName()}]: ${msg}"
	}
	
	
	public static def worker_lbq = new LinkedBlockingQueue()
	
}
