/* 
 * @(#)Json.java 2009-10-6 
 *  
 * Copyright (C) 2008-2011, 
 * LingCloud Team, 
 * Institute of Computing Technology, 
 * Chinese Academy of Sciences. 
 * P.O.Box 2704, 100190, Beijing, China. 
 * 
 * http://lingcloud.org 
 *  
 */
package org.lingcloud.molva.portal.util;

import java.util.ArrayList;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Jan 3, 2008<br>
 * @author Xiaoyi Lu<br>
 * 
 */
public class Json {

	private String singleInfo = "";

	public String getSingleInfo() {
		return singleInfo;
	}

	private boolean success = true;
	private String error = "";
	private ArrayList<ArrayList<String>> arrData = 
		new ArrayList<ArrayList<String>>();
	private ArrayList<String> dataItem = new ArrayList<String>();

	public String getError() {
		return error;
	}

	public void setError(String error) {
		if (!error.equals("")) {
			this.success = false;
		}
		this.error = error;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		if (success) {
			this.error = "";
		}
		this.success = success;
	}

	public Json() {

	}

	public void reSet() {
		arrData.clear();
		dataItem.clear();
	}

	public void addItem(String name, String value) {
		dataItem.add(name);
		dataItem.add(value);
	}

	// Add item to list.
	public void addItemOk() {
		arrData.add(dataItem);
		dataItem = new ArrayList<String>();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("datas:");
		sb.append("[");
		int ad = arrData.size();
		for (int i = 0; i < ad; i++) {
			ArrayList<String> arr = (ArrayList<String>) (arrData.get(i));
			sb.append("{");
			int t = arr.size();
			for (int j = 0; j < t; j += 2) {
				if (j == t) {
					break;
				}
				sb.append("\'");
				sb.append(arr.get(j).toString());
				sb.append("\'");
				sb.append(":");
				sb.append("\'");
				sb.append(arr.get(j + 1).toString());
				sb.append("\'");
				if (j < t - 2) {
					sb.append(",");
				}
			}
			sb.append("}");
			if (i < ad - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}
}
