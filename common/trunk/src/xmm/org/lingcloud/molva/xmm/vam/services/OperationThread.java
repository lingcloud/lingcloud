/*
 *  @(#)OperationThread.java  2010-6-2
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

import java.util.LinkedList;
import java.util.List;

import org.lingcloud.molva.xmm.vam.pojos.VAObject;
import org.lingcloud.molva.xmm.vam.util.StreamGobbler;
import org.lingcloud.molva.xmm.vam.util.VAMConstants;

/**
 * 
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-5<br>
 * @author Ruijian Wang<br>
 * 
 */
class TaskList {
	private LinkedList<CommandTask> taskList = new LinkedList<CommandTask>();

	/**
	 * add task.
	 * @param task
	 * 		command task
	 */
	public synchronized void addTask(CommandTask task) {
		this.taskList.add(task);

		notifyAll();
	}
	
	/**
	 * get one task.
	 * @return command task
	 */
	private synchronized CommandTask getListElement() {
		CommandTask task = null;
		if (this.taskList.size() > 0) {
			task = this.taskList.getFirst();
			this.taskList.removeFirst();
		}
		return task;
	}

	/**
	 * get one task.
	 * @return command task
	 * @throws InterruptedException
	 */
	public synchronized CommandTask getTask() throws InterruptedException {
		if (this.taskList.size() <= 0) {
			wait();
		}
		
		return getListElement();
	}

	/**
	 * get task list size.
	 * @return list size
	 */
	public synchronized int getCount() {
		return this.taskList.size();
	}

	/**
	 * remove all task.
	 */
	public synchronized void removeAll() {
		this.taskList.clear();
	}

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
class WorkThread extends Thread {
	private TaskList list;

	private StreamGobbler errorGobbler;

	private StreamGobbler outputGobbler;
	
	private boolean stop;

	/**
	 * initialize the thread.
	 * @param name
	 * 		thread name
	 * @param list
	 * 		task list
	 */
	public WorkThread(String name, TaskList list) {
		super(name);
		this.list = list;
		errorGobbler = new StreamGobbler("ERROR");
		errorGobbler.setName(name);
		outputGobbler = new StreamGobbler("OUTPUT");
		outputGobbler.setName(name);
		errorGobbler.start();
		outputGobbler.start();
		stop = false;
	}

	public void run() {
		while (!stop) {
			CommandTask task = null;

			try {
				task = list.getTask();
				if (task != null) {
					task.setErrorGobbler(errorGobbler);
					task.setOutputGobbler(outputGobbler);
					task.task();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * end the thread.
	 */
	public void end() {
		stop = true;
	}
	
	/**
	 * destroy the thread.
	 */
	public void destroy() {
		if (errorGobbler != null) {
			errorGobbler.end();
			errorGobbler.interrupt();
		}
		
		if (outputGobbler != null) {
			outputGobbler.end();
			outputGobbler.interrupt();
		}
	}
}

/**
 * <strong>Purpose:</strong><br>
 * TODO.
 * 
 * @version 1.0.1 2010-6-2<br>
 * @author Ruijian Wang<br>
 * 
 */
class OperationThread {
	private TaskList busyTask;
	private TaskList lightTask;
	private TaskList idleTask;
	private WorkThread[] busyThread;
	private WorkThread[] lightThread;
	private WorkThread[] idleThread;
	private static OperationThread instance = null;

	/**
	 * initialize thread pool.
	 */
	private OperationThread() {
		busyTask = new TaskList();
		lightTask = new TaskList();
		idleTask = new TaskList();
		busyThread = new WorkThread[VAMConstants.MAX_BUSY_THREAD];
		lightThread = new WorkThread[VAMConstants.MAX_LIGHT_THREAD];
		idleThread = new WorkThread[VAMConstants.MAX_IDLE_THREAD];
		for (int i = 0; i < busyThread.length; i++) {
			busyThread[i] = new WorkThread("BusyThread" + (i + 1), busyTask);
			busyThread[i].start();
		}
		
		for (int i = 0; i < lightThread.length; i++) {
			lightThread[i] = new WorkThread("LightThread" + (i + 1), lightTask);
			lightThread[i].start();
		}
		
		for (int i = 0; i < idleThread.length; i++) {
			idleThread[i] = new WorkThread("IdleThread" + (i + 1), idleTask);
			idleThread[i].start();
		}
	}

	/**
	 * get the thread pool instance.
	 * @return operation thread
	 */
	public static OperationThread getInstance() {
		if (instance == null) {
			instance = new OperationThread();
		}
		return instance;
	}
	
	/**
	 * destroy the thread pool.
	 */
	public static void destroy() {
		if (instance != null) {
			for (int i = 0; i < instance.busyThread.length; i++) {
				instance.busyThread[i].end();
				instance.busyThread[i].interrupt();
				instance.busyThread[i].destroy();
			}
			
			for (int i = 0; i < instance.lightThread.length; i++) {
				instance.lightThread[i].end();
				instance.lightThread[i].interrupt();
				instance.lightThread[i].destroy();
			}
			
			for (int i = 0; i < instance.idleThread.length; i++) {
				instance.idleThread[i].end();
				instance.idleThread[i].interrupt();
				instance.idleThread[i].destroy();
			}
		}
	}

	/**
	 * add one task.
	 * @param task
	 * 		command task
	 * @param type
	 * 		task type
	 */
	public void addTask(CommandTask task, int type) {
		if (type == VAMConstants.THREAD_TYPE_BUSY) {
			busyTask.addTask(task);
		} else if (type == VAMConstants.THREAD_TYPE_LIGHT) {
			lightTask.addTask(task);
		} else if (type == VAMConstants.THREAD_TYPE_IDLE) {
			idleTask.addTask(task);
		}
	}
	
	/**
	 * add one task.
	 * @param taskType
	 * 		task type
	 * @param function
	 * 		task function object, it contains call back function
	 * @param object
	 * 		the virtual appliance object
	 * @param successfulState
	 * 		the state should be updated when the task succeed
	 * @param failedState
	 * 		the state should be updated when the task fails
	 * @param cmdList
	 * 		the command list
	 * @param threadType
	 * 		the thread type
	 * @throws CloneNotSupportedException
	 */
	public void addTask(int taskType, TaskFunction function, VAObject object, 
			int successfulState, int failedState, List<String> cmdList, 
			int threadType) throws CloneNotSupportedException {
		CommandTask task = new CommandTask();
		task.setType(taskType);
		task.setFunction(function);
		task.setObject(object);
		task.setSuccessfulState(successfulState);
		task.setFailedState(failedState);
		task.setCmdList(cmdList);
		addTask(task, threadType);
	}

	public void run() {

	}
}
