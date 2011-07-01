/* 
 * @(#)vagexmm.js 2009-10-6 
 *  
 * Copyright (C) 2008-2011, 
 * LingCloud Team, 
 * Institute of Computing Technology, 
 * Chinese Academy of Sciences. 
 * P.O.Box 2704, 100190, Beijing, China. 
 * 
 * http://lingcloud.org 
 *  
 */

var newBasePath="";
var gnodenum = "";
var rentEndTime = "";
var rentStartTime = "";
var serverTime = '';
var partGuid = '';
var hasAppletLoaded = false;

String.prototype.trim = function() {   
    return this.replace(/^\s+/g,"").replace(/\s+$/g,"");     
}

function appChange(basePath, i){
	
		//$("#vaguid").change(function () {
			  var id = 'vaguid' + i;
			  $("#" + id + " :selected").each(function () {
					var str = $(this).val();
					
					if(str == "-1")
						return;
					var ajax = initAjax();
				    if (ajax === false || url === null) {
				        return false;
				    }
				    var url = basePath+"/JSP/GetApplianceInfo.jsp?appid=" + str;
				    ajax.open("GET", url, true);
				    ajax.onreadystatechange = function () {
				        if (ajax.readyState == 4) {
				             if (ajax.status == 200) {
				                var xmlDoc = ajax.responseText.trim();
				                if (xmlDoc === null ||xmlDoc =="") {
				                    return;
				                }
				                //alert(xmlDoc);
				                var cpuNum = xmlDoc.split(';')[0];
				                var memSize = xmlDoc.split(';')[1];
				                //alert(cpuNum + ";" + memSize);
				                if(parseInt(cpuNum) <= 1){
				                	$('#cpunum' + i).val(cpuNum);
				                	$('#cpunum' + i + ' option:gt(0)').each(function(){
				                		$(this).attr('disabled', true);
				                		//alert($(this).val());
				                	});	
				                }else{
				                	$('#cpunum' + i + ' option').each(function(){
				                		$(this).attr('disabled', false);
				                		//alert($(this).val());
				                	});	
				                	$('#cpunum' + i).val(cpuNum);
				                }
				                $('#memsize' + i).val(memSize);
				            } else {
				                alert(lingcloud.error.responseNotFound + ajax.statusText);
				            }
				        }
				    };
				    ajax.send(null);
			  });

  }

