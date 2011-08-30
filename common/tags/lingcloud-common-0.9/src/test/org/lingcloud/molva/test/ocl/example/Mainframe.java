/*
 *  @(#)InputDevice.java  Jul 27, 2011
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
public class Mainframe extends Asset {
	private static final long serialVersionUID = 371456718539575902L;
	private static final String MAINFRAME_TYPE = "MainframeType";
	private static final String MAINFRAME_BRAND = "MainframeBrand";
	private static final String MAINFRAME_MEMORY = "MainframeMemory";

	public Mainframe() {
		init();
	}
	
	public Mainframe(String name, String brand, String memory) {
		init();
		super.setName(name);
		this.setBrand(brand);
		this.setMemory(memory);
	}

	public Mainframe(Asset asset) {
		super(asset);
		if (asset.getAssetController() == null) {
			String msg = "Mainframe's asset controller should not be null.";
			throw new RuntimeException(msg);
		}
		if (asset.getType() == null
				|| !asset.getType().equals(MAINFRAME_TYPE)) {
			throw new RuntimeException(
					"The mainframe's type should be setted as "
							+ MAINFRAME_TYPE + ".");
		}
	}

	private void init() {
		super.setAcl(XMMConstants.DEFAULT_ACL);
		super.setType(MAINFRAME_TYPE);
		super.setAssetController(MainframeAC.class.getName());
	}

	public void setType(int type) {
		super.setType(MAINFRAME_TYPE);
	}

	public String getType() {
		return super.getType();
	}

	public Mainframe clone() throws CloneNotSupportedException {
		try {
			Mainframe mainframe = new Mainframe((Asset) super.clone());
			return mainframe;
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
		super.getAttributes().put(MAINFRAME_BRAND, brand);
	}
	
	public String getBrand() {
		return super.getAttributes().get(MAINFRAME_BRAND);
	}
	
	public void setMemory(String memory) {
		super.getAttributes().put(MAINFRAME_MEMORY, memory);
	}
	
	public String getMemory() {
		return super.getAttributes().get(MAINFRAME_MEMORY);
	}
}
