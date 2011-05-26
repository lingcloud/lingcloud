/*
 *  @(#)VirtualApplianceManagerImpl.java  2010-7-22
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

import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.pojos.VAConfig;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-7-22<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VirtualApplianceManagerImpl implements VirtualApplianceManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #addFile(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public VAFile addFile(String id, String location, String srcLoccation,
			String fileType, String format) throws Exception {
		if (!fileType.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			return null;
		}
		VAFileService service = ServiceFactory.getFileService();
		return service.addFile(id, location, srcLoccation, fileType, format);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #addDisc(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	public VAFile addDisc(VAFile disc, String srcLocation, String discType, 
			String os, String osVersion, List<String> appl)
		throws Exception {
		VAFileService service = ServiceFactory.getFileService();
		return service.addDisc(disc, srcLocation, discType, os,
				osVersion, appl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #queryFile(java.lang.String)
	 */
	public VAFile queryFile(String guid) throws Exception {
		VAFileService service = ServiceFactory.getFileService();
		VAFile vafile = service.queryFile(guid);
		// A small bug. 2010-08-06. Disc file no need to query.
		// if (!vafile.getFileType().equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
		// return null;
		// }
		return vafile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #updateFile(org.lingcloud.molva.xmm.vam.pojos.VAFile)
	 */
	public VAFile updateFile(VAFile vafile) throws Exception {
		VAFileService service = ServiceFactory.getFileService();
		return service.updateFile(vafile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getFilesByType(java.lang.String)
	 */

	public List<VAFile> getFilesByType(String type) throws Exception {
		if (!type.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			return null;
		}
		VAFileService service = ServiceFactory.getFileService();
		return service.getFilesByType(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #removeFile(java.lang.String)
	 */
	public boolean removeFile(String guid) throws Exception {
		VAFileService service = ServiceFactory.getFileService();
		VAFile vafile = service.queryFile(guid);
		if (!vafile.getFileType().equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			return false;
		}
		return service.removeFile(null, null, guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #clearFile(int)
	 */
	public void clearFile(int clearFlag) throws Exception {
		VAFileService service = ServiceFactory.getFileService();
		service.clearFile(clearFlag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #addAppliance(org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance,
	 * java.lang.String)
	 */
	public VirtualAppliance addAppliance(VirtualAppliance appliance,
			String filepath) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		String uploadDir = VAMConfig.getDiskUploadDirLocation();

		return service.addAppliance(appliance, uploadDir + filepath);
	}

	public VirtualAppliance updateAppliance(VirtualAppliance appliance)
			throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.updateAppliance(appliance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #removeAppliance(java.lang.String)
	 */
	public boolean removeAppliance(String guid) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.removeAppliance(guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #queryAppliance(java.lang.String)
	 */
	public VirtualAppliance queryAppliance(String guid) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.queryAppliance(guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getAllAppliance()
	 */

	public List<VirtualAppliance> getAllAppliance() throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.getAllAppliance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getMakingAppliances()
	 */

	public List<VirtualAppliance> getMakingAppliances() throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.getMakingAppliances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getAppliancesByCategory(java.lang.String)
	 */

	public List<VirtualAppliance> getAppliancesByCategory(String cateGuid)
			throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.getAppliancesByCategory(cateGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #allocateVA(java.lang.String, java.lang.String, java.lang.String)
	 */
	public VirtualAppliance allocateAppliance(String templateGuid, String host,
			String user) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		if (host == null || user == null) {
			host = VAMConfig.getMakeApplianceHost();
			user = VAMConfig.getMakeApplianceUser();
		}
		return service.allocateAppliance(templateGuid, host, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #revokeVA(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean revokeAppliance(String guid, String host, String user)
			throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.revokeAppliance(guid, host, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #makeAppliance(org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance,
	 * org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance)
	 */
	public VirtualAppliance makeAppliance(VirtualAppliance appliance,
			String existedAppGuid, int memSize) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		appliance = service.makeAppliance(appliance, existedAppGuid, memSize);
		// service.operateAppliance(appliance.getGuid(),
		// VAMConstants.APPLIANCE_OPERATION_START);
		return appliance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #startAppliance(java.lang.String)
	 */
	public String startAppliance(String guid) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.operateAppliance(guid,
				VAMConstants.APPLIANCE_OPERATION_START);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #stopAppliance(java.lang.String)
	 */
	public String stopAppliance(String guid) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.operateAppliance(guid,
				VAMConstants.APPLIANCE_OPERATION_STOP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getApplianceRunningState(java.util.List)
	 */

	public List<String> getApplianceRunningState(List<String> nameList)
			throws Exception {
		List<String> stateList = new ArrayList<String>();
		VAService service = ServiceFactory.getVirtualApplianceService();

		if (nameList != null) {
			String vmState = service.operateAppliance(null,
					VAMConstants.APPLIANCE_OPERATION_LIST);

			for (int i = 0; i < nameList.size(); i++) {
				String name = (String) nameList.get(i);
				if (name != null && !name.equals("")
						&& vmState.contains(name + " ")) {
					stateList.add(VAMConstants.MAKE_APPLIANCE_VM_STATE_RUNNING);
				} else {
					stateList.add(VAMConstants.MAKE_APPLIANCE_VM_STATE_STOP);
				}
			}
		}

		return stateList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #saveAppliance(java.lang.String)
	 */
	public VirtualAppliance saveAppliance(VirtualAppliance appliance)
			throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.saveAppliance(appliance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #operateAppliance(java.lang.String, int)
	 */
	public String operateAppliance(String guid, int type) throws Exception {
		VAService service = ServiceFactory.getVirtualApplianceService();
		return service.operateAppliance(guid, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getApplianceConfig(java.lang.String)
	 */
	public VAConfig getApplianceConfig(String appGuid) throws Exception {
		VAService appService = ServiceFactory.getVirtualApplianceService();
		VAFileService fileService = ServiceFactory.getFileService();

		VirtualAppliance va = appService.queryAppliance(appGuid);
		VAFile file = fileService.queryFile(va.getConfig());
		if (!file.getFileType().equals(VAMConstants.VAF_FILE_TYPE_CONFIG)) {
			throw new Exception("Can't get the config");
		}
		VAConfig config = new VAConfig();
		config.setPath(file.getSavePath());
		config.loadConfig();

		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #changeDisc(java.lang.String, java.lang.String)
	 */
	public void changeDisc(String applianceGuid, String discGuid)
			throws Exception {
		VAService appService = ServiceFactory.getVirtualApplianceService();
		appService.changeDisc(applianceGuid, discGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #addCategory(java.lang.String)
	 */
	public VACategory addCategory(String name) throws Exception {
		VACategoryService service = ServiceFactory.getCategoryService();
		return service.addCategory(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #queryCategory(java.lang.String)
	 */
	public VACategory queryCategory(String guid) throws Exception {
		VACategoryService service = ServiceFactory.getCategoryService();
		return service.queryCategory(guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #getAllCategory()
	 */

	public List<VACategory> getAllCategory() throws Exception {
		VACategoryService service = ServiceFactory.getCategoryService();
		return service.getAllCategory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #removeCategory(java.lang.String)
	 */
	public boolean removeCategory(String guid) throws Exception {
		VACategoryService service = ServiceFactory.getCategoryService();
		return service.removeCategory(guid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.VirtualApplianceManager
	 * #destroyThreads()
	 */
	public void destroyThreads() {
		OperationThread.destroy();
	}

}
