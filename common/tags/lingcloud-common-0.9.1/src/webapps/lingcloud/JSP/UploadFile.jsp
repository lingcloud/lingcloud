<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.lingcloud.molva.xmm.vam.util.VAMConfig"%>
<link href="<%=basePath%>js/uploadify/uploadify.css" type="text/css"
	rel="stylesheet" />
<link href="<%=basePath%>css/uploadDialog.css" type="text/css"
	rel="stylesheet" />
<script type="text/javascript" src="<%=basePath%>js/uploadify/swfobject.js"></script>
<script type="text/javascript"
	src="<%=basePath%>js/uploadify/jquery.uploadify.v2.1.4.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
  $('#file_upload').uploadify({
    'uploader'  : '<%=basePath%>js/uploadify/uploadify.swf',
    'script'    : '<%=basePath%>JSP/fileupload',
    'folder'	: '<%=VAMConfig.getUploadDirLocation()%>',
    'cancelImg' : '<%=basePath%>js/uploadify/cancel.png',
    'auto'      : false,
	'removeCompleted' : false,
	'multi'           : true,
	'method'      : 'get',
	'scriptData'  : {'type':fileType},
	'fileDataName' : 'myUpload',
	'sizeLimit'   : 40960000000,
	'queueSizeLimit' : 5,
	'onAllComplete' : function(event,data) {
		  document.getElementById("cancel").value=lingcloud.upload.close;
	      alert(data.filesUploaded + lingcloud.upload.finish)},
	'onError'     : function (event,ID,fileObj,errorObj) {
	      alert(errorObj.type + ' Error: ' + errorObj.info);
	    }
  });
});  

function open_ul(){
	
	document.getElementById("upload").value=lingcloud.upload.upload;
	document.getElementById("cancel").value=lingcloud.upload.cancel;
	var divInb = document.createElement('div');
	document.getElementById('upload_title').innerHTML=title;
	document.body.appendChild(divInb);
	divInb.setAttribute('id','upload_inbetween');
	divInb.setAttribute('style','background: rgb(136, 136, 136) none repeat scroll 0% 0%; position: absolute; z-index: 2; top: 0px; left: 0px; width: 100%; height: 100%;  opacity: 0.5;');
	document.getElementById("upload_container").setAttribute("style","position: absolute; z-index: 99999; visibility: visible; left: 30%;top: 30%;width: 500px; display: block;");
	document.getElementById("upload_container").setAttribute("class","ui-draggable");
	if(navigator.appName == "Microsoft Internet Explorer"){
		document.getElementById("upload_inbetween").style.filter = "alpha(opacity=40)";
		document.getElementById("upload_container").style.filter = "alpha(opacity=100)";
	}
	//Modified by Taoliang.
	//Date: 2011.06.07
	//Problem:When use this function on Chrome,missed click event.
	$('#upload_title').mousedown(function() {
		$("#upload_container").draggable();
	});

	$('#upload_title').mouseup(function() {
		$("#upload_container").draggable("destroy");
	});
}
function up(){
	$("#file_upload").uploadifyUpload();
}
function close_ul(){
	$("#upload_inbetween").remove();
	$('#file_upload').uploadifyClearQueue();
	$("#upload_container").css("display","none");
	if(fileType == "application"){
		window.location.reload();
	}
	
}

</script>
<body>

<div id="upload_container"
	style="display: none; position: absolute; z-index: 99999;" class="ui-draggable">
<center><h1 id="upload_title" style="cursor: move;"></h1>
<div id="upload_content"><input id="file_upload" name="file_upload"
	type="file" /></div>
<div id="upload_panel"><input type="button" id="upload" value="上传"
	onclick="up();"></input> <input type="button" id="cancel" value="取消"
	onclick="close_ul();"></input></div>
</center>
</div>
</body>

