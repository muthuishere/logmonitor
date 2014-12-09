package com.log.model

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentNavigableMap;
import java.sql.*
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.sqlite.SQLite

import groovy.sql.DataSet
import groovy.sql.Sql

import com.log.beans.Log
import com.log.beans.RemoteFile
import com.log.beans.Server
import com.log.exceptions.ServiceException
import com.log.helpers.LogMessage
import com.log.helpers.AppCrypt



class DataStore {


	Sql fetcherDB =null;


	def servers=[:]



	void close(){
		fetcherDB.close();
	}

	void init(def fetchfileName){

		Class.forName("org.sqlite.JDBC")

		println(fetchfileName)
		fetcherDB = Sql.newInstance("jdbc:sqlite:"+fetchfileName, "org.sqlite.JDBC")
		fetcherDB.execute("create table if not exists log (id integer PRIMARY KEY,name string, filename string,hosts string,team string)")
		fetcherDB.execute("create table if not exists server (id integer PRIMARY KEY,name string,servergroup string,team string,host string, port integer,user string,password string,proxyhost string, proxyport integer,proxyuser string,proxypwd string,locked string)")

		updateServerCache();
	}


	public def executeSQL( def db, def sqlString){

		def result=false


		result= fetcherDB.execute(sqlString)
		return result
	}


	void updateServerCache(){

		def tmpservers=[:]

		fetcherDB.rows("select * from server " ).each{

			def curkey="${it.user}-${it.host}-${it.servergroup}-${it.team}"
			tmpservers.put(curkey, new Server(
					id:it.id,
					host: it.host,
					port: it.port,
					user: it.user,
					password:  AppCrypt.decrypt(it.password),
					proxyhost: it.proxyhost,
					proxyport: it.proxyport,
					proxyuser: it.proxyuser,
					proxypwd:  AppCrypt.decrypt(it.proxypwd),
					name: it.name,
					servergroup: it.servergroup,
					team:it.team,
					locked:it.locked

					));
		}

		servers=tmpservers;
	}

	
	public Log getLogFile(def logid){
		
		
				Log logFile=null;
		
				
				String query="select * from log where 1=1"
				
					
				
				if(null == logid)
					throw new Exception("Invalid  parameters Id not supplied for retreival")
					
				
					query += " and id=$logid"
					
					query += " order by name,filename"
					
				
		
				// get log files server should
				//Name ,file , servers ,team
				fetcherDB.rows(query).each{
		
				
					logFile=new Log(
									id:it.id,
								name: it.name,
								filename: it.filename,
								hosts: it.hosts,
								team:it.team,
								hostsmap:getHostsMap(it.hosts,it.team)
								
		
								)
								
					}
				
		
				return logFile
			}
	public ArrayList<Log> getLogFiles(def team=null){


		ArrayList<Log> logFiles=new ArrayList<Log>()

		
		String query="select * from log where 1=1"
		
			
		if(null != team)
			query += " and team = '$team'"
		
		
			
			query += " order by name,filename"
			
		

		// get log files server should 
		//Name ,file , servers ,team
		fetcherDB.rows(query).each{

		
				logFiles.add(
						new Log(
							id:it.id,
						name: it.name,
						filename: it.filename,
						hosts: it.hosts,						
						team:it.team,
						hostsmap:getHostsMap(it.hosts,it.team)
						

						)
						);
			}
		

		return logFiles
	}

	public def getHostsMap(String hosts,String team){
	
		def hostmap=[:]
		
		hosts.split(",").each {
			
			hostmap.put(it, getServerEntries(it,team))
			
		}
		
		return hostmap;
	}
	
	public def validateHosts(String hosts,String team){
		
			def valid=true
			
			hosts.split(",").each {
				
				if(getServerEntries(it,team).size() == 0)
					valid=false;
				
				
			}
			
			return valid;
		}
	
	public RemoteFile getRemoteFileForLog(String logFileId,String host,String team){
		
		RemoteFile remotefile=new RemoteFile(
			filename: getLogFile(logFileId)?.filename, 
			server:	getServerEntries(host,team)?.get(0)
			)
		
		return remotefile;
	}
	
	public ArrayList<Server> getServerEntries(String host=null,String team=null){


		String query="select * from server where 1=1"
		if(null != host)
			query += " and host = '$host'"
			
		if(null != team)
			query += " and team = '$team'"
			
			query += " order by servergroup"
		ArrayList<Server> userEntries=new ArrayList<Server>()


		fetcherDB.rows(query ).each{

			

			
				userEntries.add(
						new Server(
							id:it.id,
						host: it.host,
						port: it.port,
						user: it.user,
						password: AppCrypt.decrypt(it.password),
						proxyhost: it.proxyhost,
						proxyport: it.proxyport,
						proxyuser: it.proxyuser,
						proxypwd:  AppCrypt.decrypt(it.proxypwd),
						name: it.name,
						servergroup: it.servergroup,
						team:it.team,
						locked:it.locked

						)
						);
			}
		

		return userEntries
	}




