//?????????????????????????????????
//?                                                              ?
//? ???? Version 1.0                                         ?
//?                                                              ?
//? Code by Chris.J(???)                                      ?
//?                                                              ?
//?????????????????????????????????

function PopupCalendar(InstanceName,container, start,end)
{
	///Global Tag
	this.instanceName=InstanceName;
	this.containerId = container;
	///Properties
	this.separator="-"
	this.oBtnTodayTitle="Today"
	this.oBtnConfirmTitle="Confirm";
	this.oBtnCancelTitle="Cancel"
	this.weekDaySting=new Array("S","M","T","W","T","F","S");
	this.monthSting=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
	this.Width=200;
	this.currDate=new Date();
	this.today=new Date();
	if(start)
		this.startYear=start;
	else
		this.startYear=1970;
	if(end)
		this.endYear=end;
	else
		this.endYear=2030;
  /// chenbo
	this.hourSting=new Array("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23");
	this.minuteSting=new Array("00","05","10","15","20","25","30","35","40","45","50","55");	
  this.selHourIndex=0;      //index 
  this.selMinuteIndex=0;    //index
  this.selHourValue="00";   //value
  this.selMinuteValue="00"; //value
  
	///Css
	this.normalfontColor="#666666";
	this.selectedfontColor="red";
	this.divBorderCss="1px solid #BCD0DE";
	this.titleTableBgColor="#98B8CD";
	this.tableBorderColor="#CCCCCC"
	///Method
	this.Init=CalendarInit;
	this.Fill=CalendarFill;
	this.Refresh=CalendarRefresh;
	this.Restore=CalendarRestore;
	this.getHtmlAll =getHtmlAll;
	///HTMLObject
	this.oTaget=null;
	this.oPreviousCell=null;
	this.htmlAll = '';
	this.sDIVID=InstanceName+"_Div";
	this.sTABLEID=InstanceName+"_Table";
	this.sMONTHID=InstanceName+"_Month";
	this.sYEARID=InstanceName+"_Year";
	this.sTODAYBTNID=InstanceName+"_TODAYBTN";
	/// chenbo
	this.sHOURID=InstanceName+"_Hour";       
	this.sMINUTEID=InstanceName+"_Minute";    
	
}
function CalendarInit()				///Create panel
{
	var sMonth,sYear
	sMonth=this.currDate.getMonth();
	sYear=this.currDate.getFullYear();
	//alert(sYear + '-' + sMonth);
	this.htmlAll="<div id='"+this.sDIVID+"' style='display:none;position:absolute;width:"+this.Width+";border:"+this.divBorderCss+";padding:2px;background-color:#FFFFFF'>";
	this.htmlAll+="<div align='center'>";
	/// Month
	htmloMonth="<select id='"+this.sMONTHID+"' onchange=CalendarMonthChange("+this.instanceName+") style='width:50%'>";
	for(i=0;i<12;i++)
	{			
		htmloMonth+="<option value='"+i+"'>"+this.monthSting[i]+"</option>";
	}
	htmloMonth+="</select>";
	/// Year
	htmloYear="<select id='"+this.sYEARID+"' onchange=CalendarYearChange("+this.instanceName+") style='width:50%'>";
	for(i=this.startYear;i<=this.endYear;i++)
	{
		htmloYear+="<option value='"+i+"'>"+i+"</option>";
	}
	htmloYear+="</select></div>";
	/// Day
	htmloDayTable="<table id='"+this.sTABLEID+"' width='100%' border=0 cellpadding=0 cellspacing=1 bgcolor='"+this.tableBorderColor+"'>";
	htmloDayTable+="<tbody bgcolor='#ffffff'style='font-size:13px'>";
	for(i=0;i<=6;i++)
	{
		if(i==0)
			htmloDayTable+="<tr bgcolor='" + this.titleTableBgColor + "'>";
		else
			htmloDayTable+="<tr>";
		for(j=0;j<7;j++)
		{

			if(i==0)
			{
				htmloDayTable+="<td height='20' align='center' valign='middle' style='cursor:hand'>";
				htmloDayTable+=this.weekDaySting[j]+"</td>"
			}
			else
			{
				htmloDayTable+="<td height='20' align='center' valign='middle' style='cursor:hand'";
				htmloDayTable+=" onmouseover=CalendarCellsMsOver(this,"+this.instanceName+")";
				htmloDayTable+=" onmouseout=CalendarCellsMsOut(this,"+this.instanceName+")";
				htmloDayTable+=" ondblclick=CalendarCellsDbClick(event,this,"+this.instanceName+")";
				htmloDayTable+=" onclick=CalendarCellsClick(this,"+this.instanceName+")>";
				htmloDayTable+="&nbsp;</td>"
			}
		}
		htmloDayTable+="</tr>";	
	}
	htmloDayTable+="</tbody></table>";
	
	/// chenbo
	/// hour
	htmloHour="<div align='center'><table border='0'>";
	htmloHour+="<tr><td><select id='"+this.sHOURID+"' onchange=CalendarHourChange("+this.instanceName+") >";
	for(i=0;i<24;i++)
	{	
		var tempvalue=this.hourSting[i];
		if(i==0)tempvalue="00";
		htmloHour+="<option value='"+tempvalue+"'>"+this.hourSting[i]+"</option>";
	}
	htmloHour+="</select>\u5C0F\u65F6</td>";
	/// minute
	htmloMinute="<td><select id='"+this.sMINUTEID+"' onchange=CalendarMinuteChange("+this.instanceName+") >";
	for(i=0;i<12;i++)
	{
		var tempvalue=this.minuteSting[i];
		if(i==0)tempvalue="00";
		htmloMinute+="<option value='"+tempvalue+"'>"+this.minuteSting[i]+"</option>";
	}	
	htmloMinute+="</select>\u5206\u949F</td></tr></table></div>";
	/// Today Button
	htmloButton="<div id=button_div_"+this.instanceName+" align='center' style='padding:3px'>"
	htmloButton+="<button id='"+this.sTODAYBTNID+"' style='width:60px;border:1px solid #BCD0DE;background-color:#eeeeee;cursor:hand'"
	htmloButton+=" onclick=CalendarTodayClick(event,"+this.instanceName+")>"+this.oBtnTodayTitle+"</button>&nbsp;"
	htmloButton+="<button style='width:60px;border:1px solid #BCD0DE;background-color:#eeeeee;cursor:hand'"
	htmloButton+=" onclick=CalendarConfirmClick(event,"+this.instanceName+")>"+this.oBtnConfirmTitle+"</button>&nbsp;"
	htmloButton+="<button style='width:60px;border:1px solid #BCD0DE;background-color:#eeeeee;cursor:hand'"
	htmloButton+=" onclick=CalendarCancel(event,"+this.instanceName+")>"+this.oBtnCancelTitle+"</button> "
	htmloButton+="</div>"	
	
	/// All
	this.htmlAll=this.htmlAll+htmloMonth+htmloYear+htmloDayTable+htmloHour+htmloMinute+htmloButton+"</div>";
	//document.write(htmlAll);
	var etc = document.getElementById(this.containerId);
	if(etc){
			etc.innerHTML = this.htmlAll;			
	}
	this.Fill();	
}
function CalendarFill()			///
{
	var sMonth,sYear,sWeekDay,sToday,oTable,currRow,MaxDay,iDaySn,sIndex,rowIndex,cellIndex,oSelectMonth,oSelectYear,oSelectHour,oSelectMinute
	sMonth=this.currDate.getMonth();
	sYear=this.currDate.getFullYear();
	sWeekDay=(new Date(sYear,sMonth,1)).getDay();
	sToday=this.currDate.getDate();
	iDaySn=1
	oTable=document.getElementById(this.sTABLEID);
	currRow=oTable.rows[1];
	MaxDay=CalendarGetMaxDay(sYear,sMonth);
	
	oSelectMonth=document.getElementById(this.sMONTHID);
	oSelectMonth.selectedIndex=sMonth;
	oSelectYear=document.getElementById(this.sYEARID);
	for(i=0;i<oSelectYear.length;i++)
	{
		if(parseInt(oSelectYear.options[i].value)==sYear)oSelectYear.selectedIndex=i;
	}
	////
	for(rowIndex=1;rowIndex<=6;rowIndex++)
	{
		if(iDaySn>MaxDay)break;
		currRow = oTable.rows[rowIndex];
		cellIndex = 0;
		if(rowIndex==1)cellIndex = sWeekDay;
		for(;cellIndex<currRow.cells.length;cellIndex++)
		{
			if(iDaySn==sToday)
			{
				currRow.cells[cellIndex].innerHTML="<font color='"+this.selectedfontColor+"'><i><b>"+iDaySn+"</b></i></font>";
				this.oPreviousCell=currRow.cells[cellIndex];
			}
			else
			{
				currRow.cells[cellIndex].innerHTML=iDaySn;	
				currRow.cells[cellIndex].style.color=this.normalfontColor;
			}
			CalendarCellSetCss(0,currRow.cells[cellIndex]);
			iDaySn++;
			if(iDaySn>MaxDay)break;	
		}
	}
	
	/// chenbo
	oSelectHour=document.getElementById(this.sHOURID);
	oSelectHour.selectedIndex=this.selHourIndex;
	oSelectMinute=document.getElementById(this.sMINUTEID);
	oSelectMinute.selectedIndex=this.selMinuteIndex;
}
function CalendarRestore()					/// Clear Data
{	
	var i,j,oTable
	oTable=document.getElementById(this.sTABLEID);
	for(i=1;i<oTable.rows.length;i++)
	{
		for(j=0;j<oTable.rows[i].cells.length;j++)
		{
			CalendarCellSetCss(0,oTable.rows[i].cells[j]);
			oTable.rows[i].cells[j].innerHTML="&nbsp;";
		}
	}	
}
function CalendarRefresh(newDate)					///
{
	
	this.currDate=newDate;
	//alert(this.currDate);
	this.Restore();	
	this.Fill();	
}

