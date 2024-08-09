package org.openmrs.module.epts.etl.etl.model;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaInfo;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObjectDAO;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParentException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationItemResult;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

public class EtlLoadHelper {
	
	protected static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private List<EtlLoadHelperRecord> loadRecordHelper;
	
	private EtlProcessor processor;
	
	private LoadingType loadingType;
	
	private List<DstConf> dstConf;
	
	public EtlLoadHelper(EtlProcessor processor, List<DstConf> dstConf, int qtySrcObjects, LoadingType loadingType) {
		this.processor = processor;
		this.loadRecordHelper = new ArrayList<>(qtySrcObjects);
		this.loadingType = loadingType;
		this.dstConf = dstConf;
	}
	
	public EtlLoadHelper(EtlProcessor processor, List<EtlLoadHelperRecord> loadRecordHelper, List<DstConf> dstConf,
	    LoadingType loadingType) {
		this(processor, dstConf, loadRecordHelper.size(), loadingType);
		
		this.loadRecordHelper = loadRecordHelper;
	}
	
	public EtlLoadHelper(EtlProcessor processor, EtlLoadHelperRecord recordToLoad, List<DstConf> dstConf,
	    LoadingType loadingType) {
		this(processor, utilities.parseToList(recordToLoad), dstConf, loadingType);
	}
	
	public EtlLoadHelper(EtlProcessor processor, LoadRecord recordToLoad, List<DstConf> dstConf, LoadingType loadingType) {
		this(processor, utilities.parseToList(new EtlLoadHelperRecord(recordToLoad)), dstConf, loadingType);
	}
	
	public List<DstConf> getDstConf() {
		return dstConf;
	}
	
	public List<EtlLoadHelperRecord> getLoadRecordHelper() {
		return loadRecordHelper;
	}
	
	public EtlProcessor getProcessor() {
		return this.processor;
	}
	
	public Engine<? extends EtlDatabaseObject> getEngine() {
		return getProcessor().getEngine();
	}
	
	public EtlController getController() {
		return (EtlController) getEngine().getRelatedOperationController();
	}
	
	public EtlOperationConfig getEtlOperationConfig() {
		return getController().getOperationConfig();
	}
	
	public boolean isPrincipalLoading() {
		return this.loadingType.isPrincipal();
	}
	
	public void addRecord(LoadRecord loadRecord) {
		EtlLoadHelperRecord loadRec = findHelperBySrcRecord(loadRecord.getSrcRecord());
		
		if (loadRec != null) {
			loadRec.addLoadRecord(loadRecord);
		} else {
			getLoadRecordHelper().add(new EtlLoadHelperRecord(loadRecord));
		}
		
	}
	
	private EtlLoadHelperRecord findHelperBySrcRecord(EtlDatabaseObject srcObject) {
		for (EtlLoadHelperRecord lr : getLoadRecordHelper()) {
			if (lr.getSrcObject() == srcObject) {
				return lr;
			}
		}
		
		return null;
	}
	
	public List<EtlStageAreaInfo> getAllSuccessifulProcessedAsEtlStageAreaObject(Connection srcConn, Connection dstConn)
	        throws DBException {
		List<EtlLoadHelperRecord> sucess = getAllSuccessfullyProcessedRecords();
		
		List<EtlStageAreaInfo> info = new ArrayList<>(sucess.size());
		
		for (EtlLoadHelperRecord rec : sucess) {
			info.add(EtlStageAreaInfo.generate(rec, srcConn, dstConn));
		}
		
		return info;
	}
	
	/**
	 * Finds all LoadRecord records which as same {@link DstConf} from the #loadRecordHelper
	 */
	private List<LoadRecord> getAllRecordsAsLoadRecord(DstConf dstConf) {
		List<LoadRecord> allOfDst = new ArrayList<>();
		
		for (EtlLoadHelperRecord lr : getLoadRecordHelper()) {
			LoadRecord rec = lr.getLoadRecord(dstConf);
			
			if (rec != null) {
				allOfDst.add(rec);
			}
		}
		
		return allOfDst;
	}
	
	/**
	 * Finds all LoadRecord records which as same {@link DstConf} from the #loadRecordHelper
	 */
	private List<EtlDatabaseObject> getAllRecordsAsEtlDstDatabaseObject(DstConf dstConf) {
		List<EtlDatabaseObject> allOfDst = new ArrayList<>();
		
		for (EtlLoadHelperRecord lr : getLoadRecordHelper()) {
			LoadRecord rec = lr.getLoadRecord(dstConf);
			
			if (rec != null) {
				allOfDst.add(rec.getDstRecord());
			}
		}
		
		return allOfDst;
	}
	