function checkLength(str){
	if(typeof(str) == 'undefined' || str === '')
		return 0;
	else{
		var len = 0;
		for(var i = str.length-1; i >= 0; i--){
			if(str.charCodeAt(i) > 127 || str.charCodeAt(i) == 94)
				len += 2;
			else 
				len++;
				
		}
		return len;
	}
}
function initAjax() {
    var ajax = false;
    try {
        ajax = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e) {
        try {
            ajax = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (E) {
            ajax = false;
        }
    }
    if (!ajax && typeof XMLHttpRequest != "undefined") {
        ajax = new XMLHttpRequest();
    }
    return ajax;
}
function changeNodeNum(showid, unshowid, callback, basePath, targetDiv, parid){
	var form = document.getElementById("fastNewVirtualClusterForm");
	var nodenum = form.nodenum.value.trim();
	if(nodenum != gnodenum)
		gnodenum = nodenum;
	else
		return;

		changeShowTable(showid, unshowid, callback, basePath, targetDiv, parid);
}

changeTab2 = function (basePath, n, d) {
	var i;
	for (i=1;i<=n;i=i+1) {
		var link=document.getElementById('link'+i);
		var tab=document.getElementById('divTab'+i);
		if (link != null && tab != null ) {
			link.className="";
			tab.style.display='none';
		}
	}
	var link=document.getElementById('link'+d);
	var tab=document.getElementById('divTab'+d);
	if (link!=null && tab !=null ) {
		link.className="active";
		tab.style.display='';
	}

	if(d == 2){
		var can = lingcloud.calendar;
		var etc = document.getElementById('etcontainer')
		if(etc && etc.innerHTML.trim() ==  ''){
			if(rentStartTime == ""){
			 rentStartTime=new PopupCalendar("rentStartTime",'etcontainer', 2010);
	
	          rentStartTime.weekDaySting=new Array(can.sun,can.mon,can.tues,can.wed,can.thurs,can.fri,can.sat);
	
	          rentStartTime.monthSting=new Array(can.jan,can.feb,can.mar,can.apr,can.may,can.jun,can.jul,can.aug,can.sep,can.oct,can.nov,can.dec);
	
	          rentStartTime.hourSting=new Array("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23");
	
	          rentStartTime.minuteSting=new Array("00","05","10","15","20","25","30","35","40","45","50","55");
	
	          rentStartTime.oBtnTodayTitle=can.today;
	
	          rentStartTime.oBtnCancelTitle=can.cancel;
	          
	          rentStartTime.oBtnConfirmTitle=can.confirm;
	
	          rentStartTime.Init();
			}else{
				etc.innerHTML = rentStartTime.getHtmlAll();
				rentStartTime.Fill();
			}
		}
        //alert('between');
		var epc = document.getElementById('epcontainer')
		if(epc && epc.innerHTML.trim() ==  ''){
			if(rentEndTime == ""){
	          rentEndTime=new PopupCalendar("rentEndTime",'epcontainer',2010);
	
	          rentEndTime.weekDaySting=new Array(can.sun,can.mon,can.tues,can.wed,can.thurs,can.fri,can.sat);
	
	          rentEndTime.monthSting=new Array(can.jan,can.feb,can.mar,can.apr,can.may,can.jun,can.jul,can.aug,can.sep,can.oct,can.nov,can.dec);
	
	          rentEndTime.hourSting=new Array("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23");
	
	          rentEndTime.minuteSting=new Array("00","05","10","15","20","25","30","35","40","45","50","55");
	
	          rentEndTime.oBtnTodayTitle=can.today;
	
	          rentEndTime.oBtnCancelTitle=can.cancel;
	          
	          rentEndTime.oBtnConfirmTitle=can.confirm;
	
	          rentEndTime.Init();
			}else{
				epc.innerHTML = rentEndTime.getHtmlAll();
				rentEndTime.Fill();
			}
		}
		//to get the server time
		var ajax = initAjax();
	    if (ajax === false) {
	        return false;
	    }
	    ajax.open("GET", basePath+"/JSP/GetServerInfoUtil.jsp?sevNum=0", true);
	    ajax.onreadystatechange = function () {
	        if (ajax.readyState == 4) {
	             if (ajax.status == 200) {
	                var xmlDoc = ajax.responseText;
	                if (xmlDoc === null || xmlDoc === '') {
	                    return;
	                }
	                var st1 = xmlDoc.split(';')[0];
	                var st2 = xmlDoc.split(';')[1];
	                document.getElementById("serverTime").innerHTML = st1;
	                serverTime = st2;
	                
	            } else {
	                //document.getElementById("serverTime").innerHTML = xmlDoc;
	            }
	        }
	    };
	    ajax.send(null);
	}
};

function showDeployPN(selectedIndex, nodeIndex){
	var dip = document.getElementById("deployInPhysicalNode" + nodeIndex);
	if(dip == null) return;
	if(selectedIndex == 3)
		dip.style.display = '';
	else
		dip.style.display = 'none';
}

function changeShowTable(showid, unshowid, callback, basePath, targetDiv, parGuid){
	
	var form = document.getElementById("fastNewVirtualClusterForm");
	
	//alert(nodenum + ";" + gnodenum);
	
	var radio = document.getElementsByName("nodeinfotype");
	if(callback != null && radio[0].checked){
		return;
	}
	var item1 = document.getElementById(showid);
	var item2 = document.getElementById(unshowid);

	item1.style.display='';
	item2.style.display='none';
	if(callback == null || basePath == null){

		return;
	}

	callback(basePath, targetDiv, parGuid);
	
}
selectAll = function(chose, targetCheckBox){
	var chosebox = document.getElementById(chose);
	var checkBox = document.getElementsByName(targetCheckBox);
	if(chosebox.checked){
		for(var i=0;i<checkBox.length;i++){
	   		checkBox[i].checked = true;  
    	} 
	}else{
		selectNone(targetCheckBox);
	}  
};
selectNone = function(targetCheckBox){
	var checkBox = document.getElementsByName(targetCheckBox);
	for(var i=0;i<checkBox.length;i++){
	    checkBox[i].checked = false;
    }
};
callbackNodeDetailForCreateCluster = function(basePath, targetDiv, parGuid){
	var form = document.getElementById("fastNewVirtualClusterForm");
	var tdiv = document.getElementById(targetDiv);
	if(tdiv){	
		var _parentElement = tdiv.parentNode;
	    if(_parentElement){
	           _parentElement.removeChild(tdiv);
	    }
	}
    tdiv = document.createElement("div");
    tdiv.id = targetDiv;
	var parid = form.parguid.value.trim();
	if (parid === "") {
        alert(lingcloud.error.partIDNotNull);
        return;
    }
    if(tdiv == null){
    	return;
    }
    var radio = document.getElementsByName("vntype");
    var nodenum, vnguid, url;
    // auto_create way.
	nodenum = form.nodenum.value.trim();
	if(nodenum === "" ){
		//alert("Please set node num.");
		var tr = document.createElement("div");
		tr.innerHTML= lingcloud.error.nodeNumNotAssigned;
		tdiv.appendChild(tr);
		if(_parentElement){
			_parentElement.appendChild(tdiv);
		}
		return;
	}
	
	url=basePath + "JSP/AjaxLoadDetailNodeConfig.jsp?nodenum="+nodenum + "&parid=" + parGuid;
	var div = document.createElement("div");
	div.innerHTML="<table id=\"loadingTable\" width=600px><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    tdiv.appendChild(div);
	var ajax = initAjax();
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
             if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                //TODO         
                var div2 = document.createElement("div");
            	div.innerHTML = xmlDoc;
            	tdiv.appendChild(div2);
//                tdiv.innerHTML = xmlDoc;
                if(radio[0].checked){
                	for(var i= 11; ;i++){
                		var xx = 'nodeDetailR' + i;
                		var dd = document.getElementById(xx);
                		if(dd){
                			dd.style.display = 'none';
                		}else{
                			break;
                		}
                	}     
                	var tp = parseInt(parseInt(nodenum)/10);
                	if(parseInt(nodenum)%10 != 0)
                		tp++;
	                var pd1 = new LingcloudPageDiv({
						totalPage:tp,
						currentPage:1,
						renderId:'nodeDetailsDiv',
						callback:function(pid){
							pageDiv4NodeDetail('nodeDetailR', pid, 10);
						}
					});
                }
                if(_parentElement){
                	_parentElement.appendChild(tdiv);
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};

pageDiv4NodeDetail = function(trPre, pid, rpp){
	var xx;
	var dd;
	if(typeof(rpp) == 'undefined')
		rpp = 10;
	for(var i = 1; ; i++){
		xx = trPre + i;
        dd = document.getElementById(xx);
        if(dd){
           dd.style.display = 'none';
        }else{
           break;
        }
	}
	for(var i = (pid-1)*rpp +1 ; i <= pid *rpp; i++){
		xx = trPre + i;
        dd = document.getElementById(xx);
        if(dd){
           dd.style.display = '';
        }else{
           break;
        }
	}
}

showDialogForDeletePNNode = function (basePath) {
	var str = "<form id=\"deletePNNodeForm\" action=\"" + basePath + "deletePNNode.do\" method=\"post\">";
    str += "<table id=\"loadingTable4DeletePartition\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>var tt = 1, t2; loadForDeletePartition('" + basePath + "','loadingTable4DeletePartition', 'deletePartitionTable', 2)</script>";
    str += "<table id=\"deletePartitionTable\" style=\"DISPLAY:none\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td>" + lingcloud.Infrastructure.targetPart + ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\" onchange=\"loadForDeletePNNode('"+basePath+"', 'loadingTable4DeletePNNode', 'deletePNNodeTable', 'parguid', this);\"></select></td><td>*</td></tr>";
    str += "</tbody></table>";
    str += "<table id=\"loadingTable4DeletePNNode\" style=\"DISPLAY:none\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<table id=\"deletePNNodeTable\" style=\"DISPLAY:none\" ><tbody>";
    str += "<tr><td>" + lingcloud.Infrastructure.physicalNode + ":&nbsp;</td><td><select id=\"pnguid\" name=\"pnguid\"></select></td><td>*</td></tr>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Infrastructure.rmNewPNNode.title, callbackForDeletePNNode);
};
loadForDeletePNNode = function (basePath, loadingTable, targetTable, parguidSelect, par) {
//	tt = tt?0:1;
//	if(!tt){ return;}
	var select_parguid = document.getElementById(parguidSelect);
	var parid = null
	if(par == null){
		for(i=0;i<select_parguid.length;i++){
		   if(select_parguid[i].selected==true){
			   parid = select_parguid.options[i].value;
		   }
		}
	}else{
		parid = par.value;
	}
	if(select_parguid.options.length > 1) {
		if(t2 == parid && par != null){return;}
		t2 = parid;
	}
	//alert(parid);
	var ltable = document.getElementById(loadingTable);
	ltable.style.display = "";
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListPhysicalNodes.jsp?parid="+parid;
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_parguid = document.getElementById("pnguid");
                select_parguid.options.length = 0;
                for (var i = 0; i < xSel.length; i++) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_parguid.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
callbackForDeletePNNode = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("deletePNNodeForm");
    	//alert(form);
        if (form.parguid.value.trim() === "") {
            alert(lingcloud.error.partNameNotNull);
            return;
        }
        if (form.pnguid.value.trim() === "") {
            alert(lingcloud.error.pnNodeNotNull);
            return;
        }
        //to add a check to avoid submit the form when there is no physical nodes. Modifed by liujie 20100908.
        if(document.getElementById("deletePNNodeForm").pnguid.value == -1)
        	$("#popup_cancel").trigger('click');
        else
        	form.submit();
        
    } else {
		// cancel.
        // alert("false!");
    }
};
showPartitionInfo = function (basePath, parid) {
	partGuid = parid;
	var tdiv = document.getElementById("asset_info_div");
	if(tdiv == null){
		return;
	}
	tdiv.innerHTML = "<table id=\"loadingTable4ShowPartitionInfo\" width=\"600px\" height=\"560px\"><tbody><tr><td valign=\"top\" align=\"center\"><img src=" + basePath + "images/table_loading.gif /></td><td valign=\"top\"><br/><br/>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    var ajax = initAjax();
    var url = basePath + "JSP/ShowPartitionInfo.jsp?parid=" + parid + "&basePath="+basePath;
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                tdiv.innerHTML = xmlDoc;
                //to make a page division
                var rpp = 10;
                for(var i= rpp+1; ;i++){
                		var xx = 'ptNodeRrd' + i;
                		var dd = document.getElementById(xx);
                		if(dd){
                			dd.style.display = 'none';
                		}else{
                			break;
                		}
                	}     
               var nodenum = document.getElementById("ptNodeNum4Trick").value.trim();
               //alert(nodenum);
               var tp = parseInt(parseInt(nodenum)/rpp);
                	if(parseInt(nodenum)%rpp != 0)
                		tp++;
	                var pd1 = new LingcloudPageDiv({
						totalPage:tp,
						currentPage:1,
						renderId:'nodeDetailsDiv4pt',
						callback:function(pid){
							//whichPage(basePath, pid);
							pageDiv4NodeDetail('ptNodeRrd', pid, rpp);
						}
					});
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};

callbackForOperateVirtualNode = function (result) {
    if (result) {
    	try {
            var form = document.getElementById("operateVirtualVirtualNodeForm");
            if (form.action.value.trim() === "migrate") {
                if (form.pnName.value == -1) {
                	alert(lingcloud.Infrastructure.virtualNodeOp.empty.trim());
                	return ;
                }
            }
            
            form.submit();
    	}catch(e) {
    		alert(e);
    	}

    } else {
    }
};

showDialogForOperateVirtualNode = function (basePath, action, vNodeGuid, hostId) {
	var strAction;
	var title = lingcloud.Infrastructure.virtualNodeOp.title;
	if (action === "save") {
		strAction = lingcloud.Infrastructure.virtualNodeOp.save;
	} else if (action === "stop") {
		strAction = lingcloud.Infrastructure.virtualNodeOp.stop;
	} else if (action === "start") {
		strAction = lingcloud.Infrastructure.virtualNodeOp.start;
	} else if (action === "migrate") {
		strAction = lingcloud.Infrastructure.virtualNodeOp.migrate;
		title = lingcloud.Infrastructure.virtualNodeOp.migrateTitle;
	} else if (action === "destroy") {
		strAction = lingcloud.Infrastructure.virtualNodeOp.destroy;
	}else {
		return ;
	}
	
    var str = "<form id=\"operateVirtualVirtualNodeForm\" action=\"" 
    			+ basePath + "operateVirtualNodeAction.do\" method=\"post\">";
    str += "<input type=\"hidden\" name=\"vNodeGuid\" value=\"" + vNodeGuid + "\" />";
    str += "<input type=\"hidden\" name=\"action\" value=\"" + action + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/ViewVirtualCluster.jsp\" />";
    if (action == "migrate") {
    	str += "<script>loadForMigrateTable('" + basePath 
        		+ "','" + vNodeGuid
        		+ "', 'migrateDiv')</script>";
    	str += "<div id=\"migrateDiv\"></div>";
    	
    	str += "<input type=\"hidden\" name=\"hostId\" value=\"" + "172.22.1.13" + "\" />";
    }else {
    	str += "<table width=\"400px\"><tbody><tr><td>&nbsp;&nbsp;"
    			+ lingcloud.Appliance.operateAppliance.confirmTip 
    			+ strAction + "?</td></tr></tbody></table>";
    }
    
    str += "</form>\n";
    jSubmit(str, title, callbackForOperateVirtualNode);
    
};

loadForMigrateTable = function (basePath, vNodeGuid, migrateDiv) {
    var ajax = initAjax();
    if (ajax === false || url === null) {
        return false;
    }
    var url = basePath + "JSP/AjaxMigrateVNode.jsp?vNodeGuid="+vNodeGuid;
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
				var xmlDoc = ajax.responseText
				if(xmlDoc == null){
					return;
				}

                var div = document.getElementById(migrateDiv);
                div.innerHTML = xmlDoc;
                
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};

showVirtualClusterInfo = function (basePath, virid, refresh) {
	var tdiv = document.getElementById("asset_info_div");
	if(tdiv == null){
		return;
	}
	tdiv.innerHTML = "<table id=\"loadingTable4ShowPartitionInfo\" width=\"600px\" height=\"560px\"><tbody><tr><td valign=\"top\" align=\"center\"><img src=" + basePath + "images/table_loading.gif /></td><td valign=\"top\"><br/><br/>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    var ajax = initAjax();
    var url = basePath + "JSP/ShowVirtualClusterInfo.jsp?virid=" + virid + "&basePath="+basePath ;
    if (refresh != null && refresh != "") {
    	url += "&refresh=" + refresh;
    }	
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                tdiv.innerHTML = xmlDoc;
                //to make a page division
                var rpp = 10;
                for(var i= rpp+1; ;i++){
                		var xx = 'vcNodeRrd' + i;
                		var dd = document.getElementById(xx);
                		if(dd){
                			dd.style.display = 'none';
                		}else{
                			break;
                		}
                	}     
               var nodenum = document.getElementById("vcNodeNum4Trick").value.trim();
               //alert(nodenum);
               var tp = parseInt(parseInt(nodenum)/rpp);
                	if(parseInt(nodenum)%rpp != 0)
                		tp++;
	                var pd1 = new LingcloudPageDiv({
						totalPage:tp,
						currentPage:1,
						renderId:'nodeDetailsDiv4vc',
						callback:function(pid){
							//whichPage(basePath, pid);
							pageDiv4NodeDetail('vcNodeRrd', pid, rpp);
						}
					});
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
showNodeInfo = function (basePath, nodeid, nodetype) {
	var tdiv = document.getElementById("asset_info_div");
	if(tdiv == null){
		return;
	}
	tdiv.innerHTML = "<table id=\"loadingTable4ShowPartitionInfo\" width=\"600px\" height=\"560px\"><tbody><tr><td valign=\"top\" align=\"center\"><img src=" + basePath + "images/table_loading.gif /></td><td valign=\"top\"><br/><br/>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    var ajax = initAjax();
    var url = basePath + "JSP/ShowNodeInfo.jsp?nodeid=" + nodeid + "&nodetype="+nodetype+"&basePath="+basePath;
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                tdiv.innerHTML = xmlDoc;
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
callbackForLogin = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("login");
    	//alert(form);
        if (form.email.value.trim() === "") {
            alert("The username should not be null or blank.");
            return;
        }
        if (!checkEmail(form.email.value.trim())) {
            alert("The username should be a valid email address.");
            return;
        }
        if (form.password.value.trim() === "") {
            alert("The password should not be null or blank.");
            return;
        }
        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
showDialogForLogin = function (basePath) {
    var str = "<form id=\"login\" action=\"" + basePath + "login.do\" method=\"post\"><table id=\"login\"><tbody>";
    str += "<tr><td>UserName:&nbsp;</td><td><input type=\"text\" name=\"email\" /></td></tr>";
    str += "<tr><td>Password:&nbsp;</td><td><input type=\"password\" name=\"password\" /></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, "Login", callbackForLogin);
};
loadForHostMonitor = function (basePath, loadingTable, targetTable, hostname) {
    var ajax = initAjax();
    //cross domain access. source=2 means the load5 data.
    var params = "host=" + hostname + "$srv=CPU_Load$display=image$view=1$source=2";
    var url = basePath + "JSP/monitorProxy.jsp?param=" + params;
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                var tdiv = document.getElementById(targetTable + "_cpu");
                ltable.style.display = "none";
                tdiv.innerHTML = xmlDoc;
                ttable.style.display = "";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
    var ajax2 = initAjax();
    params = "host=" + hostname + "$srv=PING$display=image$view=1$source=1";
    url = basePath + "JSP/monitorProxy.jsp?param=" + params;
    if (ajax2 === false || url === null) {
        return false;
    }
    ajax2.open("GET", url, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseText;
                if (xmlDoc === null) {
                    return;
                }
                var tdiv = document.getElementById(targetTable + "_ping");
                tdiv.innerHTML = xmlDoc;
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
            }
        }
    };
    ajax2.send(null);
};
showDialogForHostMonitor = function (basePath, hostname) {
    var str = "<table id=\"loadingTable4HostMonitor\" width=\"600px\" height=\"560px\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>loadForHostMonitor('" + basePath + "', 'loadingTable4HostMonitor','hostMonitorTable', '" + hostname + "')</script>";
    str += "<table id=\"hostMonitorTable\" width=\"600px\" height=\"560px\" style=\"DISPLAY:none\" ><tbody>";
    str += "<tr><td><div id=\"hostMonitorTable_cpu\" style=\"width:600px;height:280px;overflow:auto;\"/></td></tr>";
    str += "<tr><td><div id=\"hostMonitorTable_ping\" style=\"width:600px;height:280px;overflow:auto;\"/></td></tr>";
    str += "</tbody></table>\n";
    jShow(str, hostname);
};

checkIP = function(ipStr){
	var ipDomainPat = /^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/;
	var IPArray = ipStr.match(ipDomainPat);
    if (IPArray != null) {
    	//reverse reference
        for (var i = 1; i <= 4; i++) {
        	var rr = '$'+i;
            if (RegExp[rr] > 255) {
                return false;
            }
            if(i == 1){            
            	if(parseInt(RegExp[rr]) <= 0)
            	return false;
            }
        }
        return true;
    }
    return false;
}

ipTrim0 = function(ipStr){
	var result = "";
	var ss = ipStr.split('.');
	if(ss.length != 4){
		return "";
	}
	for(var i =0; i < 4; i++){
		result += parseInt(ss[i]);
		if(i < 3)
			result += '.';
	}
	return result;
}

/**
 * created by Xiaoyi Lu, modified by Jie Liu, 20100908
 * @param {} result
 */
callbackForAddNewPNNode = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("newHostNodeForm");
        if (form.parguid.value.trim() === "") {
            alert(lingcloud.error.partNameNotNull);
            return;
        }
        var privateip = form.privateip.value.trim(); 
        if (privateip === "") {
            alert(lingcloud.error.privateIpNotNull);
            return;
        }
        //to check the form of ip
        if(!checkIP(privateip)){
        	alert(lingcloud.error.privateIpNotValid);
            return;
        }
        form.privateip.value = ipTrim0(privateip); 
        var publicip = form.publicip.value;        
        if(publicip != null && publicip.trim() != ''){
        	if(!checkIP(publicip.trim())){        
        		alert(lingcloud.error.publicIpNotValid);
            	return;
        	}
        	form.publicip.value = ipTrim0(publicip.trim());
        }
        //to do something else with the ip
        
        //if (form.redeploy.value.trim() === "") {
        //	alert("The redeploy option should not be null or blank.");
        //    return;
        //}
        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
//partitionId, privateIp,publicIp,pnController,HashMap<String, String> attributes, double price,desc
showDialogForAddNewPNNode = function (basePath) {
    var str = "<form id=\"newHostNodeForm\" action=\"" + basePath + "newHostNode.do\" method=\"post\">";
    str += "<table id=\"loadingTable4DeletePartition\" width=\"400\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>loadForDeletePartition('" + basePath + "', 'loadingTable4DeletePartition', 'newHostNodeTable')</script>";
    str += "<table id=\"newHostNodeTable\" width=\"400\" style=\"DISPLAY:none\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td>" + lingcloud.Infrastructure.targetPart + ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\"></select></td><td>*</td></tr>";
    //str += "<input type="hidden" name="targetdiv" value="allHostsDiv" />";
    str += "<tr><td>" + lingcloud.Infrastructure.addNewPNNode.privateIp + ":&nbsp;</td><td><input type=\"text\" name=\"privateip\" /></td><td>*</td></tr>";
    str += "<tr style=\"display:none\" ><td>" + lingcloud.Infrastructure.addNewPNNode.publicIp + ":&nbsp;</td><td><input type=\"text\" name=\"publicip\" /></td><td/></tr>";
    str += "<tr style=\"display:none\" ><td>Redeploy:&nbsp;</td><td>YES<input type=\"radio\" name=\"redeploy\" value=\"true\">&nbsp;&nbsp;NO<input type=\"radio\" name=\"redeploy\" value=\"false\" checked><td>*</td></tr>";
    //str += "<tr><td>Price:&nbsp;</td><td><input type=\"text\" name=\"price\" />Yuan/hour</td><td>*</td></tr>";
    str += "<tr><td>" + lingcloud.Infrastructure.desc + ":&nbsp;</td><td><textarea name=\"description\" cols=\"30\" rows=\"6\" /></td><td></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Infrastructure.addNewPNNode.title, callbackForAddNewPNNode);
};
//String name, String pController,HashMap<String, String> attributes, double price, String desc
showDialogForAddPartition = function (basePath) {
    var str = "<form id=\"addPartitionForm\" action=\"" + basePath + "addPartition.do\" method=\"post\">";
    str += "<table id=\"addPartitionTable\"><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<input type=\"hidden\" name=\"basepath\" value=\"" + basePath + "\" />";
    str += "<tr><td>" + lingcloud.Infrastructure.partName + ":&nbsp;</td><td><input type=\"text\" name=\"name\" size=\"26\" maxlength=\"26\"/></td><td>*</td></tr>";
    str += "<tr style=\"Display:none\"><td>Controller:&nbsp;</td><td><select id=\"controller\" name=\"controller\" type=\"hidden\"><option value=\"MHV\">MR/HPC/VM/Storage Controller</option></select></td><td>*</td></tr>";
   
    str += "<tr><td rowspan=2 valign = \"top\">" + lingcloud.Infrastructure.newPart.type + ":&nbsp;</td>";
    str += "<td><input type=\"radio\" name=\"nodetype\" value=\"VM\" checked>" + lingcloud.Infrastructure.newPart.vmtype + "</td><td></td></tr>" +
    		"<tr><td><input type=\"radio\" name=\"nodetype\" value=\"GENERAL\">" + lingcloud.Infrastructure.newPart.gertype + "</td><td></td></tr>";
 	str += "<tr><td>" + lingcloud.Infrastructure.newPart.preInstalledSoft + ":&nbsp;</td><td><input type=\"text\" name=\"\preInstalledSoft\" size=\"26\"></input></td></tr><td></td>";
    str += "<tr><td>" + lingcloud.Infrastructure.desc + ":&nbsp;</td><td><textarea name=\"description\" cols=\"30\" rows=\"6\" /></td><td></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Infrastructure.newPart.title, callbackForAddPartition);
};
/**
 * created by Xiaoyi Lu, modified by Jie Liu 20100908
 * @param {} result
 */
callbackForAddPartition = function (result) {
    if (result) {
		// ok. //to do more check work
        var form = document.getElementById("addPartitionForm");
        var parname =form.name.value.trim();
        var basePath = form.basepath.value.trim();
        if (parname === "") {
            alert(lingcloud.error.partNameNotNull);
            return;
        }

        if(checkLength(parname) > 26){
        	alert(lingcloud.error.partNameTooLong);
        	return;
        }
        //to check whether the partition name has been used        
		var ajax = initAjax();
	    if (ajax === false) {
	        return false;
	    }
	    ajax.open("GET", basePath+"/JSP/GetServerInfoUtil.jsp?sevNum=1&parName=" + parname, true);
	    ajax.onreadystatechange = function () {
	        if (ajax.readyState == 4) {
	             if (ajax.status == 200){
	                var xmlDoc = ajax.responseText;
	                if (xmlDoc === null || xmlDoc === '') {
	                    return;
	                }	
	                xmlDoc = xmlDoc.trim();
	                if(xmlDoc === 'yes'){
	                	if (form.controller.value.trim() === "") {
				            alert(lingcloud.error.partControllerNotNull);
				            return;
				        }

				        form.submit();
	                }else if(xmlDoc === 'no'){
	                	alert(lingcloud.error.partNameBeenUsed);
	                	return;
	                }
	            } else {
	                alert(lingcloud.error.partNameNotFound);
	            }
	        }
	    };
	    ajax.send(null);
        
    } else {
		// cancel.
        // alert("false!");
    }
};
showDialogForDeletePartition = function (basePath) {
    var str = "<form id=\"deletePartitionForm\" action=\"" + basePath + "deletePartition.do\" method=\"post\">";
    str += "<table id=\"loadingTable4DeletePartition\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>loadForDeletePartition('" + basePath + "', 'loadingTable4DeletePartition', 'deletePartitionTable')</script>";
    str += "<table id=\"deletePartitionTable\" style=\"DISPLAY:none\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td>" + lingcloud.Infrastructure.partName + ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\"></select></td><td>*</td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Infrastructure.delPart.title, callbackForDeletePartition);
};
loadForDeletePartition = function (basePath, loadingTable, targetTable, from) {
    var ajax = initAjax();

    var url = basePath + "JSP/AjaxListPartition.jsp";
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, false);

    ajax.send(null);
    if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_parguid = document.getElementById("parguid");
                select_parguid.options.length = 0;
                for (var i = 0; i < xSel.length; i++) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_parguid.options[i] = option;
                        if(partGuid === xValue)
                        	select_parguid.options[i].selected = 'selected';
                    }
                    catch (e) {
                    }
                }
                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
                if(from != null){
                	if(from == 1)
						loadForFreeCluster(basePath, 'loadingTable4DestroyVirtualCluster', 'destroyVirtualClusterTable', 'parguid');
					else if(from == 2)
						loadForDeletePNNode(basePath, 'loadingTable4DeletePNNode', 'deletePNNodeTable', 'parguid');
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
     
    
};
callbackForDeletePartition = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("deletePartitionForm");
    	//alert(form);
        if (form.parguid.value.trim() === "") {
            alert(lingcloud.error.partNameNotNull);
            return;
        }
        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
