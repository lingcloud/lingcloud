/*
 *  @(#)VACategoryService.java  2010-7-12
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

package org.lingcloud.molva.xmm.vam.services;

import java.util.ArrayList;
import java.util.List;

import org.lingcloud.molva.xmm.vam.daos.DaoFactory;
import org.lingcloud.molva.xmm.vam.daos.VACategoryDao;
import org.lingcloud.molva.xmm.vam.pojos.VACategory;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 *
 * @version 1.0.1 2010-7-12<br>
 * @author Ruijian Wang<br>
 *
 */
public class VACategoryService {
	/**
	 * single instance design pattern.
	 */
	private static VACategoryService instance = null;
	
	/**
	 * get the category service instance.
	 * @return VACategoryService instance
	 */
	public static VACategoryService getInstance() {
		if (instance == null) {
			instance = new VACategoryService();
		}
		return instance;
	}
	
	private VACategoryService() {
		
	}
	
	/**
	 * add a category.
	 * @param name
	 * 		category name
	 * @return category object
	 * @throws Exception
	 */
	public VACategory addCategory(String name) throws Exception {
		
		// create new category object
		VACategory cate = new VACategory(VAMUtil.genGuid(), name);
		
		// get the category access object
		VACategoryDao cateDao = DaoFactory.getVACategoryDao();
		
		// add the category to the database
		cate = cateDao.add(cate);
		if (cate == null) {
			throw new Exception("Can't add the category to the database.");
		}
		
		return cate;
	}
	
	/**
	 * update a category.
	 * @param guid
	 * 		the category GUID
	 * @param name
	 * 		the category name
	 * @return category object
	 * @throws Exception
	 */
	public VACategory updateCategory(String guid, String name) 
		throws Exception {
		// get the category access object
		VACategoryDao cateDao = DaoFactory.getVACategoryDao();
		
		// get the VACategory objcet with the guid
		VACategory cate = cateDao.query(guid);
		if (cate == null) {
			throw new Exception("The category with guid : " + guid
					+ " is not existed");
		}
		
		// change the category object
		cate.setCategory(name);
		
		return cateDao.update(cate);
	}
	
	/**
	 * query the category.
	 * @param guid
	 * 		category GUID
	 * @return category object
	 * @throws Exception
	 */
	public VACategory queryCategory(String guid) throws Exception {
		// get the category access object
		VACategoryDao cateDao = DaoFactory.getVACategoryDao();
		
		VACategory cate = cateDao.query(guid);
		
		return cate;
	}
	
	/**
	 * get all categories.
	 * @return category list
	 * @throws Exception
	 */
	
	public List<VACategory> getAllCategory() throws Exception {
		// get the category access object
		VACategoryDao cateDao = DaoFactory.getVACategoryDao();
		
		List<VACategory> resl = new ArrayList<VACategory>();
		List<VAObject> catel = cateDao.getAll();
		if (catel != null) {
			int size = catel.size();
			for (int i = 0; i < size; i++) {
				resl.add(new VACategory(catel.get(i)));
			}
		}
		
		return resl;
	}
	
	/**
	 * remove category.
	 * @param guid
	 * 		category GUID
	 * @return whether remove category successfully
	 * @throws Exception
	 */
	public boolean removeCategory(String guid) throws Exception {
		// get the category access object
		VACategoryDao cateDao = DaoFactory.getVACategoryDao();
		
		return cateDao.remove(guid);
	}
}