	void updateServerLock(Server serverInfo){
		
		
		
				boolean exists =false
		
		
		
				def query="select user,host from userInfo  where user='" + serverInfo.user + "' and host='"+ serverInfo.host + "'"
		
			
				exists=(fetcherDB.rows(query).size()>0)
		
				// boolean exists = fetcherDB.execute("select user from userInfo  where user='${userInfo.user}'", null);
		
		
				if( exists){
		
					Log.error("Updating lock status for ${serverInfo.user} @ ${serverInfo.host} overWriting ");
					query="update userInfo set locked='${serverInfo.locked}' where user='${serverInfo.user}' and  host='${serverInfo.host}'"
				//	println(query)
					
					userDB.executeUpdate(query, []);
					
					return
		
				}else{
				
				throw new ServiceException("Server did not exist in DB")
				}
		
		
		
			}
	

	long getWorkingDaysBetweenTwoDates(Date start, Date end) {

		Calendar c1 = GregorianCalendar.getInstance();
		c1.setTime(start);
		int w1 = c1.get(Calendar.DAY_OF_WEEK);
		c1.add(Calendar.DAY_OF_WEEK, -w1 + 1);

		Calendar c2 = GregorianCalendar.getInstance();
		c2.setTime(end);
		int w2 = c2.get(Calendar.DAY_OF_WEEK);
		c2.add(Calendar.DAY_OF_WEEK, -w2 + 1);

		//end Saturday to start Saturday
		long days = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
		long daysWithoutSunday = days-(days*2/7);

		if (w1 == Calendar.SUNDAY) {
			w1 = Calendar.MONDAY;
		}
		if (w2 == Calendar.SUNDAY) {
			w2 = Calendar.MONDAY;
		}
		return daysWithoutSunday-w1+w2;
	}






	def getServerGroup(def user){

		return servers.getAt(user)?.servergroup;
	}



	public boolean deleteServer(Server serverInfo){


		boolean exists =false

		if(null == serverInfo)
			return exists

	

			LogMessage.info("Deleting server ${serverInfo.host} overWriting ");
			def query="delete from server   where id=" + serverInfo.id 
			fetcherDB.executeUpdate(query, []);

				updateServerCache();



		

		return true


	}

	void insertServer(Server serverInfo,boolean overwrite){



		boolean exists =false



		def query="select host from server  where servergroup='" + serverInfo.servergroup + "' and host='" + serverInfo.host + "' and user='" + serverInfo.user + "' and user='" + serverInfo.user + "'and team='" + serverInfo.team + "'"

		exists=(fetcherDB.rows(query).size()>0)

		if(!overwrite && exists )
			throw new ServiceException("Server already exists:  ${serverInfo.user}@${serverInfo.host}  in ${serverInfo.servergroup} environment for ${serverInfo.team} team")

		if( overwrite){

			LogMessage.info("Key Already exists ${serverInfo.host} overWriting ");
			query="delete from server   where id=" + serverInfo.id 
			fetcherDB.executeUpdate(query, []);



		}

		DataSet curServer = fetcherDB.dataSet("server")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string



		
		curServer.add(				
				host: serverInfo.host,
				port: serverInfo.port,
				user: serverInfo.user,
				password:  AppCrypt.encrypt(serverInfo.password),
				proxyhost: serverInfo.proxyhost,
				proxyport: serverInfo.proxyport,
				proxyuser: serverInfo.proxyuser,
				proxypwd: AppCrypt.encrypt(serverInfo.proxypwd),
				name: serverInfo.name,
				servergroup: serverInfo.servergroup,
				team: serverInfo.team,
				locked:false

				)

		updateServerCache();



	}

	public boolean deleteLog(Log logObject){

		boolean exists =false
	
		
			
			LogMessage.error("Deleting Log file :  ${logObject.name} overWriting ");
			def query="delete from log where id=" + logObject.id 
			fetcherDB.executeUpdate(query, []);



	
		
		return true
		
	}

	void insertLog(Log logObject,boolean overwrite){


		boolean exists =false
	
		def query="select name from log  where team='" + logObject.team + "'  and name='" + logObject.name + "'"	
		
		exists=(fetcherDB.rows(query).size()>0)
		
		
		if(!overwrite && exists )
			throw new ServiceException("Log file already exists:  ${logObject.name}   for ${logObject.team} team")

		if(! validateHosts( logObject.hosts, logObject.team))
			throw new ServiceException("Invalid Hosts")


		if( overwrite){

			LogMessage.error("Log file already exists:  ${logObject.name} overWriting ");
			query="delete from log where id=" + logObject.id 
			fetcherDB.executeUpdate(query, []);



		}

		DataSet logDataset = fetcherDB.dataSet("log")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string



		logDataset.add(
				hosts: logObject.hosts,
				name: logObject.name,
				filename: logObject.filename,
				team: logObject.team

				)





	}
}
