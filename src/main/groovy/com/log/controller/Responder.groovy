package com.log.controller


import java.text.SimpleDateFormat

import javax.servlet.http.HttpServletRequest

import com.jcraft.jsch.Channel;
import com.log.beans.Log
import com.log.beans.LogSession
import com.log.beans.Server
import com.log.beans.ShellInputStream
import com.log.beans.ShellOutputStream
import com.log.beans.ShellServer;
import com.log.beans.ShellSession;
import com.log.model.DataStore
import com.log.model.PollerSocket
import com.log.ssh.ExecuteSSHController

class Responder {

	def dataStore=null
	LogPoller logPoller

	public Responder(){


		dataStore=new DataStore();
		dataStore.init(Configurator.globalconfig.dbfile)
		logPoller = new LogPoller();
	}
	/**
	 * Create private constructor
	 */

	def xml_string = { s ->


		s?.replaceAll("[\\x00-\\x1f]", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\$", "\\\\\\\$")
	}




	public def webSocketChat(HttpServletRequest  request){
		
		
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
	public String startsession(HttpServletRequest request){


		def params=[
			"hostfileids":request.getParameter("hostfileids"),
			"team":request.getParameter("team"),
			"usesocket":request.getParameter("usesocket"),
		]
		StringBuffer response= new StringBuffer()
		String msg=""
		boolean useSocket=false
		response.append("<reply>")

		if(null == params.hostfileids || null == params.team ){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{

				println "Start Session " + params.dump()
				
				def team=params.team;
					
				def remotefiles=[];
				String uuid ="";
		
				
				
				//Split host fileids
				//create a  array of RemoteFile  objects
				params.hostfileids.split (",").each { hostfileid->
					
					def host=hostfileid.split(";")[0]
					def fileid=hostfileid.split(";")[1]
					
					def remotefile=dataStore.getRemoteFileForLog(host,fileid,team)
					if(null == remotefile){
						msg += "Invalid Data $host,$fileid,$team \t "
						
					}else{
						remotefiles.add(remotefile)
					}
					
				}
				
				

				if(remotefiles.size() == 0)
					throw new Exception("Error $msg")
					
				// Create LogSession Object
				
					msg += " Published for Log Monitoring"
					 uuid = UUID.randomUUID().toString();
					 uuid=uuid.replaceAll("-","");
					final LogSession session=new LogSession(
						sessionid:uuid,
						remotefiles: remotefiles,
						);
				
				
					Configurator.addSession(session)
					
					
					if(null != params.usesocket && params.usesocket.toLowerCase().equals("true") )
						useSocket=true
						
						
				Thread.start {

					logPoller.start(session,useSocket)


				}

				response.append("<status code='0' error='false' usesocket='${useSocket}' sessionid='$uuid' description='$msg'/>")

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

	public String logpoll(HttpServletRequest request){
		
		
				def params=[
					"sessionid":request.getParameter("sessionid"),
					
				]
				StringBuffer response= new StringBuffer()
				String msg=""
				response.append("<reply>")
		
				if(null == params.sessionid ){
		
					response.append("<status code='1' error='true' description='Invalid user inputs'/>")
		
				}else{
		
					try{
		
						def res=Configurator.resetbuffer(params.sessionid)
						
						response.append("""
									<data>
									    <![CDATA[ ${res} ]]>
									</data>
								""");
						if(Configurator.isSessionValid( params.sessionid)){
							
							response.append("<status code='0' error='false' sessionid='${params.sessionid}' description='$msg'/>")
						}else{
								throw new Exception("No Valid Sessions identified");
						
						}
						//Check for valid remoteservers
						
		
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



	public Date getParsedDate(def str){

		Date d=null
		try{
			d = new SimpleDateFormat("yyyy-MM-dd").parse(str);
		}catch(Exception e){
			println(e.toString())
		}
		return d;

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

	private def getServerEntries(String team) {
		def servers=[:]
		dataStore.getServerEntries(null, team).each { server->
			def curkey="${server.user}-${server.host}-${server.servergroup}-${server.team}"
			servers.put(curkey, server);
		}
		return servers
	}


	public String getservers(HttpServletRequest request){
		
		def params=[
			"team":request.getParameter("team")
		]

		StringBuffer response= new StringBuffer()

		response.append("<reply>")


		boolean valid=false

		int count=0;
		try{
			def servers= params.team!=null ?  getServerEntries(params.team) : dataStore.servers
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
				response.append("\n<id>${val.id}</id>")


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
	
	String startShellSession(HttpServletRequest request) {
		def params=[
			"hostids":request.getParameter("hostids"),
			"team":request.getParameter("team"),
			"usesocket":request.getParameter("usesocket"),
		]
		
		StringBuffer response= new StringBuffer()
		String msg=""
		response.append("<reply>")

		if(null == params.hostids || null == params.team) {
			response.append("<status code='1' error='true' description='Invalid user inputs'/>")
		}else{
			String uuid = UUID.randomUUID().toString()
			uuid=uuid.replaceAll("-","")
			ShellServer shellServer = new ShellServer(uuid)
			if(null != params.usesocket && params.usesocket.toLowerCase().equals("false") )
				shellServer.socketSession = false
			try{
				params.hostids.split (",").each { host->
					Server server = null;
					dataStore.servers.each{key,val->
						if(key == host) {
							server = val;
							return
						}
					}
					ShellSession shellSession = new ShellSession(server: server, inStream: new ShellInputStream(), outStream: new ShellOutputStream(uuid,server))
					Thread.start {new ExecuteSSHController().startShell(uuid, shellSession)}
					shellServer.shellSessions.add(shellSession)
				}
				response.append("<status code='0' error='false' sessionid='$uuid' description='$msg'/>")
			}catch(Exception e){
				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}
		}
		response.append("</reply>")
		return response;
	}

	String executeCommand(HttpServletRequest request) {
		def params=[
			"sessionId":request.getParameter("sessionId"),
			"command":request.getParameter("command"),
		]
		
		StringBuffer response= new StringBuffer()
		response.append("<reply>")
		
		if(null == params.sessionId || null == params.command){
			response.append("<status code='1' error='true' description='Invalid user inputs'/>")	
		} else {
			try {
				ShellServer shellServer = Configurator.shellServers.getAt(params.sessionId)
				if(shellServer != null) {
					shellServer.shellSessions.each { shellSession->
						shellSession.inStream.addBuffer(params.command + "\r\n")
					}
					response.append("<status code='1' error='false' description=''/>")
				} else {
					response.append("<status code='1' error='true' description='Session not closed'/>")
				}
			} catch (Exception e) {
				e.printStackTrace()
				response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
			}
		}
		response.append("</reply>")
		return response;
	}

	public String gethosts(HttpServletRequest request){


		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		try{



			def hosts="";
			def servers=dataStore.servers
			int count =0;

			servers.each{key,val->

				if(count == 0)
					hosts=val.host
				else
					hosts=hosts + "," + val.host
				
				count++
			}

			response.append("<status code='0' error='false' hosts='$hosts' description='Successfully retrived hosts information'/>")
		}catch(Exception e){
			response= new StringBuffer()

			response.append("<reply>")


			response.append("<status code='1' error='true' description='${xml_string(e.getMessage())}'/>")
		}






		response.append("</reply>")

		return response.toString();
	}


	public String getappConfig(HttpServletRequest request){



		String ip=getIP(request)

		StringBuffer response= new StringBuffer()

		response.append("<reply>")
		def teams=getTeams();
		def envs=getEnvironments();

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
			"id":request.getParameter("id"),
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
						team: params.team,
						id:params.id
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
			"id":request.getParameter("id"),


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
			team: params.team,
			id:params.id
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


	

	public String getlogfilesbyserver(HttpServletRequest request){
		
		def params=[
			
			"team":request.getParameter("team")
			


		]
		
				StringBuffer response= new StringBuffer()
		
				response.append("<reply>")
		
		
				boolean valid=false
		
				int count=0;
				try{
					def logfiles=dataStore.getLogFiles(params.team);
					// Add information as xml
		
					//place all servers within rather than multiple entries
		
		
					//	ArrayList
					logfiles.each{val->
		
		
						response.append("<logfile>")
						response.append("\n<name>${val.name}</name>")
						response.append("\n<filename>${val.filename}</filename>")
						response.append("\n<team>${val.team}</team>")
						response.append("\n<id>${val.id}</id>")
						response.append("\n<hosts>")
						if(val.hosts){
							
							val.hosts.split(",").each{curhost ->
								
								response.append("\n<host>${curhost}</host>")
							}
							
						}
						response.append("\n</hosts>")
					
						
						response.append("\n</logfile>")
		
		
						count++
					}
		
		
					response.append("<status code='0'  records='$count' error='false' description='Successfully retrieved Logfile  information'/>")
				}catch(Exception e){
		
				e.printStackTrace();
					response= new StringBuffer()
		
					response.append("<reply>")
		
					response.append("<status code='1' records='$count' error='true' description='${xml_string(e.getMessage())}'/>")
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

			//place all servers within rather than multiple entries


			//	ArrayList
			logfiles.each{val->


				response.append("<logfile>")
				response.append("\n<name>${val.name}</name>")
				response.append("\n<filename>${val.filename}</filename>")
				response.append("\n<hosts>${val.hosts}</hosts>")
				response.append("\n<team>${val.team}</team>")
				response.append("\n<id>${val.id}</id>")
				response.append("\n</logfile>")


				count++
			}


			response.append("<status code='0'  records='$count' error='false' description='Successfully retrieved Logfile  information'/>")
		}catch(Exception e){

		e.printStackTrace();
			response= new StringBuffer()

			response.append("<reply>")

			response.append("<status code='1' records='$count' error='true' description='${xml_string(e.getMessage())}'/>")
		}


		response.append("</reply>")

		return response;


	}

	public String updatelogfile(HttpServletRequest request){


		def params=[
			"name":request.getParameter("name"),
			"filename":request.getParameter("filename"),
			"hosts":request.getParameter("logfilehosts"),
			"team":request.getParameter("team"),
			"operation":request.getParameter("operation"),
			"id":request.getParameter("id"),

		]
	
		StringBuffer response= new StringBuffer()

		response.append("<reply>")

		def overwrite=false
		if(params.operation == "UPDATE")
			overwrite=true

		if(null == params.name || null == params.filename  || null == params.operation){

			response.append("<status code='1' error='true' description='Invalid user inputs'/>")

		}else{

			try{


				dataStore.insertLog(new Log(
					id:params.id,
				name: params.name,
				filename: params.filename,
				hosts: params.hosts,
				team:params.team
				

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
			"name":request.getParameter("name"),
			"filename":request.getParameter("filename"),
			"hosts":request.getParameter("logfilehosts"),
			"team":request.getParameter("team"),
			"id":request.getParameter("id"),
		]

		StringBuffer response= new StringBuffer()


		try{


				if(null == params.name || null == params.filename  || null == params.id)
				throw new Exception("Invalid user inputs")



			response.append("<reply>")
			if(dataStore.deleteLog(new Log(
					id:params.id,
				name: params.name,
				filename: params.filename,
				hosts: params.hosts,
				team:params.team
				

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

