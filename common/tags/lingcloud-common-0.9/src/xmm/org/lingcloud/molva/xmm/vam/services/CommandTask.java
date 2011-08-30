/*
 *  @(#)CommandTask.java  2010-6-5
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

import java.util.List;

import org.lingcloud.molva.xmm.vam.controllers.Controller;
import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.StreamGobbler;
import org.lingcloud.molva.xmm.vam.util.StreamGobblerPool;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2011-07-28<br>
 * @author Ruijian Wang<br>
 * 
 */
interface IFileTask extends Runnable {
	String getContent();
}

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-5<br>
 * @author Ruijian Wang<br>
 * 
 */
class CommandTask implements IFileTask {

	private VAObject object;

	private List<String> cmdList;

	private Controller controller;

	private int failedState;

	private int successfulState;

	private int type;

	private static StreamGobblerPool errorGobblerPool = null;

	private static StreamGobblerPool outputGobblerPool = null;

	static {
		int poolSize = VAMConstants.THREAD_TYPE_BUSY
				+ VAMConstants.THREAD_TYPE_LIGHT + 2;
		errorGobblerPool = new StreamGobblerPool(poolSize, "ERROR");
		outputGobblerPool = new StreamGobblerPool(poolSize, "OUTPUT");
	}

	public CommandTask(VAObject object, List<String> cmdList,
			Controller controller, int successfulState, int failedState,
			int type) {
		super();
		this.object = object;
		this.cmdList = cmdList;
		this.controller = controller;
		this.failedState = failedState;
		this.successfulState = successfulState;
		this.type = type;
	}

	public void setCmdList(List<String> cmdList) {
		this.cmdList = cmdList;
	}
	
	public String getContent() {
		StringBuilder buf = new StringBuilder();
		if (cmdList != null) {
			for (String cmd : cmdList) {
				buf.append(cmd);
				buf.append("###");
			}
		}
		return buf.toString();
	}

	public void setObject(VAObject object) throws CloneNotSupportedException {
		if (object != null) {
			this.object = (VAObject) object.clone();
		} else {
			this.object = null;
		}
	}
	
	public VAObject getObject() {
		return this.object;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setFailedState(int failedState) {
		this.failedState = failedState;
	}

	public void setSuccessfulState(int successfulState) {
		this.successfulState = successfulState;
	}

	public void setType(int type) {
		this.type = type;
	}

	public CommandTask() {

	}

	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		// output start mark
		VAMUtil.infoLog(threadName + ":Task start.");

		boolean isSuccess = true;

		Runtime rt = Runtime.getRuntime();
		Process proc;

		StreamGobbler errorGobbler = null;
		StreamGobbler outputGobbler = null;

		try {
			errorGobbler = errorGobblerPool.acquireStreamGobbler();
			errorGobbler.setName(Thread.currentThread().getName());
			outputGobbler = outputGobblerPool.acquireStreamGobbler();
			outputGobbler.setName(Thread.currentThread().getName());

			int n = cmdList.size();
			int cnt = -n;

			// execute all command
			for (int i = 0; i < n; i++) {

				boolean success = false;
				// if execute failed, retry
				for (int retry = 0; retry < VAMConstants.RECONNECTION_TIMES; 
					retry++) {
					String[] shell = new String[] { "/bin/sh", "-c",
							cmdList.get(i) };

					VAMUtil.infoLog(threadName + ":" + cmdList.get(i));

					// execute the command
					proc = rt.exec(shell);

					// any error message?
					errorGobbler.addStream(proc.getErrorStream());
					// any output?
					outputGobbler.addStream(proc.getInputStream());

					int exitValue = proc.waitFor();
					// check whether the command exits normally.
					if (exitValue == 0) {
						success = true;
						break;
					}
					Thread.sleep(VAMConstants.INTERVAL);
				}
				if (success) {
					cnt++;
				} else {
					break;
				}
			}

			if (cnt == 0) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}

		} catch (Exception e) {
			isSuccess = false;
			VAMUtil.errorLog(e.getMessage());
		}

		errorGobblerPool.releaseStreamGobbler(errorGobbler);
		outputGobblerPool.releaseStreamGobbler(outputGobbler);

		try {
			if (object != null) {
				// update state
				if (isSuccess) {
					object.setState(successfulState);
				} else {
					object.setState(failedState);
				}

				// run call back
				if (controller != null) {
					if (type == VAMConstants.TASK_TYPE_REMOVE) {
						controller.remove(object);
					} else if (type == VAMConstants.TASK_TYPE_CREATE) {
						controller.create(object);
					} else if (type == VAMConstants.TASK_TYPE_COPY) {
						controller.copy(object);
					} else if (type == VAMConstants.TASK_TYPE_CONVERT) {
						controller.convert(object);
					} else if (type == VAMConstants.TASK_TYPE_MOVE) {
						controller.move(object);
					} else {
						controller.update(object);
					}
				}
			}
			FileManager.getInstance().completeTask(this);
		} catch (Exception e) {
			e.printStackTrace();
			VAMUtil.errorLog(Thread.currentThread().getName() + ": "
					+ e.getMessage());
		}
		// output end mark
		VAMUtil.infoLog(Thread.currentThread().getName() 
				+ ": task complete.");
	}
}
