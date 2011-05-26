/*
 *  @(#)VADao.java  Apr 12, 2011
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

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.ocl.asset.Asset;
import org.lingcloud.molva.ocl.asset.AssetManagerImpl;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Apr 12, 2011<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VADao {
	private AssetManagerImpl ami = null;

	public VADao() {
		// create a asset manager
		ami = new AssetManagerImpl();
	}

	/**
	 * get a object from database by GUID.
	 * @param guid
	 * 		the GUID of object
	 * @return the basic object of virtual appliance
	 * @throws Exception
	 */
	public VAObject viewData(String guid) throws Exception {
		if (guid == null) {
			return null;
		}
		Asset asset = ami.view(guid);
		if (asset == null) {
			return null;
		}
		return new VAObject(asset);
	}

	/**
	 * add a object to database.
	 * @param vao
	 * 		the basic object of virtual appliance
	 * @return the basic object of virtual appliance
	 * @throws Exception
	 */
	public VAObject addData(VAObject vao) throws Exception {
		if (vao == null) {
			return null;
		}
		Asset asset = ami.add(vao, true);
		if (asset == null) {
			return null;
		}
		return new VAObject(asset);
	}

	/**
	 * update the object in database.
	 * @param vao
	 * 		the basic object of virtual appliance
	 * @return the basic object of virtual appliance
	 * @throws Exception
	 */
	public VAObject updateData(VAObject vao) throws Exception {
		if (vao == null) {
			return null;
		}
		Asset asset = ami.update(vao.getGuid(), vao);
		if (asset == null) {
			return null;
		}
		return new VAObject(asset);
	}

	/**
	 * remove the object in database by GUID.
	 * @param guid
	 * 		the GUID of object
	 * @return	the basic object of virtual appliance
	 * @throws Exception
	 */
	public VAObject removeData(String guid) throws Exception {
		if (guid == null) {
			return null;
		}
		Asset asset = ami.remove(guid, true);
		if (asset == null) {
			return null;
		}
		return new VAObject(asset);
	}

	/**
	 * search objects matching the given condition in database.
	 * @param searchFields
	 * 		the search fields
	 * @param operators
	 * 		the operators of every field
	 * @param values
	 * 		the search values of every field
	 * @return the basic object list of virtual appliance
	 * @throws Exception
	 */
	public List<VAObject> searchData(String[] searchFields, String[] operators,
			Object[] values) throws Exception {
		List<Asset> retList = ami.search(searchFields, operators, values);
		List<VAObject> resList = new ArrayList<VAObject>();
		for (int i = 0; i < retList.size(); i++) {
			resList.add(new VAObject(retList.get(i)));
		}
		return resList;
	}
}
