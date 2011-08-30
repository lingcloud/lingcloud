/*
 *  @(#)AssetConstants.java  2010-5-22
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

package org.lingcloud.molva.ocl.asset;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-22<br>
 * @author Xiaoyi Lu<br>
 */

public class AssetConstants {

	/**
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 2010-5-22<br>
	 * @author Xiaoyi Lu<br>
	 */
	public static enum AssetState {
		UNAVAILABLE, IDLE, RESERVED, LEASED
	};

	public static final String ASSET_RCONTROLLER = "ASSET_RCONTROLLER";

	public static final String ASSET_LEASER_RCONTROLLER = 
		"ASSET_LEASER_RCONTROLLER";

	public static final Object RESERVE_LOCK = new Object();
	private AssetConstants() {
		
	}
}
