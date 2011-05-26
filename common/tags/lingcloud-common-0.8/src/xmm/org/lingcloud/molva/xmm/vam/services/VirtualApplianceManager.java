/*
 *  @(#)VMManager.java  2010-5-20
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

import java.util.List;

import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.pojos.VAConfig;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-20<br>
 * @author Ruijian Wang<br>
 * 
 */
public interface VirtualApplianceManager {
	/**
	 * add file to the system.
	 * @param id
	 * 			file's id or name
	 * @param location
	 * 			file's location in the system
	 * @param srcLocation
	 * 			the temporary location of the file
	 * @param fileType
	 * 			the file's type
	 * @param format
	 * 			the file's format
	 * @return if add successfully, return VAFile object, 
	 * 			else return null
	 * @throws Exception
	 */
	VAFile addFile(String id, String location, String srcLocation,
			String fileType, String format) throws Exception;

	/**
	 * add disc to the system.
	 * @param disc
	 *            need to set the id, location and format
	 * @param srcLocation
	 * 		the temporary location of the disc
	 * @param discType
	 * 		the disc's type
	 * @param os
	 * 		operation system
	 * @param appl
	 * 		application list
	 * @return file object
	 * @throws Exception
	 */
	VAFile addDisc(VAFile disc, String srcLocation, String discType, 
			String os, String osVersion, List<String> appl)
			throws Exception;

	/**
	 * query the file with GUID.
	 * @param guid
	 * 			file's GUID
	 * @return the queried file
	 * @throws Exception
	 */
	VAFile queryFile(String guid) throws Exception;

	/**
	 * update the file.
	 * @param vafile
	 * 		file object
	 * @return updated file object
	 * @throws Exception
	 */
	VAFile updateFile(VAFile vafile) throws Exception;

	/**
	 * get the files by type.
	 * @param type
	 * 		the type of file, it can be "VDI", "VCD", "other"
	 * @return the file list
	 * @throws Exception
	 */
	
	List<VAFile> getFilesByType(String type) throws Exception;

	/**
	 * remove file with GUID.
	 * @param guid
	 * 			file's GUID
	 * @return whether the file is removed
	 * @throws Exception
	 */
	boolean removeFile(String guid) throws Exception;

	/**
	 * clear incorrect file.
	 * @param clearFlag
	 * 		the clear flag, which supports VAMConstants.FILE_CLEAR_FLAG_ERROR,  
	 * 		VAMConstants.FILE_CLEAR_FLAG_NOT_READY, 
	 * 		VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_DATA, 
	 * 		VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_FILE, 
	 * 		VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_PARENT, 
	 * 		it can use or operation, 
	 * @throws Exception 
	 */
	void clearFile(int clearFlag) throws Exception;

	/**
	 * add appliance to the system.
	 * @param appliance
	 * 		the appliance object
	 * @param filepath
	 * 		the path of the appliance file
	 * @return if add successfully, return appliance object, 
	 * 			else return null
	 * @throws Exception
	 */
	VirtualAppliance addAppliance(VirtualAppliance appliance, String filepath)
			throws Exception;

	/**
	 * update the appliance.
	 * @param appliance
	 * 		appliance object
	 * @return the updated appliance
	 * @throws Exception
	 */
	VirtualAppliance updateAppliance(VirtualAppliance appliance) 
		throws Exception;
	
	/**
	 * remove the appliance with GUID.
	 * @param guid
	 * 		appliance's GUID
	 * @return whether the appliance is removed
	 * @throws Exception
	 */
	boolean removeAppliance(String guid) throws Exception;

	/**
	 * query the appliance with GUID.
	 * @param guid
	 * 		appliance's GUID
	 * @return the queried appliance
	 * @throws Exception
	 */
	VirtualAppliance queryAppliance(String guid) throws Exception;

	/**
	 * get all the appliances.
	 * @return appliance list
	 * @throws Exception
	 */
	
	List<VirtualAppliance> getAllAppliance() throws Exception;

	/**
	 * get making appliances.
	 * @return appliance list
	 * @throws Exception
	 */
	
	List<VirtualAppliance> getMakingAppliances() throws Exception;