changeCreateClusterTable = function(basePath, parid, targetDiv, random){
	var ajax = initAjax();
    if (ajax === false || url === null) {
        return false;
    }
    var url = basePath + "JSP/AjaxCreateCluster.jsp?parid="+parid+"&&targetDiv="+targetDiv+ "&&random=" +random;
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                //var xmlDoc = ajax.responseXML; // bug here.
				var xmlDoc = ajax.responseText
				if(xmlDoc == null){
					return;
				}	
				
				var tdiv = document.getElementById(targetDiv);
				
				if(tdiv){	
					var _parentElement = tdiv.parentNode;
				    if(_parentElement){
				           _parentElement.removeChild(tdiv);
				    }
					tdiv = document.createElement("div");
				    tdiv.id = targetDiv;

				    tdiv.innerHTML = "<tbody><tr><td>" + xmlDoc + "</td></tr></tbody>";
				    
				    if(_parentElement){
	                	_parentElement.appendChild(tdiv);
				    }
				}

            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
showDialogForFastCreateCluster = function (basePath) {    
    var str = "<form id=\"fastNewVirtualClusterForm\" action=\"" + basePath + "fastNewVirtualCluster.do\" method=\"post\">";
    str += "<table id=\"loadingTable\" width=650px><tbody><tr><td><img src=" 
    		+ basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>loadForCreateCluster('" + basePath 
    		+ "', 'loadingTable', 'fastNewVirtualClusterTable', 'fastNewVirtualClusterDiv')</script>";
    
    str += "<table id=\"fastNewVirtualClusterTable\" style=\"DISPLAY:none\" ></table></form>";
    jSubmit(str, lingcloud.Infrastructure.clusterOp.createCluster, callbackForFastCreateCluster);
};

loadForCreateCluster = function (basePath, loadingTable, targetTable, targetDiv) {
    var ajax = initAjax();
    if (ajax === false || url === null) {
        return false;
    }
    var url = basePath + "JSP/AjaxCreateCluster.jsp?targetDiv="+targetTable;
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                //var xmlDoc = ajax.responseXML; // bug here.
				var xmlDoc = ajax.responseText
				if(xmlDoc == null){
					return;
				}

                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                var div = document.getElementById("innerhtml");
                var tr = document.createElement("tr");
                var td = document.createElement("td");
                td.innerHTML = "<tbody><tr><td>" + xmlDoc + "</td></tr></tbody>";
                tr.appendChild(td);
                ttable.appendChild(tr);
                ltable.style.display = "none";
                ttable.style.display = "";
                $.alerts._reposition();
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
callbackForStopCluster = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("stopClusterForm");
    	//alert(form);
        if (form.vcguid.value.trim() === "") {
            alert(lingcloud.error.clusterInfoNotNull);
            return;
        }
        if (form.vcguid.value.trim() == -1) {
            alert(lingcloud.error.clusterNotSelect);
            return;
        }

        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
showDialogForStopCluster = function (basePath) {
    var str = "<form id=\"stopClusterForm\" action=\"" + basePath + "stopCluster.do\" method=\"post\">";
    str += "<table id=\"loadingTable4FreeCluster\" width=\"400\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>var tt_vc = 1, t2_vc; loadForDeletePartition('" + basePath + "','loadingTable4FreeCluster', 'freeVirtualClusterTable',1)</script>";
    str += "<table id=\"freeVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.targetPart + ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\" onchange=\"loadForFreeCluster('"+basePath+"', 'loadingTable4DestroyVirtualCluster', 'destroyVirtualClusterTable', 'parguid', this);\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</tbody></table>";
    str += "<table id=\"loadingTable4DestroyVirtualCluster\" width=\"400\" style=\"DISPLAY:none\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<table id=\"destroyVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\"><tbody>";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.clusterName + ":&nbsp;</td><td><select id=\"vcguid\" name=\"vcguid\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Infrastructure.clusterOp.stopCluster, callbackForStopCluster);
};

directStartCluster = function(basePath, vcguid){
	var str = "<form id=\"dirHandleClusterForm\" action=\"" + basePath + "startCluster.do\" method=\"post\">";
   	str += "<table id=\"loadingTable\" width=400px><tbody><tr><td><img src=" + basePath + "images/table_loading.gif />Loading...</td></tr></tbody></table>"; 
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<input type=\"hidden\" name=\"vcguid\" value=\"" + vcguid + "\" />";
    str += "</form>\n";
    //str += "<script>cb4directSC()</script>";
    
    jShow(str, lingcloud.Infrastructure.clusterOp.startCluster);
    cb4directHc(true);
}

directStopCluster = function(basePath, vcguid){
	var str = "<form id=\"dirHandleClusterForm\" action=\"" + basePath + "stopCluster.do\" method=\"post\">";
	str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<input type=\"hidden\" name=\"vcguid\" value=\"" + vcguid + "\" />";
    str += "</form>\n";
    str += lingcloud.Infrastructure.clusterOp.tip4StopCluster;
    jSubmit(str, lingcloud.Infrastructure.clusterOp.stopCluster, cb4directHc);
}

directFreeCluster = function(basePath, vcguid){
	var str = "<form id=\"dirHandleClusterForm\" action=\"" + basePath + "freeCluster.do\" method=\"post\">";
	str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<input type=\"hidden\" name=\"vcguid\" value=\"" + vcguid + "\" />";
    str += "</form>\n";
    str += lingcloud.Infrastructure.clusterOp.tip4FreeCluster;
    jSubmit(str, lingcloud.Infrastructure.clusterOp.freeCluster, cb4directHc);
}

cb4directHc = function(result){
	if(result){
		var form = document.getElementById("dirHandleClusterForm");
		form.submit();
	}
	else{
	}
}

callbackForStartCluster = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("startClusterForm");
    	//alert(form);
        if (form.vcguid.value.trim() === "") {
            alert(lingcloud.error.clusterInfoNotNull);
            return;
        }
        if (form.vcguid.value.trim() == -1) {
            alert(lingcloud.error.clusterNotSelect);
            return;
        }

        form.submit();
    } else {

    }
};
showDialogForStartCluster = function (basePath) {
    var str = "<form id=\"startClusterForm\" action=\"" + basePath + "startCluster.do\" method=\"post\">";
    str += "<table id=\"loadingTable4FreeCluster\" width=\"400\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>var tt_vc = 1, t2_vc; loadForDeletePartition('" + basePath + "','loadingTable4FreeCluster', 'freeVirtualClusterTable',1)</script>";
    str += "<table id=\"freeVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.targetPart 
    		+ ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\" onchange=\"loadForFreeCluster('"
    		+ basePath + "', 'loadingTable4DestroyVirtualCluster', 'destroyVirtualClusterTable', 'parguid', this);\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</tbody></table>";
    str += "<table id=\"loadingTable4DestroyVirtualCluster\" width=\"400\" style=\"DISPLAY:none\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<table id=\"destroyVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\"><tbody>";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.clusterName + ":&nbsp;</td><td><select id=\"vcguid\" name=\"vcguid\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Infrastructure.clusterOp.startCluster, callbackForStartCluster);
};

loadForFreeCluster = function (basePath, loadingTable, targetTable, parguidSelect, par) {

	var select_parguid = document.getElementById(parguidSelect);
	var parid = null
	if(par == null){
		for(i=0;i<select_parguid.length;i++){
			if(select_parguid[i].selected == true){
				parid = select_parguid.options[i].value;
			}
		}
	}else{
		parid = par.value;
	}
	if(select_parguid.options.length > 1) {
		if(t2_vc == parid && par != null){return;}
		t2_vc = parid;
	}
	//alert(parid);
	var ltable = document.getElementById(loadingTable);
	ltable.style.display = "";
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListVirtualCluster.jsp?parid="+parid;
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_parguid = document.getElementById("vcguid");
                select_parguid.options.length = 0;
                for (var i = 0; i < xSel.length; i++) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_parguid.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
callbackForFreeCluster = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("freeClusterForm");
    	//alert(form);
        if (form.vcguid.value.trim() === "") {
            alert(lingcloud.error.clusterInfoNotNull);
            return;
        }
        if (form.vcguid.value.trim() == -1) {
            alert(lingcloud.error.clusterNotSelect);
            return;
        }

        form.submit();
    } else {

    }
};
showDialogForFreeCluster = function (basePath) {

    var str = "<form id=\"freeClusterForm\" action=\"" + basePath + "freeCluster.do\" method=\"post\">";
    str += "<table id=\"loadingTable4FreeCluster\" width=\"400\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>var tt_vc = 1, t2_vc; loadForDeletePartition('" + basePath + "','loadingTable4FreeCluster', 'freeVirtualClusterTable',1)</script>";
    str += "<table id=\"freeVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualCluster.jsp\" />";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.targetPart + ":&nbsp;</td><td><select id=\"parguid\" name=\"parguid\" onchange=\"loadForFreeCluster('"+basePath+"', 'loadingTable4DestroyVirtualCluster', 'destroyVirtualClusterTable', 'parguid', this);\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</tbody></table>";
    str += "<table id=\"loadingTable4DestroyVirtualCluster\" width=\"400\" style=\"DISPLAY:none\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<table id=\"destroyVirtualClusterTable\" style=\"DISPLAY:none\" width=\"400\"><tbody>";
    str += "<tr><td width=\"100\">" + lingcloud.Infrastructure.clusterName + ":&nbsp;</td><td><select id=\"vcguid\" name=\"vcguid\"></select>&nbsp;&nbsp;*</td></tr>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Infrastructure.clusterOp.freeCluster, callbackForFreeCluster);
};
callbackForFastCreateCluster = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("fastNewVirtualClusterForm");
    	//alert(form);
    	//alert(form.parguid.value.trim());
    	if(form.parguid.value.trim() === "") {
    		alert(lingcloud.error.partNotSelect);
            return;
    	}
        if (form.clustername.value.trim() === "") {
            alert(lingcloud.error.clusterNameNotAssigned);
            return;
        }
        if (checkLength(form.clustername.value.trim()) > 20) {
            alert(lingcloud.error.clusterNameTooLong);
            return;
        }
        // form.amm.value.trim();
        var vntype = form.vntype;
        var pips = form.publicIpSupport;
        // a trick to judge create for pm or vm.
        if (pips == null || pips[0] == null || pips[1] == null) {
        	//alert("PM");
        	if (vntype[0].checked){

        		// form.pnnode is not really good.
        		var pnnodes = document.getElementsByName("pnnodeip"); //not pnnode
        		if(pnnodes == null || pnnodes.length == 0){
        			alert(lingcloud.error.pnNodeNotNull);
        			return;
        		}
        		var pnnodes_str = "";
        		for(var i = 0; i < pnnodes.length; i++){
        			//alert(pnnodes[i].value.trim());
        			if(pnnodes[i].checked){
        				pnnodes_str += pnnodes[i].value.trim()+",";
        			}
        		}
        		
        		if(pnnodes_str === ""){
        			alert(lingcloud.error.pnNodeNotNull);
        			return;
        		}
        		//alert(pnnodes_str);
        		//return;
        	}else if (vntype[1].checked){
        		//alert("use exist");
        		if (form.vnguid.value.trim().length != 40) {
            		alert(lingcloud.error.virtualNetworkNotValid);
           			return;
        		}
        	}
        }else{
        	//alert("VM");
    		//alert("auto create");
    		if (form.nodenum.value.trim() === ""){
    			alert(lingcloud.error.nodeNumNotAssigned);
    			return;
    		}
    		if (pips[0] == null || pips[1] == null){
    			alert(lingcloud.error.publicIpAssignment);
    			return;
    		}
        	var nodeinfotype = document.getElementsByName("nodeinfotype");
        	if(nodeinfotype[0].checked){
        		// node simple config
        		
        	}else if(nodeinfotype[1].checked){
        		// node detail config
        		
        	}
        }
        //to check time
        var startTime = form.effectiveTime.value.trim();
        var endTime = form.expireTime.value.trim();
        if(startTime == 'click here...'){
        	startTime= '';
        	form.effectiveTime.value = '';
        }
        if(endTime == 'click here...'){
        	endTime = '';
        	form.expireTime.value = '';
        }
        //alert(endTime);
        if(endTime != null && endTime != '' ){
        	if(startTime!=null && startTime != ''){
        		if(!diffTime(serverTime,startTime)){
	        		alert(lingcloud.error.startAfterCurrent);
	        		return;
	        	}
	        	if(!diffTime(startTime,endTime)){
	        		alert(lingcloud.error.endAfterStart);
	        		return;
	        	}
        	}else{
        		//alert(serverTime);
        		if(!diffTime(serverTime,endTime)){
	        		alert(lingcloud.error.endAfterCurrent);
	        		return;
	        	}
        	}        	
        }
        form.submit();
        return;
    } else {
		// cancel.
        // alert("false!");
    }
};