function CalendarCellsMsOver(myCell, oInstance)				/// Cell MouseOver
{
	//var myCell = event.srcElement;
	CalendarCellSetCss(0,oInstance.oPreviousCell);
	if(myCell)
	{
		CalendarCellSetCss(1,myCell);
		oInstance.oPreviousCell=myCell;
	}
}
function CalendarCellsMsOut(myCell, oInstance)				////// Cell MouseOut
{
	//var myCell = event.srcElement;
	CalendarCellSetCss(0,myCell);	
}

function CalendarYearChange(oInstance)				/// Year Change
{
	var sDay,sMonth,sYear,newDate
	sDay=oInstance.currDate.getDate();
	sMonth=oInstance.currDate.getMonth();
	sYear=document.getElementById(oInstance.sYEARID).value
	newDate=new Date(sYear,sMonth,sDay);
	//alert(newDate);
	oInstance.Refresh(newDate);
}
function CalendarMonthChange(oInstance)				/// Month Change
{
	var sDay,sMonth,sYear,newDate
	sDay=oInstance.currDate.getDate();
	sMonth=document.getElementById(oInstance.sMONTHID).value
	sYear=oInstance.currDate.getFullYear();
	newDate=new Date(sYear,sMonth,sDay);
	oInstance.Refresh(newDate);	
}
function CalendarHourChange(oInstance)       /// Hour Change
{
	oInstance.selHourIndex=document.getElementById(oInstance.sHOURID).selectedIndex;
	oInstance.selHourValue=document.getElementById(oInstance.sHOURID).value;
}
function CalendarMinuteChange(oInstance)     /// Minute Change
{
	oInstance.selMinuteIndex=document.getElementById(oInstance.sMINUTEID).selectedIndex;
	oInstance.selMinuteValue=document.getElementById(oInstance.sMINUTEID).value;
}

