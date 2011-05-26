/* 
 * @(#)lingcloudTab.js 2009-10-6 
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
function LingcloudTab(conf){
	this.objIndex = LingcloudTab.prototype.__lingcloudtabIndex__++;
	this.renderId = "";
	this.height = "30px";
	this.width = "900px";
	this.margin = "0 0 0 15px";
	this.padding = "7px 5px";
	this.hover = null;
	this.current = null;
	this.activeIndex = 0;
	this.tabs = null;
	this.basePath = "./";
	this.vertical = false;
	this.prevtabId = null;
	this.nexttabId = null;
	this.innerId = null;
	
	if(conf.renderId){
		this.renderId = conf.renderId;
	}
	if(conf.height){
		this.height = "" + conf.height;	
	}
	if(conf.width){
		this.width = "" + conf.width;	
	}
	if(conf.margin){
		this.margin = "" + conf.margin; 	
	}
	if(conf.padding){
		this.padding = "" + conf.padding;	
	}
	if(conf.hover){
		this.hover = conf.hover;	
	}
	if(conf.current){
		this.current = conf.current;	
	}
	if(conf.activeIndex){
		this.activeIndex = conf.activeIndex;
	}
	if(conf.tabs){
		this.tabs = conf.tabs;
	}
	if(conf.vertical){
		this.vertical = conf.vertical;	
	}
	if(conf.basePath){
		this.basePath = conf.basePath;
		if(this.basePath.lastIndexOf("/") < this.basePath.length-1)
			this.basePath += "/";
	}
	this.init();
}

LingcloudTab.prototype = {
	__lingcloudtabIndex__: 0,
	init: function(){
		// to construct the tabs
		if(this.tabs == null || this.tabs.length == 0){
			//no tab element
			return false;	
		}
		var thisObj = this;
		this.innerId = "lingcloudtab_" + this.objIndex;
		var $ul_li = null;
		var str = '';
		if(this.vertical){
			str = '<div id="' + this.innerId + '" class = "lingcloudtab_vertical">';
			str += '<ul class= "tabpanel">';
			var j = 0;
			for(var i = 0; i< this.tabs.length; i++){
				var temp = this.tabs[i];
				if(temp.name)
					str += '<li><a >' + temp.name + '</a></li>';
				else
					str += '<li><a >' + ('tab' + j++) + '</a></li>';	
			}
			str += '</ul>';
			str += '</div>';
			//to show
			if(this.renderId == ''){	
				var $autoDiv= $('<div id = "auto_lingcloudtab_container_' + this.objIndex + '"></div>');	
				$autoDiv.html(str);		
				$autoDiv.appendTo($('body'));
			}else{
				$('#' + this.renderId).html(str);
			}
			$ul_li = $("#" + this.innerId + ' ul li');
		}else{
			this.prevtabId = "prevtab_" + this.objIndex;
			this.nexttabId = "nexttab_" + this.objIndex;
			
			var str = '<table border="0" id=""><tr>';
			str += '<td width="15px"><div title= "Previous" id = "' + this.prevtabId + '" class="prevtab">&lt&lt&nbsp</div></td>';	
			str += '<td>';		
			str += '<div id="' + this.innerId + '" class = "lingcloudtab">';
			str += '<ul class= "tabpanel">';
			var j = 0;
			for(var i = 0; i< this.tabs.length; i++){
				var temp = this.tabs[i];
				if(temp.name)
					str += '<li><a >' + temp.name + '</a></li>';
				else
					str += '<li><a >' + ('tab' + j++) + '</a></li>';	
			}
			str += '</ul>';
			str += '</div>';
			str += '</td>';
			str += '<td width= "15px"><div title= "Next" id="' + this.nexttabId +  '" class="nexttab">&nbsp&gt&gt</div></td>';
			str += '</tr></table>';
			//to show
			if(this.renderId == ''){	
				var $autoDiv= $('<div id = "auto_lingcloudtab_container_' + this.objIndex + '"></div>');	
				$autoDiv.html(str);		
				$autoDiv.appendTo($('body'));
			}else{
				$('#' + this.renderId).html(str);
			}
			
			$ul_li = $("#" + this.innerId + ' ul li');
			//to check whether to hide some tabs
			var _i =0, _j=0;				
			var defaultWidth = parseInt(this.width) - 30, totalWidth = 0;
			var needHidden = false;
			for(_i = 0; _i< $ul_li.length; _i++){
				totalWidth += $ul_li.eq(_i).width();
				if(totalWidth > defaultWidth){
					_j = _i-1;
					needHidden = true;
					break;
				}
			}
			
			if(needHidden){
				$('#' + this.innerId + ' ul li:gt(' + (_j-1) + ')').css("display", "none");	
				$("#" + this.nexttabId).css("display", "inline");
			
			}
			//to deal with the event
			$('#' + this.prevtabId).click(function(){
				thisObj.prevTab();
			});
			$('#' + this.nexttabId).click(function(){
				thisObj.nextTab();
			});
		}
		
		// to set the css
		// bugs here
		var $innerObj = $('#' + this.innerId);
		//$innerObj.css('height', this.height);
		$innerObj.css('width', parseInt(this.width)-30);
		$('#' + this.innerId + ' ul').css('margin', this.margin);
		$('#' + this.innerId + ' ul li').css('padding', this.padding);
		if(this.hover){
			for(var key in this.hover){
				//todo name to transfer. such as backgroud-color in css will be changed to backgroundColor is js	
			}	
		}
		if(this.current){}
				
		$ul_li.click(function(){    	
			$(this).addClass("current") 
				   .siblings().removeClass("current");
      var index =  $ul_li.index(this); 
      //alert(index);
      if(thisObj.tabs[index].handler)
      	thisObj.tabs[index].handler(index); 
      
    });
    
    $ul_li.eq((this.activeIndex < $ul_li.length)?this.activeIndex:0).trigger('click');
	},
	show:function(){
			if(this.renderId == ''){			
				$('#auto_lingcloudtab_container_' + this.objIndex).css("display", "inline");	
				$cont.appendTo($('body'));
			}else{
				$('#' + this.renderId).css("display", "inline");
			}
	},
	hide:function(){
			if(this.renderId == ''){				
				$('#auto_lingcloudtab_container_' + this.objIndex).css("display", "none");
			}else{
				$('#' + this.renderId).css("display", "none");
			}
	},
	prevTab:function(){
		if(this.vertical){
			//this function is designed to 
			return false;
		}
		var csssel = '#' + this.innerId + ' ul li';
		//to hidden the last visible tab in the right
		var $lc = $(csssel + ':visible:last');//$visibleElem.eq(0);
		$lc.css("display", "none");
		//to show the previous tab hidden in the left side
		var $fc = $(csssel + ':visible:first');//$visibleElem.eq(0);
		var $prevfc = $fc.prev();
		var $pprevfc = $prevfc.prev();
		if(typeof($prevfc.css("display")) != 'undefined' && $prevfc.css("display") == 'none'){
			$prevfc.css("display", "inline");
		}
		//to show the next buttion
		if($("#" + this.nexttabId).css("display") == "none")
			$("#" + this.nexttabId).css("display", "inline");
		//deside whether to hidden the prev button
		if(typeof($pprevfc.css("display")) == 'undefined'){
			$("#" + this.prevtabId).css("display", "none");
		}
		return false;
	},
	nextTab:function(){
		var csssel = '#' + this.innerId + ' ul li';	
		//to hidden the first visible tab in the left
		var $fc = $(csssel + ':visible:first');//$visibleElem.eq(0);
		$fc.css("display", "none");
		//$fc.next().trigger("click");
		//to show the next tab hidden in the right side
		var $lc = $(csssel + ':visible:last');//$visibleElem.eq(0);
		var $nextlc = $lc.next();
		var $nnextlc = $nextlc.next();
		if(typeof($nextlc.css("display")) != 'undefined' && $nextlc.css("display") == 'none'){
			$nextlc.css("display", "inline");
		}
		//to show the prev buttion
		if($("#" + this.prevtabId).css("display") == "none")
			$("#" + this.prevtabId).css("display", "inline");
		//deside whether to hidden the next button
		if(typeof($nnextlc.css("display")) == 'undefined'){
			$("#" + this.nexttabId).css("display", "none");
		}
		return false;
	}
}