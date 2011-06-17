/*
 *  @(#)VAConfig.java  Jul 23, 2010
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.lingcloud.molva.ocl.util.ConfigUtil;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;


/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 Jul 23, 2010<br>
 * @author Ruijian Wang<br>
 * 
 */
public class VAConfig {
	public static final String CONF_FILE = "xen.template.cfg";
	private static String cfgTemplate;
	
	static {
		
		try {
			String confPath = ConfigUtil.getConfigFile(CONF_FILE).toString();
			if (confPath == null) {
				throw new Exception("Can't find the config file '" + CONF_FILE
						+ "'.");
			}
			FileInputStream fin = new FileInputStream(confPath);
			InputStreamReader reader = new InputStreamReader(fin);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			cfgTemplate = "";
			while ((line = br.readLine()) != null) {
				cfgTemplate += line + "\r\n";
			}
			
		} catch (Exception e) {
			cfgTemplate = "name = '<name>'\r\n" // name
				+ "import os, re\r\n"
				+ "arch = os.uname()[4]\r\n"
				+ "if re.search('64', arch):\r\n"
				+ "    arch_libdir = 'lib64'\r\n"
				+ "else:\r\n"
				+ "    arch_libdir = 'lib'\r\n"
				+ "device_model = '/usr/' + arch_libdir + "
				+ "'/xen/bin/qemu-dm'\r\n"
				+ "kernel = '/usr/lib/xen/boot/hvmloader'\r\n" // bootloader
				+ "memory = '<memerysize>'\r\n" // memery size
				+ "vcpus=<cpuamount>\r\n" // cpu amount
				+ "disk = [<disk>]\r\n" // disk
				+ "<network>\r\n"		// network
				+ "builder = 'hvm'\r\n"
				+ "boot = '<bootorder>'\r\n" // boot order
				+ "pae = 1\r\n" + "acpi = 1\r\n"
				+ "apic = 1\r\n"
				+ "localtime = 0\r\n" + "sdl = 0\r\n"
				+ "usb=1\r\n"
				+ "usbdevice = 'tablet'\r\n"
				+ "vnclisten='0.0.0.0'\r\n"
				+ "vnc=1\r\n"
				+ "vncdisplay=<vncport>\r\n" // vnc port
				+ "vncunused=1\r\n";
		}
		
	}
	