diffTime = function(firstTime, secondTime, dateFormat){
	//to check whether the firstTime is before secondTime
	//dateFormat = yyyy-MM-dd HH:mm:ss
	var ft,et;
	var eY,eMon,eD,eH,emin,es;
	
	if(firstTime == null)
		firstTime = serverTime;//new Date();
	
	firstTime = firstTime.replace(/-/g, ' ');
	ft = Date.parse(firstTime);
	
	if(secondTime == null)
		secondTime = serverTime; //new Date();
		
	
	secondTime = secondTime.replace(/-/g, ' ');
	et = Date.parse(secondTime);
	
	
	return (ft<et);
}

callbackForCreateAppliance = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("newVirtualApplianceForm");
    	//alert(form);
        if (form.appliancename.value.trim() === "") {
            alert("The appliance name should not be null or blank.");
            return;
        }
        if (form.location.value.trim() === "") {
            alert("The appliance location should not be null or blank.");
            return;
        }
        if (form.vatype.value.trim() === "") {
            alert("The appliance type should not be null or blank.");
            return;
        }
        if (form.valoader.value.trim() === "") {
            alert("The appliance boot loader should not be null or blank.");
            return;
        }
        if (form.description.value.trim().length > 500) {
            alert("The appliance description should be less than 500 chars.");
            return;
        }
        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
