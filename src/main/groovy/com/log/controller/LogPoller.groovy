package com.log.controller

import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue


import com.log.exceptions.ServiceException

import com.log.helpers.Log


import java.util.concurrent.TimeUnit

class LogPoller {


	
	public LogPoller(){
		
		(1..Configurator.globalconfig.worker_thread_count).each
		{
			Thread.start { worker_thread( Configurator ) }
		}
		
	}
	def parseCallback(def dbreport_response){
		
		boolean flgSuccess=false
		try{
		if( null == dbreport_response ||  null == dbreport_response?.timeEntries ||  dbreport_response?.timeEntries?.size() ==0){
			
			Log.error("Error getting response for " +dbreport_response?.userInfo?.user)
			
			if(dbreport_response?.error.contains("INVALID CREDENTIALS")){
				
				Log.error("Error Invalid credentials disabling " +dbreport_response?.userInfo?.user)
				
				datamanager.disableUser(dbreport_response?.userInfo?.user)
				
			}
		
			
		}else{
		
		if(  dbreport_response?.timeEntries?.size() > 0){
			
			println("Adding entries for" +dbreport_response?.userInfo?.user)
			
			datamanager.addTimeEntries(dbreport_response.timeEntries);
			
			if( null != dbreport_response?.error  || dbreport_response?.error !="" ){
				flgSuccess=false
				
			}else
				flgSuccess=true
			
		}
		
		
		}
		}catch(Exception e){
		
			e.printStackTrace();
			
		}
		
		return flgSuccess
	}
	
	
	def start(String lstusers,Date from ,Date to){
		
		Configurator.isUpdating=true
		Configurator.resetupdatestatus()
		println("Starting DB Updation")
		
		def callbackQueue = new LinkedBlockingQueue()
		def statusmsg="Fail"
		def description=""
		try{
			
			def k=null
			
			datamanager.init();
			
			
			
			def users=datamanager.getValidUserEntries()
			def usercount=0
			for (def userInfo:users){
				if(null==lstusers || lstusers.trim() =="" || lstusers.contains(userInfo.user)){
					Configurator.worker_lbq.put([ "callbackQueue": callbackQueue, "userInfo": userInfo,"from":from,"to":to])
				
				usercount++
				}
			}
			def successCount=0
			(1..usercount).each()
			{
				
				def dbreport_response = callbackQueue.poll(Configurator.globalconfig.fetch_req_timeout, TimeUnit.MILLISECONDS)
			
					if(parseCallback(dbreport_response))
							successCount++
				
				
				}
				
			if(successCount == usercount){
				
				statusmsg="Success"
				
			}else if(successCount < usercount){
				
				statusmsg="Partial Success"
				
			}
			
			description="Updated timesheets for $successCount  of $usercount users"
			
		}catch(Exception e){
			e.printStackTrace();
			description="Fail while parsing ${e.toString()}"
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy hh:mm:ss a");
		
		  
		   
		Configurator.setupdatestatus( statusmsg, description,formatter.format(new Date()))
		Configurator.isUpdating=false
		println("Completed DB Updation")
	}
	
	

	
	
	//JsonSlurper reportapp =  new JsonSlurper()
	
	
	// The worker thread.  These threads listen for incoming requests
	// in the worker LinkedBlockingQueue, issue HTTP requests to the given
	// downstream systems and then post the HTTP reply to the temporary
	// LinkedBlockingQueue of the requester given in the request message.
	
	def worker_thread(curConfigurator)
	{
	  curConfigurator.log("Worker: Initialising")
	
	 
	
	  while(true)
	  {
		def req_msg = curConfigurator.worker_lbq.take()
		def reply_msg = req_msg
		def start_ms = System.currentTimeMillis()
			def timeEntries=new ArrayList<>()
		try
		{
		 
			
			
			
			
			
			Date origstart=req_msg.from
			Date origend=req_msg.to
			Date curstart=req_msg.from
			Date curend=req_msg.to
			def duration =0
			def durationdiff =0
			
			
			
			while(1 == 1){
			
				use(groovy.time.TimeCategory) {
					duration = curend-curstart  
				  
			   }
				println("=====================================duration.days " +duration.days )
				if(duration.days > 30){
				use(groovy.time.TimeCategory) {
					curend=curstart + 30.days
					}
					
				}	
				
				
				def fetchUserReport;
				fetchUserReport.init(Configurator.globalconfig?.proxy )
				def res=fetchUserReport.startFetch(req_msg.userInfo, curstart, curend)
				if(null != res)
					timeEntries.addAll(res)
				
				use(groovy.time.TimeCategory) {
					durationdiff = origend - curend
					
				  
				}
				println("============================durationdiff.days " +durationdiff.days )
				if(durationdiff.days <=0 ){
					break;
				}else{
				curstart=curend
				curend=origend
				}
				
			}
			
			
			
		reply_msg.timeEntries=timeEntries
		
		
		  def took = System.currentTimeMillis() - start_ms
	
	
	
		  curConfigurator.log("Worker: Took: ${took} ms")
		}
		catch(Exception e)
		{
		  e.printStackTrace()
	
		  reply_msg.error = "${e.getCause().toString()} (${e.getMessage()})"
		  if(timeEntries.size() > 0){
			  
			  reply_msg.timeEntries=timeEntries
		  }
	
		
		}
	
		try
		{
			
			//Send back the item
		  req_msg.callbackQueue.put(reply_msg)
		}
		catch(Exception e)
		{
		   curConfigurator.log("Error sending response ${e.getMessage()}")
		}
	  }
	}
	//set 
// get list of users
	
//for each user , call fetchuserreport in a thread , poll for  completion

	
	//		on complete , Configurator.isUpdating=false 
}
