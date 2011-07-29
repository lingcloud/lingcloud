/*
 *  @(#)SteamGobbler.java  Dec 1, 2010
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

package org.lingcloud.molva.xmm.vam.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-2<br>
 * @author Ruijian Wang<br>
 * 
 */
public class StreamGobbler extends Thread {
	private LinkedList<InputStream> isList;
	private String type;
	private boolean acquire;
	private boolean active;
	private Date lastActiveTime;
	private static final int HOUR = 60 * 60 * 1000;
	private Object streamLock;

	private void init() {
		isList = new LinkedList<InputStream>();
		acquire = false;
		active = false;
		streamLock = new Object();
	}
	
	public StreamGobbler() {
		init();
	}

	public StreamGobbler(String type) {
		this.type = type;
		init();
	}

	public synchronized void acquire() {
		acquire = true;
		lastActiveTime = new Date();
	}

	public synchronized void release() {
		if (!active) {
			acquire = false;
		}
	}

	public boolean isAvailable() {
		return !acquire
				|| (!active && getGapMilliSecondsBetweenTimes(lastActiveTime,
						new Date()) > HOUR / 2);
	}

	private long getGapMilliSecondsBetweenTimes(Date baseTime, Date destTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		long l1 = calendar.getTimeInMillis();
		calendar.setTime(destTime);
		long l2 = calendar.getTimeInMillis();
		return (l2 - l1); // milliseconds
	}

	/**
	 * add inputStream.
	 * 
	 * @param is
	 *            input stream
	 */
	public synchronized void addStream(InputStream is) {
			if (is != null) {
				this.isList.add(is);
			}
	
			notifyAll();
	}

	/**
	 * get one inputStream.
	 * 
	 * @return input stream
	 */
	private InputStream getListElement() {
		InputStream is = null;
		if (this.isList.size() > 0) {
			is = this.isList.getFirst();
			this.isList.removeFirst();
		}
		return is;
	}

	/**
	 * get one inputStream.
	 * 
	 * @return input stream
	 * @throws InterruptedException
	 */
	private synchronized InputStream getStream() throws InterruptedException {
			if (this.isList.size() <= 0) {
				wait();
			}
			return getListElement();
	}

	public void run() {
		String threadName = getName();
		while (!isInterrupted()) {
			InputStream is = null;
			try {
				// get one stream and out put to log
				is = getStream();
				if (is != null) {
					active = true;
					lastActiveTime = new Date();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						VAMUtil.outputLog(threadName + ": " + type + " > "
								+ line);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				VAMUtil.outputLog(e.getMessage());
			}
			active = false;
		}

	}
}
