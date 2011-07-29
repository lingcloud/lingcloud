/*
 *  @(#)VAFileDaoNsuperng.java  2010-5-25
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

import org.lingcloud.molva.xmm.vam.pojos.VADisk;
import org.lingcloud.molva.xmm.vam.pojos.VAFile;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-25<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAFileDao extends VADao {
	/**
	 * single instance design pattern.
	 */
	private static VAFileDao dao = new VAFileDao();

	/**
	 * get a file data access object instance.
	 * 
	 * @return file data access object
	 */
	public static VAFileDao getInstance() {
		if (dao == null) {
			dao = new VAFileDao();
		}
		return dao;
	}


	private VAFileDao() {
		super();
	}

	/**
	 * add the file to the database.
	 * 
	 * @param file
	 *            the file object
	 * @return if add the file to the database successfully return the file
	 *         object in the database, else return null
	 * @throws Exception
	 */
	public synchronized VAFile add(VAFile file) throws Exception {
		if (file == null) {
			return null;
		}

		// locate the file object by GUID
		VAObject vao = super.viewData(file.getGuid());
		// if the GUID has been used
		if (vao != null
				&& vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE_FILE)) {
			throw new Exception("The file's guid \"" + file.getGuid()
					+ "\" has been used. Please change one!");
		}

		// register the file
		vao = super.addData(file, true);
		if (vao == null) {
			return null;
		}

		if (!file.getParent().equals(VAMConstants.NULL)) {
			changeRef(file.getParent(), VAMConstants.INCREASE);
		}

		return new VAFile(vao);
	}

	/**
	 * update the file in the database.
	 * 
	 * @param file
	 *            the file object
	 * @return if update the file successfully return the file object in the
	 *         database, else return null
	 * @throws Exception
	 */
	public synchronized VAFile update(VAFile file) throws Exception {
		if (file == null) {
			return null;
		}

		// locate the file object by GUID
		VAObject vao = super.viewData(file.getGuid());
		// can't find the file
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE_FILE)) {
			return null;
		}

		VAFile oldFile = new VAFile(vao);

		// update the file
		file.setRef(oldFile.getRef());
		vao = super.updateData(file);

		// if update the file unsuccessfully, return null
		if (vao == null) {
			return null;
		}

		if (!oldFile.getParent().equals(file.getParent())) {
			if (!oldFile.getParent().equals(VAMConstants.NULL)) {
				changeRef(oldFile.getParent(), VAMConstants.DECREASE);
			}
			if (!file.getParent().equals(VAMConstants.NULL)) {
				changeRef(file.getParent(), VAMConstants.INCREASE);
			}
		}

		return new VAFile(vao);
	}

	/**
	 * remove the file in the database by GUID.
	 * 
	 * @param guid
	 *            the file's GUID
	 * @return return true if remove the file successfully,else return false
	 * @throws Exception
	 */

	public synchronized boolean remove(String guid) throws Exception {
		if (guid == null) {
			return false;
		}

		// locate the file object by GUID
		VAObject vao = super.viewData(guid);
		// can't find the file
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE_FILE)) {
			return false;
		}
		// remove the file with GUID in the database
		VAObject file = super.removeData(guid, true);
		if (file == null) {
			return false;
		}

		VAFile vafile = new VAFile(vao);
		if (!vafile.getParent().equals(VAMConstants.NULL)) {
			changeRef(vafile.getParent(), VAMConstants.DECREASE);
		}

		return true;
	}

	/**
	 * query the file in the database by GUID.
	 * 
	 * @param guid
	 *            the file's GUID
	 * @return return the queried file
	 * @throws Exception
	 */
	public synchronized VAFile query(String guid) throws Exception {
		if (guid == null) {
			return null;
		}

		// locate the file object by GUID
		VAObject vao = super.viewData(guid);
		
		// can't find the file
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE_FILE)) {
			return null;
		}

		return new VAFile(vao);
	}

	/**
	 * get all the file in the database.
	 * 
	 * @return the file list
	 * @throws Exception
	 */

	public synchronized List<VAObject> getAll() throws Exception {

		// set query condition
		String[] queries = new String[] { "type" };
		String[] operator = new String[] { "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * get the files by type.
	 * 
	 * @param type
	 *            the type of file, it can be "disc", "disk", "config", "other"
	 * @return the file list
	 * @throws Exception
	 */
	public synchronized List<VAObject> getFilesByType(String type)
			throws Exception {

		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_FILE_TYPE + "']" };
		String[] operator = new String[] { "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				type };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}
	
	/**
	 * get replicas.
	 * @param guid
	 * 		the file's GUID
	 * @return replicas
	 * @throws Exception
	 */
	public synchronized List<VAObject> getReplicas(String guid) 
		throws Exception {
		
		if (guid == null) {
			return null;
		}

		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_PARENT + "']",
				"attributes['" + VAMConstants.VAF_REPLICA + "']" };
		String[] operator = new String[] { "=", "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				guid, "true" };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * change the reference of the file.
	 * 
	 * @param guid
	 *            the file's GUID
	 * @param type
	 *            the type of increase or decrease
	 * @return return true if change the file's reference successfully, else
	 *         return false
	 * @throws Exception
	 */
	public synchronized boolean changeRef(String guid, int type)
			throws Exception {
		if (guid == null) {
			return false;
		}

		// locate the file object by GUID
		VAObject vao = super.viewData(guid);

		// can't find the file
		if (vao == null
				|| !vao.getType().equals(VAMConstants.VIRTUAL_APPLIANCE_FILE)) {
			return false;
		}
		// change the file's reference
		VAFile vafile = new VAFile(vao);
		int ref = vafile.getRef();
		if (type == VAMConstants.INCREASE) {
			ref++;
		} else {
			ref--;
		}
		vafile.setRef(ref);
		// update the file in the database
		super.updateData(vafile);

		return true;
	}

	/**
	 * get the count of file whose reference more than 0.
	 * 
	 * @param parentGuid
	 *            the file's parentGuid
	 * @return the count of file
	 * @throws Exception
	 */
	public synchronized int getActiveReference(String parentGuid)
			throws Exception {
		if (parentGuid == null) {
			return 0;
		}

		// search the file in the database
		List<VAObject> resls = getReplicas(parentGuid);
		VAFile parentFile = query(parentGuid);
		
		int ans = parentFile.getRef();
		
		// get the count
		if (resls != null) {
			ans -= resls.size();
			for (int i = 0; i < resls.size(); i++) {
				VAFile vafile = new VAFile(resls.get(i));
				ans += vafile.getRef();
			}
		}

		return ans;
	}
	
	/**
	 * get error files.
	 * 
	 * @return file list
	 * @throws Exception
	 */
	public synchronized List<VAObject> getErrorFiles() throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_STATE + "']" };
		String[] operator = new String[] { "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				VAMConstants.STATE_ERROR };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}

	/**
	 * get not ready files.
	 * 
	 * @return file list
	 * @throws Exception
	 */
	public synchronized List<VAObject> getNotReadyFiles() throws Exception {
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_STATE + "']",
				"attributes['" + VAMConstants.VAF_STATE + "']" };
		String[] operator = new String[] { "=", "<>", "<>" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				VAMConstants.STATE_ERROR, VAMConstants.STATE_READY };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		return resls;
	}
}
