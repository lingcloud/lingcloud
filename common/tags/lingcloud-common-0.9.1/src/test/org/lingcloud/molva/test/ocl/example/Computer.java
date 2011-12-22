/*
 *  @(#)Computer.java  Jul 27, 2011
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
package org.lingcloud.molva.test.ocl.example;

import java.util.HashMap;
import java.util.Iterator;

import org.lingcloud.molva.ocl.lease.Lease;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2011-07-27<br>
 * @author Ruijian Wang<br>
 *
 */
public class Computer extends Lease {
	private static final long serialVersionUID = -1201435239620537744L;
	private static final String COMPUTER_TYPE = "ComputerType";
	private static final String MONITOR_BRAND = "MonitorBrand";
	private static final String MONITOR_SIZE = "MonitorSize";
	private static final String MAINFRAME_BRAND = "MainframeBrand";
	private static final String MAINFRAME_MEMORY = "MainframeMemory";

	
	public Computer() {
		init();
	}
	
	public Computer(Lease lease) {
		super(lease);
		if (lease.getType() == null
				|| !lease.getType().equals(
						COMPUTER_TYPE)) {
			throw new RuntimeException(
					"The Computer's type should be setted as "
							+ COMPUTER_TYPE + ".");
		}
	}
	
	private void init() {
		super.setType(COMPUTER_TYPE);
		super.setAcl(XMMConstants.DEFAULT_ACL);
		super.setAssetMatchMaker(ComputerAMM.class.getName());
	}
	
	public void setType(int type) {
		super.setType(COMPUTER_TYPE);
	}

	public String getType() {
		return super.getType();
	}
	
	public Computer clone() throws CloneNotSupportedException {
		try {
			Computer computer = new Computer((Lease) super.clone());
			return computer;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}
	
	public Lease toLease() {
		try {
			return (Lease) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(new Exception(
					"convert to Lease failed due to : " + e));
		}
	}
	
	public void setMonitorBrand(String brand) {
		super.getAdditionalTerms().put(MONITOR_BRAND, brand);
	}
	
	public String getMonitorBrand() {
		return super.getAdditionalTerms().get(MONITOR_BRAND);
	}
	
	public void setMonitorSize(String size) {
		super.getAdditionalTerms().put(MONITOR_SIZE, size);
	}
	
	public String getMonitorSize() {
		return super.getAdditionalTerms().get(MONITOR_SIZE);
	}
	
	public void setMainframeBrand(String brand) {
		super.getAdditionalTerms().put(MAINFRAME_BRAND, brand);
	}
	
	public String getMainframeBrand() {
		return super.getAdditionalTerms().get(MAINFRAME_BRAND);
	}
	
	public void setMainframeMemory(String brand) {
		super.getAdditionalTerms().put(MAINFRAME_MEMORY, brand);
	}
	
	public String getMainframeMemory() {
		return super.getAdditionalTerms().get(MAINFRAME_MEMORY);
	}
	
	public void setMonitorId(String monitorId) {
		if (monitorId == null || "".equals(monitorId)) {
			return;
		}
		this.getAssetIdAndTypeMap().put(monitorId, Monitor.class.getName());
	}
	
	public String getMonitorId() {
		HashMap<String, String> aitm = this.getAssetIdAndTypeMap();
		if (aitm.containsValue(Monitor.class.getName())) {
			Iterator<String> iterator = aitm.keySet().iterator();
			while (iterator.hasNext()) {
				String id = (String) iterator.next();
				String type = aitm.get(id);
				if (Monitor.class.getName().equals(type)) {
					return id;
				}
			}
		}
		return null;
	}
	
	public void setMainframeId(String mainframeId) {
		if (mainframeId == null || "".equals(mainframeId)) {
			return;
		}
		this.getAssetIdAndTypeMap().put(mainframeId, Mainframe.class.getName());
	}
	
	public String getMainframeId() {
		HashMap<String, String> aitm = this.getAssetIdAndTypeMap();
		if (aitm.containsValue(Mainframe.class.getName())) {
			Iterator<String> iterator = aitm.keySet().iterator();
			while (iterator.hasNext()) {
				String id = (String) iterator.next();
				String type = aitm.get(id);
				if (Mainframe.class.getName().equals(type)) {
					return id;
				}
			}
		}
		return null;
	}
}
