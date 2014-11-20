package com.log.controller


import com.log.model.DataStore
import java.text.SimpleDateFormat
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest

class Responder {

	def dataStore=null

	public Responder(){
	
	
		dataStore=new DataStore();
		dataStore.init(Configurator.globalconfig.dbfile)
		
	}
	/**
	 * Create private constructor
	 */

	def xml_string = { s ->

		
			s?.replaceAll("[\\x00-\\x1f]", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\$", "\\\\\\\$")
	}



	
	
	
	
	
	public String getservers(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def servers=dataStore.servers
			// Add information as xml

		//	ArrayList 
			servers.each{key,val->


				
				
					response.append("<server>")
					valid=true
					response.append("\n<name>${val.name}</name>")
					response.append("\n<host>${val.host}</host>")
					response.append("\n<user>${val.user}</user>")
					response.append("\n<group>${val.servergroup}</group>")
					response.append("\n<tunnel>${val.proxyhost}</tunnel>")
					
					response.append("\n</server>")
			

			}
			if(!valid){

				throw new Exception("No Vaid Servers identified")
			}

			response.append("<status code='0' error='false' description='Successfully retrieved server  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}

	
	public String getvalidusers(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def userTimeSummaries=dataStore.getAllUserStatus( )
			// Add information as xml


			userTimeSummaries.each{val->

				if(val.userLocked == false){
					response.append("<user>")
					valid=true
					response.append("\n<name>${xml_string(val.user)}</name>")





					response.append("\n</user>")
				}

			}
			if(!valid){

				throw new Exception("No Vaid users identified")
			}

			response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}


	public String getvalidusergroups(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		try{
			def userTimeSummaries=dataStore.getAllUserStatus( )
			// Add information as xml

			println(userTimeSummaries.dump())
			def teamname=""
			def lastteamname=""
			StringBuffer userresponse= new StringBuffer()
			def teamlist=[:]
			
			
			userTimeSummaries.each{val->

				if(val.userLocked == false){

					valid=true
					userresponse= new StringBuffer()
					userresponse.append("<user>")
					userresponse.append("\n<name>${xml_string(val.user)}</name>")
					userresponse.append("\n</user>")
					
					def curuser=userresponse.toString();
					if(teamlist.containsKey(val.team))						
						teamlist.put(val.team, teamlist.get(val.team) + curuser);						
					else
						teamlist.put(val.team, curuser)
						

						
					
				}

			}
			teamlist.each{key,val->
				
				response.append("<team name='${xml_string(key)}'>")
				response.append(val);
				response.append("</team>")
			}
			
			if(!valid){

				throw new Exception("No Vaid users identified")
			}

			response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}

	public String getAllusers(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")




		try{
			def userTimeSummaries=dataStore.getAllUserStatus( )
			// Add information as xml


			userTimeSummaries.each{val->

				response.append("<entry>")

				response.append("\n<name>${xml_string(val.user)}</name>")
				response.append("\n<userLocked>${val.userLocked}</userLocked>")
				response.append("\n<team>${val.team}</team>")




				response.append("\n</entry>")

			}

			response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}


	public String fetchDbStatus(){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{


			response.append("<updatestatus inprogress='${Configurator.isUpdating}' summary='${Configurator.fetchDbInfo.status}' description='${Configurator.fetchDbInfo.description}'  lastupdated='${Configurator.fetchDbInfo.lastupdated}'  />")

			response.append("<status code='0' error='false' description='Successfully Started Parsing'/>")

		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}


	public String fetchreportDetails(HttpServletRequest request){


		def params=[
			"user":request.getParameter("username_detail"),
			"team":request.getParameter("team"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def users=[]


			if(null != params.user && params.user != "")
				users.push(params.user)
			else if(null != params.team && params.team != "") {

				ArrayList userlist=dataStore.getUserEntries(params.team)
				userlist.each{curuserInfo->

					users.push(curuserInfo.user);
				}

			}else{
				users.push("")
			}
			ArrayList timesheetdetails=new ArrayList();


			users.each{curuser->
				println("Fetching for $curuser for $from to $to")
				def res=dataStore.getTimesheetEntries( curuser, from,to)
				if(null != res)
					timesheetdetails.addAll(res)
			}


			// Add information as xml


			timesheetdetails.each{val->




				response.append("<entry>")

				response.append("\n<name>${xml_string(val.user)}</name>")
				response.append("\n<projectcode>${val.projectcode}</projectcode>")
				response.append("\n<projecttask>${xml_string(val.projecttask)}</projecttask>")
				response.append("\n<tasktype>${xml_string(val.tasktype)}</tasktype>")
				response.append("\n<hours>${val.hours}</hours>")
				response.append("\n<details>${val.details}</details>")
				response.append("\n<isLeave>${val.isLeave}</isLeave>")

				def entrydate=""
				if(val.entryDate){

					SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

					// (3) create a new String using the date format we want
					entrydate= formatter.format(val.entryDate);
				}
				response.append("\n<entryDate>${entrydate}</entryDate>")
				response.append("\n<fetchedDate>${val.fetchedDate}</fetchedDate>")



				response.append("\n</entry>")

			}

			response.append("<status code='0' error='false' description='Successfully retrieved detail  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}


	public String executesql(HttpServletRequest request){
		def params=[
			"db":request.getParameter("db"),
			"sql":request.getParameter("sql")
		]

		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		try{
			def result=	dataStore.executeSQL(params.db,params.sql)
			if(result)
				response.append("<status code='0' error='false' description='Executed Successfully'/>")
			else
				response.append("<status code='1' error='true' description='Unable to execute sql'/>")

		}catch(Exception e){


			e.printStackTrace();
			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;
	}
	public String updatefetchDb(HttpServletRequest request){


		def params=[
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate"),
			"team":request.getParameter("team"),
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(null == params.fromdate || null == params.todate ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else if(Configurator.isUpdating ){

			response.append("<status code='1' error='true' description='Already update in progress'/>")

		}else{

			try{


				Date from =getParsedDate(params.fromdate);
				Date to = getParsedDate(params.todate);

				String userlist=""
				if(null != params.users && params.users != "") {

					userlist=params.users;
				}
				else if(null != params.team && params.team != "") {

					ArrayList curuserlist=dataStore.getUserEntries(params.team)
					int i=0;
					curuserlist.each{curuserInfo->
						if(i != 0)
							userlist= "," + userlist

						userlist=userlist+ curuserInfo.user
						i++
					}

				}



				Thread.start {

					new LogPoller().start(userlist,from ,to)


				}

				response.append("<status code='0' error='false' description='Successfully Started Parsing'/>")

			}catch(Exception e){
				response= new StringBuffer()

				response.append("<reply>")

				e.printStackTrace();
				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}

		}
		response.append("</reply>")

		return response;


	}

	
	
	public String generateReport(HttpServletRequest request,String reportname){
		
		StringBuffer response= new StringBuffer()
		
						response.append("<reply>")		
						response.append("<status code='1' error='true' description='Invalid request'/>")
						response.append("</reply>")
						
		def result=response.toString();
		
		switch ( reportname ) {
			
				
				
				case "projectemployeereport":
				result = generateProjectEmployeeReport(request)
				
				break;
				case "projecthoursreport":
				result = generateProjectHoursReport(request)
				
				break;
				
				
				
				case "employeeprojectreport":
				result = generateEmployeeProjectReport(request)
				
				break;
				
		}
		
		return result;
	}
	
	
	public Date getParsedDate(def str){
		
		Date d=null
		try{
			d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
		}catch(Exception e){
		println(e.toString())
		}
		return d;
		
	}
	public String generateEmployeeProjectReport(HttpServletRequest request){


		def params=[
			"users":request.getParameter("users"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def users=[]


			if(null != params.users && params.users != ""){
				
				for(String user:params.users.split(",")){
					users.push(user)
				}
				
		}else{
				users.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			users.each{curuser->
				println("Fetching generateEmployeeProjectReport  for $curuser for $from to $to")
				//ArrayList getTimesheetEntries
				def res=dataStore.getTimesheetEntries( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->
					
					summarylist.each{val->

						/*
						 * 
						 *  $xml.find('user').each(function(index){
  					dataCount++
  		            var username = $(this).find('name').text();
  					var userdate = $(this).find('date').text();
  		          var projcode = $(this).find('code').text();
  		        var projtask = $(this).find('task').text();
  		      var projtype = $(this).find('type').text();
  		    var hours = $(this).find('hours').text();
  		    Date entryDate
	def user
	def projectcode
	def projecttask
	def tasktype
	def hours
	def details
	def isLeave
	Date fetchedDate
						 */
						
						
						response.append("<user>")
		
						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<code>${val.projectcode}</code>")
						response.append("\n<task>${xml_string(val.projecttask)}</task>")
						response.append("\n<type>${xml_string(val.tasktype)}</type>")
						response.append("\n<hours>${val.hours}</hours>")
						response.append("\n<details>${val.details}</details>")
						response.append("\n<isLeave>${val.isLeave}</isLeave>")
		
						def entrydate=""
						if(val.entryDate){
		
							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");
		
							// (3) create a new String using the date format we want
							entrydate= formatter.format(val.entryDate);
						}
						response.append("\n<date>${entrydate}</date>")
						//response.append("\n<fetchedDate>${val.fetchedDate}</fetchedDate>")
		
		
		
						response.append("\n</user>")
						/*
						response.append("<user>")

						response.append("\n<name>${xml_string(val.)}</name>")
						response.append("\n<user>${xml_string(val.user)}</user>")
						response.append("\n<total>${val.totalhrs}</total>")
					

						response.append("\n</project>")
						*/

					}

				}
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			}else{
				throw new Exception("No Timesheet Entries found")
			}



		}catch(Exception e){

			e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")


		}


		response.append("</reply>")

		return response;


	}

	
	
	public String generateProjectHoursReport(HttpServletRequest request){


		def params=[
			"projects":request.getParameter("projects"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def projects=[]


			if(null != params.projects && params.projects != ""){
				
				for(String project:params.projects.split(",")){
					projects.push(project)
				}
				
		}else{
				projects.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			projects.each{curuser->
				println("Fetching generateProjectHoursReport  for $curuser for $from to $to")

				def res=dataStore.getProjectHoursReport( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->
					
					summarylist.each{val->

						
						response.append("<project>")

						response.append("\n<name>${val.projectcode}</name>")
						response.append("\n<user>${xml_string(val.user)}</user>")
						
						def str="0"
						
						if(val.totalhrs >0)
						str= val.totalhrs.intValue() +""
						
						response.append("\n<total>${str}</total>")
					

						response.append("\n</project>")

					}

				}
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			}else{
				throw new Exception("No Timesheet Entries found")
			}



		}catch(Exception e){

			e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")


		}


		response.append("</reply>")

		return response;


	}

	
	public String generateProjectEmployeeReport(HttpServletRequest request){


		def params=[
			"projects":request.getParameter("projects"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def projects=[]


			if(null != params.projects && params.projects != ""){
				
				for(String project:params.projects.split(",")){
					projects.push(project)
				}
				
		}else{
				projects.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			projects.each{curuser->
				println("Fetching generateProjectEmployeeReport  for $curuser for $from to $to")

				def res=dataStore.getProjectEmployeeReport( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->
					def curtoken=""
					summarylist.each{val->

						//user,projectcode,entryDate,hours,projecttask,tasktype
							/*
							 * 	var dataCount=0
  					 
  				 $xml.find('project').each(function(index){
  					dataCount++
  		            var project = $(this).find('name').text();
  					var username = $(this).find('user').text();
  		          var projdate = $(this).find('date').text();
  		        var totalhrs = $(this).find('total').text();  		      
  		    var hours = $(this).find('hours').text();
							 * 
							 * 
							 */
						response.append("<project>")

						response.append("\n<name>${val.projectcode}</name>")
						response.append("\n<user>${xml_string(val.user)}</user>")
						
						response.append("\n<hours>${val.hours}</hours>")
						response.append("\n<total></total>")
					


						def projdate=""
						if(val.entryDate){

							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

							// (3) create a new String using the date format we want
							projdate= formatter.format(val.entryDate);
						}



						response.append("\n<projdate>${projdate}</projdate>")
						




						response.append("\n</project>")

					}

				}
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			}else{
				throw new Exception("No Timesheet Entries found")
			}



		}catch(Exception e){

			e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")


		}


		response.append("</reply>")

		return response;


	}

	
	public String fetchreportSummary(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user"),
			"team":request.getParameter("team"),
			"fromdate":request.getParameter("fromdate"),
			"todate":request.getParameter("todate")
		]
		StringBuffer response= new StringBuffer()

		response.append("<reply>")



		try{

			Date from =null
			if(null != params.fromdate)
				from =getParsedDate(params.fromdate);

			Date to =null
			if(null != params.todate)
				to = getParsedDate(params.todate);


			def users=[]


			if(null != params.user && params.user != "")
				users.push(params.user)
			else if(null != params.team && params.team != "") {

				ArrayList userlist=dataStore.getUserEntries(params.team)
				userlist.each{curuserInfo->

					users.push(curuserInfo.user);
				}

			}else{
				users.push("")
			}
			ArrayList hashmaplist=new ArrayList();


			users.each{curuser->
				println("Fetching summary for $curuser for $from to $to")

				def res=dataStore.getTimesheetEntriesSummary( curuser, from,to)
				if(null != res)
					hashmaplist.add(res)
			}




			// Add information as xml

			//println(summarylist.dump())
			if(hashmaplist.size() >0 ){
				hashmaplist.each {summarylist ->

					summarylist.each{key,val->


						response.append("<user>")

						response.append("\n<name>${xml_string(val.user)}</name>")
						response.append("\n<workhours>${val.workhours}</workhours>")
						response.append("\n<leavehours>${val.leavehours}</leavehours>")
						response.append("\n<workdays>${val.workdays}</workdays>")
						response.append("\n<leavedays>${val.leavedays}</leavedays>")
						response.append("\n<defaulter>${val.defaulter}</defaulter>")


						def lastupdated=""
						if(val.lastupdated){

							SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd yyyy");

							// (3) create a new String using the date format we want
							lastupdated= formatter.format(val.lastupdated);
						}



						response.append("\n<lastupdated>${lastupdated}</lastupdated>")
						response.append("\n<userlocked>${val.userLocked}</userlocked>")




						response.append("\n</user>")

					}

				}
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			}else{
				throw new Exception("No Timesheet Entries found")
			}



		}catch(Exception e){

			e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e?.getMessage())}'/>")


		}


		response.append("</reply>")

		return response;


	}

	public String getAdmins(){
		String admins=""
		int i=0
		for(def bfr:Configurator.globalconfig.admins){



			if(i > 0)
				admins=admins +","



			admins=admins +"$bfr"

			i++
		}
		return admins

	}

	public String getTeams(){
		String admins=""
		int i=0
		for(def bfr:Configurator.globalconfig.teams){



			if(i > 0)
				admins=admins +","



			admins=admins +"$bfr"

			i++
		}
		return admins

	}
	def getIP(HttpServletRequest request){

		if(request.getRemoteAddr()){

			return request.getRemoteAddr()
		}else{
			return request.getHeader("X-Forwarded-For")
		}

	}
	
	
	public String getappConfig(HttpServletRequest request){
		
		
		
				String ip=getIP(request)
		
				StringBuffer response= new StringBuffer()
		
				response.append("<reply>")
				def teams=getTeams();
				
				if( getAdmins().contains(ip))
					response.append("<status code='0' admin='true' description='Admin User' teams='$teams' />")
				else
					response.append("<status code='0' admin='false' description='Regular User' teams='$teams' />")
		
				response.append("</reply>")
		
				return response.toString();
			}
		
	
	public String getuserStatus(HttpServletRequest request){



		String ip=getIP(request)

		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		
		if( getAdmins().contains(ip))
			response.append("<status code='0' admin='true' description='Admin User'/>")
		else
			response.append("<status code='0' admin='false' description='Regular User'/>")

		response.append("</reply>")

		return response.toString();
	}


	public boolean isAdmin(HttpServletRequest request){

		String ip=getIP(request)
		return getAdmins().contains(ip)

	}


	public String deleteuser(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user")
		]

		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		if(null == params.user  ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{
				if(dataStore.deleteUser(params.user.toString()))
					response.append("<status code='0' error='false' description='Successfully updated user information'/>")
				else
					response.append("<status code='0' error='true' description='Invalid Username ,Could not delete'/>")

			}catch(Exception e){
				response= new StringBuffer()

				response.append("<reply>")


				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}

		}
		response.append("</reply>")

		return response;
	}



}
