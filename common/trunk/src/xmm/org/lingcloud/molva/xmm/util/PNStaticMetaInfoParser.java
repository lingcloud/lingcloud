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
 */
public class PNStaticMetaInfoParser {
	/**
	 * the log object.
	 */
	private static Log log = LogFactory.getLog(PNStaticMetaInfoParser.class);

	private String metaInfo;

	private float cpuSpeed;

	private int cpuNum;

	private String arch;

	private int memTotal;

	private String modelName;

	private String hostname;

	private List<Nic> nics;

	public PNStaticMetaInfoParser(String stdout) {
		this.metaInfo = stdout.trim();
		this.parse();
	}

	private void parse() {
		if (metaInfo == null || "".equals(metaInfo)) {
			this.cpuSpeed = 0;
			this.cpuNum = 0;
			this.arch = "";
			this.memTotal = 0;
			this.modelName = "";
			this.hostname = "";
			this.nics = null;
			return;
		}
		String[] ss = this.metaInfo.split(";");
		System.out.println(metaInfo);
		final int paraArch = 0;
		final int paraModelname = 1;
		final int paraHostname = 2;
		final int paraNic = 3;
		final int paraCpuSpeed = 4;
		final int paraCpuNum = 5;
		final int paraMemTotal = 6;
		final int kilo = 1000;
		
		for (int i = 0; i < ss.length; i++) {
			String[] m = ss[i].split("=");
			if (m.length < 2) {
				continue;
			}
			try {
				switch (i) {
				case paraArch:
					this.arch = m[1];
					break;
				case paraModelname:
					this.modelName = m[1];
					break;
				case paraHostname:
					this.hostname = m[1];
					break;
				case paraNic:
					this.nics = parseNics(m[1]);
					break;
				case paraCpuSpeed:
					this.cpuSpeed = Float.parseFloat(m[1]);
					break;
				case paraCpuNum:
					this.cpuNum = Integer.parseInt(m[1]);
					break;
				case paraMemTotal:
					this.memTotal = Integer.parseInt(m[1]) / kilo;
					break;
				default:
					break;
				}
			} catch (Exception e) {
				log.error(e);
			}
		}

	}

	private List<Nic> parseNics(String nicstr) {
		if (nicstr != null && !nicstr.equals("")) {
			String[] nics = nicstr.trim().split(" ");
			ArrayList<Nic> niclist = new ArrayList<Nic>();
			for (int j = 0; j < nics.length; j++) {
				String[] nic = nics[j].split("-");
				final int nicParameters = 3;
				if (nic.length != nicParameters) {
					log.warn("There are errors when collect hostinfo of nics : "
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
		return arch;
	}

	public void setARCH(String arch) {
		this.arch = arch;
	}

	public String getHOSTNAME() {
		return hostname;
	}

	public void setHOSTNAME(String hostname) {
		this.hostname = hostname;
	}

	public String getMODELNAME() {
		return modelName;
	}

	public void setMODELNAME(String modelname) {
		modelName = modelname;
	}

	public List<Nic> getNics() {
		return nics;
	}

	public void setNics(List<Nic> cs) {
		nics = cs;
	}

	public void setCpuNum(int num) {
		this.cpuNum = num;
	}

	public int getCpuNum() {
		return this.cpuNum;
	}

	public void setCpuSpeed(float speed) {
		this.cpuSpeed = speed;
	}

	public float getCpuSpeed() {
		return this.cpuSpeed;
	}

	public void setMem(int mem) {
		this.memTotal = mem;
	}

	public int getMem() {
		return this.memTotal;
	}

}
