/*
 *  @(#)VAFileServiceImpl.java  2010-5-27
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lingcloud.molva.xmm.vam.daos.DaoFactory;
import org.lingcloud.molva.xmm.vam.daos.VAFileDao;
import org.lingcloud.molva.xmm.vam.pojos.VADisk;
import org.lingcloud.molva.xmm.vam.pojos.VADisk.DiskInfo;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.VAMConfig;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-27<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAFileService implements TaskFunction {
	/**
	 * single instance design pattern.
	 */
	private static VAFileService instance = null;

	/**
	 * get file service instance.
	 * @return VAFileService instance
	 */
	public static VAFileService getInstance() {
		if (instance == null) {
			instance = new VAFileService();
		}
		return instance;
	}

	private VAFileService() {

	}

	/**
	 * create file object.
	 * 
	 * @param id
	 *            file id
	 * @param location
	 *            file location, if location = null, generate location
	 * @param fileType
	 *            file type
	 * @param format
	 *            file format
	 * @param size
	 *            file size
	 * @return file object
	 * @throws Exception
	 */
	public VAFile createFile(String id, String location, String fileType,
			String format, long size) throws Exception {
		String guid = VAMUtil.genGuid();
		if (location == null) {
			location = "file." + guid;
		}
		// create a new file object
		VAFile vafile = new VAFile(guid, fileType, format, location, id, size,
				VAMConstants.STATE_UNDEFINE);

		if (fileType == null) {
			throw new Exception("The file type must not be null");
		}

		if (format == null) {
			throw new Exception("The file format must not be null");
		}

		// check file type and file format
		if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISC)) {
			if (!format.equals(VAMConstants.VAF_FORMAT_ISO)) {
				throw new Exception("The disc format must be "
						+ VAMConstants.VAF_FORMAT_ISO);
			}
		} else if (fileType.equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
			if (!format.equals(VAMConstants.VAF_FORMAT_RAW)
					&& !format.equals(VAMConstants.VAF_FORMAT_QCOW)
					&& !format.equals(VAMConstants.VAF_FORMAT_QCOW_2)
					&& !format.equals(VAMConstants.VAF_FORMAT_VMDK)) {
				throw new Exception("The disk format must be "
						+ VAMConstants.VAF_FORMAT_RAW + ", "
						+ VAMConstants.VAF_FORMAT_QCOW + ", "
						+ VAMConstants.VAF_FORMAT_QCOW_2 + ", "
						+ VAMConstants.VAF_FORMAT_VMDK);
			}
		} else if (fileType.equals(VAMConstants.VAF_FILE_TYPE_CONFIG)) {
			if (!format.equals(VAMConstants.VAF_FORMAT_TEXT)) {
				throw new Exception("The config format must be "
						+ VAMConstants.VAF_FORMAT_TEXT);
			}
		} else {
			fileType = VAMConstants.VAF_FILE_TYPE_OTHER;
			format = VAMConstants.VAF_FORMAT_VMDK;
		}
		vafile.setFileType(fileType);
		vafile.setFormat(format);
		vafile.setParent(VAMConstants.NULL);
		vafile.setRef(0);

		return vafile;
	}

	/**
	 * add file.
	 * 
	 * @param id
	 *            file id
	 * @param location
	 *            file location, if location = null, generate location
	 * @param storeLoc
	 *            the file location
	 * @param fileType
	 *            file type
	 * @param format
	 *            file format
	 * @return file object
	 * @throws Exception
	 */
	public VAFile addFile(String id, String location, String storeLoc,
			String fileType, String format) throws Exception {
		// create new VAFile object
		VAFile vafile = createFile(id, location, fileType, format, 0);
		// get upload directory path
		String uploadDir = VAMConfig.getUploadDirLocation() + "/"
				+ VAMConstants.VAF_FILE_TYPE_OTHER + "/";

		return addFile(vafile, uploadDir + storeLoc, false);

	}

	/**
	 * add disc to the system. Need to set the id, location, srcLocation,
	 * format, discType, OS, osVersion, APP list.
	 * 
	 * @param disc
	 *            need to set the id, location and format
	 * @param srcLocation
	 *            the temporary location of the disc
	 * @param discType
	 *            the disc's type
	 * @param os
	 *            operation system
	 * @param appl
	 *            application list
	 * @return file object
	 * @throws Exception
	 */
	public VAFile addDisc(VAFile disc, String srcLocation, String discType,
			String os, String osVersion, List<String> appl) throws Exception {
		// create new VAFile object
		VAFile vafile = createFile(disc.getId(), disc.getLocation(),
				VAMConstants.VAF_FILE_TYPE_DISC, disc.getFormat(), 0);

		VADisk vadisk = new VADisk(vafile);

		vadisk.setDiskType(discType);
		if (discType.equals(VAMConstants.VAD_DISK_TYPE_OS)) {
			vadisk.setOs(os, osVersion);
		} else if (discType.equals(VAMConstants.VAD_DISK_TYPE_APP)) {
			vadisk.setApplications(appl);
		}

		// get upload directory path
		String uploadDir = VAMConfig.getDiscUploadDirLocation();

		return addFile(vadisk, uploadDir + srcLocation, false);
	}

	/**
	 * add file.
	 * 
	 * @param vafile
	 *            file object
	 * @param storeLoc
	 *            the file location
	 * @param deleteSrcFile
	 *            whether delete the source file after add file
	 * @return file object
	 * @throws Exception
	 */
	public VAFile addFile(VAFile vafile, String storeLoc,
			boolean deleteSrcFile) throws Exception {
		// check whether the store file is existed
		String storePath = storeLoc;
		if (storePath != null) {
			File storeFile = new File(storePath);
			if (!storeFile.exists()) {
				throw new Exception("The file \"" + storePath
						+ "\" is not existed!");
			}

			vafile.setSize(storeFile.length());
		}

		// check file path
		VAMUtil.checkFile(null, vafile.getLocation());

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		// add the file to the database
		vafile = fileDao.add(vafile);
		if (vafile == null) {
			throw new Exception("Can't add the file to the database.");
		}

		try {
			// if the file type is disk, convert disk format
			if (vafile.getFileType().equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
				vafile.setSrcPath(storeLoc);
				DiskInfo info = VAMUtil.getDiskInfo(storeLoc);
				String format = null;
				String backingFile = null;
				if (info != null) {
					format = info.getFormat();
					backingFile = info.getBackingFile();
				}
				if (format != null && format.equalsIgnoreCase(
						VAMConfig.getImageFormat())
						&& backingFile == null) {
					VADisk disk = new VADisk(vafile);
					disk.setCapacity(info.getVirtualSize());
					disk.setSize(info.getDiskSize());

					FileOperation.getInstance().moveFile(
							VAMConfig.getNfsHost(), VAMConfig.getNfsUser(),
							disk, storeLoc, disk.getSavePath(), this);
				} else {
					convertDiskFormat(vafile, storeLoc,
							VAMConfig.getImageFormat(), deleteSrcFile);
				}

			} else if (storeLoc != null) { // move file
				FileOperation.getInstance().moveFile(VAMConfig.getNfsHost(),
						VAMConfig.getNfsUser(), vafile, storeLoc,
						vafile.getSavePath(), this);
			}
		} catch (Exception e) {
			// remove the file in the database
			DaoFactory.getVAFileDao().remove(vafile.getGuid());
			throw new Exception(e);
		}

		return vafile;
	}

	/**
	 * create a virtual disk.
	 * 
	 * @param id
	 *            disk id
	 * @param format
	 *            disk format
	 * @param capacity
	 *            disk capacity
	 * @return file object
	 * @throws Exception
	 */
	public VAFile createDisk(VAFile vafile, long capacity) throws Exception {
		// check disk
		checkDiskType(vafile);

		// create a new disk
		VADisk vadisk = new VADisk(vafile);
		vadisk.setCapacity(capacity);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		// add the file to the database
		vafile = fileDao.add(vadisk);
		if (vafile == null) {
			throw new Exception("Can't add the file to the database.");
		}

		try {
			// create disk
			FileOperation.getInstance().createDisk(VAMConfig.getNfsHost(),
					VAMConfig.getNfsUser(), vafile, vafile.getSavePath(),
					capacity, this);
		} catch (Exception e) {
			// remove file in the database
			DaoFactory.getVAFileDao().remove(vafile.getGuid());
			throw new Exception(e);
		}

		return vafile;
	}

	/**
	 * copy disk.
	 * 
	 * @param srcFile
	 *            the source disk
	 * @return a new disk object
	 * @throws Exception
	 */
	public VAFile copyDisk(VAFile srcFile) throws Exception {
		// check disk
		checkDiskType(srcFile);

		// if the file has parent file, it can't be copied
		VADisk srcDisk = new VADisk(srcFile);
		if (!srcDisk.getParent().equals(VAMConstants.NULL)) {
			throw new Exception("Can't copy the disk");
		}

		// create new VAFile object
		VAFile vafile = createFile(srcFile.getId(), null,
				VAMConstants.VAF_FILE_TYPE_DISK, srcFile.getFormat(), srcFile
						.getSize());

		VADisk vadisk = new VADisk(vafile);
		vadisk.setCapacity(srcDisk.getCapacity());
		vadisk.setParent(srcFile.getGuid());
		vadisk.setReplica(true);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		// add the file to the database
		vafile = fileDao.add(vadisk);
		if (vafile == null) {
			throw new Exception("Can't add the file to the database.");
		}

		try {
			// copy disk
			FileOperation.getInstance().copyDisk(VAMConfig.getNfsHost(),
					VAMConfig.getNfsUser(), vafile, srcFile.getSavePath(),
					vafile.getSavePath(), this);
		} catch (Exception e) {
			// remove file in the database
			DaoFactory.getVAFileDao().remove(vafile.getGuid());
			throw new Exception(e);
		}

		return vafile;
	}

	/**
	 * convert disk format.
	 * 
	 * @param vafile
	 *            file object
	 * @param srcPath
	 *            the source disk path
	 * @param format
	 *            disk format
	 * @param deleteSrcFile
	 *            whether delete the source disk after converting
	 * @return a new file object
	 * @throws Exception
	 */
	public VAFile convertDiskFormat(VAFile vafile, String srcPath,
			String format, boolean deleteSrcFile) throws Exception {
		// check disk
		checkDiskType(vafile);

		// check state of the file
		if (vafile.getState() != VAMConstants.STATE_READY) {
			throw new Exception("The file's state is not correct");
		}

		// save old information
		String oldLocation = vafile.getLocation();
		String oldFormat = vafile.getFormat();
		// String oldParent = vafile.getParent();
		int oldState = vafile.getState();

		// set new information
		String newLocation = "file." + VAMUtil.genGuid();
		vafile.setSrcPath(srcPath);
		vafile.setLocation(newLocation);
		vafile.setFormat(format);
		// vafile.setParent(VAMConstants.NULL);
		vafile.setDeletefileAfterOperation(deleteSrcFile);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		fileDao.update(vafile);

		try {
			// convert disk format
			FileOperation.getInstance().convertDiskFormat(
					VAMConfig.getNfsHost(), VAMConfig.getNfsUser(), vafile,
					srcPath, vafile.getSavePath(), format, this);
		} catch (Exception e) {
			// restore the old information
			vafile.setLocation(oldLocation);
			vafile.setFormat(oldFormat);
			vafile.setState(oldState);
			// vafile.setParent(oldParent);
			DaoFactory.getVAFileDao().update(vafile);
			throw new Exception(e);
		}

		return vafile;
	}

	/**
	 * check disk type.
	 * 
	 * @param vafile
	 *            file object
	 * @throws Exception
	 */
	private void checkDiskType(VAFile vafile) throws Exception {
		if (vafile == null
				|| !vafile.getFileType()
						.equals(VAMConstants.VAF_FILE_TYPE_DISK)) {
			throw new Exception("The file type must be + "
					+ VAMConstants.VAF_FILE_TYPE_DISK + ".");
		}
	}

	/**
	 * create snapshot of a disk.
	 * 
	 * @param srcFile
	 *            the parent disk file object
	 * @param dstFile
	 *            the snapshot file object
	 * @param host
	 *            the name or IP of host creating snapshot
	 * @param user
	 *            the user name or IP of host creating snapshot
	 * @return snapshot file object
	 * @throws Exception
	 */
	public VAFile createSnapshot(VAFile srcFile, VAFile dstFile, String host,
			String user) throws Exception {
		// check file type
		checkDiskType(srcFile);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		VADisk instDisk = new VADisk(srcFile);

		// create new disk and set parent
		VADisk vadisk = new VADisk(dstFile);
		vadisk.setCapacity(instDisk.getCapacity());
		vadisk.setParent(srcFile.getGuid());

		// add the file to the database
		dstFile = fileDao.add(vadisk);
		if (dstFile == null) {
			throw new Exception("Can't add the file to the database.");
		}

		try {
			String path = null;
			String backingPath = null;
			if (host == null || user == null) {
				host = VAMConfig.getNfsHost();
				user = VAMConfig.getNfsUser();
			}
			
			path = dstFile.getSavePath();
			backingPath = srcFile.getSavePath();
			
			// create snapshot
			FileOperation.getInstance().createSnapshot(dstFile, host, user,
					path, backingPath, this);
		} catch (Exception e) {
			// remove file in the database
			fileDao.remove(dstFile.getGuid());
			throw new Exception(e);
		}

		return dstFile;
	}

	/**
	 * query file by GUID.
	 * 
	 * @param guid
	 *            file GUID
	 * @return file object
	 * @throws Exception
	 */
	public VAFile queryFile(String guid) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		VAFile vafile = fileDao.query(guid);

		if (vafile == null) {
			throw new Exception("The file with guid \"" + guid
					+ "\" is not existed");
		}

		return vafile;
	}

	/**
	 * update the file.
	 * 
	 * @param file
	 *            file object
	 * @return updated file object
	 * @throws Exception
	 */
	public VAFile updateFile(VAFile file) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		file = fileDao.update(file);
		if (file == null) {
			throw new Exception("Can't update the file");
		}

		return file;
	}

	/**
	 * get all files.
	 * 
	 * @return file lst
	 * @throws Exception
	 */
	
	public List<VAFile> getAllFile() throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		List<VAFile> resl = new ArrayList<VAFile>();
		List<VAObject> filel = fileDao.getAll();
		if (filel != null) {
			int size = filel.size();
			for (int i = 0; i < size; i++) {
				resl.add(new VAFile(filel.get(i)));
			}
		}

		return resl;
	}

	/**
	 * get file by type.
	 * 
	 * @param type
	 *            file type
	 * @return file list
	 * @throws Exception
	 */
	
	public List<VAFile> getFilesByType(String type) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		List<VAFile> resl = new ArrayList<VAFile>();
		List<VAObject> filel = fileDao.getFilesByType(type);
		if (filel != null) {
			int size = filel.size();
			for (int i = 0; i < size; i++) {
				resl.add(new VAFile(filel.get(i)));
			}
		}

		return resl;
	}

	/**
	 * remove file by GUID.
	 * 
	 * @param guid
	 *            file GUID
	 * @return whether remove the file successfully
	 * @throws Exception
	 */
	public boolean removeFile(String host, String user, String guid)
			throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// get the VAFile object with the GUID
		VAFile vafile = fileDao.query(guid);

		if (vafile == null) {
			// if the file is already deleted, should not throw exception
			return true;
			// throw new Exception("The file with guid \"" + guid
			//		+ "\" is not existed or available");
		}

		if (vafile.getParent().equals(VAMConstants.NULL)) {
			if (fileDao.getActiveReference(vafile.getGuid()) > 0) {
				throw new Exception("The file is in used");
			}
		} else {
			if (vafile.getRef() > 0) {
				throw new Exception("The file is in used");
			}
		}

		int oldState = vafile.getState();
		try {
			// remove file
			FileOperation.getInstance().removeFile(host, user, vafile,
					vafile.getSavePath(), this);
		} catch (RuntimeException e) {
			// restore state
			vafile.setState(oldState);
			fileDao.update(vafile);
			throw new Exception(e);
		}

		return true;
	}

	/**
	 * clear incorrect file.
	 * 
	 * @param clearFlag
	 *            the clear flag, which supports
	 *            VAMConstants.FILE_CLEAR_FLAG_ERROR,
	 *            VAMConstants.FILE_CLEAR_FLAG_NOT_READY,
	 *            VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_DATA,
	 *            VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_FILE,
	 *            VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_PARENT, it can use or
	 *            operation,
	 * @throws Exception
	 */
	
	public void clearFile(int clearFlag) throws Exception {
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		List<VAFile> fileList = getAllFile();

		// clear the file whose data can't be found in the database
		if ((clearFlag & VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_DATA) != 0) {
			Map<String, String> pathMap = new HashMap<String, String>();
			for (int i = 0; i < fileList.size(); i++) {
				VAFile va = (VAFile) fileList.get(i);
				pathMap.put(va.getSavePath(), "ok");
			}

			File fileDir = new File(VAMConfig.getFileDirLocation());
			if (fileDir.exists() && fileDir.isDirectory()) {
				checkFile(fileDir, pathMap);
			}
		}

		// clear the file whose physical file can't be found
		if ((clearFlag & VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_FILE) != 0) {
			File fileDir = new File(VAMConfig.getFileDirLocation());
			if (fileDir.exists() && fileDir.isDirectory()) {
				for (int i = 0; i < fileList.size(); i++) {
					VAFile va = (VAFile) fileList.get(i);
					File file = new File(va.getSavePath());
					if (!file.exists()) {
						fileDao.remove(va.getGuid());
					}
				}
			}
		}

		// clear the file whose state is error
		if ((clearFlag & VAMConstants.FILE_CLEAR_FLAG_ERROR) != 0) {
			List<VAObject> errorFiles = fileDao.getErrorFiles();
			for (int i = 0; i < errorFiles.size(); i++) {
				try {
					removeFile(null, null, errorFiles.get(i).getGuid());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}

		// clear the file whose state is not ready
		if ((clearFlag & VAMConstants.FILE_CLEAR_FLAG_NOT_READY) != 0) {
			List<VAObject> notReadyFiles = fileDao.getNotReadyFiles();
			for (int i = 0; i < notReadyFiles.size(); i++) {
				VAFile va = new VAFile(notReadyFiles.get(i));
				if (VAMUtil.getTimestamp() - va.getTimestamp() 
						> VAMConstants.HOUR / 2) {
					removeFile(null, null, va.getGuid());
				}
			}
		}

		// clear the file whose parent can't be found
		if ((clearFlag & VAMConstants.FILE_CLEAR_FLAG_NOT_FIND_PARENT) != 0) {
			fileList = getAllFile();
			Map<String, String> guidMap = new HashMap<String, String>();
			for (int i = 0; i < fileList.size(); i++) {
				VAFile va = (VAFile) fileList.get(i);
				guidMap.put(va.getGuid(), "ok");
			}
			for (int i = 0; i < fileList.size(); i++) {
				VAFile va = (VAFile) fileList.get(i);
				if (!va.getParent().equals(VAMConstants.NULL)
						&& guidMap.get(va.getParent()) == null
						&& va.getRef() == 0) {
					removeFile(null, null, va.getGuid());
				}
			}
		}
	}

	private void checkFile(File dir, Map<String, String> pathMap) {
		File[] subFiles = dir.listFiles();

		for (int i = 0; i < subFiles.length; i++) {
			if (subFiles[i].isDirectory()) {
				checkFile(subFiles[i], pathMap);
			} else {
				if (pathMap.get(subFiles[i].getPath()) == null
						&& !subFiles[i].getName().endsWith(".cfg")) {
					subFiles[i].delete();
				}
			}
		}
	}

	

	/**
	 * remove replicas.
	 * 
	 * @param guid
	 *            file's GUID
	 * @throws Exception
	 */
	public synchronized void removeReplicas(String guid)
			throws Exception {
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		
		List<VAObject> replicas = fileDao.getReplicas(guid);
		for (VAObject replica : replicas) {
			removeFile(null, null, replica.getGuid());
		}
	}

	/**
	 * create redundant disk.
	 * 
	 * @param file
	 *            base disk object
	 * @throws Exception
	 */
	public void createReplica(VAFile file) throws Exception {
		copyDisk(file);
	}

	/**
	 * get the capacity and size of the disk.
	 * 
	 * @param vafile
	 *            disk object
	 * @return input disk object with capacity and size
	 */
	public VADisk getDiskCapacity(VAFile vafile) {
		Runtime rt = Runtime.getRuntime();
		Process proc;
		long capacity = 0;
		long size = 0;

		// get the disk capacity and size
		try {
			String command = VAMUtil.getDiskInfoCommand(vafile.getSavePath());
			String[] shell = new String[] { "/bin/sh", "-c", command };
			proc = rt.exec(shell);

			InputStream is = proc.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("virtual size")) {
					Pattern pattern = Pattern.compile("(\\d+) bytes");
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						capacity = Long.parseLong(matcher.group(1));
					}
				}
			}

			String path = vafile.getSavePath();
			File file = new File(path);
			if (file.exists()) {
				size = file.length();
			}

		} catch (IOException e) {
			VAMUtil.outputLog(e.getMessage());
		}
		VADisk vadisk = new VADisk(vafile);
		vadisk.setCapacity(capacity);
		vadisk.setSize(size);

		return vadisk;
	}

	/**
	 * get the active reference of file with GUID.
	 * @param guid
	 * 		the file GUID
	 * @return active reference
	 * @throws Exception
	 */
	public int getActiveReference(String guid) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		
		return fileDao.getActiveReference(guid);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #updateCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void updateCallBack(VAObject obj) throws Exception {
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
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #createCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void removeCallBack(VAObject obj) throws Exception {
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
				removeReplicas(baseGuid);
			} 
		}

		// remove the file
		fileDao.remove(obj.getGuid());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #removeCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void createCallBack(VAObject obj) throws Exception {
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
					vafile.getSavePath(), null);
			return;
		}
		
		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #copyCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void copyCallBack(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath(), null);
			return;
		}

		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #convertCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void convertCallBack(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath(), null);
			return;
		}
		// update state
		vafile.setState(obj.getState());

		if (vafile.getState() != VAMConstants.STATE_READY) {
			fileDao.update(vafile);
			return;
		}

		// update disk information
		VADisk vadisk = getDiskCapacity(vafile);

		if (!vafile.getParent().equals(VAMConstants.NULL)) {
			vadisk.setParent(VAMConstants.NULL);
		}

		fileDao.update(vadisk);

		// delete file after operation
		if (vadisk.isDeleteFileAfterOperation()) {
			try {
				FileOperation.getInstance().removeFile(null, null, null,
						vadisk.getSrcPath(), null);
			} catch (Exception e) {
				VAMUtil.outputLog(e.getMessage());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #moveCallBack(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public void moveCallBack(VAObject obj) throws Exception {
		// get data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// if can't find the file , remove it
		VAFile vafile = fileDao.query(obj.getGuid());
		if (vafile == null) {
			vafile = (VAFile) obj;
			FileOperation.getInstance().removeFile(null, null, null,
					vafile.getSavePath(), null);
			return;
		}
		vafile.setState(obj.getState());
		fileDao.update(vafile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lingcloud.molva.xmm.vam.services.TaskFunction
	 * #check(org.lingcloud.molva.xmm.vam.pojos.VAObject)
	 */
	public boolean check(VAObject obj) throws Exception {
		if (obj != null) {
			if (obj.getOperationType() 
					== VAMConstants.VAO_OPERATION_TYPE_COPY_FILE) {
				// get data access object
				VAFileDao fileDao = DaoFactory.getVAFileDao();

				VAFile vafile = fileDao.query(obj.getGuid());
				return vafile.getState() != VAMConstants.STATE_READY;
			}
		}

		return true;
	}
}
