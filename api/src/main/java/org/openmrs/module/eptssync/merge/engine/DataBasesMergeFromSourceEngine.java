package org.openmrs.module.eptssync.merge.engine;

/**
 * The data bases merge performes the merge of db from several sources to the central DB. It cames after {@link DBQuickCopyEngine} process.
 * The data bases merge load the minimal information of records from the stage area and then load the full record info from the origin schema of winning record 
 * 
 */
import java.sql.Connection;
import java.util.List;

import org.openmrs.module.eptssync.common.model.SyncImportInfoVO;
import org.openmrs.module.eptssync.engine.Engine;
import org.openmrs.module.eptssync.engine.RecordLimits;
import org.openmrs.module.eptssync.engine.SyncSearchParams;
import org.openmrs.module.eptssync.exceptions.MissingParentException;
import org.openmrs.module.eptssync.merge.controller.DataBaseMergeFromSourceDBController;
import org.openmrs.module.eptssync.merge.model.DataBaseMergeFromSourceDBSearchParams;
import org.openmrs.module.eptssync.merge.model.MergingRecord;
import org.openmrs.module.eptssync.model.SearchParamsDAO;
import org.openmrs.module.eptssync.model.TableOperationProgressInfo;
import org.openmrs.module.eptssync.model.base.SyncRecord;
import org.openmrs.module.eptssync.monitor.EngineMonitor;
import org.openmrs.module.eptssync.utilities.db.conn.DBException;

public class DataBasesMergeFromSourceEngine extends Engine {
		
	public DataBasesMergeFromSourceEngine(EngineMonitor monitor, RecordLimits limits) {
		super(monitor, limits);
	}
	
	@Override	
	public List<SyncRecord> searchNextRecords(Connection conn) throws DBException{
		if (!getLimits().isLoadedFromFile()) {
			RecordLimits saveLimits = retriveSavedLimits();
			
			if (saveLimits != null) {
				this.searchParams.setLimits(saveLimits);
			}
		}
	
		logInfo("SERCHING NEXT RECORDS FOR LIMITS " + getLimits());
		
		if (getLimits().canGoNext()) {
			return  utilities.parseList(SearchParamsDAO.search(this.searchParams, conn), SyncRecord.class);
		}
		else return null;	
	}

	@Override
	protected boolean mustDoFinalCheck() {
		return false;
	}
	
	@Override
	public DataBaseMergeFromSourceDBController getRelatedOperationController() {
		return (DataBaseMergeFromSourceDBController) super.getRelatedOperationController();
	}
	
	@Override
	protected void restart() {
	}
	
	@Override
	public void performeSync(List<SyncRecord> syncRecords, Connection conn) throws DBException{
		this.getMonitor().logInfo("PERFORMING MERGE ON " + syncRecords.size() + "' " + getSyncTableConfiguration().getTableName());
		
		int i = 1;
		
		for (SyncRecord record: syncRecords) {
			String startingStrLog = utilities.garantirXCaracterOnNumber(i, (""+getSearchParams().getQtdRecordPerSelected()).length()) + "/" + syncRecords.size();
		
			logInfo(startingStrLog  +": Merging Record: [" + record + "]");
			
			MergingRecord data = new MergingRecord((SyncImportInfoVO) record , getSyncTableConfiguration(), getRelatedOperationController().getRemoteApp(), getRelatedOperationController().getMainApp());
			
			try {
				data.merge(conn);
			}
			catch (MissingParentException e) {
				logInfo(record + " - " + e.getMessage() + " The record will be skipped");
			}
			
			i++;
		}
		
		this.getMonitor().logInfo("MERGE DONE ON " + syncRecords.size() + " " + getSyncTableConfiguration().getTableName() + "!");

		getLimits().moveNext(getQtyRecordsPerProcessing());
		
		saveCurrentLimits();
		
		if (isMainEngine()) {
			TableOperationProgressInfo progressInfo = this.getRelatedOperationController().getProgressInfo().retrieveProgressInfo(getSyncTableConfiguration());
			
			progressInfo.refreshProgressMeter();
			
			progressInfo.refreshOnDB(conn);
		}
	}
	
	private void saveCurrentLimits() {
		getLimits().save();
	}
	
	@Override
	public void requestStop() {
	}

	@Override
	protected SyncSearchParams<? extends SyncRecord> initSearchParams(RecordLimits limits, Connection conn) {
		SyncSearchParams<? extends SyncRecord> searchParams = new DataBaseMergeFromSourceDBSearchParams(this.getSyncTableConfiguration(), limits, conn);
		searchParams.setQtdRecordPerSelected(getQtyRecordsPerProcessing());
		searchParams.setSyncStartDate(getSyncTableConfiguration().getRelatedSynconfiguration().getObservationDate());
		
		return searchParams;
	}
}