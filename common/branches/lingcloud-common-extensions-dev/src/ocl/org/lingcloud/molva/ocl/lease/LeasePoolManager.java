/*
 *  @(#)LeasePoolManager.java  2010-5-17
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

package org.lingcloud.molva.ocl.lease;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-17<br>
 * @author Xiaoyi Lu<br>
 */

public class LeasePoolManager {
	private static final Map<String, Lease> LEASE_POOL
		= Collections.synchronizedMap(new HashMap<String, Lease>());
	
	private LeasePoolManager() {
		
	}
	
	/**
	 * put a new lease to pool.
	 * @param lease
	 * @throws Exception
	 */
	public static synchronized void putLease(Lease lease) throws Exception {
		if (lease == null || lease.getGuid() == null
				|| "".equals(lease.getGuid())) {
			throw new Exception(
					"The lease object or the guid of lease is null or blank.");
		}
		if (LEASE_POOL.containsKey(lease.getGuid())) {
			LEASE_POOL.remove(lease.getGuid());
		}
		LEASE_POOL.put(lease.getGuid(), lease);
	}

	/**
	 * get lease from pool.
	 * @param lease
	 * @return
	 * @throws Exception
	 */
	public static synchronized Lease getLease(Lease lease) throws Exception {
		if (lease == null || lease.getGuid() == null
				|| "".equals(lease.getGuid())) {
			throw new Exception(
					"The lease object or the guid of lease is null or blank.");
		}
		return LEASE_POOL.get(lease.getGuid());
	}

	/**
	 * remove lease from pool.
	 * @param lease
	 * @throws Exception
	 */
	public static synchronized void removeLease(Lease lease) throws Exception {
		if (lease == null || lease.getGuid() == null
				|| "".equals(lease.getGuid())) {
			throw new Exception(
					"The lease object or the guid of lease is null or blank.");
		}
		if (LEASE_POOL.containsKey(lease.getGuid())) {
			LEASE_POOL.remove(lease.getGuid());
		}
	}

}
