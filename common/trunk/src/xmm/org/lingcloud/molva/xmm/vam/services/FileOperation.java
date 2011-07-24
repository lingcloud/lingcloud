/*
 *  @(#)FileOperation.java  2010-7-22
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
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
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
class FileOperation {
	/**
	 * single instance design pattern.
	 */
	private static FileOperation instance = null;

	/**
	 * get a file operation instance.
	 * @return FileOperation instance
	 */
	public static FileOperation getInstance() {
		if (instance == null) {
			instance = new FileOperation();
		}
		return instance;
	}

	private FileOperation() {

	}

	private long getTimestamp() {
		return VAMUtil.getTimestamp();
	}
	
	/**
	 * move file operation.
	 * @param host
	 * 		the host name or IP
	 * @param user
	 * 		the host user name
	 * @param vafile
	 * 		file object
	 * @param srcPath
	 * 		source file path
	 * @param dstPath
	 * 		destination file path
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void moveFile(String host, String user, VAFile vafile, 
			String srcPath, String dstPath,
			TaskFunction taskFunc) throws Exception {
		if (srcPath.equals(dstPath)) {
			return;
		}
		
		// create the directory
		VAMUtil.createDirectory(vafile.getSavePath());
		// get move file command.
		List<String> cmdList = new ArrayList<String>();
		if (host == null || user == null) {
			host = VAMConfig.getNfsHost();
			user = VAMConfig.getNfsUser();
		}
		String command = VAMUtil.getMoveFileCommand(host,
					user, srcPath, dstPath);
		cmdList.add(command);

		if (vafile != null) {
			// get file data access object
			VAFileDao fileDao = DaoFactory.getVAFileDao();

			// update the file
			vafile.setState(VAMConstants.STATE_PROCESSING);
			vafile.setOperationType(VAMConstants.VAO_OPERATION_TYPE_MOVE_FILE);
			vafile.setSrcPath(srcPath);
			vafile.setTimestamp(getTimestamp());
			fileDao.update(vafile);
		}
		
		// add task to the thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_MOVE,
				taskFunc, vafile, VAMConstants.STATE_READY,
				VAMConstants.STATE_ERROR, cmdList,
				VAMConstants.THREAD_TYPE_IDLE);
	}

	/**
	 * remove file operation.
	 * @param host
	 * 		the host name or IP
	 * @param user
	 * 		the host user name
	 * @param vafile
	 * 		the file object
	 * @param path
	 * 		the file path
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void removeFile(String host, String user, VAFile vafile,
			String path, TaskFunction taskFunc) throws Exception {

		// get remove file command.
		List<String> cmdList = new ArrayList<String>();
		
		if (host == null || user == null) {
			host = VAMConfig.getNfsHost();
			user = VAMConfig.getNfsUser();
		}
		String command = VAMUtil.getRemoveFileCommand(host, user, path);
		cmdList.add(command);

		if (vafile != null) {
			// get file data access object
			VAFileDao fileDao = DaoFactory.getVAFileDao();

			// update the file
			vafile.setState(VAMConstants.STATE_PROCESSING);
			vafile.setOperationType(
					VAMConstants.VAO_OPERATION_TYPE_DELETE_FILE);
			vafile.setSrcPath(path);
			vafile.setTimestamp(getTimestamp());
			fileDao.update(vafile);
		}
		
		// add task to the thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_REMOVE,
				taskFunc, vafile, VAMConstants.STATE_DELETED,
				VAMConstants.STATE_ERROR, cmdList,
				VAMConstants.THREAD_TYPE_LIGHT);
	}

	/**
	 * copy disk operation.
	 * @param host
	 * 		the host name or IP
	 * @param user
	 * 		the host user name
	 * @param vafile
	 * 		file object
	 * @param srcPath
	 * 		source file path
	 * @param dstPath
	 * 		destination file path
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void copyDisk(String host, String user, VAFile vafile, 
			String srcPath, String dstPath,
			TaskFunction taskFunc) throws Exception {
		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// update file
		vafile.setState(VAMConstants.STATE_PROCESSING);
		vafile.setOperationType(VAMConstants.VAO_OPERATION_TYPE_COPY_FILE);
		vafile.setSrcPath(srcPath);
		vafile.setTimestamp(getTimestamp());

		// create a command list and add copy file command, move file command, 
		// copy the disk to temporary directory, 
		// then move to file store directory
		List<String> copyCmdList = new ArrayList<String>();
		String tempPath = VAMConfig.getTempDirLocation()
				+ vafile.getLocation();
		// create the directory
		VAMUtil.createDirectory(vafile.getSavePath());
		VAMUtil.createDirectory(VAMConfig.getTempDirLocation()
				+ vafile.getLocation());
		String command = VAMUtil.getCopyFileCommand(host, user, srcPath, 
				tempPath);
		copyCmdList.add(command);
		command = VAMUtil.getMoveFileCommand(host, user, tempPath, dstPath);
		copyCmdList.add(command);

		// update the file
		fileDao.update(vafile);

		// add copy disk task to thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_COPY,
				taskFunc, vafile, VAMConstants.STATE_READY,
				VAMConstants.STATE_ERROR, copyCmdList,
				VAMConstants.THREAD_TYPE_BUSY);
	}

	/**
	 * convert disk format operation.
	 * @param host
	 * 		the host name or IP
	 * @param user
	 * 		the host user name
	 * @param vafile
	 * 		file object
	 * @param srcPath
	 * 		source file path
	 * @param dstPath
	 * 		destination file path
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void convertDiskFormat(String host, String user, VAFile vafile,
			String srcPath, String dstPath, String format, 
			TaskFunction taskFunc) throws Exception {
		// create the directory
		VAMUtil.createDirectory(vafile.getSavePath());
		// get convert disk command
		List<String> cmdList = new ArrayList<String>();
		if (host == null || user == null) {
			host = VAMConfig.getNfsHost();
			user = VAMConfig.getNfsUser();
		}
		String command = VAMUtil.getConvertDiskCommand(
				host, user,	format, srcPath, dstPath);
		cmdList.add(command);

		if (vafile != null) {
			// get file data access object
			VAFileDao fileDao = DaoFactory.getVAFileDao();
			
			// update the file
			vafile.setState(VAMConstants.STATE_PROCESSING);
			vafile.setOperationType(
					VAMConstants.VAO_OPERATION_TYPE_CONVERT_FORMAT);
			vafile.setSrcPath(srcPath);
			vafile.setTimestamp(getTimestamp());
			fileDao.update(vafile);
		}
		
		// add convert disk format task to thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_CONVERT,
				taskFunc, vafile, VAMConstants.STATE_READY,
				VAMConstants.STATE_ERROR, cmdList,
				VAMConstants.THREAD_TYPE_BUSY);
	}

	/**
	 * create virtual disk operation.
	 * @param host
	 * 		the host name or IP
	 * @param user
	 * 		the host user name
	 * @param vafile
	 * 		file object
	 * @param path
	 * 		the virtual disk path
	 * @param capacity
	 * 		the virtual disk capacity
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void createDisk(String host, String user, VAFile vafile, 
			String path, long capacity, TaskFunction taskFunc) 
	throws Exception {
		// create the directory
		VAMUtil.createDirectory(vafile.getSavePath());
		// get the create virtual disk command
		List<String> cmdList = new ArrayList<String>();
		if (host == null || user == null) {
			host = VAMConfig.getNfsHost();
			user = VAMConfig.getNfsUser();
		}
		String command = VAMUtil.getCreateDiskCommand(host,
				user, vafile.getFormat(), path, capacity, null);
		cmdList.add(command);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();
		
		// update the file
		vafile.setState(VAMConstants.STATE_PROCESSING);
		vafile.setOperationType(VAMConstants.VAO_OPERATION_TYPE_CREATE_DISK);
		vafile.setSrcPath(path);
		vafile.setTimestamp(getTimestamp());
		fileDao.update(vafile);
		
		// add create virtual disk task to thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_UPDATE,
				taskFunc, vafile, VAMConstants.STATE_READY,
				VAMConstants.STATE_ERROR, cmdList,
				VAMConstants.THREAD_TYPE_IDLE);
	}

	/**
	 * create the snapshot of a disk operation. 
	 * @param vafile
	 * 		file object
	 * @param host
	 * 		the name or IP of the host creating snapshot
	 * @param user
	 * 		the user name of the host creating snapshot
	 * @param path
	 * 		the snapshot path
	 * @param backingPath
	 * 		the parent disk path
	 * @param taskFunc
	 * 		task function object, it contains call back function
	 * @throws Exception
	 */
	public void createSnapshot(VAFile vafile, String host, String user,
			String path, String backingPath, TaskFunction taskFunc)
			throws Exception {
		// create the directory
		VAMUtil.createDirectory(vafile.getSavePath());
		// get the create snapshot command.
		List<String> cmdList = new ArrayList<String>();
		String command = VAMUtil.getCreateSnapshotCommand(host, user, 
				vafile.getFormat(), path, backingPath);
		cmdList.add(command);

		// get file data access object
		VAFileDao fileDao = DaoFactory.getVAFileDao();

		// update the file
		vafile.setState(VAMConstants.STATE_PROCESSING);
		vafile
			.setOperationType(VAMConstants.VAO_OPERATION_TYPE_CREATE_SNAPSHOT);
		vafile.setSrcPath(backingPath);
		vafile.setTimestamp(getTimestamp());
		fileDao.update(vafile);
		
		// add create snapshot task to thread pool
		OperationThread.getInstance().addTask(VAMConstants.TASK_TYPE_CREATE,
				taskFunc, vafile, VAMConstants.STATE_READY,
				VAMConstants.STATE_ERROR, cmdList,
				VAMConstants.THREAD_TYPE_IDLE);
	}
}
