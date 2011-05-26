/*
 *  @(#)VMState.java  2010-7-28
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

package org.lingcloud.molva.xmm.vam.pojos;

import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 28 Jul 2010<br>
 * @author root<br>
 *
 */
public class VMState extends VAObject {

	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -4854076780434877L;

	/**
	 * the constructor.
	 */
	public VMState() {
		super.setType(VAMConstants.MAKE_APLIANCE_VM);
	}
	
	/**
	 * to construct a VMSate from a right name.
	 * 
	 * @param vao
	 *            the basic object of virtual appliance
	 */
	public VMState(VAObject vao) {
		super(vao);
		if (vao != null
				&& !(vao.getType()
						.equals(VAMConstants.MAKE_APLIANCE_VM))) {
			String msg = "Wrong VMState object type. It should be "
					+ VAMConstants.MAKE_APLIANCE_VM + " but get "
					+ vao.getName();
			throw new RuntimeException(msg);
		}
	}

	/**
	 * get the specific virtual machine state.
	 * @param index
	 * 		virtual machine index
	 * @return
	 */
	public int getMakeApplianceVMState(int index) {
		if (VAMUtil.isBlankOrNull((String) this.getAttributes().get(
				VAMConstants.MAKE_APPLIANCE_VM_STATE + index))) {
			return VAMConstants.MAKE_APPLIANCE_VM_STATE_IDLE;
		}
		return Integer.parseInt((String) this.getAttributes().get(
				VAMConstants.MAKE_APPLIANCE_VM_STATE + index));
	}

	/**
	 * set the specific virtual machine state.
	 * @param index
	 * 		virtual machine index
	 * @param state
	 * 		virtual machine state
	 */
	public void setMakeApplianceVMState(int index, int state) {
		this.getAttributes().put(VAMConstants.MAKE_APPLIANCE_VM_STATE + index,
				"" + state);
	}

}
