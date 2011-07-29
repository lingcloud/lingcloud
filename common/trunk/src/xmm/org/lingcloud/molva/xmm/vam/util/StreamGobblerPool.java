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
	 * @return stream gobbler object
	 * @throws InterruptedException
	 */
	public synchronized StreamGobbler acquireStreamGobbler()
			throws InterruptedException {
		while (true) {
			for (StreamGobbler gobbler : streamGobblerPool) {
				if (gobbler.isAvailable()) {
					gobbler.acquire();
					return gobbler;
				}
			}
			Thread.sleep(VAMConstants.SECOND);
		}
	}
	
	/**
	 * release stream gobbler.
	 * @param gobbler
	 * 		stream gobbler object
	 */
	public void releaseStreamGobbler(StreamGobbler gobbler) {
		if (gobbler != null) {
			gobbler.release();
		}
	}
}
