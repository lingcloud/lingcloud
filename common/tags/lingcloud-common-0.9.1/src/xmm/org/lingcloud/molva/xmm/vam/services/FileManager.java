/*
 *  @(#)FileManager.java  2011-07-28
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lingcloud.molva.xmm.vam.controllers.Controller;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
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
public class FileManager {
	private ExecutorService busyThreadPool;
	private ExecutorService lightThreadPool;
	private Map<String, IFileTask> taskPool = null;
	private static FileManager instance = null;
	private static Object lock = null;

	private FileManager() {
		busyThreadPool = Executors
				.newFixedThreadPool(VAMConstants.MAX_BUSY_THREAD);
		lightThreadPool = Executors
				.newFixedThreadPool(VAMConstants.MAX_LIGHT_THREAD);
		taskPool = new HashMap<String, IFileTask>();
	}

	static {
		lock = new Object();
		synchronized (lock) {
			instance = new FileManager();
		}
		instance.load();
	}

	/**
	 * load file data.
	 */
	private void load() {
		try {
			List<VAFile> files = ServiceFactory.getFileService()
					.getProcessingFiles();
			for (VAFile file : files) {
				Controller controller = (Controller) Class.forName(
						file.getController()).newInstance();
				controller.resume(file);
			}
		} catch (Exception e) {
			VAMUtil.infoLog(e.getMessage());
			e.printStackTrace();
		}
	}

	public static FileManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new FileManager();
				}
			}
		}

		return instance;
	}

	/**
	 * add one task.
	 * @param task
	 * 		task object
	 * @param type
	 * 		task type
	 */
	public synchronized void addTask(IFileTask task, int type) {
		if (taskPool.get(task.getContent()) != null) {
			return;
		}
		taskPool.put(task.getContent(), task);
		
		if (type == VAMConstants.THREAD_TYPE_BUSY) {
			busyThreadPool.execute(task);
		} else if (type == VAMConstants.THREAD_TYPE_LIGHT) {
			lightThreadPool.execute(task);
		}
	}
	
	public synchronized void completeTask(IFileTask task) {
		taskPool.remove(task.getContent());
	}
}
