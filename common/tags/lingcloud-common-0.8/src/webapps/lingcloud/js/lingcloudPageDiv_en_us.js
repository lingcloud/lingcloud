/* 
 * @(#)lingcloudPageDiv_en_us.js 2009-10-6 
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

function LingcloudPageDiv(conf) {
	this.objIndex = LingcloudPageDiv.prototype.__lingcloudtabIndex__++;
	this.renderId = "";
	this.totalPage = 0;
	this.currentPage = 1;
	this.height = "30px";
	this.width = "80%";
	this.divStyle = 1;
	this.current = null;
	this.callback = null;
	this.innerId = null;
	this.fpid = null;
	this.ppid = null;
	this.cpid = null;
	this.npid = null;
	this.lpid = null;
	if (conf.renderId) {
		this.renderId = conf.renderId;
	}
	if (conf.totalPage) {
		this.totalPage = conf.totalPage;
	}
	if (conf.currentPage) {
		this.currentPage = conf.currentPage;
		if (this.currentPage < 0)
			this.currentPage = 0;
		if (this.currentPage > this.totalPage)
			this.currentPage = this.totalPage;
	}
	if (conf.current) {
		this.current = conf.current;
	}
	if (conf.callback) {
		this.callback = conf.callback;
	}
	this.init();
}

LingcloudPageDiv.prototype = {
	__lingcloudtabIndex__ : 0,
	init : function() {
		// to construct the tabs
		if (this.totalPage == null || this.totalPage <= 0) {
			// no page data
			return false;
		}
		var thisObj = this;
		this.innerId = "lingcloud_pagediv_" + this.objIndex;
		this.fpid = "pagediv_fp_" + this.objIndex;
		this.ppid = "pagediv_pp_" + this.objIndex;
		this.cpid = "pagediv_cp_" + this.objIndex;
		this.npid = "pagediv_np_" + this.objIndex;
		this.lpid = "pagediv_lp_" + this.objIndex;
		var str = '<div id="' + this.innerId + '" class = "lingcloud_page_div">';
		str += '<span id="' + this.fpid + '">' + 'First Page' + '</span>';
		str += '<span id="' + this.ppid + '">' + 'Previous' + '</span>';
		str += '<span><input id="' + this.cpid
				+ '" size= "6" type = "text" class= "whichpage" value = "'
				+ this.currentPage + '">/' + this.totalPage + '</span>';
		str += '<span id="' + this.npid + '">' + 'Next' + '</span>';
		str += '<span id="' + this.lpid + '">' + 'Last Page' + '</span>';
		str += '</div>';
		// to show
		if (this.renderId == '') {
			var $autoDiv = $('<div id = "auto_pagediv_container_' + this.objIndex + '"></div>');
			$autoDiv.html(str);
			$autoDiv.appendTo($('body'));
		} else {
			$('#' + this.renderId).html(str);
		}

		// to deal with the event
		$('#' + this.fpid).click(function() {
			thisObj.fp();
		});
		$('#' + this.ppid).click(function() {
			thisObj.pp();
		});
		$('#' + this.cpid).keyup(function(ev) {
			// alert(e.which);
				if (ev.which == 13) {
					thisObj.cp();
				}
			})
		$('#' + this.npid).click(function() {
			thisObj.np();
		});
		$('#' + this.lpid).click(function() {
			thisObj.lp();
		});

	},
	show : function() {
		if (this.renderId == '') {
			$('#auto_pagediv_container_' + this.objIndex).css("display",
					"inline");
			$cont.appendTo($('body'));
		} else {
			$('#' + this.renderId).css("display", "inline");
		}
	},
	hide : function() {
		if (this.renderId == '') {
			$('#auto_pagediv_container_' + this.objIndex)
					.css("display", "none");
		} else {
			$('#' + this.renderId).css("display", "none");
		}
	},
	fp : function() {
		var $cpinput = $('#' + this.cpid);
		$cpinput.val(1);
		this.callback(1);
		return false;
	},
	pp : function() {
		var $cpinput = $('#' + this.cpid);
		var pv = $cpinput.val().trim();
		if (!(this.checkPageId(pv)))
			return false;
		pv = parseInt(pv);
		if (pv == 1) {
			alert('This is the first page!');
			return;
		}
		pv -= 1;
		$cpinput.val(pv);
		this.callback(pv);
		return false;
	},
	cp : function() {
		var $cpinput = $('#' + this.cpid);
		$cpinput.blur();
		var lt = $cpinput.val().trim();
		if (!(this.checkPageId(lt)))
			return false;
		this.callback(parseInt(lt));
		return false;
	},
	np : function() {
		var $cpinput = $('#' + this.cpid);
		var pv = $cpinput.val().trim();
		if (!(this.checkPageId(pv)))
			return false;
		pv = parseInt(pv);
		if (pv == this.totalPage) {
			alert("This is the last page!");
			return;
		}
		pv += 1;
		$cpinput.val(pv);
		this.callback(pv);
		return false;
	},
	lp : function() {
		var $cpinput = $('#' + this.cpid);
		var pv = this.totalPage;
		$cpinput.val(pv);
		this.callback(pv);
		return false;
	},
	checkPageId : function(lt) {
		var isNum = /^[1-9]\d*$/;
		if (!(isNum.test(lt))) {
			alert("Please enter a valid integer!");
			return false;
		}
		var pv = parseInt(lt);
		if (pv < 1 || pv > this.totalPage) {
			alert("Please enter a valid integer between 1 and "
					+ this.totalPage + "!");
			return false;
		}
		return true;
	}
}