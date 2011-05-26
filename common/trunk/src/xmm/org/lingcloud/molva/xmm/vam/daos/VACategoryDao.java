/*
 *  @(#)VACategoryDao.java  2010-7-12
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

import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-7-12<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VACategoryDao extends VADao {
	/**
	 * single instance design pattern.
	 */
	private static VACategoryDao dao = new VACategoryDao();

	/**
	 * get a category data access object instance.
	 * 
	 * @return category data access object
	 */
	public static VACategoryDao getInstance() {
		if (dao == null) {
			dao = new VACategoryDao();
		}
		return dao;
	}
	
	private VACategoryDao() {
		super();
	}

	/**
	 * add the category to the database.
	 * 
	 * @param cate
	 *            the category object
	 * @return if add the category to the database successfully return the
	 *         category object in the database, else return null
	 * @throws Exception
	 */

	public synchronized VACategory add(VACategory cate) throws Exception {
		if (cate == null) {
			return null;
		}
		// locate the category object by GUID
		VAObject vao = super.viewData(cate.getGuid());
		// if the GUID has been used
		if (vao != null
				&& vao.getType().equals(
						VAMConstants.VIRTUAL_APPLIANCE_CATEGORY)) {
			throw new Exception("The category's guid : " + cate.getGuid()
					+ " has been used. Please change one!");
		}

		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAC_CATEGORY + "']" };
		String[] operator = new String[] { "=", "=" };
		Object[] values = new Object[] {
				VAMConstants.VIRTUAL_APPLIANCE_CATEGORY, cate.getCategory() };

		// search all category object
		List<VAObject> resls = super.searchData(queries, operator, values);
		// if find category list
		if (resls != null && resls.size() > 0) {
			throw new Exception("The category's name : " + cate.getCategory()
					+ " has been used. Please change one!");
		}

		// register the category
		vao = super.addData(cate);
		if (vao == null) {
			return null;
		}

		return new VACategory(vao);
	}

	/**
	 * update the category in the database.
	 * 
	 * @param cate
	 *            the category object
	 * @return if update the category successfully return the category object in
	 *         the database, else return null
	 * @throws Exception
	 */
	public synchronized VACategory update(VACategory cate) throws Exception {
		if (cate == null) {
			return null;
		}

		// locate the category object by GUID
		VAObject vao = super.viewData(cate.getGuid());
		// can't find the category
		if (vao == null
				|| !vao.getType().equals(
						VAMConstants.VIRTUAL_APPLIANCE_CATEGORY)) {
			return null;
		}
		// update the category
		vao = super.updateData(cate);

		// if update the category unsuccessfully, return null
		if (vao == null) {
			return null;
		}

		return new VACategory(vao);
	}

	/**
	 * remove the category in the database by GUID.
	 * 
	 * @param guid
	 *            the category's GUID
	 * @return return true if remove the category successfully,else return false
	 * @throws Exception
	 */

	public synchronized boolean remove(String guid) throws Exception {
		if (guid == null) {
			return false;
		}

		// locate the category object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the category
		if (vao == null
				|| !vao.getType().equals(
						VAMConstants.VIRTUAL_APPLIANCE_CATEGORY)) {
			return false;
		}
		// remove the category with GUID in the database
		vao = super.removeData(guid);
		if (vao == null) {
			return false;
		}

		return true;
	}

	/**
	 * query the category in the database by GUID.
	 * 
	 * @param guid
	 *            the category's GUID
	 * @return return the queried category
	 * @throws Exception
	 */
	public synchronized VACategory query(String guid) throws Exception {
		if (guid == null) {
			return null;
		}
		
		// locate the category object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the category
		if (vao == null
				|| !vao.getType().equals(
						VAMConstants.VIRTUAL_APPLIANCE_CATEGORY)) {
			return null;
		}

		return new VACategory(vao);
	}

	/**
	 * get all the category in the database.
	 * 
	 * @return the category list
	 * @throws Exception
	 */

	public synchronized List<VAObject> getAll() throws Exception {
		String[] queries = new String[] { "type" };
		String[] operator = new String[] { "=" };
		Object[] values = new Object[] { 
				VAMConstants.VIRTUAL_APPLIANCE_CATEGORY };

		// search all category object
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}
}
