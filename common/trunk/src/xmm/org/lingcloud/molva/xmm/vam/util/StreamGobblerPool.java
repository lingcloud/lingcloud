/*
 *  @(#)StreamGobblerPool.java  2011-07-29
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

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-07-29<br>
 * @author Ruijian Wang<br>
 * 
 */
public class StreamGobblerPool {
	public static final int TRY_TIMES = 10;
	public static final int WAIT_TIME = 30000;
	private StreamGobbler[] streamGobblerPool = null;

	public StreamGobblerPool(int size, String type) {
		streamGobblerPool = new StreamGobbler[size];
		for (int i = 0; i < size; i++) {
			streamGobblerPool[i] = new StreamGobbler(type);
			streamGobblerPool[i].start();
		}
	}

	/**
	 * acquire one stream gobbler, if no stream gobbler is available, wait.
	 * 
	 * @return stream gobbler object
	 * @throws InterruptedException
	 */
	public synchronized StreamGobbler acquireStreamGobbler()
			throws InterruptedException {
		for (int i = 0; i < TRY_TIMES; i++) {
			for (StreamGobbler gobbler : streamGobblerPool) {
				if (gobbler.isAvailable()) {
					gobbler.acquire();
					return gobbler;
				}
			}

			wait(WAIT_TIME);
		}

		return null;
	}

	/**
	 * release stream gobbler.
	 * 
	 * @param gobbler
	 *            stream gobbler object
	 */
	public synchronized void releaseStreamGobbler(StreamGobbler gobbler) {
		if (gobbler != null) {
			gobbler.release();
			notifyAll();
		}
	}
}
