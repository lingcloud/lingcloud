/*
 *  @(#)AssetMatchMaker.java  2010-5-27
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

package org.lingcloud.molva.ocl.amm;

import org.lingcloud.molva.ocl.lease.Lease;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-27<br>
 * @author Xiaoyi Lu<br>
 */

public interface AssetMatchMaker {

	/**
	 * According to the users' requirements to select proper assets.
	 * 
	 * @param lease
	 * @return the return is a new lease with all matched assets.
	 * @throws Exception
	 */
	Lease assetMatchMaking(Lease lease) throws Exception;

	/**
	 * Check the validity of matched assets.
	 * 
	 * @param lease
	 * @throws Exception
	 *             if you do not satisfy the matched asset set, you must throw
	 *             exception in this method.
	 */
	void validateMachedAssets(Lease lease) throws Exception;

	/**
	 * When error occurred in the validateMachedAssets method, the errorHandler
	 * will be invoked to clear garbage information. Notice, all errors in this
	 * method should be handled by itself.
	 * 
	 * @param newest
	 */
	Lease matchErrorHandler(Lease lease);

	/**
	 * When the negotiation is ongoing, but the user wants to terminate the
	 * lease, this method will be invoked.
	 * 
	 * @param lease
	 */
	void handleTermination(Lease lease);

	/**
	 * decide the lease whether or not can be satiable.
	 * 
	 * @param lease
	 * @throws Exception
	 *             before doing match making, sometimes we may need to check the
	 *             lease whether or not can be satisfied. If not, throw
	 *             exception.
	 */
	void satiableDecision(Lease lease) throws Exception;
}
