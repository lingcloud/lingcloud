/* 
 * @(#)vagevam.js 2009-10-6 
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

callbackForCreateCategory = function (result) {
    if (result) {
        var form = document.getElementById("newApplianceCategoryForm");
        if (form.categoryname.value.trim() === "") {
            alert(lingcloud.error.cateNotNull);
            return;
        }
        
        if (form.categoryname.value.trim().length > 20) {
        	alert(lingcloud.error.cateTooLong);
            return;
        }
        
        form.submit();
    } else {

    }
};
showDialogForCreateCategory = function (basePath) {
    var str = "<form id=\"newApplianceCategoryForm\" action=\"" + basePath + "newApplianceCategory.do\" method=\"post\">";
    str += "<table id=\"newApplianceCategoryTable\"><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<tr><td>"+lingcloud.Appliance.newCate.name +":&nbsp;</td><td><input type=\"text\" name=\"categoryname\" /></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Appliance.newCate.title, callbackForCreateCategory);
};

callbackForDeleteCategory = function (result) {
    if (result) {
        var form = document.getElementById("deleteApplianceCategoryForm");
        form.submit();
    } else {

    }
};

showDialogForDeleteCategory = function (basePath, categoryGuid) {
    var str = "<form id=\"deleteApplianceCategoryForm\" action=\"" + basePath + "deleteApplianceCategory.do\" method=\"post\">";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + categoryGuid + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<table width=\"400px\"><tbody><tr><td>&nbsp;&nbsp;"+lingcloud.Appliance.delCate.confirmTip+"</td></tr></tbody></table>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Appliance.delCate.title, callbackForDeleteCategory);
};

loadForCreateAppliance = function (basePath, loadingTable, targetTable) {
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListApplianceCategory.jsp";
    if (ajax === false || url === null) {
        return false;
    }
    var form = document.getElementById("newVirtualApplianceForm");
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_category = form.category;
                select_category.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText;
                    var option;
                    if (xValue == 0) {
                    	xText = lingcloud.tip.nocategory;
                    	option = new Option(xText, xValue);
                    }else{
                    	xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    	option = new Option(xText, xValue);
                    }                   
                    try {
                        select_category.options[i] = option;
                    }
                    catch (e) {
                    }
                };
                
                if (select_category.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_category.options[0] = option;
                }

            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
                return;
            }
        }
    };
    ajax.send(null);
    
    var ajax2 = initAjax();
    var url2 = basePath + "JSP/AjaxListOS.jsp";
    if (ajax2 === false || url2 === null) {
        return false;
    }
    ajax2.open("GET", url2, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_os = form.os;
                select_os.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_os.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_os.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_os.options[0] = option;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
                return;
            }
        }
    };
    ajax2.send(null);
    
    var ajax3 = initAjax();
    var url3 = basePath + "JSP/AjaxListFile.jsp?type=disk";
    if (ajax3 === false || url3 === null) {
        return false;
    }
    ajax3.open("GET", url3, true);
    ajax3.onreadystatechange = function () {
        if (ajax3.readyState == 4) {
            if (ajax3.status == 200) {
                var xmlDoc = ajax3.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_loc = document.getElementById("location");
                select_loc.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_loc.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_loc.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_loc.options[0] = option;
                }
                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
                $.alerts._reposition();
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax3.send(null);

};
callbackForCreateAppliance = function (result) {
    if (result) {
        var form = document.getElementById("newVirtualApplianceForm");
        if (form.name.value.trim() === "") {
            alert(lingcloud.error.appNameNotNull);
            return;
        }
        
        if (form.name.value.trim().length > 20) {
        	alert(lingcloud.error.appNameTooLong);
            return;
        }
        
        if (form.location.value.trim() === "" || form.location.value.trim() === "-1") {
            alert(lingcloud.error.appDiskNotNull);
            return;
        }
        if (form.category.value.trim() === "" || form.category.value.trim() === "-1") {
            alert(lingcloud.error.appCateNotNull);
            return;
        }

        if (form.os.value.trim() === "" || form.os.value.trim() === "-1") {
            alert(lingcloud.error.appOsNotNull);
            return;
        }
        if (form.osversion.value.trim().length > 20) {
        	alert(lingcloud.error.appOsVersionTooLong);
            return;
        }
        
        if (form.app.value.trim().length > 100) {
        	alert(lingcloud.error.appApplicationTooLong);
            return;
        }
        
        var selectAccessWays = 0;
        for (var i=0; i < form.accessway.length;i+=1) {
        	if (form.accessway[i].checked == true) {
        		selectAccessWays+=1;
        	}
        }
        if (selectAccessWays === 0) {
            alert(lingcloud.error.appAccessNotNull);
            return;
        }
        if (form.cpuamount.value.trim() === "") {
            alert(lingcloud.error.appCpuNotNull);
            return;
        }
        if (form.memsize.value.trim() === "") {
            alert(lingcloud.error.appMemNotNull);
            return;
        }
        if (form.language.value.trim() === "") {
            alert(lingcloud.error.appLangNotNull);
            return;
        }
        if (form.language.value.trim().length > 100) {
        	alert(lingcloud.error.appLangTooLong);
            return;
        }
        if (form.loginstyle.value.trim() === "") {
            alert(lingcloud.error.appLoginStyleNotNull);
            return;
        }
        if ( form.loginstyle.value.trim() === "User and Password" ) {
        	if (form.username.value.trim() === "") {
            	alert(lingcloud.error.appUserNotNull);
            	return;
        	}
        	if (form.username.value.trim().length > 80) {
            	alert(lingcloud.error.appUserTooLong);
                return;
            }
        	if (form.password.value.trim() === "") {
            	alert(lingcloud.error.appPassNotNull);
            	return;
        	}
        	if (form.password.value.trim().length > 80) {
            	alert(lingcloud.error.appPassTooLong);
                return;
            }
        }
        
        if (form.description.value.trim().length > 500) {
            alert(lingcloud.error.appDescTooLong);
            return;
        }
        form.submit();
    } else {

    }
};

checkDiscType = function(discType) {
	if (discType == null) alert('null');
	var os = document.getElementById('osBody');
	var app = document.getElementById('appBody');
	
	if (discType.value === 'Operating System') {
		os.style.display = "";
		app.style.display = "none";
	} else {
		os.style.display = "none";
		app.style.display = "";
	}
}

checkLoginStyle = function(loginStyle,name) {
	if (loginStyle==null ) alert('null');
	var userpass = document.getElementById(name);
	if (loginStyle.value === 'Global User' ) {
		userpass.style.display = "none";
	} else {
		userpass.style.display = "";
	}
};

addValue = function(valueid, valuesid) {
	var value = document.getElementById(valueid);
	var values = document.getElementById(valuesid);
	
	if (value.value.trim() === "") {
		return;
	}
	
	values.value = values.value.trim();
	if (values.value === "") {
		values.value = values.value + value.value.trim();
	} else {
		values.value = values.value + '|' + value.value.trim();
	}
	value.value = "";
}

addValueFromSelect = function(valueid, valuesid) {
	var valueSelect = document.getElementById(valueid);
	var values = document.getElementById(valuesid);
	var optionSelected = valueSelect.options[valueSelect.selectedIndex];

	if (optionSelected.text.trim() === "") {
		return;
	}
	
	values.value = values.value.trim();
	if (values.value === "") {
		values.value = values.value + optionSelected.text.trim();
	} else {
		values.value = values.value + '|' + optionSelected.text.trim();
	}
	//value.text = "";
}

clearValues = function(valuesid) {
	var valuesid = document.getElementById(valuesid);
	valuesid.value = "";
}

showDialogForCreateAppliance = function (basePath) {
    var str = "<form id=\"newVirtualApplianceForm\" action=\"" + basePath + "newVirtualAppliance.do\" method=\"post\">";
    str += "<table id=\"loadingTable4CreateVA\" width='400px'><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;" + lingcloud.Appliance.loading + "</td></tr></tbody></table>";
    str += "<script>loadForCreateAppliance('" + basePath + "', 'loadingTable4CreateVA', 'newVirtualApplianceTable')</script>";
    str += "<table id=\"newVirtualApplianceTable\"  style=\"DISPLAY:none\"><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<tr><td>" +lingcloud.Appliance.newAppliance.name+ ":&nbsp;</td><td><input type=\"text\" name=\"name\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.disk+":&nbsp;</td><td><select id='location' name=\"location\"></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.newAppliance.cate+":&nbsp;</td><td><select id=\"category\" name=\"category\"></select>&nbsp;&nbsp;</td></tr>";
//    str += "<tr><td>\u683C\u5F0F:&nbsp;</td><td><select name=\"format\"><option value=\"raw\">raw</option><option value=\"qcow\">qcow</option><option value=\"vmdk\">vmdk</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.os+":&nbsp;</td><td><select id=\"os\" name=\"os\"></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.osversion+":&nbsp;</td><td><input type=\"text\" name=\"osversion\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.application+":&nbsp;</td><td><input type=\"text\" id=\"oneapp\" /><a href='javascript:addValueFromSelect(\"oneapp\",\"apps\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='apps' name='app'><a href='javascript:clearValues(\"apps\");'>" + lingcloud.tip.clear + " </a></td></tr>";
//    str += "<tr><td>\u542F\u52A8\u52A0\u8F7D\u7A0B\u5E8F:&nbsp;</td><td><select name=\"loader\"><option value=\"hvm\">hvm</option><option value=\"pygrub\">pygrub</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.access+":</td><td><input type='checkbox' name='accessway' value='SSH'/>SSH<input type='checkbox' name='accessway' value='RDP'/>RDP<input type='checkbox' name='accessway' value='VNC'/>VNC</td>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.cpu+":&nbsp;</td><td><select name=\"cpuamount\"><option value=\"1\">1</option><option value=\"2\">2</option><option value=\"3\">3</option><option value=\"4\">4</option><option value=\"5\">5</option><option value=\"6\">6</option><option value=\"7\">7</option><option value=\"8\">8</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.mem+":</td><td><select name='memsize'><option value='128'>128MB</option><option value='256'>256MB</option><option value='512'>512MB</option><option value='1024'>1GB</option><option value='2048'>2GB</option><option value='4092'>4GB</option></select></td>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.lang+":&nbsp;</td><td><select id=\"lang\"><option>Chinese(simplified)</option><option>Chinese(traditional)</option><option>English</option><option>French</option><option>German</option><option>Italian</option><option>Japanese</option><option>Korean</option><option>Russian</option><option>Spanish</option><option>Other</option></select><a href='javascript:addValueFromSelect(\"lang\",\"langs\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='langs' name='language'><a href='javascript:clearValues(\"langs\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "<tr style=\"DISPLAY:none\"><td>"+lingcloud.Appliance.newAppliance.loginStyle+":&nbsp;</td><td><select id='loginstyle' name=\"loginstyle\" onChange=\"checkLoginStyle(this,'userpass')\" ><option value=\"Global User\">Global User</option><option value=\"User and Password\">User and Password</option></select></td></tr>";
    str += "<tbody id='userpass' style='DISPLAY:none'>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.user+":&nbsp;</td><td><input type=\"text\" name=\"username\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newAppliance.pass+":&nbsp;</td><td><input type=\"text\" name=\"password\" /></td></tr>";
    str += "</tbody>";
    str += "<tr><td>"+lingcloud.Appliance.desc+":&nbsp;</td><td><textarea name=\"description\" cols=\"25\" rows=\"6\" /></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Appliance.newAppliance.title , callbackForCreateAppliance);
};

callbackForModifyAppliance = function (result) {
    if (result) {
        var form = document.getElementById("modifyVirtualApplianceForm");
        if (form.name.value.trim() === "") {
            alert(lingcloud.error.appNameNotNull);
            return;
        }
       
        if (form.name.value.trim().length > 20) {
        	alert(lingcloud.error.appNameTooLong);
            return;
        }
        
        if (form.category.value.trim() === "" || form.category.value.trim() === "-1") {
            alert(lingcloud.error.appCateNotNul);
            return;
        }

        if (form.os.value.trim() === "" || form.os.value.trim() === "-1") {
            alert(lingcloud.error.appOsNotNull);
            return;
        }
        
        if (form.osversion.value.trim().length > 20) {
        	alert(lingcloud.error.appOsVersionTooLong);
            return;
        }
        
        if (form.app.value.trim().length > 100) {
        	alert(lingcloud.error.appApplicationTooLong);
            return;
        }

        var selectAccessWays = 0;
        for (var i=0; i < form.accessway.length;i+=1) {
        	if (form.accessway[i].checked == true) {
        		selectAccessWays += 1;
        	}
        }
        if (selectAccessWays === 0) {
            alert(lingcloud.error.appAccessNotNull);
            return;
        }
        if (form.cpuamount.value.trim() === "") {
            alert(lingcloud.error.appCpuNotNull);
            return;
        }
        if (form.memsize.value.trim() === "") {
            alert(lingcloud.error.appMemNotNull);
            return;
        }
        if (form.language.value.trim() === "") {
            alert(lingcloud.error.appLangNotNull);
            return;
        }
        if (form.language.value.trim().length > 100) {
        	alert(lingcloud.error.appLangTooLong);
            return;
        }
        if (form.loginstyle.value.trim() === "") {
            alert(lingcloud.error.appLoginStyleNotNull);
            return;
        }
        if ( form.loginstyle.value.trim() === "User and Password" ) {
        	if (form.username.value.trim() === "") {
            	alert(lingcloud.error.appUserNotNull);
            	return;
        	}
        	if (form.username.value.trim().length > 80) {
            	alert(lingcloud.error.appUserTooLong);
                return;
            }
        	if (form.password.value.trim() === "") {
            	alert(lingcloud.error.appPassNotNull);
            	return;
        	}
        	if (form.password.value.trim().length > 80) {
            	alert(lingcloud.error.appPassTooLong);
                return;
            }
        }
        
        if (form.description.value.trim().length > 500) {
            alert(lingcloud.error.appDescTooLong);
            return;
        }
        form.submit();
    } else {

    }
};

loadForModifyAppliance = function (basePath, loadingTable, targetTable, guid) {
	var form = document.getElementById("modifyVirtualApplianceForm");
	
	var ajax = initAjax();
    var url = basePath + "JSP/AjaxListApplianceCategory.jsp";
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
                var select_category = form.category;
                select_category.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_category.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_category.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_category.options[0] = option;
                }

            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
                return;
            }
        }
    };
    ajax.send(null);
    
	var ajax2 = initAjax();
    var url2 = basePath + "JSP/AjaxListOS.jsp";
    if (ajax2 === false || url2 === null) {
        return false;
    }
    ajax2.open("GET", url2, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_os = document.getElementById("os");
                select_os.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_os.options[i] = option;
                    }
                    catch (e) {
                    }
                }

                if (select_os.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_os.options[0] = option;
                }
                
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
                return;
            }
        }
    };
    ajax2.send(null);
    
    $.ajax({
	    url: basePath + "JSP/AjaxGetVirtualAppliance.jsp",
	    type: 'GET',
	    dataType: 'text',
	    data: "guid=" + guid,
	    error: function(MLHttpRequest, textStatus, errorThrown){
	        alert(lingcloud.error.responseNotFound + textStatus);
	    },
	    success: function(data, textStatus, XMLHttpRequest)
	    {	
	    	if(data == ''){
	    		return;
	    	}
			var result = eval('(' + data + ')');
		
			var items = result.datas;
			var itemNum = items.length, i = 0;	
			
			if(itemNum>0)
			{
				var name = items[i].name;
				var category = items[i].category;
				var guid = items[i].guid;
				var os = items[i].os;
				var osversion = items[i].osversion
				var app = items[i].app;
				var format = items[i].format;
				var accessWay = items[i].accessWay;
				var cpu = items[i].cpu;
				var memery = items[i].memery;
				var language = items[i].language;
				var loginStyle = items[i].loginStyle;
				
				var description = items[i].description;
				var username = items[i].username;
				var password = items[i].password;
				
				document.getElementById("name").value = name;
				document.getElementById("category").value = category;
				document.getElementById("langs").value = language;
				document.getElementById("apps").value = app;
				document.getElementById("description").value = description;
				document.getElementById("cpuamount").value = cpu;
				document.getElementById("memsize").value = memery;
				document.getElementById("os").value = os;
				document.getElementById("osversion").value = osversion;
				document.getElementById("loginstyle").value = loginStyle;
				if (loginStyle === "User and Password") {
					document.getElementById("username").value = username;
					document.getElementById("password").value = password;
				}
				
				checkLoginStyle(document.getElementById("loginstyle"),'userpass');
				
				var accessWays = accessWay.split("|");
				for(var j=0;j<accessWays.length;j+=1) {
					var e = document.getElementById(accessWays[j]);
					if (e!=null) {
						e.checked = true;
					}
				}
				
				var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "";
                $.alerts._reposition();
			}  
	    }
	});
}

showDialogForModifyAppliance = function (basePath, guid) {
    var str = "<form id=\"modifyVirtualApplianceForm\" action=\"" + basePath + "modifyVirtualAppliance.do\" method=\"post\">";
    str += "<table id=\"loadingTable4ModifyVA\" width='400px'><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadForModifyAppliance('" + basePath + "', 'loadingTable4ModifyVA', 'modifyVirtualApplianceTable', '" + guid + "')</script>";
    str += "<table id=\"modifyVirtualApplianceTable\"  style=\"DISPLAY:none\"><tbody>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualAppliance.jsp\" />";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + guid + "\" />";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.name+":&nbsp;</td><td><input type=\"text\" name=\"name\" id='name'/></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.cate+":&nbsp;</td><td><select id=\"category\" name=\"category\" id='category'></select>&nbsp;&nbsp;</td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.os+":&nbsp;</td><td><select id=\"os\" name=\"os\"></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.osversion+":&nbsp;</td><td><input type=\"text\" name=\"osversion\" id='osversion'/></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.application+":&nbsp;</td><td><input type=\"text\" id=\"oneapp\" /><a href='javascript:addValueFromSelect(\"oneapp\",\"apps\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='apps' name='app'><a href='javascript:clearValues(\"apps\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.access+":</td><td><input type='checkbox' name='accessway' value='SSH' id='SSH'/>SSH<input type='checkbox' name='accessway' value='RDP' id='RDP'/>RDP<input type='checkbox' name='accessway' value='VNC' id='VNC'/>VNC</td>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.cpu+":&nbsp;</td><td><select name=\"cpuamount\" id='cpuamount'><option value=\"1\">1</option><option value=\"2\">2</option><option value=\"3\">3</option><option value=\"4\">4</option><option value=\"5\">5</option><option value=\"6\">6</option><option value=\"7\">7</option><option value=\"8\">8</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.mem+":</td><td><select name='memsize' id='memsize'><option value='128'>128MB</option><option value='256'>256MB</option><option value='512'>512MB</option><option value='1024'>1GB</option><option value='2048'>2GB</option><option value='4092'>4GB</option></select></td>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.lang+":&nbsp;</td><td><select id=\"lang\"><option>Chinese(simplified)</option><option>Chinese(traditional)</option><option>English</option><option>French</option><option>German</option><option>Italian</option><option>Japanese</option><option>Korean</option><option>Russian</option><option>Spanish</option><option>Other</option></select><a href='javascript:addValueFromSelect(\"lang\",\"langs\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='langs' name='language'><a href='javascript:clearValues(\"langs\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "<tr style=\"display:none\" ><td>"+lingcloud.Appliance.modifyAppliance.loginStyle+":&nbsp;</td><td><select id='loginstyle' name=\"loginstyle\" onChange=\"checkLoginStyle(this,'userpass')\" ><option value=\"Global User\">Global User</option><option value=\"User and Password\">User and Password</option></select></td></tr>";
    str += "<tbody id='userpass' style=\"display:none\" >";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.user+":&nbsp;</td><td><input type=\"text\" name=\"username\" id='username'/></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyAppliance.pass+":&nbsp;</td><td><input type=\"text\" name=\"password\" id='password'/></td></tr>";
    str += "</tbody>";
    str += "<tr><td>"+lingcloud.Appliance.desc+":&nbsp;</td><td><textarea name=\"description\" cols=\"25\" rows=\"6\" id='description'/></td></tr>";
    str += "</tbody></table></form>\n";
    jSubmit(str, lingcloud.Appliance.modifyAppliance.title, callbackForModifyAppliance);
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
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_vaguid = document.getElementById("vaguid");
                select_vaguid.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
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
                
                if (select_vaguid.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
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

callbackForCreateVirtualDisc = function (result) {
    if (result) {
        var form = document.getElementById("newVirtualDiscForm");
        if (form.discname.value.trim() === "") {
        	alert(lingcloud.error.discNameNotNull);
            return;
        }
        
        if (form.discname.value.trim().length > 20) {
        	alert(lingcloud.error.discNameTooLong);
            return;
        }
        
        if (form.location.value.trim() === "" || form.location.value.trim() === "-1") {
        	alert(lingcloud.error.discLocNotNull);
            return;
        }
        if (form.format.value.trim() === "") {
            alert(lingcloud.error.discFormatNotNull);
            return;
        }
        if (form.type.value.trim() === "") {
            alert(lingcloud.error.discTypeNotNull);
            return;
        }
        
        if (form.type.value.trim() === "Operating System") {
        	if (form.os.value.trim() === "" || form.os.value.trim() === "-1") {
        		alert(lingcloud.error.discOsNotNull);
        	}
        	if (form.osversion.value.trim().length > 20) {
            	alert(lingcloud.error.discOsVersionTooLong);
                return;
            }
        } 
        
        form.submit();
    } else {
    	
    }
};

loadForCreateVirtualdisc = function (basePath, loadingTable, targetTable) {
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListFile.jsp?type=disc&format=iso";
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
                var select_loc = document.getElementById("location");
                select_loc.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_loc.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_loc.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_loc.options[0] = option;
                }
                
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
                return;
            }
        }
    };
    ajax.send(null);
    
    var ajax2 = initAjax();
    var url2 = basePath + "JSP/AjaxListOS.jsp";
    if (ajax2 === false || url2 === null) {
        return false;
    }
    ajax2.open("GET", url2, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_os = document.getElementById("os");
                select_os.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_os.options[i] = option;
                    }
                    catch (e) {
                    }
                }

                if (select_os.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_os.options[0] = option;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
                return;
            }
        }
    };
    ajax2.send(null);
    
    var ltable = document.getElementById(loadingTable);
    var ttable = document.getElementById(targetTable);
    ltable.style.display = "none";
    ttable.style.display = "";
};

showDialogForCreateVirtualDisc = function (basePath) {
    var str = "<form id=\"newVirtualDiscForm\" action=\"" + basePath + "newVirtualDisc.do\" method=\"post\">";
    str += "<table id=\"loadingTable4CreateVirtualDisc\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadForCreateVirtualdisc('" + basePath + "', 'loadingTable4CreateVirtualDisc', 'newVirtualDiscTable')</script>";
    str += "<table id=\"newVirtualDiscTable\" style='display:none; width:400px'>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualDisc.jsp\" />";
    str += "<tr><td width='40%'>"+lingcloud.Appliance.newVirtualDisc.name+":&nbsp;</td><td><input type=\"text\" name=\"discname\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.location+":&nbsp;</td><td><select id='location' name=\"location\"></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.format+":&nbsp;</td><td><select name=\"format\"><option value=\"iso\">iso</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.type+":&nbsp;</td><td><select name=\"type\" OnChange=\"checkDiscType(this);\"><option value=\"Operating System\">"+lingcloud.Appliance.newVirtualDisc.os+"</option><option value=\"Application\">"+lingcloud.Appliance.newVirtualDisc.application+"</option></select></td></tr>";
    str += "<tbody id='osBody'>";
    str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.os+": </td><td><select id='os' name='os'></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.osversion+": </td><td><input type='text' name='osversion'/></td></tr>";
	str += "</tbody>";
	str += "<tbody id='appBody' style='DISPLAY:none'>";
    str += "<tr><td>"+lingcloud.Appliance.newVirtualDisc.application+":&nbsp;</td><td><input type=\"text\" id=\"oneapp\" /><a href='javascript:addValueFromSelect(\"oneapp\",\"apps\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='apps' name='app'><a href='javascript:clearValues(\"apps\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "</tbody>";
    str += "</table></form>\n";
    jSubmit(str, lingcloud.Appliance.newVirtualDisc.title, callbackForCreateVirtualDisc);
};

callbackForModifyVirtualDisc = function (result) {
    if (result) {
        var form = document.getElementById("modifyVirtualDiscForm");
        if (form.discname.value.trim() === "") {
        	alert(lingcloud.error.discNameNotNull);
            return;
        }
        
        if (form.discname.value.trim().length > 20) {
        	alert(lingcloud.error.discNameTooLong);
            return;
        }
        
        if (form.format.value.trim() === "") {
            alert(lingcloud.error.discFormatNotNull);
            return;
        }
        if (form.type.value.trim() === "") {
            alert(lingcloud.error.discTypeNotNull);
            return;
        }
        
        if (form.type.value.trim() === "Operating System") {
        	if (form.os.value.trim() === "" || form.os.value.trim() === "-1") {
        		alert(lingcloud.error.discOsNotNull);
        	}
        	
        	if (form.osversion.value.trim().length > 20) {
            	alert(lingcloud.error.discOsVersionTooLong);
                return;
            }
        } 
        
        form.submit();
    } else {

    }
};

loadForModifyVirtualdisc = function (basePath, loadingTable, targetTable, guid) {
	
	var ajax2 = initAjax();
    var url2 = basePath + "JSP/AjaxListOS.jsp";
    if (ajax2 === false || url2 === null) {
        return false;
    }
    ajax2.open("GET", url2, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_os = document.getElementById("os");
                select_os.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_os.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_os.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_os.options[0] = option;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
                return;
            }
        }
    };
    ajax2.send(null);
    
    $.ajax({
	    url: basePath + "JSP/AjaxGetVirtualDisc.jsp",
	    type: 'GET',
	    dataType: 'text',
	    data: "guid=" + guid,
	    error: function(MLHttpRequest, textStatus, errorThrown){
	        alert(lingcloud.error.responseNotFound + textStatus);
	    },
	    success: function(data, textStatus, XMLHttpRequest)
	    {	
	    	if(data == ''){
	    		return;
	    	}
			var result = eval('(' + data + ')');
		
			var items = result.datas;
			var itemNum = items.length, i = 0;	
			
			if(itemNum>0)
			{
				var name = items[i].name;
				var guid = items[i].guid;
				var os = items[i].os;
				var osversion = items[i].osversion
				var app = items[i].app;
				var disktype = items[i].diskType;
				var format = items[i].format;
			
				document.getElementById("discname").value=name;
				var eType = document.getElementById("type");
				var eFormat = document.getElementById("format");
				
				var i;
				
				for (i=0;i<eType.options.length; i+=1) {
					if (eType.options[i].value === disktype) {
						eType.options[i].selected = true;
						eType.value = disktype;
						break;
					}
				}
 				
				for (i=0;i<eFormat.options.length; i+=1) {
					if (eFormat.options[i].value === format) {
						eFormat.options[i].selected = true;
						break;
					}
				}
				
				var osBody = document.getElementById("osBody");
			    var appBody = document.getElementById("appBody");
			    
				if (disktype === "Application") {
					document.getElementById("apps").value=app;
					osBody.style.display="none";
					appBody.style.display="";
				} else {
					document.getElementById("osversion").value=osversion;
					var eOS = document.getElementById("os");
					osBody.style.display="";
					appBody.style.display="none";
					for (i=0;i<eOS.options.length; i+=1) {
						if (eOS.options[i].value === os) {
							eOS.options[i].selected = true;
							break;
						}
					}
				}
				
			}  
	    }
	});
	
	var ltable = document.getElementById(loadingTable);
    var ttable = document.getElementById(targetTable);
    ltable.style.display = "none";
    ttable.style.display = "";
}

showDialogForModifyVirtualDisc = function (basePath, guid) {
    var str = "<form id=\"modifyVirtualDiscForm\" action=\"" + basePath + "modifyVirtualDisc.do\" method=\"post\">";
    str += "<table id=\"loadingTable4ModifyVirtualDisc\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadForModifyVirtualdisc('" + basePath + "', 'loadingTable4ModifyVirtualDisc', 'modifyVirtualDiscTable' ,'" + guid + "')</script>";
    str += "<table id=\"modifyVirtualDiscTable\" style='display:none; width:400px'>";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualDisc.jsp\" />";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + guid + "\" />";
    str += "<tr><td width='30%'>"+lingcloud.Appliance.modifyVirtualDisc.name+":&nbsp;</td><td><input type=\"text\" name=\"discname\" id=\"discname\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyVirtualDisc.format+":&nbsp;</td><td><select name=\"format\" id=\"format\"><option value=\"iso\">iso</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.modifyVirtualDisc.type+":&nbsp;</td><td><select name=\"type\" id=\"type\" OnChange=\"checkDiscType(this);\"><option value=\"Operating System\">"+lingcloud.Appliance.modifyVirtualDisc.os+"</option><option value=\"Application\">"+lingcloud.Appliance.modifyVirtualDisc.application+"</option></select></td></tr>";
    str += "<tbody id='osBody'>";
    str += "<tr><td>"+lingcloud.Appliance.modifyVirtualDisc.os+": </td><td><select id='os' name='os'></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.modifyVirtualDisc.osversion+": </td><td><input type='text' name='osversion' id=\"osversion\"/></td></tr>";
	str += "</tbody>";
	str += "<tbody id='appBody'>";
    str += "<tr><td>"+lingcloud.Appliance.modifyVirtualDisc.application+":&nbsp;</td><td><input type=\"text\" id=\"oneapp\" /><a href='javascript:addValueFromSelect(\"oneapp\",\"apps\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='apps' name='app'><a href='javascript:clearValues(\"apps\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "</tbody>";
    str += "</table></form>\n";
    jSubmit(str, lingcloud.Appliance.modifyVirtualDisc.title, callbackForModifyVirtualDisc);
};

callbackForDeleteVirtualDisc = function (result) {
    if (result) {
        var form = document.getElementById("deleteVirtualDiscForm");
        form.submit();
    } else {
    	
    }
};

showDialogForDeleteVirtualDisc = function (basePath, discGuid) {
    var str = "<form id=\"deleteVirtualDiscForm\" action=\"" + basePath + "deleteVirtualDisc.do\" method=\"post\">";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + discGuid + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewVirtualDisc.jsp\" />";
    str += "<table width=\"400px\"><tbody><tr><td>&nbsp;&nbsp;"+lingcloud.Appliance.deleteVirtualDisc.confirmTip+"</td></tr></tbody></table>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Appliance.deleteVirtualDisc.title, callbackForDeleteVirtualDisc);
};

callbackForMakeVirtualAppliance = function (result) {
    if (result) {
        var tab1 = document.getElementById("divTab1");
        var tab2 = document.getElementById("divTab2");
        if (tab1.className === "divShow") {
        	var form = document.getElementById("makeNewVirtualApplianceForm");
        	
	        if (form.appname.value.trim() === "") {
	            alert(lingcloud.error.appNameNotNull);
	            return;
	        }
	        
	        if (form.appname.value.trim().length > 20) {
	        	alert(lingcloud.error.appNameTooLong);
	            return;
	        }
	       
	        if (form.vcd.value.trim() === "") {
	            alert(lingcloud.error.appVcdNotNull);
	            return;
	        }
	        if (form.os.value.trim() === "" || form.os.value.trim() === "-1") {
	            alert(lingcloud.error.appOsNotNull);
	            return;
	        }
	        
	        if (form.osversion.value.trim().length > 20) {
	        	alert(lingcloud.error.appOsVersionTooLong);
	            return;
	        }
	        
	        if (form.memsize.value.trim() === "") {
	            alert(lingcloud.error.appMemNotNull);
	            return;
	        }
	        if ( form.diskcapacity.value.trim() === "") {
	            alert(lingcloud.error.appDiskCapNull);
	            return;
	        }
	        var cap = parseInt(form.diskcapacity.value.trim());
	        if (cap != NaN && (cap < 1 || cap > 128)) {
	        	alert(lingcloud.error.appDiskCapLimt);
	            return;
	        }
	        
	        if (form.cpuamount.value.trim() === "") {
	            alert(lingcloud.error.appCpuNotNull);
	            return;
	        }
	        
	        form.submit();
        }
        else if(tab2.className === "divShow") {
        	var form = document.getElementById("useExistedVirtualApplianceForm");
	    	if (form.appliance.value.trim() === "") {
	            alert(lingcloud.error.appIdNotNull);
	            return;
	        }
	        if (form.appname.value.trim() === "") {
	            alert(lingcloud.error.appNameNotNull);
	            return;
	        }
	        
	        if (form.appname.value.trim().length > 20) {
	        	alert(lingcloud.error.appNameTooLong);
	            return;
	        }
	        
	        if (form.memsize.value.trim() === "") {
	            alert(lingcloud.error.appMemNotNull);
	            return;
	        }
	        
	        form.submit();
        } else {
        	var form = document.getElementById("modifyExistedVirtualApplianceForm");
	    	if (form.appliance.value.trim() === "") {
	            alert(lingcloud.error.appIDNotNull);
	            return;
	        }
	        
	        if (form.memsize.value.trim() === "") {
	            alert(lingcloud.error.appMemNotNull);
	            return;
	        }
	        
	        form.submit();
        }

    } else {

    }
};

loadForMakeAppliance = function (basePath) {
    
    var formNew = document.getElementById("makeNewVirtualApplianceForm");
    var formExisted = document.getElementById("useExistedVirtualApplianceForm");
    var formModify = document.getElementById("modifyExistedVirtualApplianceForm");
    
    var ajax2 = initAjax();
    var url2 = basePath + "JSP/AjaxListOS.jsp";
    if (ajax2 === false || url2 === null) {
        return false;
    }
    ajax2.open("GET", url2, true);
    ajax2.onreadystatechange = function () {
        if (ajax2.readyState == 4) {
            if (ajax2.status == 200) {
                var xmlDoc = ajax2.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_os = formNew.os;
                select_os.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_os.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_os.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_os.options[0] = option;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax2.statusText);
                return;
            }
        }
    };
    ajax2.send(null);
    
    var ajax3 = initAjax();
    var url3 = basePath + "JSP/AjaxListVirtualCD.jsp";
    if (ajax3 === false || url3 === null) {
        return false;
    }
    ajax3.open("GET", url3, true);
    ajax3.onreadystatechange = function () {
        if (ajax3.readyState == 4) {
            if (ajax3.status == 200) {
                var xmlDoc = ajax3.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_vcd = formNew.vcd;
                select_vcd.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_vcd.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_vcd.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_vcd.options[0] = option;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax3.statusText);
                return;
            }
        }
    };
    ajax3.send(null);
	
	var ajax4 = initAjax();
    var url4 = basePath + "JSP/AjaxListVirtualAppliance.jsp";
    if (ajax4 === false || url4 === null) {
        return false;
    }
    ajax4.open("GET", url4, true);
    ajax4.onreadystatechange = function () {
        if (ajax4.readyState == 4) {
            if (ajax4.status == 200) {
                var xmlDoc = ajax4.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_va = formExisted.appliance;
                var select_va2 = formModify.appliance;
                select_va.options.length = 0;
                select_va2.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    var option2 = new Option(xText, xValue);
                    try {
                        select_va.options[i] = option;
                        select_va2.options[i] = option2;
                    }
                    catch (e) {
                    }
                }
                
                if (select_va.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    var option2 = new Option(lingcloud.error.noResult, "-1");
                    select_va.options[0] = option;
                    select_va2.options[0] = option2;
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax4.statusText);
                return;
            }
        }
    };
    ajax4.send(null);
    
	var divTab=document.getElementById('divTab');
	var loadingTable=document.getElementById('loadingTable4MakeVA');
	var divTab1=document.getElementById('divTab1');
	divTab.style.display='';
	divTab1.style.display='';
	loadingTable.style.display='none';
	
	
};

showDialogForMakeVirtualAppliance = function (basePath) {

    var str = "<div id=divTab class='divTab' style='display:none;'><ul><li><a id='link1' href='javascript:changeTab(3,1);' class = 'active'><span>"+ lingcloud.Appliance.makeAppliance.newApp +"</span></a></li><li><a id='link2' href='javascript:changeTab(3,2);' ><span>"+lingcloud.Appliance.makeAppliance.baseApp+"</span></a></li><li><a id='link3' href='javascript:changeTab(3,3);' ><span>"+lingcloud.Appliance.makeAppliance.modifyApp+"</span></a></li></ul></div>";
    str += "<table id=\"loadingTable4MakeVA\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadForMakeAppliance('" + basePath + "')</script>";
	str += "<div id='divTab1' class='divShow' style='display:none;'>";
	str += "<form id='makeNewVirtualApplianceForm' action='" + basePath + "makeNewVirtualAppliance.do' method='post'>";
	str += "<table>";
	str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.name+": </td><td><input type='text' name='appname'/></td></tr>";
//	str += "<tr><td>\u683C\u5F0F:&nbsp;</td><td><select name=\"format\"><option value=\"raw\">raw</option><option value=\"qcow\">qcow</option><option value=\"vmdk\">vmdk</option></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.vcd+": </td><td><select id='vcd' name='vcd'></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.os+": </td><td><select id='os' name='os'></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.osversion+": </td><td><input type='text' name='osversion'/></td></tr>";
//	str += "<tr><td>\u542F\u52A8\u52A0\u8F7D\u7A0B\u5E8F: </td><td><select name='loader'><option value='hvm'>hvm</option><option value='pygrub'>pygrub</option></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.mem+":</td><td><select name='memsize'><option value='128'>128MB</option><option value='256'>256MB</option><option value='512'>512MB</option><option value='1024'>1GB</option><option value='2048'>2GB</option><option value='4092'>4GB</option></select></td>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.disk+":</td><td><input type='text' name='diskcapacity' style='width:50px'/>GB</td>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.cpu+":</td><td><select name='cpuamount'><option value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option></select></td>";
	str += "</table></form></div>";
	str += "<div id='divTab2' class='divHide'>";
	str += "<form id='useExistedVirtualApplianceForm' action='" + basePath + "makeByExistedVirtualAppliance.do' method='post'>";
	str += "<table>";
	str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
	str += "<input type=\"hidden\" name=\"action\" value=\"add\" />";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.app+":</td><td><select id='appliance' name='appliance'></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.name+": </td><td><input type='text' name='appname'/></td></tr>";
//	str += "<tr><td>\u683C\u5F0F:&nbsp;</td><td><select name=\"format\"><option value=\"raw\">raw</option><option value=\"qcow\">qcow</option><option value=\"vmdk\">vmdk</option></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.mem+":</td><td><select name='memsize'><option value='128'>128MB</option><option value='256'>256MB</option><option value='512'>512MB</option><option value='1024'>1GB</option><option value='2048'>2GB</option><option value='4092'>4GB</option></select></td>";
	str += "</table></form></div>\n";
	str += "<div id='divTab3' class='divHide'>";
	str += "<form id='modifyExistedVirtualApplianceForm' action='" + basePath + "makeByExistedVirtualAppliance.do' method='post'>";
	str += "<table>";
	str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
	str += "<input type=\"hidden\" name=\"action\" value=\"modify\" />";
	str += "<input type=\"hidden\" name=\"appname\" value=\"appliance\" />";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.app+":</td><td><select id='appliance' name='appliance'></select></td></tr>";
//	str += "<tr><td>\u7535\u5668\u540D\u79F0: </td><td><input type='text' name='appname'/></td></tr>";
//	str += "<tr><td>\u683C\u5F0F:&nbsp;</td><td><select name=\"format\"><option value=\"raw\">raw</option><option value=\"qcow\">qcow</option><option value=\"vmdk\">vmdk</option></select></td></tr>";
	str += "<tr><td>"+lingcloud.Appliance.makeAppliance.mem+":</td><td><select name='memsize'><option value='128'>128MB</option><option value='256'>256MB</option><option value='512'>512MB</option><option value='1024'>1GB</option><option value='2048'>2GB</option><option value='4092'>4GB</option></select></td>";
	str += "</table></form></div>\n";
    jSubmit(str, lingcloud.Appliance.makeAppliance.title, callbackForMakeVirtualAppliance);
};

changeTab = function (n, d) {
	var i;
	for (i=1;i<=n;i=i+1) {
		var link=document.getElementById('link'+i);
		var tab=document.getElementById('divTab'+i);
		if (link != null && tab != null ) {
			link.className="";
			tab.className="divHide"
		}
	}
	var link=document.getElementById('link'+d);
	var tab=document.getElementById('divTab'+d);
	if (link!=null && tab !=null ) {
		link.className="active";
		tab.className="divShow"
	}
};

callbackForDeleteAppliance = function (result) {
    if (result) {
        var form = document.getElementById("deleteVirtualApplianceForm");
        form.submit();
    } else {

    }
};

showDialogForDeleteAppliance = function (basePath, appGuid, thispage) {
    var str = "<form id=\"deleteVirtualApplianceForm\" action=\"" + basePath + "deleteVirtualAppliance.do\" method=\"post\">";
    str += "<input type=\"hidden\" name=\"vaguid\" value=\"" + appGuid + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"" + thispage + "\" />";
    str += "<table width=\"400px\"><tbody><tr><td>&nbsp;&nbsp;"+lingcloud.Appliance.delAppliance.confirmTip+"</td></tr></tbody></table>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Appliance.delAppliance.title, callbackForDeleteAppliance);
};

loadForSaveAppliance = function (basePath, loadingTable, targetTable) {
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListApplianceCategory.jsp";
    if (ajax === false || url === null) {
        return false;
    }
    var form = document.getElementById("saveVirtualApplianceForm");
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("option");
                if (xSel === null) {
                    return;
                }
                var select_category = form.category;
                select_category.options.length = 0;
                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xValue == -1) {
                        break;
                    }
                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
                    var option = new Option(xText, xValue);
                    try {
                        select_category.options[i] = option;
                    }
                    catch (e) {
                    }
                }
                
                if (select_category.options.length == 0) {
                    var option = new Option(lingcloud.error.noResult, "-1");
                    select_category.options[0] = option;
                }
                var ltable = document.getElementById(loadingTable);
                var ttable = document.getElementById(targetTable);
                ltable.style.display = "none";
                ttable.style.display = "block";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
                return;
            }
        }
    };
    ajax.send(null);

};
callbackForSave = function (result) {
    if (result) {
        var form = document.getElementById("saveVirtualApplianceForm");

        if (form.category.value.trim() === "" || form.category.value.trim() === "-1") {
            alert(lingcloud.error.appCateNotNull);
            return;
        }

        if (form.app.value.trim().length > 100) {
        	alert(lingcloud.error.appApplicationTooLong);
            return;
        }
        
        var selectAccessWays = 0;
        for (var i=0; i < form.accessway.length;i+=1) {
        	if (form.accessway[i].checked == true) {
        		selectAccessWays+=1;
        	}
        }
        if (selectAccessWays === 0) {
            alert(lingcloud.error.appAccessNotNull);
            return;
        }

        if (form.language.value.trim() === "") {
            alert(lingcloud.error.appLangNotNull);
            return;
        }
        if (form.language.value.trim().length > 100) {
        	alert(lingcloud.error.appLangTooLong);
            return;
        }
        if (form.loginstyle.value.trim() === "") {
        	alert(lingcloud.error.appLoginStyleNotNull);
            return;
        }
        if ( form.loginstyle.value.trim() === "User and Password" ) {
        	if (form.username.value.trim() === "") {
            	alert(lingcloud.error.appUserNotNull);
            	return;
        	}
        	if (form.username.value.trim().length > 80) {
            	alert(lingcloud.error.appUserTooLong);
                return;
            }
        	if (form.password.value.trim() === "") {
            	alert(lingcloud.error.appPassNotNull);
            	return;
        	}
        	if (form.password.value.trim().length > 80) {
            	alert(lingcloud.error.appPassTooLong);
                return;
            }
        }
        
        if (form.description.value.trim().length > 500) {
        	alert(lingcloud.error.appDescTooLong);
            return;
        }
        form.submit();
    } else {

    }
};

showDialogForSave = function() {
	var basePath = document.getElementById('basePath').value;
	var appGuid = document.getElementById('guid').value;
	var str = "<form id=\"saveVirtualApplianceForm\" action=\"" + basePath + "saveVirtualAppliance.do\" method=\"post\">";
	str += "<table id=\"loadingTable4SaveVA\" width='400px'><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadForSaveAppliance('" + basePath + "', 'loadingTable4SaveVA', 'saveVirtualApplianceTable')</script>";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + appGuid + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
    str += "<table width=\"400px\"  id=\"saveVirtualApplianceTable\"  style=\"DISPLAY:none\">";
	str += "<tr><td>"+lingcloud.Appliance.saveAppliance.cate+":&nbsp;</td><td><select id=\"category\" name=\"category\"></select>&nbsp;&nbsp;</td></tr>";
//    str += "<tr><td>\u683C\u5F0F:&nbsp;</td><td><select name=\"format\"><option value=\"raw\">raw</option><option value=\"qcow\">qcow</option><option value=\"vmdk\">vmdk</option></select></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.application+":&nbsp;</td><td><input type=\"text\" id=\"oneapp\" /><a href='javascript:addValueFromSelect(\"oneapp\",\"apps\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='apps' name='app'><a href='javascript:clearValues(\"apps\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.access+":</td><td><input type='checkbox' name='accessway' value='SSH'/>SSH<input type='checkbox' name='accessway' value='RDP'/>RDP<input type='checkbox' name='accessway' value='VNC'/>VNC</td>";
    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.lang+":&nbsp;</td><td><select id=\"lang\"><option>Chinese(simplified)</option><option>Chinese(traditional)</option><option>English</option><option>French</option><option>German</option><option>Italian</option><option>Japanese</option><option>Korean</option><option>Russian</option><option>Spanish</option><option>Other</option></select><a href='javascript:addValueFromSelect(\"lang\",\"langs\");'>+</a><font color='red'>("+lingcloud.Appliance.newAppliance.clickToAdd+")</font></td></tr><tr><td></td><td><input type='text' readonly='true' id='langs' name='language'><a href='javascript:clearValues(\"langs\");'>" + lingcloud.tip.clear + " </a></td></tr>";
    str += "<tbody id='userpass' style='DISPLAY:none'>";
    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.loginStyle+":&nbsp;</td><td><select id='loginstyle' name=\"loginstyle\" onChange=\"checkLoginStyle(this,'userpass')\" ><option value=\"Global User\">Global User</option><option value=\"User and Password\">User and Password</option></select></td></tr>";

    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.user+":&nbsp;</td><td><input type=\"text\" name=\"username\" /></td></tr>";
    str += "<tr><td>"+lingcloud.Appliance.saveAppliance.pass+":&nbsp;</td><td><input type=\"text\" name=\"password\" /></td></tr>";
    str += "</tbody>";
    str += "<tr><td>"+lingcloud.Appliance.desc+":&nbsp;</td><td><textarea name=\"description\" cols=\"25\" rows=\"6\" /></td></tr>";
    str += "</table></form>\n";
    jSubmit(str, lingcloud.Appliance.saveAppliance.title, callbackForSave);
}

callbackForSaveAppliance = function (result) {
    if (result) {
    	showDialogForSave();
    } else {
    	
    }
};

showDialogForSaveAppliance = function (basePath, appGuid) {
	var str = "<form>";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + appGuid + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
    str += "<table width=\"400px\">";
    str += "<tr><td><input type=\"hidden\" id=\"basePath\" value=\"" + basePath + "\"/></td></tr>";
    str += "<tr><td><input type=\"hidden\" id=\"guid\" value=\"" + appGuid + "\"/></td></tr>";
    str += lingcloud.Appliance.saveAppliance.confirmTip;
    str += "</table>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Appliance.saveAppliance.title, callbackForSaveAppliance);
}

callbackForOperateAppliance = function (result) {
    if (result) {
        var form = document.getElementById("operateVirtualApplianceForm");
        form.submit();
    } else {

    }
};

showDialogForOperateAppliance = function (basePath, appGuid, action) {
	var strAction;
	if (action === "save") {
		strAction = lingcloud.Appliance.operateAppliance.save;
	} else if (action === "stop") {
		strAction = lingcloud.Appliance.operateAppliance.stop;
	} else if (action === "start") {
		strAction = lingcloud.Appliance.operateAppliance.start
	} 
    var str = "<form id=\"operateVirtualApplianceForm\" action=\"" + basePath + "operateVirtualAppliance.do\" method=\"post\">";
    str += "<input type=\"hidden\" name=\"guid\" value=\"" + appGuid + "\" />";
    str += "<input type=\"hidden\" name=\"action\" value=\"" + action + "\" />";
    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
    str += "<table width=\"400px\"><tbody><tr><td>&nbsp;&nbsp;" + lingcloud.Appliance.operateAppliance.confirmTip + strAction + "?</td></tr></tbody></table>";
    str += "</form>\n";
    jSubmit(str, lingcloud.Appliance.operateAppliance.title, callbackForOperateAppliance);
};

showDialogForShowVNC = function (basePath, host, port) {
	var str = "<table align=\"center\">"
		+ "<tr><td>" + host + " : </td><td>" + port + "</td></tr>"
		+ "</table>"
		+ "<center>" + lingcloud.tip.vnctip +"</center>";
	jShow(str,"VNC " + lingcloud.Infrastructure.connection);
};

loadApplianceInfo = function (basePath, loadingTable, targetTd, guid) {
    var ajax = initAjax();
    var url = basePath + "JSP/AjaxListApplianceInfo.jsp?guid=" + guid;
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
                if (ltable != null) {
                    ltable.style.display = "none";
                }
                var tTd = document.getElementById(targetTd);
                tTd.innerHTML = xmlDoc;
                $.alerts._reposition();
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};

showApplianceInfo = function(basePath, appGuid, appName) {
    var str = "<table id=\"loadingApplianceInfo\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
    str += "<script>loadApplianceInfo('" + basePath + "', 'loadingApplianceInfo', 'applianceInfo', '" + appGuid + "')</script>";
    str += "<div class='divContent' id='applianceInfo'/>";
    jShow(str, appName);
};

showDialogForMakeApplianceHelp = function() {
    var str = "";
    str += "<table width=\"400px\">";
    str += "<tr><td colspan='2'>&nbsp;&nbsp;"+lingcloud.Appliance.makeApplianceHelp.title+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;1. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step1+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;2. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step2+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;3. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step3+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;4. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step4+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;5. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step5+"</td></tr>";
    str += "<tr><td valign='top'>&nbsp;&nbsp;&nbsp;&nbsp;6. </td><td>"+lingcloud.Appliance.makeApplianceHelp.step6+"</td></tr>";
    str += "</table>";
    jShow(str, "Help");
};

loadRunningState = function(basePath, names) {
	var ajax = initAjax();
    var url = basePath + "JSP/AjaxListRunningState.jsp?names=" + names;
    if (ajax === false || url === null) {
        return false;
    }

    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseXML;
                var xSel = xmlDoc.getElementsByTagName("vm");
                if (xSel === null) {
                    return;
                }

                var i;
                for (i = 0; i < xSel.length; i+=1) {
                    var xName = xSel[i].childNodes[0].firstChild.nodeValue;
                    if (xName == -1) {
                        break;
                    }
                    var xState = xSel[i].childNodes[1].firstChild.nodeValue;
                    try {
                        var state = document.getElementById(xName);
                        if (state != null) {
                        	state.innerHTML = xState;
                        }
                    }
                    catch (e) {
                    }
                }
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
};

/**
 * some global variables declaration
 * @type String
 */

