	package org.openmrs.module.eptssync.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.openmrs.module.eptssync.controller.conf.SyncOperationConfig;
import org.openmrs.module.eptssync.controller.conf.SyncTableConfiguration;
import org.openmrs.module.eptssync.engine.Engine;
import org.openmrs.module.eptssync.engine.RecordLimits;
import org.openmrs.module.eptssync.monitor.ControllerStatusMonitor;
import org.openmrs.module.eptssync.monitor.EngineActivityMonitor;
import org.openmrs.module.eptssync.utilities.CommonUtilities;
import org.openmrs.module.eptssync.utilities.DateAndTimeUtilities;
import org.openmrs.module.eptssync.utilities.concurrent.MonitoredOperation;
import org.openmrs.module.eptssync.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.eptssync.utilities.concurrent.TimeController;
import org.openmrs.module.eptssync.utilities.concurrent.TimeCountDown;
import org.openmrs.module.eptssync.utilities.db.conn.OpenConnection;
import org.openmrs.module.eptssync.utilities.io.FileUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represent a controller of an synchronization operation. Eg. Export data from tables to JSON files.

 * @author jpboane
 *
 */
public abstract class OperationController implements Controller{
	protected Logger logger;
	
	private ProcessController processController;
	
	private List<EngineActivityMonitor> enginesActivititieMonitor;
	private ControllerStatusMonitor activitieMonitor;
	
	private OperationController child;
	
	private String controllerId;
	
	private int operationStatus;
	private boolean stopRequested;
	
	private SyncOperationConfig operationConfig;
	private OperationController parent;
	
	private TimeController timer;
	
	public OperationController(ProcessController processController, SyncOperationConfig operationConfig) {
		this.logger = Logger.getLogger(this.getClass());
		
		this.processController = processController;
		this.operationConfig = operationConfig;
		
		this.controllerId = processController.getControllerId() + "_" + getOperationType();	
		
		this.operationStatus = MonitoredOperation.STATUS_NOT_INITIALIZED;	
	}
	