	public void load(Connection srcConn, Connection dstConn) throws ParentNotYetMigratedException, DBException {
		
		for (DstConf dst : this.getDstConf()) {
			load(dst, srcConn, dstConn);
			
			if (hasUnresolvedError(dst)) {
				logError("Found issues loading to " + dst);
				logError("Aborting operation");
				
				return;
			}
		}
		
		if (getEtlOperationConfig().writeOperationHistory()) {
			EtlStageAreaObjectDAO.saveAll(getAllSuccessifulProcessedAsEtlStageAreaObject(srcConn, dstConn), srcConn);
		}
		
		if (getEtlOperationConfig().getAfterEtlActionType().isDelete()) {
			for (EtlLoadHelperRecord obj : getAllSuccessfullyProcessedRecords()) {
				DatabaseObjectDAO.remove(obj.getSrcObject(), srcConn);
			}
		}
	}
	
	private boolean hasUnresolvedError(DstConf dst) {
		for (LoadRecord lr : this.getAllRecordsAsLoadRecord(dst)) {
			
			if (lr.isInFailStatus()) {
				return true;
			}
		}
		
		return false;
	}
	
	private void load(DstConf dstConf, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		EtlDstType dstType = getProcessor().determineDstType(dstConf);
		
		if (dstType.isDb()) {
			loadToDb(dstConf, srcConn, dstConn);
		} else if (dstType.isFile()) {
			loadToFile(dstConf);
		} else if (dstType.isInstantaneo()) {
			getEngine().requestDisplayOfEtlResult(dstConf, getAllRecordsAsEtlDstDatabaseObject(dstConf));
		} else {
			throw new ForbiddenOperationException("Unsupported dstType '" + dstType + "'");
		}
	}
	
	private void loadToDb(DstConf dstConf, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		if (!dstConf.isFullLoaded()) {
			dstConf.fullLoad();
		}
		
		this.beforeLoadToDb(dstConf, srcConn, dstConn);
		
		this.onLoadToDb(dstConf, dstConn);
		
		this.afterLoadToDb(dstConf, srcConn, dstConn);
		
	}
	
	/**
	 * @param srcConn
	 * @param dstConn
	 * @param config
	 * @param processedRecords
	 * @throws ParentNotYetMigratedException
	 * @throws DBException
	 */
	public void afterLoadToDb(DstConf dstConf, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		if (getActionType().isCreate() || getActionType().isUpdate()) {
			
			for (LoadRecord loadRec : this.getReadyRecordsAsLoadRecord(dstConf)) {
				if (loadRec.hasParentsWithDefaultValues()) {
					this.tryToReloadDefaultParents(loadRec, srcConn, dstConn);
				} else {
					loadRec.setStatus(LoadStatus.SUCCESS);
				}
			}
		}
	}
	
	void tryToAddToResult(EtlOperationItemResult<EtlDatabaseObject> resultItem) {
		if (isPrincipalLoading()) {
			getProcessor().getTaskResultInfo().addOrUpdate(resultItem);
		}
	}
	
	void tryToAddToResult(EtlOperationResultHeader<EtlDatabaseObject> result) {
		if (isPrincipalLoading()) {
			getProcessor().getTaskResultInfo().addAllFromOtherResult_(result);
		}
	}
	
	/**
	 * @param srcConn
	 * @param dstConn
	 * @param loadRec
	 * @throws ParentNotYetMigratedException
	 * @throws DBException
	 */
	public void tryToReloadDefaultParents(LoadRecord loadRec, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		logTrace("Reloading parents for dstRecord " + loadRec.getDstRecord());
		
		loadRec.reloadParentsWithDefaultValues(srcConn, dstConn);
		
		if (loadRec.getResultItem().hasUnresolvedInconsistences()) {
			getProcessor().logDebug(
			    "The dstRecord has inconsistence after reloading of default parent.  Removing it " + loadRec.getDstRecord());
			loadRec.getDstRecord().remove(dstConn);
			
			loadRec.setStatus(LoadStatus.FAIL);
			
			this.tryToAddToResult(loadRec.getResultItem());
			
		} else {
			try {
				loadRec.getDstRecord().update(loadRec.getDstConf(), dstConn);
			}
			catch (DBException e) {
				throw new DBException("Error reloading parents on transformation: " + loadRec, e);
			}
			
			loadRec.setStatus(LoadStatus.SUCCESS);
		}
	}
	
