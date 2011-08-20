/*
 *  @(#)FileController.java  2011-07-28
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
package org.lingcloud.molva.xmm.vam.controllers;

import org.lingcloud.molva.xmm.vam.daos.DaoFactory;
import org.lingcloud.molva.xmm.vam.daos.VAFileDao;
import org.lingcloud.molva.xmm.vam.pojos.VADisk;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.services.FileOperation;
import org.lingcloud.molva.xmm.vam.services.ServiceFactory;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-07-28<br>
 * @author Ruijian Wang<br>
 * 
 */
public class FileController extends Controller {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#update(org.lingcloud.
	 * molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public void update(VAObject obj) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		VAFile file = new VAFile(obj);

		// update the file
		file = fileDao.query(obj.getGuid());
		// a small bug, 2010-08-23.should check the object whether is null.
		if (file == null || file.getState() == VAMConstants.STATE_READY) {
			return;
		}
		file.setState(obj.getState());
		fileDao.update(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#create(org.lingcloud.
	 * molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public void create(VAObject obj) throws Exception {
		if (obj.getState() == VAMConstants.STATE_ERROR) {
			return;
		}

		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		VAFile vafile = fileDao.query(obj.getGuid());

		// if can't find the file in the database, remove the file
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath());
			return;
		}

		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#remove(org.lingcloud.
	 * molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public void remove(VAObject obj) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		if (obj.getState() == VAMConstants.STATE_ERROR) {
			return;
		}

		// if the file type is not disk do nothing
		VAFile vafile = new VAFile(obj);
		if (vafile.getFileType().equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
			String baseGuid;
			if (vafile.getParent().equals(VAMConstants.NULL)) { // base disk
				baseGuid = vafile.getGuid();

				// remove redundant disk
				ServiceFactory.getFileService().removeReplicas(baseGuid);
			}
		}

		// remove the file
		fileDao.remove(obj.getGuid());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#copy(org.lingcloud.molva
	 * .xmm.vam.pojos.VAObject)
	 */
	@Override
	public void copy(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath());
			return;
		}

		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#convert(org.lingcloud
	 * .molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public void convert(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath());
			return;
		}
		// update state
		vafile.setState(obj.getState());

		if (vafile.getState() != VAMConstants.STATE_READY) {
			fileDao.update(vafile);
			return;
		}

		// update disk information
		VADisk vadisk = ServiceFactory.getFileService().getDiskCapacity(vafile);

		if (!vafile.getParent().equals(VAMConstants.NULL)) {
			vadisk.setParent(VAMConstants.NULL);
		}

		fileDao.update(vadisk);

		// delete file after operation
		if (vadisk.isDeleteFileAfterOperation()) {
			try {
				FileOperation.getInstance().removeFile(null, null, null,
						vadisk.getSrcPath());
			} catch (Exception e) {
				VAMUtil.errorLog(e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#move(org.lingcloud.molva
	 * .xmm.vam.pojos.VAObject)
	 */
	@Override
	public void move(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath());
			return;
		}
		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#resume(org.lingcloud.
	 * molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public void resume(VAObject obj) throws Exception {
		if (obj == null) {
			return;
		}

		VAFile file = new VAFile(obj);
		int type = file.getOperationType();

		if (type == VAMConstants.VAO_OPERATION_TYPE_MOVE_FILE) {
			FileOperation.getInstance().moveFile(VAMConfig.getNfsHost(),
					VAMConfig.getNfsUser(), file, file.getSrcPath(),
					file.getSavePath());
		} else if (type == VAMConstants.VAO_OPERATION_TYPE_DELETE_FILE) {
			FileOperation.getInstance().removeFile(null, null, file,
					file.getSrcPath());
		} else if (type == VAMConstants.VAO_OPERATION_TYPE_COPY_FILE) {
			FileOperation.getInstance().copyDisk(VAMConfig.getNfsHost(),
					VAMConfig.getNfsUser(), file, file.getSrcPath(),
					file.getSavePath());
		} else if (type == VAMConstants.VAO_OPERATION_TYPE_CONVERT_FORMAT) {
			FileOperation.getInstance().convertDiskFormat(
					VAMConfig.getNfsHost(), VAMConfig.getNfsUser(), file,
					file.getSrcPath(), file.getSavePath(), file.getFormat());
		} else if (type == VAMConstants.VAO_OPERATION_TYPE_CREATE_DISK) {
			VADisk disk = new VADisk(file);
			FileOperation.getInstance().createDisk(VAMConfig.getNfsHost(),
					VAMConfig.getNfsUser(), file, file.getSrcPath(),
					disk.getCapacity());
		} else if (type == VAMConstants.VAO_OPERATION_TYPE_CREATE_SNAPSHOT) {
			FileOperation.getInstance().createSnapshot(file,
					VAMConfig.getNfsHost(), VAMConfig.getNfsUser(),
					file.getSavePath(), file.getSrcPath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.lingcloud.molva.xmm.vam.services.Controller#validate(org.lingcloud
	 * .molva.xmm.vam.pojos.VAObject)
	 */
	@Override
	public boolean validate(VAObject obj) throws Exception {
		return true;
	}
}