	public OperationController getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return this.parent != null;
	}
	
	public boolean hasChild() {
		return this.child != null;
	}
	
	public boolean hasNestedController() {
		return hasChild() || hasParent();
	}
	
	public void setParent(OperationController parent) {
		this.parent = parent;
	}
	
	public SyncOperationConfig getOperationConfig() {
		return operationConfig;
	}
	
	public OperationController getChild() {
		return child;
	}
	
	public void setChild(OperationController child) {
		this.child = child;
	}
	
	public ProcessController getProcessController() {
		return processController;
	}
	
	public boolean isParallelModeProcessing() {
		return this.getOperationConfig().isParallelModeProcessing();
	}
	
	private void runIsSequencialMode() {
		List<SyncTableConfiguration> allSync = getProcessController().getConfiguration().getTablesConfigurations();
		
		for (SyncTableConfiguration syncInfo: allSync) {
			if (operationTableIsAlreadyFinished(syncInfo)) {
				logInfo(("The operation '" + getOperationType() + "' On table '" + syncInfo.getTableName() + "' was already finished!").toUpperCase());
			}
			else 
			if (stopRequested()) {
				logInfo("ABORTING THE ENGINE PROCESS DUE STOP REQUESTED!");
				break;		
			}
			else {
				logInfo(("Starting operation '" + getOperationType() + "' On table '" + syncInfo.getTableName() + "'").toUpperCase());
				
				EngineActivityMonitor engine = initAndStartEngine(syncInfo);
				
				while (engine != null && !engine.getMainEngine().isFinished() && !engine.getMainEngine().isStopped()) {
					if (stopRequested()) {
						logInfo("STOP REQUEST!!! THE OPERATION ON TABLE " + syncInfo.getTableName().toUpperCase() + " WILL STOPPED SOON");
						engine.getMainEngine().requestStop();
					}
					else {
						logInfo(("The operation '" + getOperationType() + "' Is still working on table '" + syncInfo.getTableName() + "'").toUpperCase());
					}
					
					TimeCountDown.sleep(10);
				}
				
				if (stopRequested() && engine != null && engine.getMainEngine().isStopped()) {
					logInfo(("The operation '" + getOperationType() + "' On table '" + syncInfo.getTableName() + "'  is stopped successifuly!").toUpperCase());
					break;
				}
				else {
					logInfo(("The operation '" + getOperationType() + "' On table '" + syncInfo.getTableName() + "' is finished!").toUpperCase());
				}
			}
		}
		
		if (!stopRequested()) {
			changeStatusToFinished();
		}
		else {
			changeStatusToStopped();
		}
	}

	private void runInParallelMode() {
		List<SyncTableConfiguration> allSync = getProcessController().getConfiguration().getTablesConfigurations();
		
		this.enginesActivititieMonitor = new ArrayList<EngineActivityMonitor>();
		
		for (SyncTableConfiguration syncInfo: allSync) {
			if (operationTableIsAlreadyFinished(syncInfo)) {
				logInfo(("The operation '" + getOperationType() + "' On table '" + syncInfo.getTableName() + "' was already finished!").toUpperCase());
			}
			else 
			if (stopRequested()) {
				logInfo("ABORTING THE ENGINE INITIALIZER DUE STOP REQUESTED!");
				
				break;
			}
			else{
				logInfo("INITIALIZING '" + getOperationType().toUpperCase() + "' ENGINE FOR TABLE '" + syncInfo.getTableName().toUpperCase() + "'");
					
				EngineActivityMonitor activitityMonitor = initAndStartEngine(syncInfo);
					
				if (activitityMonitor != null) {
					startAndAddToEnginesActivititieMonitor(activitityMonitor);
					
					logInfo("INITIALIZED '" + getOperationType().toUpperCase() + "' ENGINE FOR TABLE '" + syncInfo.getTableName().toUpperCase() + "'");
				}
				else {
					logInfo("NO ENGINE FOR '" + getOperationType().toUpperCase() + "' FOR TABLE '" + syncInfo.getTableName().toUpperCase() + "' WAS CREATED...");
				}
			}
		}
	}

	private boolean operationTableIsAlreadyFinished(SyncTableConfiguration conf) {
		String operationId = this.getControllerId() + "_" + conf.getTableName();
		
		String fileName = getProcessController().getConfiguration().getSyncRootDirectory() + "/process_status/"+operationId;
		
		return new File(fileName).exists(); 
	}

	private boolean operationIsAlreadyFinished() {
		String operationId = this.getControllerId();
		
		String fileName = getProcessController().getConfiguration().getSyncRootDirectory() + "/process_status/"+operationId;
		
		return new File(fileName).exists(); 
	}

	public String getControllerId() {
		return controllerId;
	}
	
	public List<EngineActivityMonitor> getEnginesActivititieMonitor() {
		return enginesActivititieMonitor;
	}
	
	@JsonIgnore
	public OpenConnection openConnection() {
		return processController.openConnection();
	}
	
	@JsonIgnore
	public CommonUtilities utilities() {
		return CommonUtilities.getInstance();
	}
	
	public void logInfo(String msg) {
		utilities().logInfo(msg, logger);
	}
	
	public void logError(String msg) {
		utilities().logErr(msg, logger);
	}
	
	public void logDebug(String msg) {
		utilities().logDebug(msg, logger);
	}

	protected EngineActivityMonitor initAndStartEngine(SyncTableConfiguration syncInfo) {
		EngineActivityMonitor activitityMonitor = new EngineActivityMonitor(this, syncInfo);
		Engine engine = activitityMonitor.initEngine();
		
		if (engine != null) {
			return activitityMonitor;
		}
		else return null;
	}
	
	private void startAndAddToEnginesActivititieMonitor(EngineActivityMonitor activitityMonitor) {
		this.enginesActivititieMonitor.add(activitityMonitor);
		
		ThreadPoolService.getInstance().createNewThreadPoolExecutor(getControllerId().toUpperCase() + "_ENGINE_OPERATION_MONITOR").execute(activitityMonitor);
	}
	
	@Override
	public String toString() {
		return this.controllerId;
	}
	
	@Override
	public void run() {
		changeStatusToRunning();
		
		timer = new TimeController();
		timer.start();
		
		this.activitieMonitor = new ControllerStatusMonitor(this);
		
		ExecutorService executor = ThreadPoolService.getInstance().createNewThreadPoolExecutor(this.controllerId.toUpperCase() + "_MONIGTOR");
		executor.execute(this.activitieMonitor);

		if (stopRequested()) {
			logInfo("THE OPERATION " + getControllerId()  + " COULD NOT BE INITIALIZED DUE STOP REQUESTED!!!!");
			
			changeStatusToStopped();
			
			if (getChild() != null) {
				getChild().requestStop();
			}
		}
		else
		if (operationIsAlreadyFinished()) {
			logInfo("THE OPERATION " + getControllerId() + " WAS ALREADY FINISHED!");
			
			changeStatusToFinished();
		}
		else
		if (isParallelModeProcessing()) {
			runInParallelMode();
		}
		else {
			runIsSequencialMode();
		}
	}
	
	
	@Override
	public TimeController getTimer() {
		return this.timer;
	}
	
	@Override
	public boolean stopRequested() {
		return this.stopRequested;
	}

	public boolean isInitialized() {
		return this.operationStatus != MonitoredOperation.STATUS_NOT_INITIALIZED;
	}

	@Override
	public boolean isNotInitialized() {
		return this.operationStatus == MonitoredOperation.STATUS_NOT_INITIALIZED;
	}
	
	@Override
	public boolean isRunning() {
		return this.operationStatus == MonitoredOperation.STATUS_RUNNING;
	}
	
	@Override
	public boolean isStopped() {
		if (isFinished()) return true;
		
		if (isParallelModeProcessing() && this.enginesActivititieMonitor != null) {
			for (EngineActivityMonitor monitor : this.enginesActivititieMonitor) {
				Engine engine = monitor.getMainEngine();
				
				if (engine == null) throw new RuntimeException("No engine for minitor '" + monitor.getSyncTableInfo().getTableName() + "'");
				
				if (!engine.isStopped()) {
					return false;
				}
			}
			
			return true;
		}
		
		return this.operationStatus == MonitoredOperation.STATUS_STOPPED;
	}
	
	@Override
	public boolean isFinished() {
		if(isNotInitialized()) {
			return false;
		}
		
		if (isParallelModeProcessing() && this.enginesActivititieMonitor != null) {
			for (EngineActivityMonitor monitor : this.enginesActivititieMonitor) {
				Engine engine = monitor.getMainEngine();
				
				if (engine == null) throw new RuntimeException("No engine for minitor '" + monitor.getSyncTableInfo().getTableName() + "'");
				
				if (!engine.isFinished()) {
					return false;
				}
			}
			
			return true;
		}
		else {
			return this.operationStatus == MonitoredOperation.STATUS_FINISHED;
		}
	}
	
	
	public void markTableOperationAsFinished(SyncTableConfiguration conf, Engine engine, TimeController timer) {
		String operationId = this.getControllerId() + "_" + conf.getTableName();
		
		String fileName = getProcessController().getConfiguration().getSyncRootDirectory() + "/process_status/"+operationId;
		
		logInfo("FINISHING OPERATION ON TABLE " + conf.getTableName().toUpperCase());
		
		if (!new File(fileName).exists()) {
			logInfo("WRITING OPERATION STATUS ON "+ fileName);
			
			String desc = "";
			
			int qtyRecords = engine != null && engine.getProgressMeter() != null ? engine.getProgressMeter().getTotal() : 0;
			
			desc += "{\n";
			desc += "	operationName: \"" + this.getControllerId() + "\",\n";
			desc += "	operationTable: \"" + conf.getTableName() + "\"\n";
			desc += "	qtyRecords: " + qtyRecords + ",\n";
			desc += "	startTime: \"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(timer.getStartTime()) + "\",\n";
			desc += "	finishTime: \"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(DateAndTimeUtilities.getCurrentDate()) + "\",\n";
			desc += "	elapsedTime: \"" + timer.getDuration(TimeController.DURACAO_IN_MINUTES) + "\"\n";
			desc += "}";
			
			FileUtilities.write(fileName, desc);
			
			logInfo("FILE WROTE");
		} 
		else {
			logInfo("THE FILE WAS ALREADY EXISTS");
		}
		
	}
	
	public void markOperationAsFinished() {
		String operationId = this.getControllerId();
		
		String fileName = getProcessController().getConfiguration().getSyncRootDirectory() + "/process_status/"+operationId;
		
		logInfo("FINISHING OPERATION "+ getControllerId());
		
		if (!new File(fileName).exists()) {
			logInfo("WRITING OPERATION STATUS ON "+ fileName);
			
			String desc = "";
			
			desc += "{\n";
			desc += "	operationName: \"" + this.getControllerId() + "\",\n";
			desc += "	startTime: \"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(this.getTimer().getStartTime()) + "\",\n";
			desc += "	finishTime: \"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(DateAndTimeUtilities.getCurrentDate()) + "\",\n";
			desc += "	elapsedTime: \"" + this.getTimer().getDuration(TimeController.DURACAO_IN_HOURS) + "\"\n";
			desc += "}";
			
			FileUtilities.write(fileName, desc);
			
			logInfo("FILE WROTE");
		}
		else {
			logInfo("THE FILE WAS ALREADY EXISTS");
		}
	}
	
	@Override
	public boolean isPaused() {
		return this.operationStatus == MonitoredOperation.STATUS_PAUSED;
	}
	
	@Override
	public boolean isSleeping() {
		return this.operationStatus == MonitoredOperation.STATUS_SLEEPENG;
	}

	@Override
	public void changeStatusToSleeping() {
		this.operationStatus = MonitoredOperation.STATUS_SLEEPENG;
	}
	
	@Override
	public void changeStatusToRunning() {
		this.operationStatus = MonitoredOperation.STATUS_RUNNING;
	}
	
	@Override
	public void changeStatusToStopped() {
		this.operationStatus = MonitoredOperation.STATUS_STOPPED;		
	}
	
	@Override
	public void changeStatusToFinished() {
		this.operationStatus = MonitoredOperation.STATUS_FINISHED;	
	
		markOperationAsFinished();
	}
	
	@Override	
	public void changeStatusToPaused() {
		this.operationStatus = MonitoredOperation.STATUS_PAUSED;	
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onSleep() {
	}

	@Override
	public void onStop() {
		logInfo("THE PROCESS "+getControllerId().toUpperCase() + " WAS STOPPED!!!");
	}
	
	@Override
	public void onFinish() {
		getTimer().stop();
		
		logInfo("FINISHING OPERATION");
		OperationController nextOperation = getChild();
		
		while (nextOperation != null && nextOperation.getOperationConfig().isDisabled()) {
			nextOperation = nextOperation.getChild();
		}
		
		if (nextOperation != null) {
			
			if (!stopRequested()) {
				ExecutorService executor = ThreadPoolService.getInstance().createNewThreadPoolExecutor(nextOperation.getControllerId());
				executor.execute(nextOperation);
			}
			else {
				logInfo("THE OPERATION " + nextOperation.getControllerId().toUpperCase() + " COULD NOT BE INITIALIZED BECAUSE THERE WAS A STOP REQUEST!!!");
			}
		}
	}
	
	@Override
	public synchronized void requestStop() {
		if (isNotInitialized()) {
			changeStatusToStopped();
		}
		else
		if (!stopRequested()) {
			if (this.enginesActivititieMonitor != null) {
				for (EngineActivityMonitor monitor : this.enginesActivititieMonitor) {
					monitor.getMainEngine().requestStop();
				}
			}
			
			this.stopRequested = true;
		}
		
		if (getChild() != null) getChild().requestStop();
	}
	
	@Override
	public int getWaitTimeToCheckStatus() {
		return 15;
	}
	
	@JsonIgnore
	public abstract boolean mustRestartInTheEnd();
	
	@JsonIgnore
	public abstract String getOperationType();
	
	public abstract Engine initRelatedEngine(EngineActivityMonitor monitor, RecordLimits limits) ;

	public abstract long getMinRecordId(SyncTableConfiguration tableInfo);
	public abstract long getMaxRecordId(SyncTableConfiguration tableInfo);
	
	public void refresh() {
	}
}