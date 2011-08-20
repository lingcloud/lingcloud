/*
 *  @(#)VACategory.java  2010-5-20
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


/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2010-7-12<br>
 * @author Ruijian Wang<br>
 *
 */
public class VACategory extends VAObject {
	/**
	 * the serial ID.
	 */
	private static final long serialVersionUID = -7944171237325875398L;

	/**
	 * the constructor.
	 */
	public VACategory() {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_CATEGORY);
	}
	
	/**
	 * the constructor.
	 * @param guid 
	 * 		the category's GUID.
	 * @param category
	 * 		the category's name.
	 */
	public VACategory(String guid, String category) {
		setGuid(guid);
		setCategory(category);

		super.setType(VAMConstants.VIRTUAL_APPLIANCE_CATEGORY);
	}
	
	/**
	 * to construct a VACategory from a right type.
	 * 
	 * @param vao
	 *            the basic object of virtual appliance
	 */
	public VACategory(VAObject vao) {
		super(vao);
		if (vao != null
				&& !(vao.getType()
						.equals(VAMConstants.VIRTUAL_APPLIANCE_CATEGORY))) {
			String msg = "Wrong virtual appliance object type. It should be "
					+ VAMConstants.VIRTUAL_APPLIANCE_CATEGORY + " but get "
					+ vao.getType();
			throw new RuntimeException(msg);
		}
	}
	
	/**
	 * Clone an object.
	 * 
	 * @return the cloned object.
	 * @throws CloneNotSupportedException
	 *             Not support clone.
	 */
	public VACategory clone() throws CloneNotSupportedException {
		try {
			VACategory vacate = new VACategory(this);
			return vacate;
		} catch (Exception e) {
			throw new CloneNotSupportedException("Clone failed due to " + e);
		}
	}
	
	/**
	 * set the name. It must override the method that make sure the
	 * object has right name.
	 * 
	 * @param name
	 *            the name of GNode.
	 */
	public void setName(String name) {
		super.setName(VAMConstants.VIRTUAL_APPLIANCE_CATEGORY);
	}
	
	/**
	 * set the type. It must override the method that make sure the
	 * object has right type.
	 * 
	 * @param type
	 *            the type of GNode.
	 */
	public void setType(String type) {
		super.setType(VAMConstants.VIRTUAL_APPLIANCE_CATEGORY);
	}
	
	/**
	 * get the category name.
	 * @return
	 */
	public String getCategory() {
		return (String) this.getAttributes().get(VAMConstants.VAC_CATEGORY);
	}

	/**
	 * set the category name.
	 * @param category
	 * 		category name
	 */
	public void setCategory(String category) {
		this.getAttributes().put(VAMConstants.VAC_CATEGORY, category);
	}
}
