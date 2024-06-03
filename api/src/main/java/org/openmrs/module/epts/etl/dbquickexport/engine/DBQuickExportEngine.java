package org.openmrs.module.epts.etl.dbquickexport.engine;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.dbquickexport.controller.DBQuickExportController;
import org.openmrs.module.epts.etl.dbquickexport.model.DBQuickExportSearchParams;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.SyncJSONInfo;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.monitor.EngineMonitor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

public class DBQuickExportEngine extends Engine {
	
	public DBQuickExportEngine(EngineMonitor monitor, ThreadRecordIntervalsManager limits) {
		super(monitor, limits);
	}
	
	@Override
	protected boolean mustDoFinalCheck() {
		return false;
	}
	
	@Override
	public DBQuickExportController getRelatedOperationController() {
		return (DBQuickExportController) super.getRelatedOperationController();
	}
	
	@Override
	protected void restart() {
	}
	
	@Override
	public void performeSync(List<? extends EtlObject> etlObjects, Connection conn) throws DBException {
		try {
			List<EtlDatabaseObject> syncRecordsAsOpenMRSObjects = utilities.parseList(etlObjects, EtlDatabaseObject.class);
			
			this.getMonitor().logInfo("GENERATING '" + etlObjects.size() + "' " + getMainSrcTableName() + " TO JSON FILE");
			
			for (EtlDatabaseObject rec : syncRecordsAsOpenMRSObjects) {
				rec.setUniqueKeysInfo(UniqueKeyInfo.cloneAllAndLoadValues(getSrcConf().getUniqueKeys(), rec));
			}
			
			SyncJSONInfo jsonInfo = SyncJSONInfo.generate(getMainSrcTableName(), syncRecordsAsOpenMRSObjects,
			    getEtlConfiguration().getOriginAppLocationCode(), false);
			
			jsonInfo.clearOriginApplicationCodeForAllChildren();
			
			//Generates the File to store the tmp json file
			File jsonFIle = generateJSONTempFile(jsonInfo, syncRecordsAsOpenMRSObjects.get(0).getObjectId().getSimpleValueAsInt(),
			    syncRecordsAsOpenMRSObjects.get(etlObjects.size() - 1).getObjectId().getSimpleValueAsInt());
			
			this.getMonitor().logInfo("WRITING '" + etlObjects.size() + "' " + getMainSrcTableName() + " TO JSON FILE ["
			        + jsonFIle.getAbsolutePath() + ".json]");
			
			//Try to remove not terminate files
			{
				FileUtilities.removeFile(jsonFIle.getAbsolutePath());
				FileUtilities.removeFile(jsonFIle.getAbsolutePath() + ".json");
			}
			
			FileUtilities.write(jsonFIle.getAbsolutePath(), jsonInfo.parseToJSON());
			
			this.logDebug("JSON [" + jsonFIle + ".json] CREATED!");
			
			this.logDebug("MARKING '" + etlObjects.size() + "' " + getMainSrcTableName() + " AS SYNCHRONIZED");
			
			this.logDebug("MARKING '" + etlObjects.size() + "' " + getMainSrcTableName() + " AS SYNCHRONIZED FINISHED");
			
			this.logDebug("MAKING FILES AVALIABLE");
			
			FileUtilities.renameTo(jsonFIle.getAbsolutePath(), jsonFIle.getAbsolutePath() + ".json");
			
			logInfo("WRITEN FILE " + jsonFIle.getPath() + ".json" + " WITH SIZE "
			        + new File(jsonFIle.getAbsolutePath() + ".json").length());
			
			if (new File(jsonFIle.getAbsolutePath() + ".json").length() == 0) {
				new File(jsonFIle.getAbsolutePath() + ".json").delete();
				
				throw new ForbiddenOperationException("EMPTY FILE WAS WROTE!!!!!");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		catch (DBException e) {
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected AbstractEtlSearchParams<? extends EtlObject> initSearchParams(ThreadRecordIntervalsManager limits, Connection conn) {
		AbstractEtlSearchParams<? extends EtlObject> searchParams = new DBQuickExportSearchParams(this.getEtlConfiguration(),
		        limits);
		searchParams.setQtdRecordPerSelected(getQtyRecordsPerProcessing());
		searchParams.setSyncStartDate(getEtlConfiguration().getRelatedSyncConfiguration().getStartDate());
		
		return searchParams;
	}
	
	private File generateJSONTempFile(SyncJSONInfo jsonInfo, Integer startRecord, Integer lastRecord) throws IOException {
		return getRelatedOperationController().generateJSONTempFile(jsonInfo, getSrcConf(), startRecord,
		    lastRecord);
	}
}
