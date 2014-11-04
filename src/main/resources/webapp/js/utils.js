/**
 * 
 */

function updateConfig(callback){
	
	
}
function generateTeamMenu(divID,name,defaulttxt){
	
	
	if($("#teams").val() ==""){
		
		setTimeout(function(){generateTeamMenu(divID,name,defaulttxt)}, 1000);
		return 
	}
	
	var teamArray=$("#teams").val().split(",")
	
	var resp='  <select id="'+name+'" name="'+name+'">'
	   resp = resp + '<option value="">'+defaulttxt+'</option>'
	for(i=0;i<teamArray.length;i++){
		
		resp = resp + '<option value="'+teamArray[i]+'">'+teamArray[i]+'</option>'
		
	}
	resp = resp + '</select>'
	$("#"+divID).html(resp);
	 $( "#"+name ).selectmenu()
}
function preparecsv(tblid){
	
	$('#'+ tblid).after('<input id="csv_'+tblid +'" type="hidden"/>');
	
	var container = $('<div>').html($('#'+tblid).html());

	// then use .replaceWith(function()) to modify the HTML structure
	container.find('img').replaceWith(function() { return this.alt; })
	var strAlt = container.html();
	
	
	$rows = $(container).find('tr');
	//$rows = $('#'+ tblid).find('tr');

var csvData = "";

for(var i=0;i<$rows.length;i++){
                var $cells = $($rows[i]).children('th,td'); //header or content cells

                for(var y=0;y<$cells.length;y++){
                    if(y>0){
                      csvData += ",";
                    }
                    var txt = ($($cells[y]).text()).toString().trim();
					if(txt =="")
						console.log($($cells[y]).html())
                   // if(txt.indexOf(',')>=0 || txt.indexOf('\"')>=0 || txt.indexOf('\n')>=0){
                        txt = "\"" + txt.replace(/\"/g, "\"\"") + "\"";
                   // }
                    csvData += txt;
                }
                csvData += '\r\n';
 }
 
 
	
	$('#csv_'+tblid).val(csvData)
}
function downloadcsv(tblid){
	
		$('#repname').val(tblid.replace("tbl",""))
	$('#csv').val($('#csv_'+tblid).val())
	
	
	 $( "#frmcsv" ).submit();
	
}
function showinfo(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box notice"><span>notice: </span>' + content +'</div>')
			


	
}

function showerror(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box error"><span>error: </span>' + content +'</div>')
			
	
}

function showsuccess(divid,content){
	
	
	$( "#"+divid ).html('<div class="alert-box success"><span>success: </span>' + content +'</div>')
			
	
	
}

function showwarning(divid,content){
	
	$( "#"+divid ).html('<div class="alert-box warning"><span>warning: </span>' + content +'</div>')
			
	
	
}
function getMonday(d) {
	  d = new Date(d);
	  var day = d.getDay(),
	      diff = d.getDate() - day + (day == 0 ? -6:1); // adjust when day is sunday
	  return new Date(d.setDate(diff));
	}

function getLastDayofweek(curr) {
	 
	      
	 // var curr = new Date;
	  var firstday = new Date(curr.setDate(curr.getDate() - curr.getDay()));
	  var lastday = new Date(curr.setDate(curr.getDate() - curr.getDay()+6));
	  return lastday
	}
function getLastDayofMonth(date) {
	 
   
	 var y = date.getFullYear(), m = date.getMonth();
	 
	 var lastDay = new Date(y, m + 1, 0);
	 return lastDay
	
	}