function isAllDigits(argvalue) {
    argvalue = argvalue.toString();
    var validChars = "0123456789";
    var startFrom = 0;
    if (argvalue.substring(0, 2) == "0x") {
        validChars = "0123456789abcdefABCDEF";
        startFrom = 2;
    } else {
        if (argvalue.charAt(0) == "0") {
            validChars = "01234567";
            startFrom = 1;
        } else {
            if (argvalue.charAt(0) == "-") {
                startFrom = 1;
            }
        }
    }
    for (var n = startFrom; n < argvalue.length; n++) {
        if (validChars.indexOf(argvalue.substring(n, n + 1)) == -1) {
            return false;
        }
    }
    return true;
}

function CalendarCellsDbClick(e,oCell,oInstance)
{
	//alert(oCell);
	//alert(oInstance.currDate + ' ' + oCell.innerHTML);
	var sDay,sMonth,sYear,newDate; 
	sYear=oInstance.currDate.getFullYear();
	sMonth=oInstance.currDate.getMonth();
	sDay=oInstance.currDate.getDate();
	//alert(sYear + '-' + sMonth + '-' + sDay);
	//if(oCell.innerHTML!="&nbsp;")
	if(isAllDigits(oCell.innerHTML.trim()))
	{
		//alert(oCell.innerHTML);
		sDay=parseInt(oCell.innerHTML);
		if(sDay!=oInstance.currDate.getDate())
		{
			newDate=new Date(sYear,sMonth,sDay);
			oInstance.Refresh(newDate);
		}
	}
	
	sDateString=sYear+oInstance.separator+CalendarDblNum(sMonth+1)+oInstance.separator+CalendarDblNum(sDay);		///return sDateString
	//luxiaoyi
	//sDateString=CalendarDblNum(sMonth+1)+oInstance.separator+CalendarDblNum(sDay)+oInstance.separator+sYear;
	/// chenbo
  sDateString+=" "+oInstance.selHourValue+":"+oInstance.selMinuteValue+":00";	

	if(oInstance.oTaget.tagName.toLowerCase()=="input")oInstance.oTaget.value = sDateString;
	CalendarCancel(e,oInstance);
	//alert(sDateString);
	return sDateString;
}

