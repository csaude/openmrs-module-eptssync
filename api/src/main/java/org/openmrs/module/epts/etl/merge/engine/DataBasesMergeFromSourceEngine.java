package org.openmrs.module.epts.etl.merge.engine;

/**
 * The data bases merge performes the merge of db from several sources to the central DB. It cames after {@link DBQuickCopyEngine} process.
 * The data bases merge load the minimal information of records from the stage area and then load the full record info from the origin schema of winning record 
 * 
 */
import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.common.model.SyncImportInfoVO;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.exceptions.MissingParentException;
import org.openmrs.module.epts.etl.merge.controller.DataBaseMergeFromSourceDBController;
import org.openmrs.module.epts.etl.merge.model.DataBaseMergeFromSourceDBSearchParams;
import org.openmrs.module.epts.etl.merge.model.MergingRecord;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.monitor.EngineMonitor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DataBasesMergeFromSourceEngine extends Engine {
	
	public DataBasesMergeFromSourceEngine(EngineMonitor monitor, ThreadRecordIntervalsManager limits) {
		super(monitor, limits);
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
	public void performeSync(List<? extends EtlObject> etlObjects, Connection conn) throws DBException {
		logInfo("PERFORMING MERGE ON " + etlObjects.size() + "' " + getMainSrcTableName());
		
		int i = 1;
		
		for (EtlObject record : etlObjects) {
			String startingStrLog = utilities.garantirXCaracterOnNumber(i,
			    ("" + getSearchParams().getQtdRecordPerSelected()).length()) + "/" + etlObjects.size();
			
			logDebug(startingStrLog + ": Merging Record: [" + record + "]");
			
			MergingRecord data = new MergingRecord((SyncImportInfoVO) record, getSrcConf(),
			        getRelatedOperationController().getRemoteApp(), getRelatedOperationController().getMainApp());
			
			try {
				data.merge(conn);
			}
			catch (MissingParentException e) {
				logWarn(record + " - " + e.getMessage() + " The record will be skipped");
			}
			
			i++;
		}
	}
	
	@Override
	protected AbstractEtlSearchParams<? extends EtlObject> initSearchParams(ThreadRecordIntervalsManager limits, Connection conn) {
		AbstractEtlSearchParams<? extends EtlObject> searchParams = new DataBaseMergeFromSourceDBSearchParams(
		        this.getEtlConfiguration(), limits, conn, getRelatedOperationController());
		searchParams.setQtdRecordPerSelected(getQtyRecordsPerProcessing());
		searchParams.setSyncStartDate(getEtlConfiguration().getRelatedSyncConfiguration().getStartDate());
		
		return searchParams;
	}
}