var applianceTabId = "applianceTab";
var applianceContentId = "applianceContent";
var appliancePageId = "appliancePage";
var cateGuid;
var currentCate = "all"
var loadingStr = "";
var name="";

/**
 * add the tab of categories
 * @param {} basePath
 */
 function applianceManagementTab(basePath)
 {
 	loadingStr = "<table><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
 	$('#' + applianceTabId).html(loadingStr);
 	
 	var ajax = initAjax();
    var url = basePath + "JSP/AjaxListApplianceCategory.jsp";
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
                
                var cate=new Array();
                cateGuid = new Array();
                var i;
                cate[0]={					  	
				  		name:lingcloud.Appliance.manageAppliance.allCate + '<a href="javascript:showDialogForCreateCategory(\'' + basePath + '\');">+</a>',
				  		handler:function(index)
				  		{
                    		currentCate=index;
                    		applianceManagementContent(basePath, null);
				  		}
				  	};
                cateGuid[0] = 'All';
                for (i = 0,j=0; j < xSel.length; i+=1,j+=1) {
                    var categuid = xSel[j].childNodes[0].firstChild.nodeValue;
                    var catename = xSel[j].childNodes[1].firstChild.nodeValue;

                    if (categuid==='0') continue;

                    cateGuid[i]=categuid;
                    cate[i]={					  	
				  		name:catename + '<a href="javascript:showDialogForDeleteCategory(\'' + basePath + '\', \'' + categuid + '\');">-',
				  		handler:function(index)
				  		{
                    		currentCate=index;
                    		applianceManagementContent(basePath, cateGuid[index]);
				  		}
				  	};
                }
                
                var t = new LingcloudTab({
			  		renderId:"applianceTab",
			  		basePath:'.',
			  		width:"875px",
			  		vertical:false,
			  		
			  		tabs:cate
			  	});
                
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
            }
        }
    };
    ajax.send(null);
 	
  }

 /**
  * add detail message of appliances
  * @param {} basePath
  */
 function applianceManagementContent(basePath,categuid)
 {
 	$('#' + applianceContentId).html(loadingStr);
 	$.ajax({
 				    url: basePath + "JSP/AjaxListApplianceByCate.jsp",
 				    type: 'GET',
 				    dataType: 'text',
 				    data: categuid != null ? "cate=" + categuid : "",
 				    error: function(MLHttpRequest, textStatus, errorThrown){
 				        alert(lingcloud.error.responseNotFound + textStatus);
 				    },
 				    success: function(data, textStatus, XMLHttpRequest)
 				    {	
 				    	if(data == ''){
 				    		$('#' + applianceContentId).html(lingcloud.error.noResult);
 				    		return;
 				    	}
 						var result = eval('(' + data + ')');
						
					    var items = result.datas;
 						
 						var str = " <table border=\"0\" cellspacing=1 cellpadding=5 width=\"95%\">";
 						str+="<tr class=\"actionlog_title\" >";
 						str += '<th width=\"50\" id=\"actionlog_title\" valign=\"middle\">'
 							+ '<a title="'
							+ lingcloud.tip.refresh
							+ '" href="#" onclick="applianceManagementContent(\''
							+ basePath
							+ '\',' + categuid +');"><img src="'
							+ basePath
							+ 'images/refresh.png" style="border: medium none ;" width="16" height="16" /></a>&nbsp;'
 							+ '<a title="'
							+ lingcloud.Appliance.manageAppliance.tip4AddAppliance
							+ '" href="#" onclick="showDialogForCreateAppliance(\''
							+ basePath
							+ '\');"><img src="'
							+ basePath
							+ 'images/increase.png" style="border: medium none ;" width="16" height="16" /></a>&nbsp;'
							
							+ '<a  title="'
							+ lingcloud.Appliance.manageAppliance.tip4UploadAppliance
							+ '" href="#" onclick="open_ul();"><img src="'
							+ basePath
							+ 'images/upload.png" style="border: medium none ;" width="16" height="16" /></a></th>';
 						str+="<th width=\"80\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.name+"</th>";	//name
 						str+="<th width=\"60\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.os+"</th>";	//os
 						str+="<th width=\"80\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.application+"</th>";	//applications
 						str+="<th width=\"50\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.disk+"</th>";	//capacity
 						str+="<th width=\"50\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.size+"</th>";	//size
 						str+="<th width=\"50\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.format+"</th>";	//format
 						str+="<th width=\"30\" id=\"actionlog_title\" valign=\"middle\">"+lingcloud.Appliance.manageAppliance.detail+"</th>";	
 						str+="</tr>";
 						
 						var itemNum = items.length, i = 0;	
 						
 						if(itemNum>0)
						{
							for(i=0;i<itemNum;i+=1)
							{
								var name = items[i].name;
								var guid = items[i].guid;
								var os = items[i].os;
								var app = items[i].app;
								var capacity = items[i].capacity;
								var size = items[i].size;
								var format = items[i].format;
								var state = items[i].state;
								
								str+="<tr style='font-size:9pt; line-height:16pt; color:black;'>";
								str+="<td align='center'>" + '<a  title="'+ lingcloud.Appliance.manageAppliance.tip4DelAppliance +'" href="#" onclick="showDialogForDeleteAppliance(\''+ basePath +'\', \'' + guid + '\');"><img src="'+ basePath +'images/vcdelete.png" style="border: medium none ;" width="16" height="16" /></a>&nbsp;&nbsp;<a  title="'+ lingcloud.Appliance.manageAppliance.tip4UpdateAppliance +'" href="#" onclick="showDialogForModifyAppliance(\''+ basePath +'\', \'' + guid + '\');"><img src="'+ basePath +'images/edit.png" style="border: medium none ;" width="16" height="16" /></a>' + "</td>";
								str+="<td align='center'>" + name + "</td>";
								str+="<td align='center'>" + os + "</td>";
								str+="<td align='center'>" + app + "</td>";
								str+="<td align='center'>" + capacity + "</td>";
								str+="<td align='center'>" + size + "</td>";
								str+="<td align='center'>" + state + "</td>";
								str+="<td align='center'>" + '<a title="'+lingcloud.Appliance.manageAppliance.detail+'" href="#" onclick="showApplianceInfo(\''+ basePath +'\',\''+ guid +'\',\''+ name +'\');"><img src="'+ basePath +'images/list.png" style="border: medium none ;" width="16" height="16" /></a>' + "</td>";
								str+="</tr><tr><td colspan=8><div style='height:10px'></div></td></tr>";
							}
				  		
						}
 						
 						
 					    str+="</table>";
 					    
 					    $('#' + applianceContentId).html(str);
 					      
 				    }
 				});
 	}

 
 callbackForChangeDisc = function (result) {
	    if (result) {
	        var form = document.getElementById("changeDiscForm");
	        if (form.discguid.value.trim() === "") {
	        	alert(lingcloud.error.appVcdNotNull);
	            return;
	        }

	        form.submit();
	    } else {

	    }
	};