showDialogForCreateAppliance = function (basePath) {
    var str = "<form id=\"newVirtualApplianceForm\" action=\"" + basePath + "newVirtualAppliance.do\" method=\"post\">";
    str += "<table id=\"newVirtualApplianceTable\"><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<tr><td>Appliance Name:&nbsp;</td><td><input type=\"text\" name=\"appliancename\" /></td></tr>";
    str += "<tr><td>Location:&nbsp;</td><td><input type=\"text\" name=\"location\" /></td></tr>";
    str += "<tr><td>Appliance Type:&nbsp;</td><td><select name=\"vatype\"><option value=\"os\">os</option><option value=\"app\">app</option></select></td></tr>";
    str += "<tr><td>Appliance Bootloader:&nbsp;</td><td><select name=\"valoader\"><option value=\"hvm\">hvm</option><option value=\"pygrub\">pygrub</option></select></td></tr>";
    str += "<tr><td>Description:&nbsp;</td><td><textarea name=\"description\" cols=\"30\" rows=\"6\" /></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, "New Virtual Appliance", callbackForCreateAppliance);
};
loadForDeleteAppliance = function (basePath, loadingTable, targetTable) {
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListVirtualAppliance.jsp";
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("optionos");
                if (xSel === null) {
                    return;
                }
                var select_vaguid = document.getElementById("vaguid");
                select_vaguid.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i++) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_vaguid.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                //alert(i);
                xSel = xmlDoc.getElementsByTagName("optionapp");
                if (xSel === null) {
                    return;
                }
                //var select_vnguid = document.getElementById("vaguid");
                //select_vaguid.options.length = 0;
                for (var j = 0; j < xSel.length; j++, i++) {
                    var xValue = xSel[j].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[j].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                    	//Notice i and j.
                        select_vaguid.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                if (select_vaguid.options.length == 0) {
                    var option = new Option("Please Select..", "-1");
                    select_vaguid.options[0] = option;
                }
                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};