	/**
	 * @param dstConn
	 * @param config
	 * @param objects
	 * @throws DBException
	 * @throws ForbiddenOperationException
	 */
	public void onLoadToDb(DstConf dstConf, Connection dstConn) throws DBException, ForbiddenOperationException {
		
		List<EtlDatabaseObject> objects = getReadyOBjectsAsEtlDatabaseObject(dstConf);
		
		if (getActionType().isCreate()) {
			logDebug("Starting the insertion of " + objects.size() + " on db...");
			
			tryToAddToResult(DatabaseObjectDAO.load(objects, dstConf, dstConn));
			
			logDebug(objects.size() + " records inserted on db!");
		} else if (getActionType().isUpdate()) {
			logDebug("Starting the upodate of " + objects.size() + " on db...");
			
			tryToAddToResult(DatabaseObjectDAO.updateAll(objects, dstConf, dstConn));
			logDebug(objects.size() + " records updated from db!");
			
		} else if (getActionType().isDelete()) {
			logDebug("Starting the deletion of " + objects.size() + " on db...");
			
			tryToAddToResult(DatabaseObjectDAO.deleteAll(objects, dstConf, dstConn));
			
			logDebug(objects.size() + " records deleted on db!");
			
		} else {
			throw new ForbiddenOperationException("Unsupported operation " + getActionType() + " on ETL");
		}
	}
	
	private List<EtlDatabaseObject> getReadyOBjectsAsEtlDatabaseObject(DstConf dstConf) {
		return LoadRecord.parseToEtlObject(getReadyRecordsAsLoadRecord(dstConf));
	}
	
	private List<LoadRecord> getReadyRecordsAsLoadRecord(DstConf dstConf) {
		List<LoadRecord> ready = new ArrayList<>();
		
		for (LoadRecord rec : getAllRecordsAsLoadRecord(dstConf)) {
			if (rec.getStatus().isReady()) {
				ready.add(rec);
			}
		}
		return ready;
	}
	
	/**
	 * @param srcConn
	 * @param dstConn
	 * @param objects
	 * @param processedRecords
	 * @throws DBException
	 * @throws ParentNotYetMigratedException
	 * @throws MissingParentException
	 */
	public void beforeLoadToDb(DstConf dstConf, Connection srcConn, Connection dstConn)
	        throws DBException, ParentNotYetMigratedException, MissingParentException {
		
		this.logDebug("Preparing the load of " + this.qtyRecordsToLoad());
		
		if (dstConf.useSharedPKKey()) {
			this.logDebug("Trying to do the shared pk loading...");
			
			List<EtlLoadHelperRecord> parentToLoad = new ArrayList<>();
			
			DstConf sharedDstConf = null;
			
			for (LoadRecord loadRecord : this.getAllRecordsAsLoadRecord(dstConf)) {
				if (!loadRecord.getDstRecord().getSharedPkObj().checkIfExistsOnDstDb(dstConn)) {
					sharedDstConf = (DstConf) loadRecord.getDstRecord().getSharedPkObj().getRelatedConfiguration();
					SrcConf sharedSrcConf = sharedDstConf.getSrcConf();
					
					EtlDatabaseObject sharedDstObj = loadRecord.getDstRecord().getSharedPkObj();
					EtlDatabaseObject sharedSrcObj = sharedDstObj.getSrcRelatedObject();
					
					parentToLoad.add(new EtlLoadHelperRecord(
					        new LoadRecord(sharedSrcObj, sharedDstObj, sharedSrcConf, sharedDstConf, getProcessor())));
				}
			}
			
			if (utilities.arrayHasElement(parentToLoad)) {
				this.logDebug("Found " + parentToLoad.size() + " shared pk that are not present on DB... Loding them first");
				
				EtlLoadHelper helper = new EtlLoadHelper(getProcessor(), parentToLoad, utilities.parseToList(sharedDstConf),
				        LoadingType.INNER);
				
				helper.load(srcConn, dstConn);
			}
			
		}
		
		for (LoadRecord loadRecord : this.getAllRecordsAsLoadRecord(dstConf)) {
			this.logTrace("Preparing the load of dstRecord " + loadRecord.getDstRecord());
			
			boolean recursiveKeys = this.getProcessor().isRunningInConcurrency()
			        ? loadRecord.hasUnresolvedRecursiveRelationship(srcConn, dstConn)
			        : false;
			
			if (recursiveKeys) {
				this.logDebug("Record " + loadRecord.getDstRecord()
				        + " has recursive relationship and will be skipped to avoid dedlocks!");
				
				tryToAddToResult(
				    EtlOperationItemResult.fastCreateRecordWithRecursiveRelationship(loadRecord.getDstRecord()));
				
				loadRecord.getDstConf().saveSkippedRecord(loadRecord.getDstRecord(), srcConn);
				
				loadRecord.setStatus(LoadStatus.SKIP);
			} else {
				if (getActionType().isCreate() || getActionType().isUpdate()) {
					
					loadRecord.loadDstParentInfo(srcConn, dstConn);
					
					if (!loadRecord.getResultItem().hasUnresolvedInconsistences()) {
						loadRecord.setStatus(LoadStatus.READY);
						
						if (loadRecord.getResultItem().hasInconsistences()) {
							this.logTrace("Found inconsistences on dstRecord " + loadRecord.getDstRecord()
							        + " but all were resolved!");
						}
					} else {
						loadRecord.setStatus(LoadStatus.FAIL);
					}
				} else {
					loadRecord.setStatus(LoadStatus.READY);
				}
				
				tryToAddToResult(loadRecord.getResultItem());
				
			}
		}
		
	}
	