function CalendarCellsClick(oCell,oInstance)
{
	//alert(oCell);
	//alert(oInstance.currDate + ' ' + oCell.innerHTML);
	var sDay,sMonth,sYear,newDate; 
	sYear=oInstance.currDate.getFullYear();
	sMonth=oInstance.currDate.getMonth();
	sDay=oInstance.currDate.getDate();
	//alert(sYear + '-' + sMonth + '-' + sDay);
	//if(oCell.innerHTML!="&nbsp;")
	if(isAllDigits(oCell.innerHTML.trim()))
	{
		//alert(oCell.innerHTML);
		sDay=parseInt(oCell.innerHTML);
		if(sDay!=oInstance.currDate.getDate())
		{
			newDate=new Date(sYear,sMonth,sDay);
			oInstance.Refresh(newDate);
		}
	}
}
function CalendarTodayClick(e,oInstance)				/// "Today" button Change
{	
	oInstance.Refresh(new Date());
	if (e){
     e.stopPropagation();
     e.preventDefault(); 
     e.cancelBubble = true;
	}
    else{
     window.event.stopPropagation();
     window.event.preventDefault(); 
     window.event.cancelBubble = true;	    
    }
}
function getDateString(oInputSrc,oInstance)
{
	if(oInputSrc&&oInstance) 
	{
		var CalendarDiv=document.getElementById(oInstance.sDIVID);
		oInstance.oTaget=oInputSrc;
		
		//alert('its left is ' + CalendarDiv.style.left);
		//CalendarDiv.style.left=CalendargetPos(oInputSrc,"Left")+ oInputSrc.offsetWidth;//CalendargetPos(oInputSrc,"Left")+ oInputSrc.clientWidth;
		//alert('its new left is ' + CalendarDiv.style.left +";" + (CalendargetPos(oInputSrc,"Left")+ oInputSrc.offsetWidth));
		//CalendarDiv.style.top=CalendargetPos(oInputSrc,"Top") ;//+ oInputSrc.offsetHeight;
		CalendarDiv.style.display=(CalendarDiv.style.display=="none")?"":"none";
		//CalendarDiv.style.zIndex = 100000;
		
	}	
}
function CalendarCellSetCss(sMode,oCell)			/// Set Cell Css
{
	// sMode
	// 0: OnMouserOut 1: OnMouseOver 
	if(sMode)
	{
		oCell.style.border="1px solid #5589AA";
		oCell.style.backgroundColor="#BCD0DE";
	}
	else
	{
		oCell.style.border="1px solid #FFFFFF";
		oCell.style.backgroundColor="#FFFFFF";
	}	
}
function CalendarGetMaxDay(nowYear,nowMonth)			/// Get MaxDay of current month
{
	var nextMonth,nextYear,currDate,nextDate,theMaxDay
	nextMonth=nowMonth+1;
	if(nextMonth>11)
	{
		nextYear=nowYear+1;
		nextMonth=0;
	}
	else	
	{
		nextYear=nowYear;	
	}
	currDate=new Date(nowYear,nowMonth,1);
	nextDate=new Date(nextYear,nextMonth,1);
	theMaxDay=(nextDate-currDate)/(24*60*60*1000);
	return theMaxDay;
}
function CalendargetPos(el,ePro)				/// Get Absolute Position
{
	var ePos=0;
	while(el!=null)
	{		
		ePos+=el["offset"+ePro];
		el=el.offsetParent;
	}
	return ePos;
}
function CalendarDblNum(num)
{
	if(num < 10) 
		return "0"+num;
	else
		return num;
}

function CalendarConfirmClick(e,oInstance)
{
	var sDay,sMonth,sYear,newDate; 
	sYear=oInstance.currDate.getFullYear();
	sMonth=oInstance.currDate.getMonth();
	sDay=oInstance.currDate.getDate();
	
	sDateString=sYear+oInstance.separator+CalendarDblNum(sMonth+1)+oInstance.separator+CalendarDblNum(sDay);		///return sDateString
	//luxiaoyi
	//sDateString=CalendarDblNum(sMonth+1)+oInstance.separator+CalendarDblNum(sDay)+oInstance.separator+sYear;
	/// chenbo
  sDateString+=" "+oInstance.selHourValue+":"+oInstance.selMinuteValue+":00";	

	if(oInstance.oTaget.tagName.toLowerCase()=="input")oInstance.oTaget.value = sDateString;
	CalendarCancel(e,oInstance);
	//alert(sDateString);
	return sDateString;
}
function CalendarCancel(e,oInstance)			///Cancel
{
	var CalendarDiv=document.getElementById(oInstance.sDIVID);
	CalendarDiv.style.display="none";		
	CalendarDiv = null;
	if (e){
     e.stopPropagation();
     e.preventDefault(); 
     e.cancelBubble = true;
	}
    else{
     window.event.stopPropagation();
     window.event.preventDefault(); 
     window.event.cancelBubble = true;	    
    }
}

function getHtmlAll(){
	return this.htmlAll;
}