loadForChangeDisc = function (basePath, loadingTable, targetTable) {
	    var ajax = initAjax();
	    var url = basePath + "JSP/AjaxListVirtualCD.jsp";
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
	                var select_disc = document.getElementById("discguid");
	                select_disc.options.length = 0;
	                var i;
	                var option = new Option("\u65E0","none");
	                select_disc.options[0] = option;
	                for (i = 0; i < xSel.length; i+=1) {
	                    var xValue = xSel[i].childNodes[0].firstChild.nodeValue;
	                    if (xValue == -1) {
	                        break;
	                    }
	                    var xText = xSel[i].childNodes[1].firstChild.nodeValue;
	                    var option = new Option(xText, xValue);
	                    try {
	                        select_disc.options[i+1] = option;
	                    }
	                    catch (e) {
	                    }
	                }
	                
	                if (select_disc.options.length == 0) {
	                    var option = new Option(lingcloud.error.noResult, "-1");
	                    select_disc.options[0] = option;
	                }
	                
	                var ltable = document.getElementById(loadingTable);
	        	    var ttable = document.getElementById(targetTable);
	        	    ltable.style.display = "none";
	        	    ttable.style.display = "";
	                
	            } else {
	                alert(lingcloud.error.responseNotFound + ajax.statusText);
	                return;
	            }
	        }
	    };
	    ajax.send(null);
	};