callbackForDeleteAppliance = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("deleteVirtualApplianceForm");
    	//alert(form);
        if (form.vaguid.value.trim() === "") {
            alert("The appliance should not be null or blank.");
            return;
        }
        if (form.vaguid.value.trim() == -1) {
            alert("No valid appliance selected.");
            return;
        }
        form.submit();
    } else {
		// cancel.
        // alert("false!");
    }
};
showDialogForDeleteAppliance = function (basePath) {
    var str = "<form id=\"deleteVirtualApplianceForm\" action=\"" + basePath + "deleteVirtualAppliance.do\" method=\"post\">";
    str += "<table id=\"loadingTable4DeleteVA\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;Loading Information...</td></tr></tbody></table>";
    str += "<script>loadForDeleteAppliance('" + basePath + "', 'loadingTable4DeleteVA', 'deleteVirtualApplianceTable')</script>";
    str += "<table id=\"deleteVirtualApplianceTable\" style=\"DISPLAY:none\" ><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<tr><td>Virtual Appliance:&nbsp;</td><td><select id=\"vaguid\" name=\"vaguid\"></select></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, "Delete Virtual Appliance", callbackForDeleteAppliance);
};
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
checkEmail = function (emailStr) {
    if (emailStr.length == 0) {
        return true;
    }
        // TLD checking turned off by default
    var checkTLD = 0;
    var knownDomsPat = /^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;
    var emailPat = /^(.+)@(.+)$/;
    var specialChars = "\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
    var validChars = "[^\\s" + specialChars + "]";
    var quotedUser = "(\"[^\"]*\")";
    var ipDomainPat = /^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
    var atom = validChars + "+";
    var word = "(" + atom + "|" + quotedUser + ")";
    var userPat = new RegExp("^" + word + "(\\." + word + ")*$");
    var domainPat = new RegExp("^" + atom + "(\\." + atom + ")*$");
    var matchArray = emailStr.match(emailPat);
    if (matchArray == null) {
        return false;
    }
    var user = matchArray[1];
    var domain = matchArray[2];
    var i =0;
    for (i = 0; i < user.length; i++) {
        if (user.charCodeAt(i) > 127) {
            return false;
        }
    }
    for (i = 0; i < domain.length; i++) {
        if (domain.charCodeAt(i) > 127) {
            return false;
        }
    }
    if (user.match(userPat) == null) {
        return false;
    }
    var IPArray = domain.match(ipDomainPat);
    if (IPArray != null) {
        for ( i = 1; i <= 4; i++) {
            if (IPArray[i] > 255) {
                return false;
            }
        }
        return true;
    }
    var atomPat = new RegExp("^" + atom + "$");
    var domArr = domain.split(".");
    var len = domArr.length;
    for (i = 0; i < len; i++) {
        if (domArr[i].search(atomPat) == -1) {
            return false;
        }
    }
    if (checkTLD && domArr[domArr.length - 1].length != 2 && domArr[domArr.length - 1].search(knownDomsPat) == -1) {
        return false;
    }
    if (len < 2) {
        return false;
    }
    return true;
};
loading4ShowVNC = function (url, loadingTable, targetDiv) {
	hasAppletLoaded = false;
    var ajax = initAjax();
    if (ajax === false || url === null) {
    	hasAppletLoaded = true;
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                xmlDoc=xmlDoc.trim();
                if(xmlDoc.indexOf("error=",0) > -1){
                	var error = xmlDoc.substr(xmlDoc.indexOf("error=")+"error=".length, xmlDoc.length);
                	hasAppletLoaded = true; //has loaded, but failed
                	jAlert(error,lingcloud.error.errorTip);
                	return;
                }
                var ltable = document.getElementById(loadingTable);
                if (ltable != null) {
                    ltable.style.display = "none";
                }
                var tdiv = document.getElementById(targetDiv);
                //alert(tdiv);
                tdiv.innerHTML = xmlDoc;
                hasAppletLoaded = true;
                $("#popup_cancel").trigger('click');
                //tdiv.style.display = "";
            } else {
            	hasAppletLoaded = true; //has loaded, but failed
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
    //to cancel the request and clear the applet
    setTimeout(function(){
    	if(!hasAppletLoaded){
    		ajax.abort();
    		var tdiv = document.getElementById(targetDiv);
    		tdiv.innerHTML = "";
    		$("#popup_cancel").trigger('click');
    		jAlert(lingcloud.error.connectTimeout, lingcloud.error.errorTip);
    	}
    }, 60000);
};

callbackForVNC = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("vncsettings");
    	var basePath = form.basePath.value;
    	var guid = form.guid.value;
    	var nodetype = form.nodetype.value;
    	var ss = form.vncss.value;
        //alert(basePath + ";" + guid + ";" + nodetype + ";" + ss);
        //var rdprfile = document.getElementById("rdprfile").value.trim();
        //to do
    	var ltable = document.getElementById("loadingTable");
        ltable.style.display = "";
        
        loading4ShowVNC(basePath + "JSP/ShowVNC.jsp?guid=" + guid + "&nodetype=" + nodetype + "&screensize=" + ss, "", "vnccontainer");
    	
    } else {
    }
};

