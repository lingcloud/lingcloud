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

import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.StreamGobbler;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;
import org.lingcloud.molva.xmm.vam.util.VAMUtil;



/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-5<br>
 * @author Ruijian Wang<br>
 * 
 */
interface ITask {
	void task();
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
class CommandTask implements ITask {

	private VAObject object;

	private List<String> cmdList;

	private TaskFunction function;

	private int failedState;

	private int successfulState;

	private int type;

	private StreamGobbler errorGobbler;

	private StreamGobbler outputGobbler;

	public void setCmdList(List<String> cmdList) {
		this.cmdList = cmdList;
	}

	public void setObject(VAObject object) throws CloneNotSupportedException {
		if (object != null) {
			this.object = (VAObject) object.clone();
		} else {
			this.object = null;
		}
		
	}

	public void setFunction(TaskFunction function) {
		this.function = function;
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

	public void setErrorGobbler(StreamGobbler errorGobbler) {
		this.errorGobbler = errorGobbler;
	}

	public void setOutputGobbler(StreamGobbler outputGobbler) {
		this.outputGobbler = outputGobbler;
	}
	
	// task function
	public void task() {
		String threadName = Thread.currentThread().getName();
		// output start mark
		VAMUtil.outputLog(threadName + ":Task start.");

		boolean isSuccess = true;
		
		Runtime rt = Runtime.getRuntime();
		Process proc;
		
		try {
			
			if (function != null && !function.check(object)) {
				// output end mark
				VAMUtil.outputLog(Thread.currentThread().getName()
						 + ": task complete.");
				return;
			}
			
			int n = cmdList.size();
			int cnt = -n;
			// execute all command
			for (int i = 0; i < n; i++) {
				
				boolean success = false;
				// if execute failed, retry
				for (int retry = 0; 
					retry < VAMConstants.RECONNECTION_TIMES; retry++) {
					String[] shell = new String[] { "/bin/sh", 
							"-c", cmdList.get(i) };
					
					VAMUtil.outputLog(threadName + ":" + cmdList.get(i));
					// System.out.println(threadName + ":" + cmdList.get(i));
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
			VAMUtil.outputLog(e.getMessage());
		}

		
		try {
			if (object != null) {
				// update state
				if (isSuccess) {
					object.setState(successfulState);
				} else {
					object.setState(failedState);
				}
				
				// run call back
				if (function != null) {
					if (type == VAMConstants.TASK_TYPE_REMOVE) {
						function.removeCallBack(object);
					} else if (type == VAMConstants.TASK_TYPE_CREATE) {
						function.createCallBack(object);
					} else if (type == VAMConstants.TASK_TYPE_COPY) {
						function.copyCallBack(object);
					} else if (type == VAMConstants.TASK_TYPE_CONVERT) {
						function.convertCallBack(object);
					} else if (type == VAMConstants.TASK_TYPE_MOVE) {
						function.moveCallBack(object);
					} else {
						function.updateCallBack(object);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			VAMUtil.outputLog(Thread.currentThread().getName() 
					+ ": " + e.getMessage());
		}
		// output end mark
		VAMUtil.outputLog(Thread.currentThread().getName()
				 + ": task complete.");
	}
}
