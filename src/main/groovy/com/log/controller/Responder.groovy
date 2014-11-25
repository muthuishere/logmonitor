package com.log.controller


import com.log.beans.Server
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








	public String getservers(HttpServletRequest request){

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		int count=0;
		try{
			def servers=dataStore.servers
			// Add information as xml

			//	ArrayList
			servers.each{key,val->



				def formattedpwd=Configurator.globalconfig.transmitpasswords?val.password:""
				def formattedproxypwd=Configurator.globalconfig.transmitpasswords?val.proxypwd:""

				response.append("<server>")
				//valid=true
				response.append("\n<name>${val.name}</name>")
				response.append("\n<host>${val.host}</host>")
				response.append("\n<port>${val.port}</port>")
				response.append("\n<user>${val.user}</user>")
				response.append("\n<password>${formattedpwd}</password>")
				response.append("\n<proxyhost>${val.proxyhost}</proxyhost>")
				response.append("\n<proxyport>${val.proxyport}</proxyport>")
				response.append("\n<proxyuser>${val.proxyuser}</proxyuser>")
				response.append("\n<proxypwd>${formattedproxypwd}</proxypwd>")


				response.append("\n<group>${val.servergroup}</group>")
				response.append("\n<team>${val.team}</team>")
				response.append("\n<locked>${val.locked}</locked>")

				response.append("\n</server>")


				count++
			}


			response.append("<status code='0'  records='$count' error='false' description='Successfully retrieved server  information'/>")
		}catch(Exception e){

			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' records='$count' error='true' description='${xml_string(e.getMessage())}'/>")
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


	public String getEnvironments(){
		String environments=""
		int i=0
		for(def bfr:Configurator.globalconfig.environments){



			if(i > 0)
				environments=environments +","



			environments=environments +"$bfr"

			i++
		}
		return environments

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
		def envs=getEnvironments();

		//	if( getAdmins().contains(ip))
		//	response.append("<status code='0' admin='true' description='Admin User' teams='$teams' />")
		//else
		response.append("<status code='0' description='Regular User'  environments='$envs'  teams='$teams' />")

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

	public String updateserver(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user"),
			"password":request.getParameter("password"),
			"host":request.getParameter("host"),
			"port":request.getParameter("port"),
			"proxyhost":request.getParameter("proxyhost"),
			"proxyport":request.getParameter("proxyport"),
			"proxyuser":request.getParameter("proxyuser"),
			"proxypwd":request.getParameter("proxypwd"),
			"servergroup":request.getParameter("servergroup"),
			"team":request.getParameter("team"),
			"operation":request.getParameter("operation"),

		]
		String ip=getIP(request)
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		def overwrite=false
		if(params.operation == "UPDATE")
			overwrite=true

		if(null == params.user || null == params.host  || null == params.password){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{




				dataStore.insertServer(new Server(

						host: params.host,
						port:params.port,
						user: params.user,
						password: params.password,
						proxyhost: params.proxyhost,
						proxyport: params.proxyport,
						proxyuser: params.proxyuser,
						proxypwd: params.proxypwd,
						name: params.user +"@" + params.host,
						servergroup: params.servergroup,
						team: params.team
						),overwrite)

				response.append("<status code='0' error='false' description='Successfully updated server information'/>")
			}catch(Exception e){
				response= new StringBuffer()

				response.append("<reply>")


				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}

		}
		response.append("</reply>")

		return response;
	}


	public String deleteserver(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user"),
			"host":request.getParameter("host"),
			"servergroup":request.getParameter("servergroup"),
			"team":request.getParameter("team"),


		]

		StringBuffer response= new StringBuffer()


		try{


			if(null == params.user || null == params.host || null == params.servergroup || null == params.team )
				throw new Exception("Invalid user inputs")



			response.append("<reply>")
			if(dataStore.deleteServer(new Server(
			host: params.host,
			user: params.user,
			servergroup: params.servergroup,
			team: params.team
			)))
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			else
				response.append("<status code='0' error='true' description='Invalid data or data does not exists'/>")

		}catch(Exception e){
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;
	}


	
	
	
		public String getlogfiles(HttpServletRequest request){
	
			StringBuffer response= new StringBuffer()
	
			response.append("<reply>")
	
	
			boolean valid=false
	
			int count=0;
			try{
				def logfiles=dataStore.getLogFiles();
				// Add information as xml
	
				//	ArrayList
				logfiles.each{key,val->
	
	
	
					def formattedpwd=Configurator.globalconfig.transmitpasswords?val.password:""
					def formattedproxypwd=Configurator.globalconfig.transmitpasswords?val.proxypwd:""
	
					response.append("<server>")
					//valid=true
					response.append("\n<name>${val.name}</name>")
					response.append("\n<host>${val.host}</host>")
					response.append("\n<port>${val.port}</port>")
					response.append("\n<user>${val.user}</user>")
					response.append("\n<password>${formattedpwd}</password>")
					response.append("\n<proxyhost>${val.proxyhost}</proxyhost>")
					response.append("\n<proxyport>${val.proxyport}</proxyport>")
					response.append("\n<proxyuser>${val.proxyuser}</proxyuser>")
					response.append("\n<proxypwd>${formattedproxypwd}</proxypwd>")
	
	
					response.append("\n<group>${val.servergroup}</group>")
					response.append("\n<team>${val.team}</team>")
					response.append("\n<locked>${val.locked}</locked>")
	
					response.append("\n</server>")
	
	
					count++
				}
	
	
				response.append("<status code='0'  records='$count' error='false' description='Successfully retrieved server  information'/>")
			}catch(Exception e){
	
				response= new StringBuffer()
	
				response.append("<reply>")
	
				response.append("<status code='1' records='$count' error='true' description='${xml_string(e.getMessage())}'/>")
			}
	
	
			response.append("</reply>")
	
			return response;
	
	
		}
		
	public String updatelogfile(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user"),
			"password":request.getParameter("password"),
			"host":request.getParameter("host"),
			"port":request.getParameter("port"),
			"proxyhost":request.getParameter("proxyhost"),
			"proxyport":request.getParameter("proxyport"),
			"proxyuser":request.getParameter("proxyuser"),
			"proxypwd":request.getParameter("proxypwd"),
			"servergroup":request.getParameter("servergroup"),
			"team":request.getParameter("team"),
			"operation":request.getParameter("operation"),

		]
		String ip=getIP(request)
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		def overwrite=false
		if(params.operation == "UPDATE")
			overwrite=true

		if(null == params.user || null == params.host  || null == params.password){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{




				dataStore.insertServer(new Server(

						host: params.host,
						port:params.port,
						user: params.user,
						password: params.password,
						proxyhost: params.proxyhost,
						proxyport: params.proxyport,
						proxyuser: params.proxyuser,
						proxypwd: params.proxypwd,
						name: params.user +"@" + params.host,
						servergroup: params.servergroup,
						team: params.team
						),overwrite)

				response.append("<status code='0' error='false' description='Successfully updated server information'/>")
			}catch(Exception e){
				response= new StringBuffer()

				response.append("<reply>")


				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}

		}
		response.append("</reply>")

		return response;
	}


	public String deletelogfile(HttpServletRequest request){


		def params=[
			"user":request.getParameter("user"),
			"host":request.getParameter("host"),
			"servergroup":request.getParameter("servergroup"),
			"team":request.getParameter("team"),


		]

		StringBuffer response= new StringBuffer()


		try{


			if(null == params.user || null == params.host || null == params.servergroup || null == params.team )
				throw new Exception("Invalid user inputs")



			response.append("<reply>")
			if(dataStore.deleteServer(new Server(
			host: params.host,
			user: params.user,
			servergroup: params.servergroup,
			team: params.team
			)))
				response.append("<status code='0' error='false' description='Successfully updated user information'/>")
			else
				response.append("<status code='0' error='true' description='Invalid data or data does not exists'/>")

		}catch(Exception e){
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;
	}

}

