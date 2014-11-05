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

	void init(def userfileName,def fetchfileName){

		Class.forName("org.sqlite.JDBC")

		fetcherDB = Sql.newInstance("jdbc:sqlite:"+fetchfileName, "org.sqlite.JDBC")

		//create table if not exists TableName (col1 typ1, ..., colN typN)
		/*
		 def host
		 def port
		 def user
		 def password
		 def proxyhost
		 def proxyport
		 def proxyuser
		 def proxypwd
		 def name
		 def group=""
		 */



		fetcherDB.execute("create table if not exists server (name string,group,string,host string, port integer,user string,password string,proxyhost string, proxyport integer,proxyuser string,proxypwd string)")

		/*
		 * 
		 * 	def name
		 def filename
		 def host
		 * 
		 */
		fetcherDB.execute("create table if not exists log (name string, filename string,host string)")




		updateServerCache();

	}


	public def executeSQL( def db, def sqlString){

		def result=false


		result= fetcherDB.execute(sqlString)
		return result
	}


	void updateServerCache(){

		def tmpservers=[:]

		/*
		 * 
		 * def host
		 def port
		 def user
		 def password
		 def proxyhost
		 def proxyport
		 def proxyuser
		 def proxypwd
		 def name
		 def group=""
		 */

		fetcherDB.rows("select * from server " ).each{

			tmpservers.put(it.host, new Server(
					host: it.host,
					port: it.port,
					user: it.user,
					password: it.password,
					proxyhost: it.proxyhost,
					proxyport: it.proxyport,
					proxyuser: it.proxyuser,
					proxypwd: it.proxypwd,
					name: it.name,
					group: it.group

					));
		}

		servers=tmpservers;
	}


	public ArrayList<Server> getServerEntries(String group){


		ArrayList<Server> userEntries=new ArrayList<Server>()


		fetcherDB.rows("select * from server order by group " ).each{

			boolean canadd=true

			if(null != group &&  group.equals(it?.group) == false){

				canadd=false
			}
			if(canadd){
				userEntries.add(
						new Server(
						host: it.host,
						port: it.port,
						user: it.user,
						password: it.password,
						proxyhost: it.proxyhost,
						proxyport: it.proxyport,
						proxyuser: it.proxyuser,
						proxypwd: it.proxypwd,
						name: it.name,
						group: it.group

						)
						);
			}
		}

		return userEntries
	}

	public boolean deleteServer(String host){



		if(null == host)

			return false



		LogMessage.error("Deleting  ${host} overWriting ");
		def query="delete from server  where host=:host"
		fetcherDB.executeUpdate(query, ["host":host]);




		return true
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

		return servers.getAt(user)?.group;
	}

	/* public def getProjectEmployeeReport(String projectcode,Date from,Date to){
	 //def timeEntries=new HashMap<String,ProjectEmployeeReport>()
	 ArrayList<ProjectEmployeeReport> lstProjectEmployeeReport=new ArrayList<ProjectEmployeeReport>()
	 String cond=" where 1=1 "
	 if(null != projectcode)
	 cond=cond + " AND projectcode like '${projectcode}' "
	 if(null != from )
	 cond=cond + " AND entryDate >= "+ from.getTime()
	 if(null != to)
	 cond=cond + " AND entryDate <= "+ to.getTime()
	 String reportqry="Select user,projectcode,entryDate,hours,projecttask,tasktype from timeentry ${cond}   "
	 String totalqry="Select user,projectcode,total(hours) from timeentry ${cond}  group by user,projectcode "
	 fetcherDB.rows(reportqry).each{
	 def usergroup=getUserTeam(it.user)
	 lstProjectEmployeeReport.add( new ProjectEmployeeReport(
	 user: it.user,
	 projectcode:it.projectcode,
	 entryDate: new Date(it.entryDate),
	 hours: it.hours,
	 projecttask:it.projecttask,
	 tasktype:it.tasktype,
	 group:usergroup,
	 projectInfo:projects.getAt(it.projectcode)
	 ))
	 }
	 return lstProjectEmployeeReport
	 }
	 */


	void insertServer(Server serverInfo){



		boolean exists =false



		def query="select host from server  where host='" + serverInfo.host + "'"

		exists=(fetcherDB.rows(query).size()>0)

		// boolean exists = fetcherDB.execute("select user from server  where user='${server.user}'", null);


		if( exists){

			LogMessage.error("Key Already exists ${serverInfo.host} overWriting ");
			query="delete from server  where host='${serverInfo.host}'"
			fetcherDB.executeUpdate(query, []);



		}

		DataSet curServer = fetcherDB.dataSet("server")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string



		curServer.add(
				host: serverInfo.host,
				port: serverInfo.port,
				user: serverInfo.user,
				password: serverInfo.password,
				proxyhost: serverInfo.proxyhost,
				proxyport: serverInfo.proxyport,
				proxyuser: serverInfo.proxyuser,
				proxypwd: serverInfo.proxypwd,
				name: serverInfo.name,
				group: serverInfo.group

				)

		updateServerCache();



	}
	
	
	void insertLog(LogMessage serverInfo){



		boolean exists =false



		def query="select host from server  where host='" + serverInfo.host + "'"

		exists=(fetcherDB.rows(query).size()>0)

		// boolean exists = fetcherDB.execute("select user from server  where user='${server.user}'", null);


		if( exists){

			LogMessage.error("Key Already exists ${serverInfo.host} overWriting ");
			query="delete from server  where host='${serverInfo.host}'"
			fetcherDB.executeUpdate(query, []);



		}

		DataSet curServer = fetcherDB.dataSet("server")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string



		curServer.add(
				host: serverInfo.host,
				port: serverInfo.port,
				user: serverInfo.user,
				password: serverInfo.password,
				proxyhost: serverInfo.proxyhost,
				proxyport: serverInfo.proxyport,
				proxyuser: serverInfo.proxyuser,
				proxypwd: serverInfo.proxypwd,
				name: serverInfo.name,
				group: serverInfo.group

				)

		updateServerCache();



	}
}
