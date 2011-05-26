/*
 *  @(#)PNStaticMetaInfoParser.java  2010-5-27
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

package org.lingcloud.molva.xmm.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lingcloud.molva.xmm.pojos.Nic;

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-5-31<br>
 * @author Xiaoyi Lu<br>
 * @email luxiaoyi@software.ict.ac.cn
 */
public class PNStaticMetaInfoParser {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PNStaticMetaInfoParser.class);

	private String metaInfo;
	
	private float CPUSPEED;
	
	private int CPUNUM;

	private String ARCH;
	
	private int MEMTOTAL;

	private String MODELNAME;

	private String HOSTNAME;

	private List Nics;

	public PNStaticMetaInfoParser(String stdout) {
		this.metaInfo = stdout.trim();
		this.parse();
	}

	private void parse() {
		if (metaInfo == null || "".equals(metaInfo)) {
			this.CPUSPEED = 0;
			this.CPUNUM = 0;
			this.ARCH = "";
			this.MEMTOTAL = 0;
			this.MODELNAME = "";
			this.HOSTNAME = "";
			this.Nics = null;
			return;
		}
		String[] ss = this.metaInfo.split(";");
		System.out.println(metaInfo);
		for (int i = 0; i < ss.length; i++) {
			String[] m = ss[i].split("=");
			if (m.length < 2) {
				continue;
			}
			try {
				switch (i) {
				case 0:
					this.ARCH = m[1];
					break;
				case 1:
					this.MODELNAME = m[1];
					break;
				case 2:
					this.HOSTNAME = m[1];
					break;
				case 3:
					this.Nics = parseNics(m[1]);
					break;
				case 4:
					this.CPUSPEED = Float.parseFloat(m[1]);
					break;
				case 5:
					this.CPUNUM = Integer.parseInt(m[1]);
					break;
				case 6:
					this.MEMTOTAL = Integer.parseInt(m[1])/1000;
					break;
				default:
					break;
				}
			}catch(Exception e) {
				log.error(e);
			}
		}
		
	}

	private List parseNics(String nicstr) {
		if (nicstr != null && !nicstr.equals("")) {
			String[] nics = nicstr.trim().split(" ");
			ArrayList<Nic> niclist = new ArrayList<Nic>();
			for (int j = 0; j < nics.length; j++) {
				String[] nic = nics[j].split("-");
				if (nic.length != 3) {
					log
							.warn("There are errors when collect hostinfo of nics : "
									+ nics[j] + ".");
					// Ignore this error.
					continue;
				} else {
					String name = nic[0].trim();
					String mac = nic[1].trim();
					String ip = nic[2].trim();
					Nic lease = new Nic();
					lease.setIp(ip);
					lease.setMac(mac);
					lease.setNicName(name);
					niclist.add(lease);
				}
			}
			return niclist;
		}
		return null;
	}

	public String getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getARCH() {
		return ARCH;
	}

	public void setARCH(String arch) {
		ARCH = arch;
	}

	public String getHOSTNAME() {
		return HOSTNAME;
	}

	public void setHOSTNAME(String hostname) {
		HOSTNAME = hostname;
	}

	public String getMODELNAME() {
		return MODELNAME;
	}

	public void setMODELNAME(String modelname) {
		MODELNAME = modelname;
	}

	public List getNics() {
		return Nics;
	}

	public void setNics(List cs) {
		Nics = cs;
	}
	
	public void setCpuNum(int num) {
		this.CPUNUM = num;
	}
	
	public int getCpuNum() {
		return this.CPUNUM;
	}
	
	public void setCpuSpeed(float speed) {
		this.CPUSPEED = speed;
	}
	
	public float getCpuSpeed() {
		return this.CPUSPEED;
	}
	
	public void setMem(int mem) {
		this.MEMTOTAL = mem;
	}
	
	public int getMem() {
		return this.MEMTOTAL;
	}

}
