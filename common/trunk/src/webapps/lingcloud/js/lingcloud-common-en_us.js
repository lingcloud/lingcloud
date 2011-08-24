/* 
 * @(#)lingcloud-common-en_us.js 2009-10-6 
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

var lingcloud = {
	tip:{
		refresh:'Refresh',
		delapp:'Are you sure to delete this application.',
		delapptitle:'Delete Application',
		nocategory:'No Category',
		clear:'Clear',
		vnctip:'Please login the VM via local VNC client.'
	},
	upload:{
		finish:' files uploaded successfully!',
		upload:'upload',
		cancel:'cancel',
		close:'close',
		disc:'Upload CD Image',
		disk:'Upload Appliance',
		app:'Upload Application'
	},
	Infrastructure:{
		login:'Please Login',
		noAsset:'No Asset Now',
		partName:'Partition Name',
		desc:'Description',
		targetPart:'Target Partition',
		physicalNode:'Physical Node',
		clusterName:'Virtual Cluster',
		connection:'Connection',
		screenSize:'Screen Size',
		fullScreen:'Full Screen',
		newPart:{
			title:'New Partition',			
			type:'Type',
			preInstalledSoft:'Pre-Install software',			
			vmtype:'VM Partition',
			hpctype:'HPC Partition',
			dctype:'DC Partition',
			stotype:'Storage Partition',
			gertype:'General Partition'
		},
		delPart:{
			title:'Delete Partition'			
		},
		addNewPNNode:{
			title:'New Physical Node',
			privateIp:'Private IP',
			publicIp:'Public IP'			
		},
		rmNewPNNode:{
			title:'Delete Physical Node'			
		},
		clusterOp:{
			createCluster:'New Cluster',
			startCluster:'Start Cluster',
			stopCluster:'Stop Cluster',
			freeCluster:'Destroy Cluster',
			tip4StopCluster:'Are you sure to stop the cluster now?',
			tip4FreeCluster:'Are you sure to destroy the cluster now?',
			unfoldMacInfo:'Unflod Network Information',
			foldMacInfo:'Fold Network Information'
		},
		physicalNodeOp:{
			title:'Operate Physical Node',
			boot:'boot this physical node',
			shutdown:'shutdown this physical node',
			warn:'The physical node has running virtual node. ',
			confirmTip:'Are you sure to '
		},
		virtualNodeOp:{
			title:'Operate Virtual Node',
			save:'save this virtual node',
			start:'start this virtual node',
			stop:'stop this virtual node',
			boot:'boot this virtual node',
			shutdown:'shutdown this virtual node',
			migrate:'migrate this virtual node',
			migrateTitle:'Migrate Virtual Node',
			empty:'No Physical Node',
			destroy:'destroy this virtual node(cannot be undone)',
			confirmTip:'Are you sure to '
		}
	},
	Appliance:{
		desc:'Description',
		loading:'Loading information..',
		newCate:{
			title:'New Category',
			name:'Category Name'
		},
		delCate:{
			title:'Delete Category',
			confirmTip:'Are you sure to delete the category?'
		},
		newAppliance:{
			title:'New Appliance',
			clickToAdd:'Click to add',
			name:'Name',
			disk:'Disk',
			cate:'Category',
			os:'Operating System',
			osversion:'Operating System Version',
			application:'Applications',
			access:'Access Way',
			cpu:'CPU Mount',
			mem:'Memory size',
			lang:'Supported Language',
			loginStyle:'Login Style',
			user:'Username',
			pass:'password'
		},
		modifyAppliance:{
			title:'Modify Appliance',
			clickToAdd:'Click to add',
			name:'Name',
			disk:'Disk',
			cate:'Category',
			os:'Operating System',
			osversion:'Operating System Version',
			application:'Applications',
			access:'Access Way',
			cpu:'CPU Mount',
			mem:'Memory size',
			lang:'Supported Language',
			loginStyle:'Login Style',
			user:'Username',
			pass:'password'
		},
		makeAppliance:{
			title:'Make Appliance',
			newApp:'New Appliance',
			baseApp:'Base Existed Appliance',
			modifyApp:'Modify Appliance',
			clickToAdd:'Click to add',
			app:'Appliance',
			name:'Name',
			vcd:'Disc',
			disk:'Disk',
			cate:'Category',
			os:'Operating System',
			osversion:'Operating System Version',
			application:'Applications',
			access:'Access Way',
			cpu:'CPU Mount',
			mem:'Memory size',
			lang:'Supported Language',
			loginStyle:'Login Style',
			user:'Username',
			pass:'password'
		},
		makeApplianceHelp:{
			title:'The procedure of making appliance is as follows:',
			step1:'Waiting for the preparation for making appliance is ready;',
			step2:'Mount the CD.Please notice that this operation will take effect after next start of the appliance;',
			step3:'Click the start appliance button, the appliance begins to start, when the appliance is closed this operation will make the it start;',
			step4:'Click the stop appliance button, the appliance will be terminated, when any problem is occured during the process, this operation will shut down the appliance;',
			step5:'Click the connect appliance button, can connect to the appliance by VNC;',
			step6:'Click the save appliance button, will save the appliance to template.'
		},
		saveAppliance:{
			title:'Save Appliance',
			clickToAdd:'Click to add',
			name:'Name',
			disk:'Disk',
			cate:'Category',
			os:'Operating System',
			osversion:'Operating System Version',
			application:'Applications',
			access:'Access Way',
			cpu:'CPU Mount',
			mem:'Memory size',
			lang:'Supported Language',
			loginStyle:'Login Style',
			user:'Username',
			pass:'password',
			confirmTip:'<tr><td>Are you sure you want to save this appliance?</td></tr>'
		},
		operateAppliance:{
			title:'Operate Appliance',
			save:'save this appliance',
			stop:'stop this appliance',
			start:'start this appliance',
			confirmTip:'Are you sure to '
		},
		delAppliance:{
			title:'Delete Appliance',
			confirmTip:'Are you sure to delete the appliance?'
		},
		manageAppliance:{
			allCate:'All Category',
			name:'Name',
			os:'Operating System',
			application:'Applications',
			disk:'Capacity',
			size:'Size',
			state:'State',
			detail:'Detail',
			more:'More',
			tip4AddAppliance:'Add Appliance',
			tip4DelAppliance:'Delete Appliance',
			tip4UpdateAppliance:'Update Appliance Information',
			tip4UploadAppliance:'Upload Appliance'
		},
		newVirtualDisc:{
			title:'New Virtual Disc',
			name:'Name',
			location:'Location',
			format:'Format',
			type:'Type',
			os:'Operating System',
			osversion:'Version',
			application:'Applications'
		},
		modifyVirtualDisc:{
			title:'Modify Virtual Disc',
			name:'Name',
			location:'Location',
			format:'Format',
			type:'Type',
			os:'Operating System',
			osversion:'Version',
			application:'Applications'
		},
		changeVirtualDisc:{
			title:'Change Disc',
			name:'Name',
			disc:'Disc',
			tip:'This change will take effect after next start of the appliance!'
		},
		deleteVirtualDisc:{
			title:'Delete virtual Disc',
			confirmTip:'Are you sure you want to delete this disc?'
		}
	},
	Monitor:{
		name:'LingCloud Monitor',				

		service: {
			pcnode:'Host Node',
			cpu:'CPU Load',
			mem:'Memory Usage',
			disk:'Disk Usage',
			net:'Net Traffic',
			ping:'Ping',
			process:'Total Processes',
			xen:'Xend',
			lingcloud:'LingCloud',
			user:'Current Users',
			zombie:'Zombie Process',
			vm:'Virtual Machines',
			
			swap:'Swap Usage',
			http:'HTTP',
			ssh:'SSH',
			mysql:'MySQL'
			
			
		},
		status: {
			ok:		'OK',		
			warn:	'Warning',			
			crit:	'Critical',	
			unkw:	'Unknown'		
		},
		monitorSummary: {
			name:'System Summary',				
			selectTime:'Please select time',
			tips14Move:' machine(s) with the ',
			tips24Move:' ',
			sumInfor: {
				total: 'Total',			
				parNode: 'Partition',		
				hostNode: 'Host Node',		
				
				parHealth:'Health',		
				hostNum: 'Host Num'	
			}
			
		},
		monitorDetail: {
			name:'Detail Infor',		

			tHost: 'Host',		
			tSrv: 'Metric',	
			tStat: 'Status',
			tCheck: 'Last Check Time',	
			tInfor: 'Status Information'		
		},
		vmInfor:{
			vmTittle:'Virtual Machine RunTime Information',
			
			vmInfor:'Virtual Machince',
			vmCpu:'CPU Usage',
			vmMem:'Mem Resource',
			vmDisk:'Disk Usage',
			vmNet:'Network',
			infor:{
				vmName:'VM Name',
				chkTime:'Check Time',
				hostName:'Host Name',
				status:'Status'
			},
			status:{
				r:'Running',
				b:'Blocked',
				d:'Dying',
				c:'Crash',
				s:'Suspend',
				p:'Pause',
				u:'Unknown'
			},
			cpu:{
				vcpu:'Virt CPU',
				cpu:'CPU',
				time:'CPU Time',
				stat:'CPU status',
				usage:'Usage',
				uptime:'Run Time'
			},
			mem:{
				mem:'Usage',
				usage:'Percent',
				max:'Limited',
				maxPer:'Percent'
			},
			disk:{
				usage:'Usage',
				img:'Image',
				fmt:'Format',
				max:'Max',
				dir:'Dir',
				virSize:'Virtual Size',
				size:'Size',
				bak:'Backing File'
			},
			net:{
				nets:'Virt Nets',
				dev:'Device',
				mac:'MAC',
				ip:'Addr',
				tx:'Transmit',
				rx:'Receive'
			}
		},
		monitorMgt: {
			name:'Monitor Manager',					
			perf:'Performance',			
			process:'Process',			
			network:'Network',		
			app:	'Application',			
			user:	'User',			
			select:	'Select/Unselect All',			
			inverse:'Inverse',			
			interval:'Check Interval',			
			item:	'Items/Page',		
			btnSave:'Save',		
			subSucc:'Save successfully.',
			subFail:'Save Failed.',
			timeUnit:'sec'
		},
		
		errors: {
			noRes:'Sorry, no result!',
			noVm:'No virtual machines on node !',
			rppNotValid:'The number of items per page must be a valid integer!',
			refreshIntervalNotValid:'The refresh interval must be a valid integer!',
			hostNameNotValid:'Wrong hostname!',
			itemNameNotValid:'Wrong itemName!'
		}
	},
	calendar:{
			today:'Today',
			confirm:'Confirm',
			cancel:'Cancel',
			sun:'Sun',
			mon:'Mon',
			tues:'Tues',
			wed:'Wed',
			thurs:'Thurs',
			fri:'Fri',
			sat:'Sat',
			jan:'Jan',
			feb:'Feb',
			mar:'Mar',
			apr:'Apr',
			may:'May',
			jun:'Jun',
			jul:'Jul',
			aug:'Aug',
			sep:'Sep',
			oct:'Oct',
			nov:'Nov',
			dec:'Dec'
	},
	error:{
		responseNotFound:'Can\'t get the information:',
		tip:'Tip',
		errorTip:'Error Tip',
		noResult:'None',
		//------------------- infrastruture --------------------
		partNameNotNull:'The partition name should not be null or blank.',
		partIDNotNull:'The partition id should not be null or blank.',
		partNameTooLong:'The length of partition name is too long! (less then 26)',
		partNameBeenUsed:'The partition name has been used, please change it.',
		partControllerNotNull:'The partition controller should not be null or blank.',
		partNameNotFound:'something wrong happened when querying the partition names, please check it.',
		privateIpNotNull:'The private ip should not be null or blank.',
		privateIpNotValid:'The private ip is not valid, please retype.',
		publicIpNotValid:'The public ip is not valid, please retype.',		
		clusterInfoNotNull:'The cluster info should not be null or blank.',
		clusterNotSelect:'No valid cluster selected.',
		partNotSelect:'The partition should be selected.',
		clusterNameNotAssigned:'The cluster name should be assigned.',
		clusterNameTooLong:'The length of cluster name should be less than 20.',
		pnNodeNotNull:'No pnnode selected.',
		virtualNetworkNotValid:'The virtual network guid should be a valid guid.',
		nodeNumNotAssigned:'Please assign the node num.',
		publicIpAssignment:'Please select public ip assignment policy.',
		startAfterCurrent:'The startTime must be after current server time!',
		endAfterStart:'The endTime must be after startTime!',
		endAfterCurrent:'The endTime must be after current server time!',				
		connectTimeout:'Sorry, Connection Timeout.',
		nodeIdNull:'The node guid is blank.',
		nodeTypeNull:'The node type is blank.',
		//--------------end of infrastructure -------
		//-------------- User ----------------
		userNameNotNull:'The user name should not be null or blank.',
		userNameNotValid:'The user name must be a valid email address.',
		passNotNull:'The password should not be null or blank.',
		passRetype:'Please retype the password.',
		passConfirm:'Please check your password.',
		proxyNotNull:'The proxy file should not be null or blank.',
		newPassConfirm:'Please confirm your new password.',
		oldPassNotNull:'The old password should not be null or blank.',
		groupNameNotNull:'The gourp name should not be null or blank.',
		acSaveError:'Inner Error!',
		acSaveSuccess:'Save successfully!',
		acSaveFail:'Save Failed!',
		caUserNameNotNull:'The proxy name must not be null!',
		caValidityNotValid:'The validity must be a valid integer!',
		caTimeTooLong:'The validity is at most 60 year.Please input again.',
		caTimeNotValid:'Invalid input, please check!',
		caNumNotValid:'The number of CA must be a valid integer!',
		caPrefixNotNull:'The prefix of proxy name must not be null!',
		//--------------end of User ----------
		//-------------- Appliance ----------------
		cateNotNull:"The category name should not be null or blank!",
		cateTooLong:"The length of category name should not greater than 20!",
		appIdNotNull:"The appliance should not be null or blank!",
		appNameNotNull:"The appliance name should not be null or blank!",
		appNameTooLong:"The length of appliance name should not greater than 20!",
		appDiskNotNull:"The appliance disk should not be null or blank!",
		appCateNotNull:"The appliance category should not be null or blank!",
		appVcdNotNull:"The Installation CD should not be null or blank!",
		appOsNotNull:"The appliance operation system should not be null or blank!",
		appOsVersionTooLong:"The length of operation system version should not greater than 20!",
		appApplicationTooLong:"The length of application should not greater than 100!",
		appAccessNotNull:"The appliance should have at least one access way!",
		appCpuNotNull:"The appliance CPU amount should not be null or blank!",
		appMemNotNull:"The memory size should not be null or blank!",
		appDiskCapNull:"The disk capacity should not be null or blank!",
		appDiskCapLimt:"The capacity of the disk must be range in 1~128GB!",
		appLangNotNull:"The appliance supported languages should not be null or blank!",
		appLangTooLong:"The length of supported languages should not greater than 100!",
		appLoginStyleNotNull:"The appliance login style should not be null or blank!",
		appUserNotNull:"The appliance username should not be null or blank!",
		appUserTooLong:"The length of username should not greater than 80!",
		appPassNotNull:"The appliance password should not be null or blank!",
		appPassTooLong:"The length of password should not greater than 80!",
		appDescTooLong:"The appliance description should be less than 500 chars!",
		discNameNotNull:"\u5149\u76D8\u540D\u79F0\u4E0D\u5E94\u4E3A\u7A7A!",
		discNameTooLong:"The length of disc name should not greater than 20!",
		discLocNotNull:"The disc location should not be null or blank!",
		discFormatNotNull:"The disc format should not be null or blank!",
		discTypeNotNull:"The disc type should not be null or blank!",
		discOsNotNull:"The appliance operation system should not be null or blank!",
		discOsVersionTooLong:"The length of operation system version should not greater than 20!"
		//--------------Appliance ----------
	}
}