	/**
	 * @return
	 */
	public EtlActionType getActionType() {
		return getEtlOperationConfig().getActionType();
	}
	
	/**
	 * @return
	 */
	public int qtyRecordsToLoad() {
		return this.getLoadRecordHelper().size();
	}
	
	public void loadToFile(DstConf dstConf) throws ParentNotYetMigratedException, DBException {
		this.logDebug("Preparing the load of " + this.qtyRecordsToLoad());
		
		for (LoadRecord loadRecord : this.getAllRecordsAsLoadRecord(dstConf)) {
			loadRecord.setStatus(LoadStatus.READY);
		}
		
		List<EtlDatabaseObject> objs = getReadyOBjectsAsEtlDatabaseObject(dstConf);
		
		String dataFile = getEngine().getDataDir().getAbsolutePath() + File.separator + objs.get(0).generateTableName();
		
		String data = null;
		
		if (getEngine().isJsonDst()) {
			data = utilities.parseToJSON(objs);
			
			dataFile += ".json";
		} else if (getEngine().isCsvDst()) {
			dataFile += ".csv";
			
			data = utilities.parseToCSVWithoutHeader(objs);
		} else if (getEngine().isDumpDst()) {
			dataFile += ".sql";
			
			data = TableConfiguration.generateInsertDump(objs);
		}
		
		synchronized (getEngine()) {
			boolean includeHeader = FileUtilities.isEmpty(new File(dataFile));
			
			if (includeHeader) {
				FileUtilities.write(dataFile, utilities.generateCsvHeader(objs.get(0)));
			}
			
			FileUtilities.write(dataFile, data);
		}
		
		getProcessor().getTaskResultInfo()
		        .addAllToRecordsWithNoError(EtlOperationItemResult.parseFromEtlDatabaseObject(objs));
	}
	
	void logTrace(String msg) {
		getProcessor().logTrace(msg);
	}
	
	void logDebug(String msg) {
		getProcessor().logDebug(msg);
	}
	
	void logInfo(String msg) {
		getProcessor().logInfo(msg);
	}
	
	void logWarn(String msg) {
		getProcessor().logWarn(msg);
	}
	
	void logError(String msg) {
		getProcessor().logError(msg);
	}
	
	public static void performeParentLoading(LoadRecord loadRecord, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		new EtlLoadHelper(loadRecord.getProcessor(), loadRecord, utilities.parseToList(loadRecord.getDstConf()),
		        LoadingType.INNER).load(loadRecord.getDstConf(), srcConn, dstConn);
	}
	
	public List<EtlDatabaseObject> getAllSuccessfullyProcessedRecordsAsEtlObject() {
		List<EtlDatabaseObject> sucess = new ArrayList<>(qtyRecordsToLoad());
		
		for (EtlLoadHelperRecord lr : getAllSuccessfullyProcessedRecords()) {
			sucess.add(lr.getSrcObject());
		}
		
		return sucess;
	}
	
	private List<EtlLoadHelperRecord> getAllSuccessfullyProcessedRecords() {
		List<EtlLoadHelperRecord> sucess = new ArrayList<>(qtyRecordsToLoad());
		
		for (EtlLoadHelperRecord lr : getLoadRecordHelper()) {
			if (lr.determineGlobalStatus().isSuccess()) {
				sucess.add(lr);
			}
		}
		
		return sucess;
	}
}
