/*
 *  @(#)DeviceFactory.java  2011-07-27
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetConstants.AssetState;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-07-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class DeviceFactory {
	private static DeviceFactory instance = null;
	private static Object lock = new Object();
	private Map<String, Asset> devices = null;

	private DeviceFactory() {
		devices = new HashMap<String, Asset>();
	}

	public static DeviceFactory getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DeviceFactory();
				}
			}
		}

		return instance;
	}

	public void addDevice(Asset device) {
		devices.put(device.getGuid(), device);
	}
	
	public void setStateOfDevice(String key, AssetState state) {
		Asset asset = devices.get(key);
		if (asset != null) {
			asset.setAssetState(state);
		}
	}

	public List<Monitor> getMonitors(Monitor monitor) {
		List<Monitor> res = new ArrayList<Monitor>();

		if (devices != null) {
			for (String key : devices.keySet()) {
				Asset asset = devices.get(key);
				if (asset.getClass().getName().equals(
						Monitor.class.getName())) {
					Monitor device = new Monitor(asset);
					if (monitor.getBrand().equals(device.getBrand())
							&& monitor.getSize().equals(device.getSize())) {
						res.add(device);
					}

				}
			}
		}

		return res;
	}

	public List<Mainframe> getMainframes(Mainframe mainframe) {
		List<Mainframe> res = new ArrayList<Mainframe>();

		if (devices != null) {
			for (String key : devices.keySet()) {
				Asset asset = devices.get(key);
				if (asset.getClass().getName()
						.equals(Mainframe.class.getName())) {
					Mainframe device = new Mainframe(asset);
					if (mainframe.getBrand().equals(device.getBrand())
							&& mainframe.getMemory().equals(
									device.getMemory())) {
						res.add(device);
					}

				}
			}
		}

		return res;
	}

	public List<Asset> getAllDevices() {
		List<Asset> res = new ArrayList<Asset>();

		if (devices != null) {
			for (String key : devices.keySet()) {
				res.add(devices.get(key));
			}
		}

		return res;
	}
}
