/*
 *  @(#)VAService.java  2010-7-22
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

package org.lingcloud.molva.xmm.vam.services;

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.xmm.vam.daos.DaoFactory;
import org.lingcloud.molva.xmm.vam.daos.VAFileDao;
import org.lingcloud.molva.xmm.vam.daos.VirtualApplianceDao;
import org.lingcloud.molva.xmm.vam.pojos.VAConfig;
import org.lingcloud.molva.xmm.vam.pojos.VADisk;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.util.DiskInfo;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-7-22<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAService {
	/**
	 * single instance design pattern.
	 */
	private static VAService instance = null;

	/**
	 * get virtual appliance service instance.
	 * @return VAService instance
	 */
	public static VAService getInstance() {
		if (instance == null) {
			instance = new VAService();
		}
		return instance;
	}

	private VAService() {

	}

	/**
	 * add appliance to the system.
	 * 
	 * @param appliance
	 *            the appliance object
	 * @param filepath
	 *            the path of the appliance file
	 * @return if add successfully, return appliance object, else return null
	 * @throws Exception
	 */
	public VirtualAppliance addAppliance(VirtualAppliance appliance,
			String filepath) throws Exception {
		return addAppliance(appliance, filepath, true);
	}

	/**
	 * add appliance to the system.
	 * 
	 * @param appliance
	 *            the appliance object
	 * @param filepath
	 *            the path of the appliance file
	 * @param deleteFile
	 *            whether delete the source file after add the appliance
	 * @return if add successfully, return appliance object, else return null
	 * @throws Exception
	 */
	public VirtualAppliance addAppliance(VirtualAppliance appliance,
			String filepath, boolean deleteFile)
			throws Exception {
		VAFileService fileService = ServiceFactory.getFileService();
		// create a new file
		VAFile disk = fileService.createFile(appliance.getVAName(), null,
				VAMConstants.VAF_FILE_TYPE_DISK, appliance.getFormat(), 0);

		String guid = VAMUtil.genGuid();
		appliance.setGuid(guid);
		appliance.setParent(VAMConstants.NULL);
		appliance.setRef(0);
		appliance.setState(VAMConstants.STATE_CONVERTING);
		List<String> diskl = new ArrayList<String>();
		diskl.add(disk.getGuid());
		appliance.setDisks(diskl);

		// get appliance data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		// add the appliance to the database
		appliance = vaDao.add(appliance);
		if (appliance == null) {
			throw new Exception("Can't add the appliance to the database.");
		}

		try {
			// add file
			disk.setState(VAMConstants.STATE_READY);
			fileService.addFile(disk, filepath, deleteFile);
		} catch (Exception e) {
			// remove file and appliance
			try {
				fileService.removeFile(null, null, disk.getGuid());
				vaDao.remove(appliance.getGuid());
			} catch (Exception ex) {
				VAMUtil.outputLog(ex.getMessage());
			}
			throw new Exception(e);
		}

		return appliance;
	}

	/**
	 * update the appliance.
	 * 
	 * @param appliance
	 *            appliance object
	 * @return the updated appliance
	 * @throws Exception
	 */
	public VirtualAppliance updateAppliance(VirtualAppliance appliance)
			throws Exception {
		// get appliance data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		appliance = vaDao.update(appliance);
		if (appliance == null) {
			throw new Exception("Can't update the appliance");
		}

		return appliance;
	}

	/**
	 * remove the appliance with GUID.
	 * 
	 * @param guid
	 *            appliance's GUID
	 * @return whether the appliance is removed
	 * @throws Exception
	 */
	public boolean removeAppliance(String guid) throws Exception {
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		// query the appliance by GUID
		VirtualAppliance va = vaDao.query(guid);
		if (va == null) {
			throw new Exception("The appliance with guid \"" + guid
					+ "\" is not existed");
		}

		// check the appliance state
		checkState(va);

		// if the appliance is being made, stop it
		if (va.getState() == VAMConstants.STATE_MAKING) {
			try {
				operateAppliance(va.getGuid(),
						VAMConstants.APPLIANCE_OPERATION_STOP);
			} catch (Exception e) {
				VAMUtil.outputLog(e.getMessage());
			}
		}

		// get the appliance's disk
		List<String> diskl = va.getDisks();
		String diskGuid = diskl.get(0);

		// remove file
		ServiceFactory.getFileService().removeFile(null, null, diskGuid);
		removeConfig(va);

		// if the appliance is being made, release virtual machine it uses
		if (va.getState() == VAMConstants.STATE_MAKING) {
			VAMUtil.releaseMakeApplianceVM(va.getSelectVm());
		}
		// remove the appliance in the database
		va.setState(VAMConstants.STATE_DELETING);
		va.setSelectVm(VAMConstants.NO_MAKE_APPLIANCE_VM);
		vaDao.update(va);
		vaDao.remove(va.getGuid());

		return true;
	}

	/**
	 * remove the appliance configuration data and file.
	 * 
	 * @param appliance
	 *            appliance object
	 * @throws Exception
	 */
	private void removeConfig(VirtualAppliance appliance) throws Exception {
		if (appliance == null) {
			return;
		}
		String confGuid = appliance.getConfig();
		if (confGuid != null) {
			try {
				// get configuration object
				VAFile fileConfig = ServiceFactory.getFileService().queryFile(
						confGuid);
				VAConfig config = new VAConfig();
				config.setPath(fileConfig.getSavePath());

				// remove configuration file
				ServiceFactory.getFileService()
						.removeFile(null, null, confGuid);

				// remove XEN configuration file
				VAMUtil.deleteFile(config.getXenCfgPath());
			} catch (Exception e) {
				VAMUtil.outputLog(e.getMessage());
			}
		}
	}

	/**
	 * query the appliance with GUID.
	 * 
	 * @param guid
	 *            appliance's GUID
	 * @return the queried appliance
	 * @throws Exception
	 */
	public VirtualAppliance queryAppliance(String guid) throws Exception {
		// get file data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		VirtualAppliance va = vaDao.query(guid);

		if (va == null) {
			throw new Exception("The appliance with guid \"" + guid
					+ "\" is not existed");
		}

		checkState(va);

		return va;
	}

	/**
	 * get all the appliances.
	 * 
	 * @return appliance list
	 * @throws Exception
	 */
	
	public List<VirtualAppliance> getAllAppliance() throws Exception {
		// get appliance data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		List<VirtualAppliance> resl = new ArrayList<VirtualAppliance>();
		List<VAObject> filel = vaDao.getAll();
		if (filel != null) {
			int size = filel.size();
			for (int i = 0; i < size; i++) {
				VirtualAppliance va = new VirtualAppliance(filel.get(i));

				try {
					va.setState(checkState(va));
				} catch (Exception e) {
					VAMUtil.outputLog(e.getMessage());
				}

				resl.add(va);
			}
		}

		return resl;
	}

	/**
	 * get making appliances.
	 * 
	 * @return appliance list
	 * @throws Exception
	 */
	
	public List<VirtualAppliance> getMakingAppliances() throws Exception {
		// get appliance data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		List<VirtualAppliance> resl = new ArrayList<VirtualAppliance>();
		List<VAObject> filel = vaDao.getMakingAppliances();
		if (filel != null) {
			int size = filel.size();
			for (int i = 0; i < size; i++) {
				resl.add(new VirtualAppliance(filel.get(i)));
			}
		}

		return resl;
	}

	/**
	 * get the appliances whose category GUID is cateGuid.
	 * 
	 * @param cateGuid
	 *            the GUID of category
	 * @return the appliance list
	 * @throws Exception
	 */
	
	public List<VirtualAppliance> getAppliancesByCategory(String cateGuid) 
		throws Exception {
		// get appliance data access object
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		List<VirtualAppliance> resl = new ArrayList<VirtualAppliance>();
		List<VAObject> filel = vaDao.getAppliancesByCategory(cateGuid);
		if (filel != null) {
			int size = filel.size();
			for (int i = 0; i < size; i++) {
				VirtualAppliance va = new VirtualAppliance(filel.get(i));
				// if (va.getState() != VAMConstants.STATE_READY) {
				try {
					va.setState(checkState(va));
				} catch (Exception e) {
					VAMUtil.outputLog(e.getMessage());
				}
				// }
				resl.add(va);
			}
		}

		return resl;
	}

	/**
	 * make appliance.
	 * 
	 * @param appliance
	 *            appliance object
	 * @param existedAppGuid
	 *            the GUID of an existed appliance, is GUID = null, make a new
	 *            appliance
	 * @param memSize
	 *            the memory size of virtual machine making the appliance
	 * @return appliance object
	 * @throws Exception
	 */
	public VirtualAppliance makeAppliance(VirtualAppliance appliance,
			String existedAppGuid, int memSize) throws Exception {
		if (appliance == null) {
			throw new Exception("The appliance should not be empty");
		}
		// get a idle virtual machine
		int selectVM = VAMUtil.getIdleMakeApplianceVM();
		if (selectVM == VAMConstants.NO_MAKE_APPLIANCE_VM) {
			throw new Exception("There is no more virtual machine to "
					+ "make the appliance");
		}

		// get the existed appliance
		VirtualAppliance existedApp = null;
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		
		if (existedAppGuid != null) {
			existedApp = vaDao.query(existedAppGuid);
			if (existedApp == null
					|| checkState(existedApp) != VAMConstants.STATE_READY) {
				throw new Exception("The appliance with guid \""
						+ existedAppGuid + "\" is not existed or available");
			} else if (appliance.getGuid() != null 
					&& appliance.getGuid().equals(existedAppGuid)) {
				appliance = existedApp;
			}
		}
		
		
		// create a configuration
		VAFileService fileService = ServiceFactory.getFileService();

		VAFile config = fileService.createFile(appliance.getVAName(), null,
				VAMConstants.VAF_FILE_TYPE_CONFIG,
				VAMConstants.VAF_FORMAT_TEXT, 0);

		// set memory size
		appliance.setMemory(memSize);
		// set virtual machine information
		appliance.setConfig(config.getGuid());
		appliance.setVncPort(VAMConfig.getMakeApplianceVMVncPort(selectVM));
		appliance.setVmName(VAMConfig.getMakeApplianceVMName(selectVM));
		appliance.setSelectVm(selectVM);
		
		appliance.setState(VAMConstants.STATE_MAKING);
		VAFile disk = null;
		
		if (appliance.getGuid() == null 
				|| !appliance.getGuid().equals(existedAppGuid)) {
			// create a disk
			disk = fileService.createFile(appliance.getVAName(), null,
					VAMConstants.VAF_FILE_TYPE_DISK, appliance.getFormat(), 0);
			
			// set the key information
			String guid = VAMUtil.genGuid();
			appliance.setGuid(guid);
			appliance.setParent(VAMConstants.NULL);
			appliance.setRef(0);
			List<String> diskl = new ArrayList<String>();
			diskl.add(disk.getGuid());
			appliance.setDisks(diskl);
			if (existedApp != null) {
				appliance.setOs(existedApp.getOs(), existedApp.getOsVersion());
				appliance.setBootLoader(existedApp.getBootLoader());
				appliance.setCapacity(existedApp.getCapacity());
				appliance.setCpuAmount(existedApp.getCpuAmount());
			}
			
			// add the appliance to the database
			appliance = vaDao.add(appliance);
			if (appliance == null) {
				throw new Exception("Can't add the appliance to the database.");
			}
		}

		long capacity = 0;

		try {
			if (existedAppGuid == null) {
				// create disk
				capacity = appliance.getCapacity();
				fileService.createDisk(disk, capacity);
			} else {
				
				VAFile existedDisk = fileService.queryFile(existedApp.getDisks()
						.get(0));
				if (!appliance.getGuid().equals(existedAppGuid)) {
					// create snapshot
					capacity = existedApp.getCapacity();
					fileService.createSnapshot(existedDisk, disk, VAMConfig
							.getMakeApplianceHost(), VAMConfig
							.getMakeApplianceUser());
				} else {
					if (fileService.getActiveReference(
							existedDisk.getGuid()) > 0) {
						throw new Exception("The appliance is in used!");
					}
					disk = existedDisk;
					fileService.removeRedundantDisk(0, existedDisk.getGuid());
					vaDao.update(appliance);
				}
				
			}

			// add configuration file
			addConfig(appliance, selectVM, disk, config);

			// allocate virtual machine
			selectVM = VAMUtil.allocateMakeApplianceVM();
			if (selectVM == VAMConstants.NO_MAKE_APPLIANCE_VM) {
				throw new Exception("There is no more virtual machine to "
						+ "make the appliance");
			}
		} catch (Exception e) {
			// remove configuration, disk and appliance
			try {
				fileService.removeFile(null, null, config.getGuid());
				if (!appliance.getGuid().equals(existedAppGuid)) {
					fileService.removeFile(null, null, disk.getGuid());
					vaDao.remove(appliance.getGuid());
				} else {
					appliance.setState(VAMConstants.STATE_READY);
					vaDao.update(appliance);
				}
			} catch (Exception ex) {
				VAMUtil.outputLog(ex.getMessage());
			}
			throw new Exception(e);
		}

		return appliance;
	}

	/**
	 * add configuration.
	 * 
	 * @param appliance
	 *            appliance object
	 * @param selectVM
	 *            the virtual machine used to make appliance
	 * @param disk
	 *            virtual disk object
	 * @param config
	 *            configuration object
	 * @throws Exception
	 */
	private void addConfig(VirtualAppliance appliance, int selectVM,
			VAFile disk, VAFile config) throws Exception {
		// add configuration file
		ServiceFactory.getFileService().addFile(config, null, false);

		// set and save configuration
		VAConfig conf = new VAConfig();
		conf.addCpu(appliance.getCpuAmount());
		conf.addMemory(appliance.getMemory());
		conf.addDisk(disk.getId(), disk.getSavePath(), disk.getFormat(),
				appliance.getCapacity() / VAMConstants.GB);

		if (appliance.getDiscs().size() > 0) {
			String discGuid = appliance.getDiscs().get(0);
			String discPath = null;
			if (discGuid == null) {
				discPath = "";
			} else if (discGuid.equals(VAMConstants.LINGCLOUD_AGENT)) {
				discPath = VAMConfig.getLingCloudAgent();
			} else {
				VAFile disc = ServiceFactory.getFileService().
					queryFile(discGuid);
				discPath = disc.getSavePath();
			}
			conf.addDisc(discPath);
			conf.setBoot(VAMConstants.BOOT_CDROM);
		} else {
			conf.addDisc("");
		}

		conf.setBootLoader(appliance.getBootLoader());
		conf.setVncPort(VAMConfig.getMakeApplianceVMVncPort(selectVM));
		String bridge = VAMConfig.getMakeApplianceVMBridge(selectVM);
		String[] macAddrs = VAMConfig.getMakeApplianceVMMacAddr(selectVM);
		for (int i = 0; i < macAddrs.length; i++) {
			conf.addNetwork(macAddrs[i], bridge);
		}
		conf.setName(VAMConfig.getMakeApplianceVMName(selectVM));
		conf.setMakeApplianceVM(selectVM);
		conf.setPath(config.getSavePath());

		conf.saveConfig();
	}

	/**
	 * operate an appliance.
	 * 
	 * @param guid
	 *            appliance GUID
	 * @param type
	 *            the operation type, it can be
	 *            VAMConstants.APPLIANCE_OPERATION_START,
	 *            VAMConstants.APPLIANCE_OPERATION_STOP,
	 *            VAMConstants.APPLIANCE_OPERATION_LIST,
	 *            VAMConstants.APPLIANCE_OPERATION_SAVE now
	 * @return operation result
	 * @throws Exception
	 */
	public String operateAppliance(String guid, int type) throws Exception {
		String command = null;

		if (type != VAMConstants.APPLIANCE_OPERATION_LIST) {
			// query appliance by GUID
			VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
			VirtualAppliance appliance = vaDao.query(guid);

			if (appliance == null
					|| appliance.getState() != VAMConstants.STATE_MAKING) {
				throw new Exception("The appliance with guid \"" + guid
						+ "\" is not existed or available");
			}

			VAFileService fileService = ServiceFactory.getFileService();

			String diskGuid = appliance.getDisks().get(0);
			VAFile disk = fileService.queryFile(diskGuid);
			if (disk.getState() != VAMConstants.STATE_READY) {
				return "The appliance is preparing to make.";
			}

			// get configuration
			String configGuid = appliance.getConfig();
			if (configGuid == null) {
				throw new Exception("Can't get the config");
			}
			VAFile fileConfig = null;
			fileConfig = fileService.queryFile(configGuid);

			// load configuration
			VAConfig config = new VAConfig();
			config.setPath(fileConfig.getSavePath());
			config.loadConfig();

			if (type == VAMConstants.APPLIANCE_OPERATION_START) {
				String path = config.saveToXenCfg(null);
				// get start virtual machine command
				command = VAMUtil.getStartVirtualMachineCommand(VAMConfig
						.getMakeApplianceHost(), VAMConfig
						.getMakeApplianceUser(), path);
			} else if (type == VAMConstants.APPLIANCE_OPERATION_STOP) {
				// get stop virtual machine command
				command = VAMUtil.getStopVirtualMachineCommand(VAMConfig
						.getMakeApplianceHost(), VAMConfig
						.getMakeApplianceUser(), config.getName());
			}
		} else {
			// get list virtual machines info command
			command = VAMUtil.getVirtualMachineInfoCommand(VAMConfig
					.getMakeApplianceHost(), VAMConfig.getMakeApplianceUser());
		}

		if (command == null) {
			return null;
		}

		String res = VAMUtil.executeCommand(command);

		if (res == null && type != VAMConstants.APPLIANCE_OPERATION_STOP) {
			throw new Exception("Operation failed!");
		}

		if (res != null
				&& (type == VAMConstants.APPLIANCE_OPERATION_START 
						|| type == VAMConstants.APPLIANCE_OPERATION_STOP)) {
			VAMUtil.outputLog(res);
		}

		return res;
	}

	/**
	 * allocate appliance by GUID.
	 * 
	 * @param applianceGuid
	 *            appliance GUID
	 * @param host
	 *            the host storing the new appliance
	 * @param user
	 *            the user name of host
	 * @return appliance object
	 * @throws Exception
	 */
	public VirtualAppliance allocateAppliance(String applianceGuid,
			String host, String user) throws Exception {
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		// query the appliance by GUID
		VirtualAppliance templateApp = vaDao.query(applianceGuid);
		if (templateApp == null
				|| !templateApp.getParent().equals(VAMConstants.NULL)
				|| templateApp.getState() == VAMConstants.STATE_MAKING
				|| checkState(templateApp) != VAMConstants.STATE_READY) {
			throw new Exception("The appliance with guid \"" + applianceGuid
					+ "\" is not existed or available");
		}

		// query the disk file
		VAFileService fileService = ServiceFactory.getFileService();
		VAFile file = fileService.queryFile(templateApp.getDisks().get(0));

		// create a disk
		VAFile disk = fileService.createFile(file.getId(), null, file
				.getFileType(), file.getFormat(), 0);
		// create a new appliance and set the disk
		VirtualAppliance appliance = templateApp.getCopy();
		String guid = VAMUtil.genGuid();
		appliance.setGuid(guid);
		appliance.setParent(templateApp.getGuid());
		appliance.setRef(0);
		appliance.setState(VAMConstants.STATE_COPING);

		List<String> diskl = new ArrayList<String>();
		diskl.add(disk.getGuid());
		appliance.setDisks(diskl);

		// add the appliance to the database
		appliance = vaDao.add(appliance);
		if (appliance == null) {
			throw new Exception("Can't add the appliance to the database.");
		}

		try {
			// create snapshot
			fileService.createSnapshot(file, disk, host, user);
		} catch (Exception e) {
			// remove file and appliance
			try {
				fileService.removeFile(null, null, disk.getGuid());
				vaDao.remove(appliance.getGuid());
			} catch (Exception ex) {
				VAMUtil.outputLog(ex.getMessage());
			}
			throw new Exception(e);
		}

		return appliance;
	}

	/**
	 * revoke appliance by GUID.
	 * 
	 * @param guid
	 *            the appliance GUID
	 * @param host
	 *            the host storing the new appliance
	 * @param user
	 *            the user name of host
	 * @return whether revoke the appliance successfully
	 * @throws Exception
	 */
	public boolean revokeAppliance(String guid, String host, String user)
			throws Exception {
		// query the appliance by GUID
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		VirtualAppliance va = vaDao.query(guid);
		if (va == null || va.getParent().equals(VAMConstants.NULL)) {
			throw new Exception("The appliance with guid \"" + guid
					+ "\" is not existed or available");
		}

		// check the appliance state
		checkState(va);

		List<String> diskl = va.getDisks();
		String diskGuid = diskl.get(0);

		try {
			// remove file
			ServiceFactory.getFileService().removeFile(host, user, diskGuid);
		} catch (Exception e) {
			VAMUtil.outputLog(e.getMessage());
		}
		// remove appliance
		va.setState(VAMConstants.STATE_DELETING);
		vaDao.update(va);
		vaDao.remove(va.getGuid());

		return true;
	}

	/**
	 * save appliance to a template.
	 * 
	 * @param app
	 *            appliance object
	 * @return appliance object
	 * @throws Exception
	 */
	public VirtualAppliance saveAppliance(VirtualAppliance app)
			throws Exception {
		// query appliance by GUID
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		VirtualAppliance appliance = vaDao.query(app.getGuid());

		if (appliance == null) {
			throw new Exception("The appliance with guid \"" + app.getGuid()
					+ "\" is not existed");
		}
		// check appliance state
		int state = checkState(appliance);

		// get the appliance disk
		VAFileService fileService = ServiceFactory.getFileService();
		List<String> diskList = appliance.getDisks();
		if (diskList == null || diskList.size() == 0) {
			throw new Exception("Can't get the appliance's disk");
		}

		// query the disk file
		VAFile disk = fileService.queryFile(diskList.get(0));

		VirtualAppliance saveAppliance = null;

		if (state == VAMConstants.STATE_READY) { // snapshot
			if (appliance.getParent().equals(VAMConstants.NULL)) {
				throw new Exception("It's now allowed to save the appliance");
			}
			VirtualAppliance templateAppliance = vaDao.query(appliance
					.getParent());
			if (templateAppliance == null) {
				return null;
			}
			// add a new appliance
			saveAppliance = templateAppliance.getCopy();
			saveAppliance = addAppliance(saveAppliance, disk.getSavePath(),
					false);

		} else if (state == VAMConstants.STATE_MAKING) { // making appliance
			// stop virtual machine
			try {
				operateAppliance(appliance.getGuid(),
					VAMConstants.APPLIANCE_OPERATION_STOP);
			} catch (Exception e) {
				VAMUtil.outputLog(e.getMessage());
			}

			// update appliance information
			appliance.setAccessWay(app.getAccessWay());
			appliance.setApplications(app.getApplications());
			appliance.setCategory(app.getCategory());
			appliance.setDescription(app.getDescription());
			appliance.setLanguages(app.getLanguages());
			appliance.setLoginStyle(app.getLoginStyle());
			appliance.setPassword(app.getPassword());
			appliance.setUsername(app.getUsername());
			appliance.setDiscs(new ArrayList<String>());
			appliance.setState(VAMConstants.STATE_CONVERTING);
			appliance = vaDao.update(appliance);

			try {
				DiskInfo info = VAMUtil.getDiskInfo(disk.getSavePath());
				String format = null;
				String backingFile = null;
				if (info != null) {
					format = info.getFormat();
					backingFile = info.getBackingFile();
				}
				if (format != null && format.equalsIgnoreCase(
						VAMConstants.VAF_FORMAT_DEFAULT)
						&& backingFile == null) {
					disk = fileService.getDiskCapacity(disk);
					fileService.updateFile(disk);
					
					try {
						fileService.createRedundantDisk(disk);
					} catch (Exception e) {
						VAMUtil.outputLog(e.getMessage());
					}
					
					appliance.setState(VAMConstants.STATE_READY);
					appliance = vaDao.update(appliance);
				} else {
					// convert disk format
					fileService.convertDiskFormat(disk, 
							disk.getSavePath(),
							VAMConstants.VAF_FORMAT_DEFAULT, true);
				}
			} catch (Exception e) {
				// restore old state
				try {
					appliance.setState(state);
					appliance = vaDao.update(appliance);
				} catch (Exception ex) {
					VAMUtil.outputLog(ex.getMessage());
				}
				throw new Exception(e);
			}

			// release virtual machine and remove configuration
			try {
				if (appliance.getConfig() != null) {
					VAMUtil.releaseMakeApplianceVM(appliance.getSelectVm());
					removeConfig(appliance);
				}
			} catch (Exception e) {
				VAMUtil.outputLog(e.getMessage());
			}

			saveAppliance = appliance;
		}

		return saveAppliance;
	}

	/**
	 * change the disc of a appliance.
	 * 
	 * @param applianceGuid
	 *            appliance's GUID
	 * @param discGuid
	 *            the disc's GUID, if want to unmount the disc, please set GUID
	 *            as null; if want to mount the LingCloudAgent disc, please set
	 *            GUID as VAMConstants.LINGCLOUD_AGENT
	 * @throws Exception
	 */
	public void changeDisc(String applianceGuid, String discGuid)
			throws Exception {
		// query appliance by GUID
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		VirtualAppliance appliance = vaDao.query(applianceGuid);

		if (appliance == null) {
			throw new Exception("The appliance with guid \"" + applianceGuid
					+ "\" is not existed");
		}

		if (appliance.getState() != VAMConstants.STATE_MAKING) {
			throw new Exception("The appliance is not being made");
		}

		VAFileService fileService = ServiceFactory.getFileService();

		String discPath = null;
		if (discGuid == null) {
			discPath = "";
		} else if (discGuid.equals(VAMConstants.LINGCLOUD_AGENT)) {
			discPath = VAMConfig.getLingCloudAgent();
		} else {
			VAFile disc = fileService.queryFile(discGuid);
			discPath = disc.getSavePath();
		}

		String configGuid = appliance.getConfig();
		VAFile config = fileService.queryFile(configGuid);

		List<String> discList = appliance.getDiscs();
		String oldDisc = null;
		if (discList.size() > 0) {
			oldDisc = discList.get(0);
			discList.set(0, discGuid);
		} else {
			oldDisc = "";
			discList.add(discGuid);
		}
		appliance.setDiscs(discList);
		appliance = vaDao.update(appliance);

		if (appliance == null) {
			throw new Exception("Update appliance failed");
		}

		try {
			VAConfig fileConfig = new VAConfig();
			fileConfig.setPath(config.getSavePath());
			fileConfig.loadConfig();
			if (fileConfig.getDiscCount() > 0) {
				fileConfig.setDisc(0, discPath);
			} else {
				fileConfig.addDisc(discPath);
			}

			fileConfig.saveConfig();
		} catch (Exception e) {
			VAMUtil.outputLog(e.getMessage());
			discList.set(0, oldDisc);
			appliance.setDiscs(discList);
			vaDao.update(appliance);
			throw new Exception("File operation failed");
		}
	}

	/**
	 * operate disk.
	 * 
	 * @param type
	 *            operation type, it support MOUNT now
	 * @param applianceGuid
	 *            the appliance GUID
	 * @param args
	 *            the argument of operation, MOUNT : name, location, format,
	 *            capacity
	 * @throws Exception
	 */
	public void operateDisk(int type, String applianceGuid, String[] args)
			throws Exception {
		// query appliance by GUID
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();
		VirtualAppliance appliance = vaDao.query(applianceGuid);

		if (appliance == null) {
			throw new Exception("The appliance with guid \"" + applianceGuid
					+ "\" is not existed");
		}

		if (appliance.getState() != VAMConstants.STATE_MAKING) {
			throw new Exception("The appliance is not being made");
		}

		VAFileService fileService = ServiceFactory.getFileService();

		String configGuid = appliance.getConfig();
		VAFile config = fileService.queryFile(configGuid);

		try {
			VAConfig fileConfig = new VAConfig();
			fileConfig.setPath(config.getSavePath());
			fileConfig.loadConfig();
			
			final int argsLen = 4;
			if (type == VAMConstants.DISK_MOUNT && args.length == argsLen) {
				if (fileConfig.getDiskCount() == 1) {
					fileConfig.addDisk(args[0], args[1], args[2], Long
							.parseLong(args[argsLen - 1]));
					fileConfig.saveConfig();
				} else if (fileConfig.getDiskCount() == 2) {
					fileConfig.setDisk(args[0], args[1], args[2], Long
							.parseLong(args[argsLen - 1]), 1);
					fileConfig.saveConfig();
				}
			}

		} catch (Exception e) {
			VAMUtil.outputLog(e.getMessage());
			throw new Exception("File operation failed");
		}
	}

	/**
	 * check appliance state.
	 * 
	 * @param appliance
	 *            appliance object
	 * @return the update state
	 * @throws Exception
	 */
	private int checkState(VirtualAppliance appliance) throws Exception {
		int state = appliance.getState();

		// if the appliance is being made or its state is ready,
		// it's no need to check the state
		if (state == VAMConstants.STATE_MAKING
				|| state == VAMConstants.STATE_READY
				&& appliance.getCapacity() > 0 && appliance.getSize() > 0) {
			return state;
		}

		// get information
		long capacity = appliance.getCapacity();
		long size = appliance.getSize();
		String format = appliance.getFormat();

		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		VirtualApplianceDao vaDao = DaoFactory.getVirtualApplianceDao();

		// get the virtual disk object
		List<String> diskl = appliance.getDisks();
		if (diskl.size() == 0) { // no disk
			state = VAMConstants.STATE_ERROR;
		} else {
			VAFile disk = fileDao.query(diskl.get(0));

			if (disk == null) { // can't find the disk, remove the appliance
				state = VAMConstants.STATE_DELETED;
				vaDao.remove(appliance.getGuid());

				throw new Exception("The appliance has been removed");

			} else {
				// get the latest information
				VADisk vadisk = new VADisk(disk);
				state = vadisk.getState();
				format = vadisk.getFormat();
				capacity = vadisk.getCapacity();
				size = vadisk.getSize();
			}
		}

		// update appliance information
		if (state != appliance.getState()
				|| capacity != appliance.getCapacity()
				|| size != appliance.getSize()) {
			appliance.setState(state);
			appliance.setFormat(format);
			appliance.setCapacity(capacity);
			appliance.setSize(size);
			vaDao.update(appliance);
		}

		return state;
	}

}
