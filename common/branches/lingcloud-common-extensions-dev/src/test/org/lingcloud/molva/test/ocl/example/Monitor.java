/*
 *  @(#)Monitor.java  Jul 27, 2011
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

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.xmm.util.XMMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-7-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class Monitor extends Asset {
	private static final long serialVersionUID = -2886218619176247193L;
	private static final String MONITOR_TYPE = "MonitorType";
	private static final String MONITOR_BRAND = "MonitorBrand";
	private static final String MONITOR_SIZE = "MonitorSize";

	public Monitor() {
		init();
	}
	
	public Monitor(String name, String brand, String size) {
		init();
		this.setName(name);
		this.setBrand(brand);
		this.setSize(size);
	}

	public Monitor(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "Monitor's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
		if (asset.getType() == null || !asset.getType().equals(MONITOR_TYPE)) {
			throw new RuntimeException(
					"The monitor's type should be setted as " + MONITOR_TYPE
							+ ".");
		}
	}

	private void init() {
		super.setAcl(XMMConstants.DEFAULT_ACL);
		super.setType(MONITOR_TYPE);
		super.setAssetController(MonitorAC.class.getName());
	}

	public void setType(int type) {
		super.setType(MONITOR_TYPE);
	}

	public String getType() {
		return super.getType();
	}

	public Monitor clone() throws CloneNotSupportedException {
		try {
			Monitor monitor = new Monitor((Asset) super.clone());
			return monitor;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}
	
	public Asset toAsset() {
		try {
			return (Asset) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(new Exception(
					"convert to Asset failed due to : " + e));
		}
	}
	
	public void setBrand(String brand) {
		super.getAttributes().put(MONITOR_BRAND, brand);
	}
	
	public String getBrand() {
		return super.getAttributes().get(MONITOR_BRAND);
	}
	
	public void setSize(String size) {
		super.getAttributes().put(MONITOR_SIZE, size);
	}
	
	public String getSize() {
		return super.getAttributes().get(MONITOR_SIZE);
	}
}