showDialogForChangeDisc = function (basePath,guid,discName) {
	    var str = "<form id=\"changeDiscForm\" action=\"" + basePath + "changeDisc.do\" method=\"post\">";
	    str += "<table id=\"loadingTable4ChangeDisc\"><tbody><tr><td><img src=" + basePath + "images/table_loading.gif /></td><td>&nbsp;&nbsp;"+lingcloud.Appliance.loading+"</td></tr></tbody></table>";
	    str += "<script>loadForChangeDisc('" + basePath + "', 'loadingTable4ChangeDisc', 'changeDiscTable')</script>";
	    str += "<table id=\"changeDiscTable\" style='display:none; width:400px'>";
	    str += "<input type=\"hidden\" name=\"thispage\" value=\"/JSP/viewMakeVirtualAppliance.jsp\" />";
	    str += "<input type=\"hidden\" name=\"guid\" value=\"" + guid + "\" />";
	    str += "<tr><td width='25%'>"+lingcloud.Appliance.changeVirtualDisc.name+":&nbsp;</td><td>" + discName + "</td></tr>";
	    str += "<tr><td>"+lingcloud.Appliance.changeVirtualDisc.disc+":&nbsp;</td><td><select id='discguid' name=\"discguid\"></select></td></tr>";
	    str += "</table></form>\n";
	    str += "<table><tr>("+lingcloud.Appliance.changeVirtualDisc.tip+")</tr></table>";
	    jSubmit(str, lingcloud.Appliance.changeVirtualDisc.title, callbackForChangeDisc);
	};

deleteFile = function (basePath,url) {
	var ajax = initAjax();
    if (ajax === false || url === null) {
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
            	window.location.href = basePath + "JSP/VirtualAppMgnt.jsp";
            } else {
                alert(lingcloud.error.responseNotFound + ajax.statusText);
                return;
            }
        }
    };
    ajax.send(null);
};