	/**
	 * get the appliances whose category GUID is cateGuid.
	 * @param cateGuid
	 * 		the GUID of category
	 * @return the appliance list
	 * @throws Exception
	 */
	
	List<VirtualAppliance> getAppliancesByCategory(String cateGuid) 
		throws Exception;

	/**
	 * allocate one virtual appliance.
	 * @param applianceGuid
	 * 		the appliance's GUID
	 * @param host
	 * 		the host saving the virtual appliance
	 * @param user
	 * 		the user of the host
	 * @return if allocate successful, return appliance object,
	 * 			else return null
	 * @throws Exception
	 */
	VirtualAppliance allocateAppliance(String applianceGuid, String host,
			String user) throws Exception;

	/**
	 * revoke the virtual appliance with GUID.
	 * @param guid
	 * 		the virtual appliance's GUID
	 * @param host
	 * 		the host saving the virtual appliance
	 * @param user
	 * 		the user of the host
	 * @return whether the virtual appliance is revoked
	 * @throws Exception
	 */
	boolean revokeAppliance(String guid, String host, String user)
			throws Exception;

	/**
	 * make a new appliance by creating a new disk or creating a existed 
	 * appliance's snapshot.
	 * @param appliance 
	 * 		new appliance
	 * @param existedAppGuid
	 * 		if existedAppGuid is null, create a new disk, else create 
	 * 		snapshot
	 * @return a new making appliance
	 * @throws Exception
	 */
	VirtualAppliance makeAppliance(VirtualAppliance appliance,
			String existedAppGuid, int memSize) throws Exception;

	/**
	 * start a appliance which is making.
	 * @param guid
	 * 		GUID of the appliance
	 * @return operation result
	 * @throws Exception
	 */
	String startAppliance(String guid) throws Exception;

	/**
	 * stop appliance to a template.
	 * @param guid
	 * 		the appliance's GUID
	 * @return operation result
	 * @throws Exception
	 */
	String stopAppliance(String guid) throws Exception;

	/**
	 * get the appliances' running state.
	 * @param nameList
	 * 		appliances' name list
	 * @return
	 * 		the running state list
	 * @throws Exception
	 */
	
	List<String> getApplianceRunningState(List<String> nameList) 
		throws Exception;

	/**
	 * save appliance to a template.
	 * @param appliance
	 * 		the appliance object
	 * @return appliance object
	 * @throws Exception
	 */
	VirtualAppliance saveAppliance(VirtualAppliance appliance) throws Exception;

	/**
	 * get a appliance's configuration.
	 * @param appGuid
	 * 		the appliance's GUID
	 * @return configuration object
	 * @throws Exception
	 */
	VAConfig getApplianceConfig(String appGuid) throws Exception;
	
	/**
	 * change the disc of a appliance.
	 * @param applianceGuid
	 * 		appliance's GUID
	 * @param discGuid
	 * 		the disc's GUID, if want to unmount the disc, 
	 * 		please set GUID as null;
	 * 		if want to mount the LingCloudAgent disc, 
	 * 		please set GUID as VAMConstants.LINGCLOUD_AGENT
	 * @throws Exception
	 */
	void changeDisc(String applianceGuid, String discGuid) throws Exception; 
	
	/**
	 * add category to the system.
	 * @param name
	 * 		the category's name
	 * @return if add successfully, return VACategory object, 
	 * 			else return null
	 * @throws Exception
	 */
	VACategory addCategory(String name) throws Exception;

	/**
	 * query the category with GUID.
	 * @param guid
	 * 		category's GUID
	 * @return the queried category
	 * @throws Exception
	 */
	VACategory queryCategory(String guid) throws Exception;

	/**
	 * get all categories.
	 * @return category list
	 * @throws Exception
	 */
	
	List<VACategory> getAllCategory() throws Exception;

	/**
	 * remove the category with GUID.
	 * @param guid
	 * 		category's GUID
	 * @return whether the category is removed
	 * @throws Exception
	 */
	boolean removeCategory(String guid) throws Exception;

	/**
	 * destroy the operation threads.
	 *
	 */
	void destroyThreads();
}