function showProjectMenu(divid) {

	var containerid= "#" +divid
		
		$.get("/services/getallprojects/rand?inte="+Math.random(), function(data) {

			
			//parse xml
	 		  $xml = $( data )
	 		  $status = $xml.find( "status" )
	 		  
	 		  
	 		  
	 		  if( $status.attr("error") =="true"){
	 			  
	 			  showerror(divid,$status.attr("description"))
	 			  
	 		  } else{

					var dataCount=0
						
					
					var tbl=""
					tbl=tbl + ' <select style="width: 280px"  multiple="multiple" id="lstprojects">'
					
					
					
				//	tbl=tbl + "        <option value=''>Select</option>" 
					
						//select multiple="multiple">
  
      
				
						 
			 			 $xml.find('project').each(function(index){
			 				
			 				//var groupname = $(this).attr('name');
			 				
			 				// tbl=tbl + "<optgroup label='"+groupname+"' >" 
			 				 
			 				//$(this).find('user').each(function(index){
			 					 
			 					// dataCount++
					 	            var projectName = $(this).find('code').text();
					 	      
					 				
					 				 tbl=tbl + "        <option value='"+projectName+"'>&nbsp;&nbsp;&nbsp;&nbsp;"+projectName+"</option>" 
					 			
			 				 //});
			 				 
			 				 //tbl=tbl + "</optgroup>" 
			 			 
			 				 
			 	        });
			 			 
		 				 
			 			
			
			tbl=tbl + '</select>'
			
						$(containerid).html(tbl)
						
					//	$("select#lstusers").multiselectfilter("destroy");
						$("select#lstprojects").multipleSelect();
						
					

	 		  }
		});

	}



function showUserMenu(divid) {

	var containerid= "#" +divid
		
		$.get("/services/getvalidusergroups/rand?inte="+Math.random(), function(data) {

			
			//parse xml
	 		  $xml = $( data )
	 		  $status = $xml.find( "status" )
	 		  
	 		  
	 		  
	 		  if( $status.attr("error") =="true"){
	 			  
	 			  showerror(divid,$status.attr("description"))
	 			  
	 		  } else{

					var dataCount=0
						
					
					var tbl=""
					tbl=tbl + ' <select style="width: 280px"  multiple="multiple" id="lstusers">'
					
					
					
				//	tbl=tbl + "        <option value=''>Select</option>" 
					
						//select multiple="multiple">
  
      
				
						 
			 			 $xml.find('team').each(function(index){
			 				
			 				var groupname = $(this).attr('name');
			 				
			 				 tbl=tbl + "<optgroup label='"+groupname+"' >" 
			 				 
			 				$(this).find('user').each(function(index){
			 					 
			 					 dataCount++
					 	            var userName = $(this).find('name').text();
					 	      
					 				
					 				 tbl=tbl + "        <option value='"+userName+"'>&nbsp;&nbsp;&nbsp;&nbsp;"+userName+"</option>" 
					 			
			 				 });
			 				 
			 				 tbl=tbl + "</optgroup>" 
			 			 
			 				 
			 	        });
			 			 
		 				 
			 			
			
			tbl=tbl + '</select>'
			
						$(containerid).html(tbl)
						
					//	$("select#lstusers").multiselectfilter("destroy");
						$("select#lstusers").multipleSelect();
						
						/*
						$("select#lstusers").multiselect().multiselectfilter({
						    filter: function(event, matches){
						    	
						    	var res=""
						        if( matches.length ){
						            // do something
						            
						            for(i=0;i<matches.length;i++){
						            	if(i>0)
						            		res=res +","
						            		
						            	res=res+matches[i]
						            	
						            	
						            }
						            
						            
						        }
						    	
						    	$("#users").val(res)
						    }
						}); */

	 		  }
		});

	}
function showpage(pageName){
	
	
	/*
	if( pageName !="home"){
		
		if($( "#sessioncookie" ).val() ==""){
			
			$( "#dialog-form" ).dialog( "open" );
			return
		}
	}
	*/
	$( "#site_content" ).load( pageName+".html?session_id="+Math.random() );
	
	$( "div.menu ul li" ).removeClass("selected")
	
	$( "div.menu ul li#" +pageName  ).addClass("selected")
	
	
	
}


function validateadmin() {

 var name = $( "#adminname" )
    
     var password = $( "#adminpassword" )
      var allFields = $( [] ).add( name ).add( password );
	  
    var valid = true;
    allFields.removeClass( "ui-state-error" );

valid=false

if($( "#adminname" ).val() == "admin"  &&  $( "#adminpassword" ).val() == "admin"){
valid=true
}

    if ( valid ) {
	  $( "#sessioncookie" ).val('admin')
      
     // dialog.dialog( "close" );
	  $( "#dialog-form" ).dialog( "close" );
    }else{
	alert("Invalid inputs")
	}
    return valid;
  }
	
