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
		vao = super.addData(file);
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
		VAObject file = super.removeData(guid);
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
	 * query the file who's reference not exceed maxRef in the database by
	 * parentGuid.
	 * 
	 * @param parentGuid
	 *            the file's parentGuid
	 * @param maxRef
	 *            the max count of file's reference
	 * @return the queried instance
	 * @throws Exception
	 */

	public synchronized VAFile query(String parentGuid, int maxRef, int type)
			throws Exception {
		if (parentGuid == null) {
			return null;
		}

		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_PARENT + "']",
				"attributes['" + VAMConstants.VAF_STATE + "']",
				"attributes['" + VAMConstants.VAF_STATE + "']",
				"attributes['" + VAMConstants.VAF_STATE + "']",
				"attributes['" + VAMConstants.VAF_REF + "']" };
		String[] operator = new String[] { "=", "=", "<>", "<>", "<>", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				parentGuid, "" + VAMConstants.STATE_DELETING,
				"" + VAMConstants.STATE_DELETED, "" + VAMConstants.STATE_ERROR,
				"" + maxRef };

		final int refference = 5;
		if (type != VAMConstants.TYPE_EQUAL) {
			operator[refference] = "<>";
			maxRef -= 1;
		}

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);
		VADisk res = null;
		int max = -1;
		// find the file matching the condition
		if (resls != null && resls.size() > 0) {
			for (int i = 0; i < resls.size(); i++) {
				VADisk disk = new VADisk((VAObject) resls.get(i));
				int ref = disk.getRef();
				int state = disk.getState();

				// if the time using to copy is more than half hour, 
				// the file may be error
				if (disk.getState() == VAMConstants.STATE_COPING
						&& VAMUtil.getTimestamp() - disk.getTimestamp() 
						> VAMConstants.HOUR / 2) {
					continue;
				}

				if (ref <= maxRef
						&& (ref > max || ref == max
								&& (state == VAMConstants.STATE_READY || res
										.getState() == VAMConstants.STATE_COPING
										&& state == VAMConstants.STATE_COPING
										&& disk.getAddTime().compareTo(
												res.getAddTime()) < 0))) {
					max = ref;
					res = disk;
				}

			}
		}

		return res;
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
	 * get the count of file whose reference equal 0.
	 * 
	 * @param parentGuid
	 *            the file's parentGuid
	 * @return the count of file
	 * @throws Exception
	 */
	public synchronized int getUnusedCount(String parentGuid) throws Exception {
		if (parentGuid == null) {
			return 0;
		}
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_PARENT + "']",
				"attributes['" + VAMConstants.VAF_REF + "']" };
		String[] operator = new String[] { "=", "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				parentGuid, "0" };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);
		int ans = 0;

		// get the count
		if (resls != null) {
			for (int i = 0; i < resls.size(); i++) {
				VADisk disk = new VADisk((VAObject) resls.get(i));
				if (disk.getState() != VAMConstants.STATE_ERROR
						&& disk.getState() != VAMConstants.STATE_DELETING
						&& disk.getState() != VAMConstants.STATE_DELETED) {
					if (disk.getState() != VAMConstants.STATE_READY
							&& VAMUtil.getTimestamp() - disk.getTimestamp()
							> VAMConstants.HOUR / 2) {
						continue;
					}
					ans++;
				}
			}
		}

		return ans;
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

		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_PARENT + "']",
				"attributes['" + VAMConstants.VAF_REF + "']" };
		String[] operator = new String[] { "=", "=", "<>" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				parentGuid, "0" };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		int ans = 0;

		// get the count
		if (resls != null) {
			ans = resls.size();
		}

		return ans;
	}

	/**
	 * update state of one file's all child.
	 * 
	 * @param parentGuid
	 *            the parent's GUID
	 * @param state
	 *            the file's state
	 * @return return true if update the state successfully,else return false
	 * @throws Exception
	 */
	public synchronized boolean updateState(String parentGuid, int state)
			throws Exception {
		if (parentGuid == null) {
			return false;
		}
		// set query condition
		String[] queries = new String[] { "type",
				"attributes['" + VAMConstants.VAF_PARENT + "']" };
		String[] operator = new String[] { "=", "=" };
		Object[] values = new Object[] { VAMConstants.VIRTUAL_APPLIANCE_FILE,
				parentGuid };

		// search the file in the database
		List<VAObject> resls = super.searchData(queries, operator, values);

		// update the file state by instanceGuid
		for (int i = 0; i < resls.size(); i++) {
			VADisk disk = new VADisk((VAObject) resls.get(i));
			if (disk.getState() != VAMConstants.STATE_ERROR
					&& disk.getState() != state) {
				disk.setState(state);
				super.updateData(disk);
			}
		}

		return true;
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