function showVNC(basePath, guid, nodetype) {
    if(guid === ""){
    	jAlert(lingcloud.error.nodeIdNull, lingcloud.error.errorTip);
    	return;
    }
    if(nodetype === ""){
    	jAlert(lingcloud.error.nodeTypeNull, lingcloud.error.errorTip);
    	return;
    }
    
    var ajax = initAjax();
    if (ajax === false) {
    	hasAppletLoaded = true;
        return false;
    }
    ajax.open("GET", basePath + "JSP/ShowVNC.jsp?guid=" + guid + "&nodetype=" + nodetype , true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if (xmlDoc === null) {
                    return;
                }
                xmlDoc=xmlDoc.trim();
                if(xmlDoc.indexOf("error=",0) > -1){
                	var error = xmlDoc.substr(xmlDoc.indexOf("error=")+"error=".length, xmlDoc.length);
                	hasAppletLoaded = true; //has loaded, but failed
                	jAlert(error,lingcloud.error.errorTip);
                	return;
                }
                jShow(xmlDoc,"VNC " + lingcloud.Infrastructure.connection);
            } else {
            	hasAppletLoaded = true; //has loaded, but failed
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
    //to cancel the request and clear the applet
    setTimeout(function(){
    	if(!hasAppletLoaded){
    		ajax.abort();
    		var tdiv = document.getElementById(targetDiv);
    		tdiv.innerHTML = "";
    		$("#popup_cancel").trigger('click');
    		jAlert(lingcloud.error.connectTimeout, lingcloud.error.errorTip);
    	}
    }, 60000);
}


showSSH = function (basePath, guid, nodetype) {
	if(guid === ""){
    	jAlert(lingcloud.error.nodeIdNull, lingcloud.error.errorTip);
    	return;
    }
    if(nodetype === ""){
    	jAlert(lingcloud.error.nodeTypeNull, lingcloud.error.errorTip);
    	return;
    }
    var str = "<table id=\"loadingTable\" width=400px><tbody><tr><td><img src=" + basePath + "images/table_loading.gif />Loading...</td></tr></tbody></table>";
    jShow(str, "SSH " + lingcloud.Infrastructure.connection);
    loading4ShowVNC(basePath + "JSP/ShowSSH.jsp?guid=" + guid + "&&nodetype="+nodetype, "", "sshcontainer");
};

callbackForRDP = function (result) {
    if (result) {
		// ok.
        var form = document.getElementById("rdpsettings");
    	var basePath = form.basePath.value;
    	var guid = form.guid.value;
    	var nodetype = form.nodetype.value;
    	var ss = form.rdpss.value;
        //alert(basePath + ";" + guid + ";" + nodetype + ";" + ss);
        //var rdprfile = document.getElementById("rdprfile").value.trim();
        //to do
    	var ltable = document.getElementById("loadingTable");
        ltable.style.display = "";
        loading4ShowVNC(basePath + "JSP/ShowRDP.jsp?guid=" + guid + "&nodetype=" + nodetype + "&screensize=" + ss, "", "rdpcontainer");
    	
    	//$("#popup_cancel").trigger('click');
    } else {
		// cancel.
        // alert("false!");
    }
};

showRDP = function (basePath, guid, nodetype) {
	if(guid === ""){
    	jAlert(lingcloud.error.nodeIdNull, lingcloud.error.errorTip);
    	return;
    }
    if(nodetype === ""){
    	jAlert(lingcloud.error.nodeTypeNull, lingcloud.error.errorTip);
    	return;
    }
    var str = "<form id = \"rdpsettings\">";
    str += "<table ><tbody>";
    str += "<input type=\"hidden\" name=\"basePath\" value=\"" + basePath + "\" />";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + guid + "\" />";
    str += "<input type=\"hidden\" name=\"nodetype\" value=\"" + nodetype + "\" />";
    str += '<tr><td>' + lingcloud.Infrastructure.screenSize + ':&nbsp;</td><td><select id="rdpss" name="rdpss">' +
    		'<option value ="800x600" selected>800x600</option>' +
    		'<option value ="1024x768" >1024x768</option>' +
    		'<option value ="1280x1024">1280x1024</option>' +
    		'<option value ="full screen">' + lingcloud.Infrastructure.fullScreen + '</option>' +
    		'</select>*</td></tr>';
    str += "</tbody></table></form>\n";
    str += "<table id=\"loadingTable\" style=\"DISPLAY:none\" width=550px><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td></tr></tbody></table>";
    jSubmit(str, "RDP " + lingcloud.Infrastructure.connection, callbackForRDP);
};

showWrongStatus = function(status){
	jShow(status,lingcloud.error.errorTip);
}

function showMac(basePath, id, picId){
	var macShowId = document.getElementById(id);	
	var img = document.getElementById(picId);
	if(macShowId == null || img == null) return;
	
	if(macShowId.style.display==''){
		macShowId.style.display='none';
		img.src = basePath + '/images/allopen.png';
		img.title = lingcloud.Infrastructure.clusterOp.unfoldMacInfo;
	}else{
		macShowId.style.display='';
		img.src = basePath + '/images/allclose.png';
		img.title = lingcloud.Infrastructure.clusterOp.foldMacInfo;
	}
	
	
		
}

function uniencode(text) 
{
	alert(text);
	text = escape(text.toString()).replace(/\+/g, "%2B"); 
	var matches = text.match(/(%([0-9A-F]{2}))/gi); 
	if (matches) 
	{ 
		for (var matchid = 0; matchid < matches.length; matchid++) 
		{ 
			var code = matches[matchid].substring(1,3); 
			if (parseInt(code, 16) >= 128) 
			{ 
				text = text.replace(matches[matchid], '%u00' + code); 
			} 
		} 
	} 
	text = text.replace('%25', '%u0025').replace(/(%u)(\w{4})/gi,"&#x$2;"); 
	alert(text);
	return text; 
} 