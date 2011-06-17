/*
 *  @(#)PollingTaskManager.java  2010-5-30
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

package org.lingcloud.molva.ocl.poll;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.lingcloud.molva.ocl.util.VoalUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-30<br>
 * @author Xiaoyi Lu<br>
 */
public class PollingTaskManager {
	private PollingTaskManager() {
		
	}

	private static final int MAX_THREADS = 10;

	private static final int INIT_DELAY = 20 * 1000;

	private static final int DEFAULT_INTERVAL = 5 * 60 * 1000; // milliseconds

	private static int pollingInterval; // milliseconds

	private static Map<String, ScheduledFuture<?>> futures;

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(MAX_THREADS);

	static {
		futures = new HashMap<String, ScheduledFuture<?>>();
		String pi = VoalUtil.getPollIntervalInCfgFile();
		if (pi == null || "".equals(pi)) {
			pollingInterval = DEFAULT_INTERVAL;
		} else {
			try {
				pollingInterval = Integer.valueOf(pi).intValue();
				if (pollingInterval < INIT_DELAY) {
					pollingInterval = INIT_DELAY;
				}
			} catch (Exception e) {
				pollingInterval = DEFAULT_INTERVAL;
			}
			
		}
	}

	public static synchronized String addPollingTask(PollingTask task) {
		ScheduledFuture<?> future = threadPool.scheduleWithFixedDelay(task,
				INIT_DELAY, pollingInterval, TimeUnit.MILLISECONDS);
		futures.put(task.getTaskGuid(), future);
		return task.getTaskGuid();
	}

	public static synchronized void removePollingTask(String taskId) {
		ScheduledFuture<?> future = futures.get(taskId);
		if (future != null) {
			future.cancel(false);
		}
		futures.remove(taskId);
	}

	public static synchronized boolean isPolled(String taskId) {
		if (futures.containsKey(taskId)) {
			return true;
		}
		return false;
	}
}
