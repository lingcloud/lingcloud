/*
 *  @(#)VirtualAppliance.java  2010-5-20
 *
 *  Copyright (C) 2008-2011,
 *  LingCloud Team,
 *  Institute of Computing Technology,
 *  Chinese Academy of Sciences.
 *  P.O.Box 2704, 100190, Beijing, China.
 *
 *  http://lingcloud.org
 *  
 */

package org.lingcloud.molva.xmm.vam.pojos;

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;


/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-20<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VirtualAppliance extends VAObject {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -7872022490955180528L;

	/**
	 * the constructor.
	 */
	public VirtualAppliance() {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE);
	}
	
	/**
	 * to construct a VAInstance from a right type.
	 * 
	 * @param vao
	 *            the basic object of virtual appliance
	 */
	public VirtualAppliance(VAObject vao) {
		super(vao);
		if (vao != null
				&& !(vao.getType()
						.equals(VAMConstants.VIRTUAL_APPLIANCE))) {
			String msg = "Wrong virtual appliance object type. It should be "
					+ VAMConstants.VIRTUAL_APPLIANCE + " but get "
					+ vao.getType();
			throw new RuntimeException(msg);
		}
	}
	
	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public VirtualAppliance clone() throws CloneNotSupportedException {
		try {
			VirtualAppliance va = new VirtualAppliance(this);
			return va;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}

	/**
	 * set the name. It must override the method that make sure the
	 * object has right name.
	 * 
	 * @param name
	 *            the name of appliance.
	 */
	public void setName(String name) {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE);
	}
	
	/**
	 * set the type. It must override the method that make sure the
	 * object has right type.
	 * 
	 * @param type
	 *            the type of appliance.
	 */
	public void setType(String type) {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE);
	}
	
	/**
	 * get the parent appliance GUID.
	 * @return the parent appliance GUID.
	 */
	public String getParent() {
		return (String) this.getAttributes().get(VAMConstants.VA_PARENT);
	}

	/**
	 * set the parent appliance GUID.
	 * @param parent
	 * 		the parent appliance GUID.
	 */
	public void setParent(String parent) {
		this.getAttributes().put(VAMConstants.VA_PARENT, parent);
	}
	
	/**
	 * get reference.
	 * @return reference
	 */
	public int getRef() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_REF))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_REF));
	}

	/**
	 * set reference.
	 * @param ref
	 * 		reference
	 */
	public void setRef(int ref) {
		if (ref < 0) {
			ref = 0;
		}
		this.getAttributes().put(VAMConstants.VA_REF, "" + ref);
	}
	
	/**
	 * get disks.
	 * @return disk list
	 */
	public List<String> getDisks() {
		List<String> diskl = new ArrayList<String>();
		String disks = (String) this.getAttributes().
			get(VAMConstants.VA_DISKS);
		if (disks == null) {
			return diskl;
		}
		String[] appArray = disks.split("\\|");
		
		for (int i = 0; i < appArray.length; i++) {
			diskl.add(appArray[i]);
		}
		return diskl;
	}
	
	/**
	 * set disks.
	 * @param diskl
	 * 		disk list
	 */
	public void setDisks(List<String> diskl) {
		if (diskl == null) {
			return;
		}
		String disks = "";
		int size = diskl.size();
		if (diskl.size() > 0) {
			disks = diskl.get(0);
			for (int i = 1; i < size; i++) {
				if (diskl.get(i) != null) {
					disks += "|" + diskl.get(i);
				}
				
			}
		}
		
		this.getAttributes().put(VAMConstants.VA_DISKS, disks);
	}
	
	/**
	 * get CDs.
	 * @return CD list
	 */
	public List<String> getDiscs() {
		List<String> diskl = new ArrayList<String>();
		String disks = (String) this.getAttributes().
			get(VAMConstants.VA_DISCS);
		if (disks == null || disks.equals("")) {
			return diskl;
		}
		String[] appArray = disks.split("\\|");
		
		for (int i = 0; i < appArray.length; i++) {
			diskl.add(appArray[i]);
		}
		return diskl;
	}
	
	/**
	 * set CDs.
	 * @param discl
	 * 		CD list
	 */
	public void setDiscs(List<String> discl) {
		if (discl == null) {
			return;
		}
		String disks = "";
		int size = discl.size();
		if (discl.size() > 0) {
			disks = discl.get(0);
			for (int i = 1; i < size; i++) {
				if (discl.get(i) != null) {
					disks += "|" + discl.get(i);
				}
				
			}
		}
		
		this.getAttributes().put(VAMConstants.VA_DISCS, disks);
	}
	
	/**
	 * get appliance name.
	 * @return appliance name
	 */
	public String getVAName() {
		return (String) this.getAttributes().get(VAMConstants.VA_NAME);
	}

	/**
	 * set appliance name.
	 * @param name
	 * 		appliance name
	 */
	public void setVAName(String name) {
		this.getAttributes().put(VAMConstants.VA_NAME, name);
	}
	
	/**
	 * get the capacity of appliance.
	 * @return the capacity of appliance
	 */
	public long getCapacity() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_CAPACITY))) {
			return 0;
		}
		return Long.parseLong((String) this.getAttributes().get(
				VAMConstants.VA_CAPACITY));
	}

	/**
	 * set the capacity of appliance.
	 * @param capacity
	 * 		the capacity of appliance
	 */
	public void setCapacity(long capacity) {
		this.getAttributes().put(VAMConstants.VA_CAPACITY, "" + capacity);
	}
	
	/**
	 * get the physical size of appliance.
	 * @return the physical size of appliance
	 */
	public long getSize() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_SIZE))) {
			return 0;
		}
		return Long.parseLong((String) this.getAttributes().get(
				VAMConstants.VA_SIZE));
	}

	/**
	 * set the physical size of appliance.
	 * @param size
	 * 		the physical size of appliance
	 */
	public void setSize(long size) {
		this.getAttributes().put(VAMConstants.VA_SIZE, "" + size);
	}
	
	/**
	 * get the configuration of appliance.
	 * @return the configuration of appliance
	 */
	public String getConfig() {
		return (String) this.getAttributes().get(VAMConstants.VA_CONFIG);
	}

	/**
	 * set the configuration of appliance.
	 * @param config
	 * 		the configuration of appliance
	 */
	public void setConfig(String config) {
		this.getAttributes().put(VAMConstants.VA_CONFIG, config);
	}
	
	/**
	 * get the format of virtual machine image.
	 * @return the format of virtual machine image
	 */
	public String getFormat() {
		return (String) this.getAttributes().get(VAMConstants.VA_FORMAT);
	}
	
	/**
	 * set the format of virtual machine image.
	 * @param format
	 *		the format of virtual machine image
	 */
	public void setFormat(String format) {
		this.getAttributes().put(VAMConstants.VA_FORMAT, format);
	}
	
	/**
	 * get the category of appliance.
	 * @return the category of appliance
	 */
	public String getCategory() {
		return (String) this.getAttributes().get(VAMConstants.VA_CATEGORY);
	}

	/**
	 * set the category of appliance.
	 * @param category
	 * 		the category of appliance
	 */
	public void setCategory(String category) {
		this.getAttributes().put(VAMConstants.VA_CATEGORY, category);
	}

	/**
	 * get the virtual machine boot loader.
	 * @return the virtual machine boot loader
	 */
	public String getBootLoader() {
		return (String) this.getAttributes().get(VAMConstants.VA_BOOTLOAD);
	}

	/**
	 * set the virtual machine boot loader.
	 * @param bootLoader
	 * 		the virtual machine boot loader
	 */
	public void setBootLoader(String bootLoader) {
		this.getAttributes().put(VAMConstants.VA_BOOTLOAD, bootLoader);
	}

	/**
	 * get the supported languages of appliance.
	 * @return the supported languages of appliance
	 */
	public List<String> getLanguages() {
		List<String> langl = new ArrayList<String>();
		String langs = (String) this.getAttributes().
		get(VAMConstants.VA_LANGUAGES);
		if (langs == null) {
			return langl;
		}
		String[] langArray = langs.split("\\|");
		
		for (int i = 0; i < langArray.length; i++) {
			langl.add(langArray[i]);
		}
		return langl;
	}
	
	/**
	 * set the supported languages of appliance.
	 * @param languages
	 * 		the supported languages of appliance
	 */
	public void setLanguages(List<String> languages) {
		if (languages == null) {
			return;
		}
		String langs = "";
		int size = languages.size();
		if (languages.size() > 0) {
			langs = languages.get(0);
			for (int i = 1; i < size; i++) {
				if (languages.get(i) != null) {
					langs += "|" + languages.get(i);
				}
				
			}
		}
		
		this.getAttributes().put(VAMConstants.VA_LANGUAGES, langs);
	}
	
	/**
	 * get the login style of appliance.
	 * @return the login style of appliance
	 */
	public int getLoginStyle() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_LOGIN_STYLE))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_LOGIN_STYLE));
	}
	
	/**
	 * set the login style of appliance.
	 * @param loginSytle
	 * 		the login style of appliance
	 */
	public void setLoginStyle(int loginSytle) {
		this.getAttributes().put(VAMConstants.VA_LOGIN_STYLE, "" + loginSytle);
	}
	
	/**
	 * set the user name of appliance.
	 * @return the user name of appliance
	 */
	public String getUsername() {
		return (String) this.getAttributes().get(VAMConstants.VA_USERNAME);
	}
	
	/**
	 * set the user name of appliance.
	 * @param username
	 * 		the user name of appliance
	 */
	public void setUsername(String username) {
		this.getAttributes().put(VAMConstants.VA_USERNAME, username);
	}
	
	/**
	 * set the password of appliance.
	 * @return the password of appliance
	 */
	public String getPassword() {
		return (String) this.getAttributes().get(VAMConstants.VA_PASSWORD);
	}
	
	/**
	 * set the password of appliance.
	 * @param password
	 * 		the password of appliance
	 */
	public void setPassword(String password) {
		this.getAttributes().put(VAMConstants.VA_PASSWORD, password);
	}
	
	/**
	 * get the CPU amount of appliance.
	 * @return the CPU amount of appliance
	 */
	public int getCpuAmount() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_CPU_AMOUNT))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_CPU_AMOUNT));
	}

	/**
	 * set the CPU amount of appliance.
	 * @param cpuAmount
	 * 		the CPU amount of appliance
	 */
	public void setCpuAmount(int cpuAmount) {
		this.getAttributes().put(VAMConstants.VA_CPU_AMOUNT, "" + cpuAmount);
	}
	
	/**
	 * get the operating system of appliance.
	 * @return the operating system of appliance
	 */
	public String getOs() {
		String os = (String) this.getAttributes().get(VAMConstants.VA_OS);
		if (os == null) {
			return "";
		}
		String[] osArray = os.split("\\|");
		if (osArray.length > 0) {
			return osArray[0];
		}
		return "";
	}
	
	/**
	 * get the operating system version of appliance.
	 * @return the operating system version of appliance
	 */
	public String getOsVersion() {
		String os = (String) this.getAttributes().get(VAMConstants.VA_OS);
		if (os == null) {
			return "";
		}
		String[] osArray = os.split("\\|");
		if (osArray.length > 1) {
			return osArray[1];
		}
		return "";
	}

	/**
	 * set the operating system of appliance.
	 * @param os
	 * 		operating system name
	 * @param osVersion
	 * 		operation system version
	 */
	public void setOs(String os, String osVersion) {
		this.getAttributes().put(VAMConstants.VA_OS, os + "|" + osVersion);
	}
	
	/**
	 * get the ways accessing the virtual machine created by this appliance.
	 * @return the ways accessing the virtual machine
	 */
	public List<String> getAccessWay() {
		List<String> accessWayList = new ArrayList<String>();
		String acceseWays = (String) this.getAttributes().
			get(VAMConstants.VA_ACCESS_WAY);
		if (acceseWays == null) {
			return accessWayList;
		}
		String[] appArray = acceseWays.split("\\|");
		
		for (int i = 0; i < appArray.length; i++) {
			accessWayList.add(appArray[i]);
		}
		return accessWayList;
	}

	/**
	 * set the ways accessing the virtual machine created by this appliance.
	 * @param accessWayList
	 * 		the ways accessing the virtual machine
	 */
	public void setAccessWay(List<String> accessWayList) {
		if (accessWayList == null) {
			return;
		}
		String accessWays = "";
		int size = accessWayList.size();
		if (size > 0) {
			accessWays = accessWayList.get(0);
			for (int i = 1; i < size; i++) {
				if (accessWayList.get(i) != null) {
					accessWays += "|" + accessWayList.get(i);
				}
				
			}
		}
		
		this.getAttributes().put(VAMConstants.VA_ACCESS_WAY, accessWays);
	}
	
	/**
	 * get the application list installed in the appliance.
	 * @return the application list installed in the appliance.
	 */
	public List<String> getApplications() {
		List<String> appl = new ArrayList<String>();
		String apps = (String) this.getAttributes().
			get(VAMConstants.VA_APPLICATIONS);
		if (apps == null) {
			return appl;
		}
		String[] appArray = apps.split("\\|");
		
		for (int i = 0; i < appArray.length; i++) {
			appl.add(appArray[i]);
		}
		return appl;
	}
	
	/**
	 * set the application list installed in the appliance.
	 * @param applications
	 * 		the application list installed in the appliance
	 */
	public void setApplications(List<String> applications) {
		if (applications == null) {
			return;
		}
		String apps = "";
		int size = applications.size();
		if (applications.size() > 0) {
			apps = applications.get(0);
			for (int i = 1; i < size; i++) {
				if (applications.get(i) != null) {
					apps += "|" + applications.get(i);
				}
				
			}
		}
		
		this.getAttributes().put(VAMConstants.VA_APPLICATIONS, apps);
	}
	
	/**
	 * get the memory size of appliance.
	 * @return the memory size of appliance
	 */
	public int getMemory() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_MEMORY))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_MEMORY));
	}

	/**
	 * set the memory size of appliance.
	 * @param memorySize
	 * 		the memory size of appliance
	 */
	public void setMemory(int memorySize) {
		this.getAttributes().put(VAMConstants.VA_MEMORY, "" + memorySize);
	}
	
	/**
	 * get the port connecting to virtual machine by VNC.
	 * @return vnc port
	 */
	public int getVncPort() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_VNC_PORT))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_VNC_PORT));
	}

	/**
	 * set the port connecting to virtual machine by VNC.
	 * @param vncPort
	 * 		vnc port
	 */
	public void setVncPort(int vncPort) {
		this.getAttributes().put(VAMConstants.VA_VNC_PORT, "" + vncPort);
	}
	
	/**
	 * get the name of virtual machine.
	 * @return the name of virtual machine
	 */
	public String getVmName() {
		return (String) this.getAttributes().get(VAMConstants.VA_VM_NAME);
	}

	/**
	 * set the name of virtual machine.
	 * @param vmName
	 * 		the name of virtual machine
	 */
	public void setVmName(String vmName) {
		this.getAttributes().put(VAMConstants.VA_VM_NAME, vmName);
	}
	
	/**
	 * get the selected virtual machine.
	 * @return the selected virtual machine
	 */
	public int getSelectVm() {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.VA_SELECT_VM))) {
			return 0;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.VA_SELECT_VM));
	}
	
	/**
	 * set the selected virtual machine.
	 * @param selectVm
	 * 		the selected virtual machine
	 */
	public void setSelectVm(int selectVm) {
		this.getAttributes().put(VAMConstants.VA_SELECT_VM, "" + selectVm);
	}

	/**
	 * get a replica.
	 * @return appliance
	 */
	public VirtualAppliance getCopy() {
		VirtualAppliance appliance = new VirtualAppliance();
		
		appliance.setAccessWay(getAccessWay());
		appliance.setApplications(getApplications());
		appliance.setBootLoader(getBootLoader());
		appliance.setCapacity(getCapacity());
		appliance.setCategory(getCategory());
		appliance.setCpuAmount(getCpuAmount());
		appliance.setDescription(getDescription());
		appliance.setFormat(getFormat());
		appliance.setLanguages(getLanguages());
		appliance.setLoginStyle(getLoginStyle());
		appliance.setMemory(getMemory());
		appliance.setOs(getOs(), getOsVersion());
		appliance.setPassword(getPassword());
		appliance.setSize(getSize());
		appliance.setVAName(getVAName());
		appliance.setUsername(getUsername());
		
		return appliance;
	}
}
