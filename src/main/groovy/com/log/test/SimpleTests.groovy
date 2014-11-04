package com.log.test

import java.text.SimpleDateFormat
import java.util.Date;

class SimpleTests {


	static def test(){
		def RAW="""

<reply><history>
<event>	
<date>Wed, Jul 30 2014 10:58 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786b2e2040e06</prevproblemid>	
<problem>Network busy</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:58 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786b2f7ec0e08</prevproblemid>	
<problem>No Signal</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:56 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786b1eb0f0dfc</prevproblemid>	
<problem>Make calls - delayed ring tone specific number</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>Cause to be defined</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:56 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786b102020df2</prevproblemid>	
<problem>Make calls - delayed ring tone</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:53 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786ae77580de6</prevproblemid>	
<problem>Dropped calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:52 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786adaaa70ddc</prevproblemid>	
<problem>Cannot receive international calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:51 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786ad94a90dda</prevproblemid>	
<problem>Cannot receive calls while roaming abroad</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:50 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786ab73db0dd4</prevproblemid>	
<problem>Cannot receive calls from specific number</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:49 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786ab09ae0dce</prevproblemid>	
<problem>Cannot receive calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:47 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786aa48670dca</prevproblemid>	
<problem>Cannot make and receive calls while roaming abroad</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:47 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786a8d2060dc6</prevproblemid>	
<problem>Cannot make and receive calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 30 2014 10:46 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e476c9d79014786a852040dc4</prevproblemid>	
<problem>Cannot make international calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 12:45 PM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014763091187223e</prevproblemid>	
<problem>Cannot make calls while roaming abroad</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 12:43 PM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be99301476306112b2233</prevproblemid>	
<problem>Cannot make calls</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 12:42 PM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be99301476306110d2232</prevproblemid>	
<problem>Cannot call premium numbers</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 11:58 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014762dde59e2202</prevproblemid>	
<problem>Number not recognised</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 11:44 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014762d14a6321d4</prevproblemid>	
<problem> Cannot call specific number</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 23 2014 11:19 AM</date>	
<ban>9574250669</ban>	
<imsi>234207301403109</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014762bb843b21ac</prevproblemid>	
<problem>Other</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>A manual issue description was entered</result>
</event>
<event>	
<date>Fri, Jul 18 2014 11:02 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014748eabe6d1b0e</prevproblemid>	
<problem>Speech quality</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Fri, Jul 18 2014 10:33 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>BOTH</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-05-12 10:00:00.0</last_occured>	
<signal_strength>NOSIGNAL</signal_strength>	
<prevproblemid>4028782e466be993014748d028271ae8</prevproblemid>	
<problem>Emergency calls only</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>The diagnostic did not find any issues</result>
</event>
<event>	
<date>Wed, Jul 02 2014 11:57 AM</date>	
<ban>9574250669</ban>	
<imsi>234207201090694</imsi>	
<handset>SAMSUNG GALAXY NOT</handset>	
<indoors_outdoors>NONE</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-06-13 19:00:00.0</last_occured>	
<signal_strength>NONE</signal_strength>	
<prevproblemid>4028782e466be9930146f6b7dd480f65</prevproblemid>	
<problem>LBO related roaming issues</problem>	
<postcodes>SL61EH</postcodes>	
<specificlocations>NONE</specificlocations>	
<result>ARPLBO_007</result>
</event>
<event>	
<date>Thu, Jun 19 2014 11:04 AM</date>	
<ban>NA</ban>	
<imsi>NA</imsi>	
<handset>SAMSUNG GALAXY NOTE 2 GT-N7100</handset>	
<indoors_outdoors>INDOORS</indoors_outdoors>	
<all_day>ALLDAY</all_day>	
<last_occured>2014-06-19 00:00:00.0</last_occured>	
<signal_strength>DONTKNOW</signal_strength>	
<prevproblemid>4028782e466be9930146b3940f750c5e</prevproblemid>	
<problem>Cannot make calls</problem>	
<postcodes>RG46LX</postcodes>	
<specificlocations>SPECIFIC</specificlocations>	
<result>There is no Three coverage in this area and is served by Orange. But there is no issue identified.</result>
</event>
<event>	
<date>Wed, Jun 18 2014 04:10 PM</date>	
<ban>NA</ban>	
<imsi>NA</imsi>	
<handset>SAMSUNG GALAXY NOTE 2 GT-N7100</handset>	
<indoors_outdoors>INDOORS</indoors_outdoors>	
<all_day>MORNING</all_day>	
<last_occured>2014-06-15 11:00:00.0</last_occured>	
<signal_strength>DONTKNOW</signal_strength>	
<prevproblemid>4028782e466be9930146af8582ec0c0d</prevproblemid>	
<problem>Cannot make calls</problem>	
<postcodes>RG46LX</postcodes>	
<specificlocations>ALL</specificlocations>	
<result>There is no Three coverage in this area and is served by Orange. But there is no issue identified.</result>
</event>
</history>
  <status code="0" description="OK" />
</reply>



"""
		def PROBLEMID="dsd"
		def prevlist = RAW
		def condition = "GOOD"
		def severity = "GOOD"
		def summary = "PreviousDiagnostic query complete"
		def msisdn = ""
		def detail = ""
		
		try
		{
		  // MSISDN is only needed if the interface is run standalone.
		
		  msisdn ="4456565"
		
		  // Render a table header for the results.
		
		  detail = """
  <div class="subsectiontext" style="padding-bottom: 5px">MSISDN:</div>
  <div class="biggreentext">${msisdn}</div>
  <table id="prevdiagtable"  class="clsprevdiagtable" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td class="tablecell"><b>DATE</b></td>
      <td class="tablecell"><b>Problem</b></td>
      <td class="tablecell"><b>Specific Locations</b></td>
      <td class="tablecell"><b>Postcodes</b></td>
      <td class="tablecell"><b>Result</b></td>     
    </tr>
  """
		
		  def in_msg = new XmlParser().parseText(prevlist)
		
		  // If the call to PreviousDiagnostic succeeded, render the results.
		  // Otherwise, render an error message.
		
		  if(in_msg.'status'[0].attributes()['description'] == "OK")
		  {
		
		   int count=0
		   in_msg.'history'.'event'.each()
			{
			  c_item ->
		
			  detail += "  \n<tr class=\"rowhighlight\" onmouseup='highlightrow(this)'>"
			  detail += "    <td class=\"tablecell\">${c_item.'date'.text()}</td>"
			  detail += "    <td class=\"tablecell\">${c_item.'problem'.text()}</td>"
			  detail += "    <td class=\"tablecell\">${c_item.'specificlocations'.text()}</td>"
			  detail += "    <td class=\"tablecell\">${c_item.'postcodes'.text()}</td>"
			  detail += "    <td class=\"tablecell\">${c_item.'result'.text()}</td>"
			  
			  
			  detail += "  </tr>"
				  count++
			}
		
			   detail += "</table>"
			   
				if(count >0){
								detail = detail + """
					<table  border="0" class="clsprevdiagtable">
						<tr>
						  <td  align="right">
						  <input id="btneditdiag"  class="btndisable"  onclick="edit_prevdiag();return false;" value="Proceed With Selected" />
						 </td>
						</tr>
					  </table>
					""" 
				}
		  }
		  else
		  {
			detail += "</table>"
			detail += "<br/><br/><br/>"
			detail += "<div class=\"subsectiontext\">" + in_msg.'status'[0].attributes()['description'] + "</div>"
		  }
		}
		catch(Exception e)
		{
		  detail = "Error: Unable to contact the PreviousDiagnostic service."
		  println("PREVDIAGNOSTIC_INTERFACE : " + PROBLEMID + " : Error: Failed to parse PreviousDiagnostic response: " + prevlist)
		  condition = "BAD"
		  severity = "CRITICAL"
		  summary = "Error: ${e.getStackTrace()}"
		}
		
		// Return PreviousDiagnostic output to the calling RunBook.
		
		
		
		println("PREVDIAGNOSTIC_INTERFACE : " + PROBLEMID + " : Finishing")
		
		
	}
	def printme(){
		
		ClassLoader loader = this.getClass().getClassLoader();
		File indexLoc = new File(loader.getResource("."+File.separator).getFile());
		String htmlLoc = indexLoc.getParentFile().getParentFile().getParentFile().absolutePath
		
		println(htmlLoc)
	}
	static main(args) {
	
		
	
		test()
		
	}

}
