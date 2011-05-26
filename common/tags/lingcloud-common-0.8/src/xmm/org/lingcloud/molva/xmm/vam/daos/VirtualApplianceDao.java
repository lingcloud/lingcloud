/*
 *  @(#)VAInstanceDaoNaming.java  2010-5-25
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

package org.lingcloud.molva.xmm.vam.daos;

import java.util.List;

import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.pojos.VMState;
import org.lingcloud.molva.xmm.vam.pojos.VirtualAppliance;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-25<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VirtualApplianceDao extends VADao {
	/**
	 * single instance design pattern.
	 */
	private static VirtualApplianceDao dao = new VirtualApplianceDao();

	/**
	 * get a appliance data access object instance.
	 * 
	 * @return appliance data access object
	 */
	public static VirtualApplianceDao getInstance() {
		if (dao == null) {
			dao = new VirtualApplianceDao();
		}
		return dao;
	}

	private VirtualApplianceDao() {
		super();
	}

	/**
	 * add the appliance to the database.
	 * 
	 * @param appliance
	 *            the appliance object
	 * @return if add the appliance to the database successfully return the
	 *         appliance object in the database, else return null
	 * @throws Exception
	 */
	public synchronized VirtualAppliance add(VirtualAppliance appliance)
			throws Exception {
		if (appliance == null) {
			return null;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(appliance.getGuid());

		// if the GUID has been used
		if (vao != null && vao.getType().equals(
				VAMConstants.VIRTUAL_APPLIANCE)) {
			throw new Exception("The appliance's guid \"" + appliance.getGuid()
					+ "\" has been used. Please change one!");
		}

		// register the appliance
		vao = super.addData(appliance);
		if (vao == null) {
			return null;
		}

		// increase the reference of the appliance's parent
		if (!appliance.getParent().equals(VAMConstants.NULL)) {
			changeRef(appliance.getParent(), VAMConstants.INCREASE);
		}

		return new VirtualAppliance(vao);
	}

	/**
	 * update the appliance in the database.
	 * 
	 * @param appliance
	 *            the appliance object
	 * @return if update the appliance successfully return the appliance object
	 *         in the database, else return null
	 * @throws Exception
	 */
	public synchronized VirtualAppliance update(VirtualAppliance appliance)
			throws Exception {
		if (appliance == null) {
			return null;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(appliance.getGuid());
		// can't find the appliance
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE)) {
			return null;
		}

		VirtualAppliance oldApp = new VirtualAppliance(vao);

		// update the appliance
		appliance.setRef(oldApp.getRef());
		vao = super.updateData(appliance);

		if (vao == null) {
			return null;
		}

		// update the reference of the appliance's parent
		if (!oldApp.getParent().equals(appliance.getParent())) {
			if (!oldApp.getParent().equals(VAMConstants.NULL)) {
				changeRef(oldApp.getParent(), VAMConstants.DECREASE);
			}
			if (!appliance.getParent().equals(VAMConstants.NULL)) {
				changeRef(appliance.getParent(), VAMConstants.INCREASE);
			}
		}

		return new VirtualAppliance(vao);
	}

	/**
	 * remove the appliance in the database by GUID.
	 * 
	 * @param guid
	 *            the appliance GUID
	 * @return return true if remove the appliance successfully, else return
	 *         false
	 * @throws Exception
	 */

	public synchronized boolean remove(String guid) throws Exception {
		if (guid == null) {
			return false;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the appliance
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE)) {
			return false;
		}
		// remove the appliance with GUID in the database
		VAObject instances = super.removeData(guid);
		if (instances == null) {
			return false;
		}

		// decrease the reference of the appliance's parent
		VirtualAppliance va = new VirtualAppliance(vao);
		if (!va.getParent().equals(VAMConstants.NULL)) {
			changeRef(va.getParent(), VAMConstants.DECREASE);
		}

		return true;
	}

	/**
	 * query the appliance in the database by GUID.
	 * 
	 * @param guid
	 *            the appliance GUID
	 * @return return the queried appliance
	 * @throws Exception
	 */
	public synchronized VirtualAppliance query(String guid) throws Exception {
		if (guid == null) {
			return null;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the appliance
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE)) {
			return null;
		}

		return new VirtualAppliance(vao);
	}

	/**
	 * get all the appliance in the database.
	 * 
	 * @return the appliance list
	 * @throws Exception
	 */

	public synchronized List<VAObject> getAll() throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VA_PARENT + "']",
				"attributes['" + VAMConstants.VAO_STATE + "']" };
		String[] operator = new String[] { "=", "=", "<>" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE,
				VAMConstants.NULL, VAMConstants.STATE_MAKING };

		// search the instance in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * get the appliances whose category GUID is cateGuid.
	 * 
	 * @param cateGuid
	 *            the GUID of category
	 * @return the appliance list
	 * @throws Exception
	 */

	public synchronized List<VAObject> getAppliancesByCategory(String cateGuid)
			throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VA_CATEGORY + "']",
				"attributes['" + VAMConstants.VA_PARENT + "']",
				"attributes['" + VAMConstants.VAO_STATE + "']" };
		String[] operator = new String[] { "=", "=", "=", "<>" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE,
				cateGuid, VAMConstants.NULL, VAMConstants.STATE_MAKING };

		// search the appliance in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * get the appliances which are making.
	 * 
	 * @return the appliance list
	 * @throws Exception
	 */

	public synchronized List<VAObject> getMakingAppliances() throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VA_PARENT + "']",
				"attributes['" + VAMConstants.VAO_STATE + "']" };
		String[] operator = new String[] { "=", "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE,
				VAMConstants.NULL, VAMConstants.STATE_MAKING };

		// search the instance in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * get all the copies of appliance.
	 * 
	 * @return the appliance list
	 * @throws Exception
	 */
	public synchronized List<VAObject> getApplianceCopys() throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VA_PARENT + "']", };
		String[] operator = new String[] { "=", "<>" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE,
				VAMConstants.NULL };

		// search the instance in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * change the reference of the appliance.
	 * 
	 * @param guid
	 *            the appliance's GUID
	 * @param type
	 *            the type of increase or decrease
	 * @return return true if change the appliance's reference successfully,
	 *         else return false
	 * @throws Exception
	 */
	public synchronized boolean changeRef(String guid, int type)
			throws Exception {
		if (guid == null) {
			return false;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the appliance
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE)) {
			return false;
		}
		// change the appliance's reference
		VirtualAppliance va = new VirtualAppliance(vao);
		int ref = va.getRef();
		if (type == VAMConstants.INCREASE) {
			ref++;
		} else {
			ref--;
		}
		va.setRef(ref);
		// update the appliance in the database
		super.updateData(va);

		return true;
	}

	/**
	 * get the make appliance virtual machines' state.
	 * 
	 * @return a state object
	 * @throws Exception
	 */

	public synchronized VMState getMakeApplianceVMState()
			throws Exception {
		// set query condition
		String[] queries = new String[] { "type" };
		String[] operator = new String[] { "=" };
		Object[] values = new Object[] { VAMConstants.MAKE_APLIANCE_VM };

		// search the instance in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		VMState vmState = null;
		VAObject vao = null;
		// if can't get the object add one
		if (resls.size() == 0) {
			vmState = new VMState();
			vao = super.addData(vmState);
		} else {
			vao = (VAObject) resls.get(0);
		}

		if (vao == null) {
			return null;
		}
		return new VMState(vao);
	}

	/**
	 * update the make appliance virtual machines' state.
	 * 
	 * @param vmState
	 *            virtual machines' state object
	 * @return the state object
	 * @throws Exception
	 */
	public synchronized VMState udateMakeApplianceVMState(
			VMState vmState) throws Exception {
		if (vmState == null) {
			return null;
		}
		// locate the appliance object by GUID
		VAObject vao = super.viewData(vmState.getGuid());
		// can't find the appliance
		if (vao == null 
				|| !vao.getType().equals(VAMConstants.MAKE_APLIANCE_VM)) {
			return null;
		}

		vao = super.updateData(vmState);

		if (vao == null) {
			return null;
		}

		return new VMState(vao);
	}
}
