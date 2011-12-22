/* 
 * @(#)locale.js 2009-10-6
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

function changeLocale(basePath, sIndex){
	var ajax = initAjax();
	var url = basePath + "JSP/GetServerInfoUtil.jsp?sevNum=2&lan=" + sIndex;
    if (ajax === false || url === null) {    	
        return false;
    }
    ajax.open("GET", url, true);
    ajax.onreadystatechange = function () {
        if (ajax.readyState == 4) {
            if (ajax.status == 200) {
                var xmlDoc = ajax.responseText;
                if(xmlDoc == '' || xmlDoc == 'no')
                	alert('Failed');
				location.reload();
            } else {
                alert("Inner Error:" + ajax.statusText);
            }
        }
    };
    ajax.send(null);
}