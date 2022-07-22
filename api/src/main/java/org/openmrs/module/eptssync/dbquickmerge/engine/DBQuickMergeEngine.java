package org.openmrs.module.eptssync.dbquickmerge.engine;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.eptssync.controller.conf.AppInfo;
import org.openmrs.module.eptssync.dbquickmerge.controller.DBQuickMergeController;
import org.openmrs.module.eptssync.dbquickmerge.model.DBQuickMergeSearchParams;
import org.openmrs.module.eptssync.dbquickmerge.model.MergingRecord;
import org.openmrs.module.eptssync.engine.Engine;
import org.openmrs.module.eptssync.engine.RecordLimits;
import org.openmrs.module.eptssync.engine.SyncSearchParams;
import org.openmrs.module.eptssync.exceptions.ConflictWithRecordNotYetAvaliableException;
import org.openmrs.module.eptssync.exceptions.MissingParentException;
import org.openmrs.module.eptssync.model.SearchParamsDAO;
import org.openmrs.module.eptssync.model.base.SyncRecord;
import org.openmrs.module.eptssync.model.pojo.generic.OpenMRSObject;
import org.openmrs.module.eptssync.monitor.EngineMonitor;
import org.openmrs.module.eptssync.utilities.concurrent.TimeCountDown;
import org.openmrs.module.eptssync.utilities.db.conn.DBException;
import org.openmrs.module.eptssync.utilities.db.conn.OpenConnection;

/**
 * 
 * @author jpboane
 *
 * @see DBQuickMergeController
 */
public class DBQuickMergeEngine extends Engine {
	private AppInfo mainApp;
	private AppInfo remoteApp;
	
	public DBQuickMergeEngine(EngineMonitor monitor, RecordLimits limits) {
		super(monitor, limits);
	
		this.mainApp = getRelatedOperationController().getConfiguration().find(AppInfo.init("main"));
		this.remoteApp = getRelatedOperationController().getConfiguration().find(AppInfo.init("remote"));
	}
	
	@Override	
	public List<SyncRecord> searchNextRecords(Connection conn) throws DBException{
		return  utilities.parseList(SearchParamsDAO.search(this.searchParams, conn), SyncRecord.class);
	}
	
	@Override
	protected boolean mustDoFinalCheck() {
		return false;
	}
	
	@Override
	public DBQuickMergeController getRelatedOperationController() {
		return (DBQuickMergeController) super.getRelatedOperationController();
	}
	
	@Override
	protected void restart() {
	}
	
	@Override
	public void performeSync(List<SyncRecord> syncRecords, Connection conn) throws DBException{
		this.getMonitor().logInfo("PERFORMING MERGE ON " + syncRecords.size() + "' " + getSyncTableConfiguration().getTableName());
		
		int i = 1;
		
		OpenConnection srcConn = getRelatedOperationController().openSrcConnection();
		
		try {
			for (SyncRecord record: syncRecords) {
				String startingStrLog = utilities.garantirXCaracterOnNumber(i, (""+getSearchParams().getQtdRecordPerSelected()).length()) + "/" + syncRecords.size();
			
				
				OpenMRSObject rec = (OpenMRSObject)record;
				
				MergingRecord data = new MergingRecord(rec , getSyncTableConfiguration(), this.remoteApp, this.mainApp);
				
				process(data, startingStrLog, 0, srcConn, conn);
				
				i++;
			}
			
			this.getMonitor().logInfo("MERGE DONE ON " + syncRecords.size() + " " + getSyncTableConfiguration().getTableName() + "!");
			
			srcConn.markAsSuccessifullyTerminected();
		}
		finally {
			srcConn.finalizeConnection();
		}
	}
	
	private void process(MergingRecord mergingData, String startingStrLog, int reprocessingCount, Connection srcConn, Connection destConn) throws DBException {
		String reprocessingMessage = reprocessingCount == 0 ? "Merging Record" : "Re-merging " + reprocessingCount + " Record";
		
		logDebug(startingStrLog  +": " + reprocessingMessage + ": [" + mergingData.getRecord() + "]");
		
		try {
			if (getRelatedOperationController().getMergeType().isMissing()) {
				mergingData.merge(srcConn, destConn);
			}
			else {
				mergingData.resolveConflict(srcConn, destConn);
			}	
		}
		catch (MissingParentException e) {
			logWarn(mergingData.getRecord() + " - " + e.getMessage() + " The record will be skipped");
		}
		catch (ConflictWithRecordNotYetAvaliableException e) {
			logWarn("Error while merging record: [" + mergingData.getRecord() + "]! " + e.getLocalizedMessage() + ". Re-merging the record...");
			
			TimeCountDown.sleep(5);
			
			process(mergingData, startingStrLog, ++reprocessingCount, srcConn, destConn);
		}
	}
	
	@Override
	public void requestStop() {
	}

	@Override
	protected SyncSearchParams<? extends SyncRecord> initSearchParams(RecordLimits limits, Connection conn) {
		SyncSearchParams<? extends SyncRecord> searchParams = new DBQuickMergeSearchParams(this.getSyncTableConfiguration(), limits, getRelatedOperationController());
		searchParams.setQtdRecordPerSelected(getQtyRecordsPerProcessing());
		searchParams.setSyncStartDate(getSyncTableConfiguration().getRelatedSynconfiguration().getObservationDate());
		
		return searchParams;
	}

}