	private static String format(String output) {
		if (output == null) {
			return "";
		}
		return output;
	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 2010-7-24<br>
	 * @author Ruijian Wang<br>
	 * 
	 */
	public class DiskInfo {
		private String name;
		private String location;
		private String format;
		private long capacity;

		/**
		 * get disk name.
		 * @return disk name
		 */
		public String getName() {
			return format(name);
		}

		/**
		 * set disk name.
		 * @param name
		 * 		disk name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * get disk location.
		 * @return disk location
		 */
		public String getLocation() {
			return format(location);
		}

		/**
		 * set disk location.
		 * @param location
		 * 		disk location
		 */
		public void setLocation(String location) {
			this.location = location;
		}

		/**
		 * get disk format.
		 * @return	disk format
		 */
		public String getFormat() {
			return format(format);
		}

		/**
		 * set disk format.
		 * @param format
		 * 		disk format
		 */
		public void setFormat(String format) {
			this.format = format;
		}

		/**
		 * get disk capacity.
		 * @return	disk capacity
		 */
		public long getCapacity() {
			return capacity;
		}

		/**
		 * set disk capacity.
		 * @param capacity
		 * 		disk capacity
		 */
		public void setCapacity(long capacity) {
			this.capacity = capacity;
		}
	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 2010-7-24<br>
	 * @author Ruijian Wang<br>
	 * 
	 */
	public class CpuInfo {
		private int number;

		/**
		 * get the amount of cpu.
		 * @return	the amount of cpu
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * set the amount of cpu.
		 * @param number
		 * 		the amount of cpu
		 */
		public void setNumber(int number) {
			this.number = number;
		}
	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 2010-7-24<br>
	 * @author Ruijian Wang<br>
	 * 
	 */
	public class MemoryInfo {
		private long size;

		/**
		 * get the memory size.
		 * @return	memory size
		 */
		public long getSize() {
			return size;
		}

		/**
		 * set the memory size.
		 * @param size
		 * 		memory size
		 */
		public void setSize(long size) {
			this.size = size;
		}
	}

	/**
	 * 
	 * <strong>Purpose:</strong><br>
	 * TODO.
	 * 
	 * @version 1.0.1 2010-7-24<br>
	 * @author Ruijian Wang<br>
	 * 
	 */
	public class NetworkInfo {
		private String macAddr;
		private String bridge;

		/**
		 * get the name of network interface card bridge.
		 * @return the name of network interface card bridge
		 */
		public String getBridge() {
			return bridge;
		}

		/**
		 * set the name of network interface card bridge.
		 * @param bridge
		 * 		the name of network interface card bridge
		 */
		public void setBridge(String bridge) {
			this.bridge = bridge;
		}

		/**
		 * get the MAC address.
		 * @return	MAC address
		 */
		public String getMacAddr() {
			return format(macAddr);
		}

		/**
		 * set the MAC address.
		 * @param macAddr
		 * 		MAC address
		 */
		public void setMacAddr(String macAddr) {
			this.macAddr = macAddr;
		}

	}

	private List<DiskInfo> diskList = null;
	private List<CpuInfo> cpuList = null;
	private List<MemoryInfo> memeryList = null;
	private List<NetworkInfo> networkList = null;
	private List<String> discList = null;
	private List<String> osList = null;
	private List<String> appList = null;
	private List<String> langList = null;
	private int vncPort = 0;
	private String bootLoader = null;
	private String accessway = null;
	private String description = null;
	private String path = null;
	private String name = null;
	private int makeApplianceVM = 0;
	private String runningState = null;
	private int boot = 0;

	private void init() {
		diskList = new ArrayList<DiskInfo>();
		cpuList = new ArrayList<CpuInfo>();
		memeryList = new ArrayList<MemoryInfo>();
		networkList = new ArrayList<NetworkInfo>();
		discList = new ArrayList<String>();
		osList = new ArrayList<String>();
		appList = new ArrayList<String>();
		langList = new ArrayList<String>();
		vncPort = 0;
		bootLoader = null;
		accessway = null;
		description = null;
		name = null;
		makeApplianceVM = VAMConstants.NO_MAKE_APPLIANCE_VM;
		runningState = VAMConstants.MAKE_APPLIANCE_VM_STATE_STOP;
		boot = VAMConstants.BOOT_HARDDISK | VAMConstants.BOOT_CDROM;
	}

	public VAConfig() {
		init();
	}

	/**
	 * get running state of the virtual machine.
	 * @return running state
	 */
	public String getRunningState() {
		return runningState;
	}

	/**
	 * set running state of the virtual machine.
	 * @param runningState
	 * 		running state of the virtual machine.
	 */
	public void setRunningState(String runningState) {
		this.runningState = runningState;
	}

	/**
	 * get disk count of the virtual machine.
	 * @return	disk count
	 */
	public int getDiskCount() {
		return diskList.size();
	}

	/**
	 * set disk of the virtual machine.
	 * @param index
	 * 		the disk index
	 * @return	the specific disk info
	 */
	public DiskInfo getDisk(int index) {
		return diskList.get(index);
	}

	/**
	 * add disk.
	 * @param name
	 * 		disk name
	 * @param location
	 * 		disk location
	 * @param format
	 * 		disk format
	 * @param capacity
	 * 		disk capacity
	 */
	public void addDisk(String name, String location, String format,
			long capacity) {
		DiskInfo disk = new DiskInfo();
		disk.setCapacity(capacity);
		disk.setFormat(format);
		disk.setLocation(location);
		disk.setName(name);

		diskList.add(disk);
	}

	/**
	 * set disk.
	 * @param name
	 * 		disk name
	 * @param location
	 * 		disk location
	 * @param format
	 * 		disk format
	 * @param capacity
	 * 		disk capacity
	 * @param index
	 * 		disk index
	 */
	public void setDisk(String name, String location, String format,
			long capacity, int index) {
		DiskInfo disk = new DiskInfo();
		disk.setCapacity(capacity);
		disk.setFormat(format);
		disk.setLocation(location);
		disk.setName(name);

		diskList.set(index, disk);
	}
	
	/**
	 * get CD count.
	 * @return CD count
	 */
	public int getDiscCount() {
		return discList.size();
	}

	/**
	 * get the specific CD location.
	 * @param index
	 * 		CD index
	 * @return CD location
	 */
	public String getDisc(int index) {
		return discList.get(index);
	}

	/**
	 * add CD.
	 * @param disc
	 * 		CD location
	 */
	public void addDisc(String disc) {
		discList.add(disc);
	}

	/**
	 * set CD location.
	 * @param index
	 * 		CD index
	 * @param disc
	 * 		CD location
	 */
	public void setDisc(int index, String disc) {
		discList.set(index, disc);
	}

	/**
	 * get cpu count.
	 * @return cpu count
	 */
	public int getCpuCount() {
		return cpuList.size();
	}

	/**
	 * get the specific cpu info.
	 * @param index
	 * 		cpu index
	 * @return cpu info
	 */
	public CpuInfo getCpu(int index) {
		return cpuList.get(index);
	}

	/**
	 * add cpu info.
	 * @param number
	 * 		the amount of cpu
	 */
	public void addCpu(int number) {
		CpuInfo cpu = new CpuInfo();
		cpu.setNumber(number);

		cpuList.add(cpu);
	}

	/**
	 * get memory list size.
	 * @return memory list size
	 */
	public int getMemoryCount() {
		return memeryList.size();
	}

	/**
	 * get the specific memory info.
	 * @param index
	 * 		memory index
	 * @return memory info
	 */
	public MemoryInfo getMemory(int index) {
		return memeryList.get(index);
	}

	/**
	 * add memory info.
	 * @param size
	 * 		memory size
	 */
	public void addMemory(long size) {
		MemoryInfo memery = new MemoryInfo();
		memery.setSize(size);

		memeryList.add(memery);
	}

	/**
	 * get network list size.
	 * @return network list size
	 */
	public int getNetworkCount() {
		return networkList.size();
	}

	/**
	 * get the specific network info.
	 * @param index
	 * 		network index
	 * @return network info
	 */
	public NetworkInfo getNetwork(int index) {
		return networkList.get(index);
	}

	/**
	 * add network info.
	 * @param macAddr
	 * 		MAC address
	 * @param bridge
	 * 		network interface card bridge
	 */
	public void addNetwork(String macAddr, String bridge) {
		NetworkInfo network = new NetworkInfo();
		network.setMacAddr(macAddr);
		network.setBridge(bridge);

		networkList.add(network);
	}

	/**
	 * get operating system list size.
	 * @return operating system list size
	 */
	public int getOperatingSystemCount() {
		return osList.size();
	}

	/**
	 * get the specific operating system.
	 * @param index
	 * 		operating system index
	 * @return operating system name
	 */
	public String getOperatingSystem(int index) {
		return format(osList.get(index));
	}

	/**
	 * add operating system.
	 * @param os
	 * 		operating system name
	 */
	public void addOperatingSystem(String os) {
		osList.add(os);
	}

	/**
	 * get application list size.
	 * @return application list size
	 */
	public int getApplicationCount() {
		return appList.size();
	}

	/**
	 * get the specific application.
	 * @param index
	 * 		application index
	 * @return application name
	 */
	public String getApplication(int index) {
		return format(appList.get(index));
	}

	/**
	 * add application.
	 * @param app
	 * 		application name
	 */
	public void addApplication(String app) {
		appList.add(app);
	}

	/**
	 * get language list size.
	 * @return language list size
	 */
	public int getLanguageCount() {
		return langList.size();
	}

	/**
	 * get the specific language.
	 * @param index
	 * 		language index
	 * @return language name
	 */
	public String getLanguage(int index) {
		return format(langList.get(index));
	}

	/**
	 * add language.
	 * @param language
	 * 		language name
	 */
	public void addLanguage(String language) {
		langList.add(language);
	}

	/**
	 * get xen boot loader.
	 * @return
	 */
	public String getBootLoader() {
		return format(bootLoader);
	}

	/**
	 * set xen boot loader.
	 * @param bootLoader
	 * 		xen boot loader
	 */
	public void setBootLoader(String bootLoader) {
		this.bootLoader = bootLoader;
	}

	/**
	 * get the way accessing the virtual machine.
	 * @return
	 */
	public String getAccessway() {
		return format(accessway);
	}

	/**
	 * set the way accessing the virtual machine.
	 * @param accessway
	 * 		the way accessing the virtual machine.
	 */
	public void setAccessway(String accessway) {
		this.accessway = accessway;
	}

	/**
	 * get description of the virtual appliance.
	 * @return
	 */
	public String getDescription() {
		return format(description);
	}

	/**
	 * set description of the virtual appliance.
	 * @param description
	 * 		description of the virtual appliance
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * get the location of the virtual machine image.
	 * @return location of the virtual machine image
	 */
	public String getPath() {
		return path;
	}

	/**
	 * set the location of the virtual machine image.
	 * @param path
	 * 		location of the virtual machine image.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * get the port connecting to VNC.
	 * @return port
	 */
	public int getVncPort() {
		return vncPort;
	}

	/**
	 * set the port connecting to VNC.
	 * @param vncPort
	 * 		the port connecting to VNC
	 */
	public void setVncPort(int vncPort) {
		this.vncPort = vncPort;
	}

	/**
	 * get the virtual appliance name.
	 * @return the virtual appliance name
	 */
	public String getName() {
		return format(name);
	}

	/**
	 * set the virtual appliance name.
	 * @param name
	 * 		the virtual appliance name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get the virtual machine making this appliance.
	 * @return
	 */
	public int getMakeApplianceVM() {
		return makeApplianceVM;
	}

	/**
	 * set the virtual machine making this appliance.
	 * @param makeApplianceVM
	 * 		the virtual machine making this appliance
	 */
	public void setMakeApplianceVM(int makeApplianceVM) {
		this.makeApplianceVM = makeApplianceVM;
	}

	/**
	 * set the boot order.
	 * @param boot
	 * 		boot order
	 */
	public void setBoot(int boot) {
		this.boot = boot;
	}

	/**
	 * parse software section.
	 * @param software
	 * 		software section element.
	 */
	
	@SuppressWarnings("rawtypes")
	private void parseSoftware(Element software) {
		for (Iterator it = software.elementIterator(); it.hasNext();) {
			Element section = (Element) it.next();
			String name = section.getName();
			if (name.equals(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_ACCESSWAY)) {
				setAccessway(section.getText());
			} else if (name.equals(
					VAMConstants.CONFIG_APPLIANCE_SOFTWARE_BOOTLOADER)) {
				setBootLoader(section.getText());
			} else if (name.equals(
					VAMConstants.CONFIG_APPLIANCE_SOFTWARE_APP)) {
				for (Iterator itApp = section.elementIterator(); itApp
						.hasNext();) {
					Element node = (Element) itApp.next();
					addApplication(node.getText());
				}
			} else if (name.equals(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_OS)) {
				for (Iterator itOs = section.elementIterator(); 
				itOs.hasNext();) {
					Element node = (Element) itOs.next();
					addOperatingSystem(node.getText());
				}
			} else if (name
					.equals(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_LANGUAGE)) {
				for (Iterator itOs = section.elementIterator(); 
				itOs.hasNext();) {
					Element node = (Element) itOs.next();
					addLanguage(node.getText());
				}
			} else if (name
					.equals(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_VNCPORT)) {
				setVncPort(Integer.parseInt(section.getText()));
			}
		}
	}

	/**
	 * parse hardware section.
	 * @param hardware
	 * 		hardware section element
	 */
	
	@SuppressWarnings("rawtypes")
	private void parseHardware(Element hardware) {
		for (Iterator it = hardware.elementIterator(); it.hasNext();) {
			Element section = (Element) it.next();
			String name = section.getName();
			if (name.equals(VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK)) {
				String diskname = null;
				String location = null;
				String format = null;
				long capacity = 0;
				for (Iterator itDisk = section.elementIterator(); itDisk
						.hasNext();) {
					Element node = (Element) itDisk.next();
					if (node.getName().equals(
							VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK_NAME)) {
						diskname = node.getText();
					} else if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_DISK_FORMAT)) {
						format = node.getText();
					} else if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_DISK_LOCATION)) {
						location = node.getText();
					} else if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_DISK_CAPACITY)) {
						capacity = Long.parseLong(node.getText());
					}
				}
				addDisk(diskname, location, format, capacity);
			} else if (name.equals(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_CPU)) {
				for (Iterator itCpu = section.elementIterator(); itCpu
						.hasNext();) {
					Element node = (Element) itCpu.next();
					if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_CPU_NUMBER)) {
						addCpu(Integer.parseInt(node.getText()));
					}
				}
			} else if (name
					.equals(VAMConstants.CONFIG_APPLIANCE_HARDWARE_MEMERY)) {
				for (Iterator itApp = section.elementIterator(); itApp
						.hasNext();) {
					Element node = (Element) itApp.next();
					if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_MEMERY_SIZE)) {
						addMemory(Long.parseLong(node.getText()));
					}
				}
			} else if (name
					.equals(VAMConstants.CONFIG_APPLIANCE_HARDWARE_NETWORK)) {
				String macAddr = null;
				String bridge = null;
				for (Iterator itApp = section.elementIterator(); itApp
						.hasNext();) {
					Element node = (Element) itApp.next();
					if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_NETWORK_MACADDR)) {
						macAddr = node.getText();
					} else if (node.getName().equals(VAMConstants.
							CONFIG_APPLIANCE_HARDWARE_NETWORK_BRIDGE)) {
						bridge = node.getText();
					}

				}
				if (macAddr != null && bridge != null) {
					addNetwork(macAddr, bridge);
				}

			} else if (name.equals(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISC)) {
				addDisc(section.getText());
			} else if (name.equals(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_BOOT)) {
				boot = Integer.parseInt(section.getText());
			}
		}
	}

	/**
	 * load config from file, must set the path before the operation.
	 */
	
	@SuppressWarnings("rawtypes")
	public void loadConfig() {
		// load XML document from file
		File inputXml = new File(getPath());
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(inputXml);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}

		init();

		Element appliance = document.getRootElement();
		// parse the node in the Appliance node
		for (Iterator it = appliance.elementIterator(); it.hasNext();) {
			Element section = (Element) it.next();
			String name = section.getName();
			if (name.equals(VAMConstants.CONFIG_APPLIANCE_DESCRIPTION)) {
				setDescription(section.getText());
			} else if (name.equals(VAMConstants.CONFIG_APPLIANCE_HARDWARE)) {
				parseHardware(section);
			} else if (name.equals(VAMConstants.CONFIG_APPLIANCE_SOFTWARE)) {
				parseSoftware(section);
			} else if (name.equals(VAMConstants.CONFIG_APPLIANCE_NAME)) {
				setName(section.getText());
			} else if (name
					.equals(VAMConstants.CONFIG_APPLIANCE_MAKE_APPLIANCE_VM)) {
				setMakeApplianceVM(Integer.parseInt(section.getText()));
			}
		}
	}

	/**
	 * create hardware section.
	 * @param appliance
	 * 		appliance element
	 */
	private void createHardwareElement(Element appliance) {
		// create an Hardware node in the Appliance node
		Element hardware = appliance
				.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE);
		for (int i = 0; i < diskList.size(); i++) {
			DiskInfo diskinfo = diskList.get(i);
			// create an Disk node in the Hardware node
			Element disk = hardware
					.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK);

			// create Disk node's child node Name, Capacity, Format, Location
			Element diskName = disk.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK_NAME);
			diskName.setText(diskinfo.getName());

			Element capacity = disk.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK_CAPACITY);
			capacity.setText("" + diskinfo.getCapacity());

			Element format = disk.addElement(VAMConstants.
					CONFIG_APPLIANCE_HARDWARE_DISK_FORMAT);
			format.setText(diskinfo.getFormat());

			Element location = disk.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISK_LOCATION);
			location.setText(diskinfo.getLocation());
		}

		for (int i = 0; i < discList.size(); i++) {
			// create an Disc node in the Hardware node
			Element disc = hardware
					.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_DISC);
			disc.setText(discList.get(i));
		}

		for (int i = 0; i < cpuList.size(); i++) {
			// create an Cpu node in the Hardware node
			CpuInfo cpuinfo = cpuList.get(i);
			Element cpu = hardware
					.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_CPU);

			// create Cpu node's child node CpuNumber
			Element number = cpu.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_CPU_NUMBER);
			number.setText("" + cpuinfo.getNumber());
		}

		for (int i = 0; i < memeryList.size(); i++) {
			// create an Memery node in the Hardware node
			MemoryInfo memeryinfo = memeryList.get(i);
			Element memery = hardware
					.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_MEMERY);

			// create Memery node's child node MemerySize
			Element size = memery.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_MEMERY_SIZE);
			size.setText("" + memeryinfo.getSize());
		}

		for (int i = 0; i < networkList.size(); i++) {
			// create an Network node in the Hardware node
			NetworkInfo networkinfo = networkList.get(i);
			Element network = hardware
					.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_NETWORK);

			// create Network node's child node MacAddr, Bridge
			Element macAddr = network.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_NETWORK_MACADDR);
			macAddr.setText(networkinfo.getMacAddr());

			Element bridge = network.addElement(
					VAMConstants.CONFIG_APPLIANCE_HARDWARE_NETWORK_BRIDGE);
			bridge.setText(networkinfo.getBridge());
		}

		Element bootSection = hardware
				.addElement(VAMConstants.CONFIG_APPLIANCE_HARDWARE_BOOT);
		bootSection.addText("" + boot);
	}

	/**
	 * create software section.
	 * @param appliance
	 * 		appliance element
	 */
	private void createSoftwareElement(Element appliance) {
		// create an Software node in the Appliance node
		Element software = appliance
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE);
		// create an OperationSystem node in the Software node
		Element operationSystem = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_OS);
		for (int i = 0; i < osList.size(); i++) {
			String os = osList.get(i);
			// create an Name node in the OperationSystem node
			Element osName = operationSystem
					.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_OS_NAME);
			osName.setText(os);
		}

		// create an Application node in the Software node
		Element application = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_APP);
		for (int i = 0; i < appList.size(); i++) {
			String app = appList.get(i);
			// create an Name node in the Application node
			Element appName = application.addElement(
					VAMConstants.CONFIG_APPLIANCE_SOFTWARE_APP_NAME);
			appName.setText(app);
		}

		// create an Bootloader node in the Software node
		Element bootLoader = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_BOOTLOADER);
		bootLoader.setText(getBootLoader());

		// create an Accessway node in the Software node
		Element accessWay = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_ACCESSWAY);
		accessWay.setText(getAccessway());

		// create an VncPort node in the Software node
		Element vncPort = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_VNCPORT);
		vncPort.setText("" + getVncPort());

		// create an Language node in the Software node
		Element language = software
				.addElement(VAMConstants.CONFIG_APPLIANCE_SOFTWARE_LANGUAGE);
		for (int i = 0; i < langList.size(); i++) {
			String lang = langList.get(i);
			// create an Name node in the Language node
			Element langName = language.addElement(
					VAMConstants.CONFIG_APPLIANCE_SOFTWARE_LANGUAGE_NAME);
			langName.setText(lang);
		}
	}

	/**
	 * save the config to file.
	 */
	public void saveConfig() {
		// create a document object
		Document document = DocumentHelper.createDocument();

		// create an Appliance node as a root node
		Element appliance = document.addElement(VAMConstants.CONFIG_APPLIANCE);

		// create an Name node in the Appliance node
		Element name = appliance.addElement(VAMConstants.CONFIG_APPLIANCE_NAME);
		name.setText(getName());

		// create an Description node in the Appliance node
		Element desc = appliance
				.addElement(VAMConstants.CONFIG_APPLIANCE_DESCRIPTION);
		if (getDescription() != null) {
			desc.setText(getDescription());
		}

		// create an MakeApplianceVM node in the Appliance node
		Element makeAppaliceVM = appliance
				.addElement(VAMConstants.CONFIG_APPLIANCE_MAKE_APPLIANCE_VM);
		makeAppaliceVM.setText("" + getMakeApplianceVM());

		createHardwareElement(appliance);

		createSoftwareElement(appliance);

		// write the XML config to file
		try {
			VAMUtil.createDirectory(getPath());
			OutputFormat format = OutputFormat.createPrettyPrint();
			Writer fileWriter = new FileWriter(getPath());
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			xmlWriter.write(document);
			xmlWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * save the config to the Xen config format.
	 * 
	 * @param path
	 *            the config's store path
	 * @return the path of the xen config
	 */
	public String saveToXenCfg(String path) {
		// generate the xen config's path
		if (path == null) {
			path = getPath() + ".xen.cfg";
		} else {
			path = path + ".xen.cfg";
		}

		String bootOrder = "";
		bootOrder += "c";
		if ((boot & VAMConstants.BOOT_CDROM) != 0) {
			bootOrder += "d";
		}

		// xen config file content
		String strConfig = cfgTemplate;

		strConfig = strConfig.replace("<name>", getName());
		strConfig = strConfig.replace("<memerysize>", ""
				+ memeryList.get(0).getSize());
		strConfig = strConfig.replace("<cpuamount>", ""
				+ cpuList.get(0).getNumber());
		strConfig = strConfig.replace("<vncport>", "" + getVncPort());
		strConfig = strConfig.replace("<bootorder>", bootOrder);
		// set the disk config
		String strDisk = "";
		String[] devices = { "hda", "hdb", "hdc", "hdd" };
		for (int i = 0; i < diskList.size() && i < 2; i++) {
			String handle = diskList.get(i).getFormat();
			if (handle.equals("raw")) {
				handle = "aio";
			}
			strDisk += "'tap:" + handle + ":" + diskList.get(i).getLocation() 
				+ ",ioemu:" + devices[i] + ",w',";
		}

		if (discList.size() > 0) {
			String discPath = discList.get(0);
			if (discPath.equals("")) {
				strDisk += "',hdc:cdrom,r',";
			} else {
				strDisk += "'file:" + discList.get(0) + ",hdc:cdrom,r',";
			}
		}

		strConfig = strConfig.replace("<disk>", strDisk);

		// set the network config
		String strNetwork = "";
		if (networkList.size() > 0) {
			strNetwork = "vif = [";
			for (int i = 0; i < networkList.size(); i++) {
				NetworkInfo network = networkList.get(i);
				strNetwork += "'mac=" + network.getMacAddr() + ",bridge="
						+ network.getBridge() + "',";
			}
			strNetwork += "]\r\n";
		}
		strConfig = strConfig.replace("<network>", strNetwork);

		// write the config to file
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(path)));
			out.write(strConfig);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return path;
	}

	/**
	 * get the xen configuration path according to the configuration path.
	 * 
	 * @return xen configuration path
	 */
	public String getXenCfgPath() {
		return getPath() + ".xen.cfg";
	}
}
