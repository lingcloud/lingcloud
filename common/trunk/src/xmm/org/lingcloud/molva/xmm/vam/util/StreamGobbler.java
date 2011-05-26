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
	
	private boolean stop;
	
	public StreamGobbler() {
		isList = new LinkedList<InputStream>();
	}

	public StreamGobbler(String type) {
		isList = new LinkedList<InputStream>();
		this.type = type;
		stop = false;
	}

	/**
	 * add inputStream.
	 * @param is
	 * 		input stream
	 */
	public synchronized void addStream(InputStream is) {
		this.isList.add(is);
		
		notifyAll();
	}
	
	/**
	 * get one inputStream.
	 * @return input stream
	 */
	private synchronized InputStream getListElement() {
		InputStream is = null;
		if (this.isList.size() > 0) {
			is = this.isList.getFirst();
			this.isList.removeFirst();
		}
		return is;
	}
	
	/**
	 * get one inputStream.
	 * @return input stream
	 * @throws InterruptedException
	 */
	private synchronized InputStream getStream() throws InterruptedException {
		if (this.isList.size() <= 0) {
			wait();
		}
		
		return getListElement();
	}
	
	/**
	 * end the thread.
	 */
	public void end() {
		stop = true;
	}

	public void run() {
		String threadName = getName();
		while (!stop) {
			InputStream is = null;
			try {
				// get one stream and out put to log
				is = getStream();
				if (is != null) {
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						VAMUtil.outputLog(threadName + ":" + type + ">" + line);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				VAMUtil.outputLog(e.getMessage());
			}
		}

	}
}
