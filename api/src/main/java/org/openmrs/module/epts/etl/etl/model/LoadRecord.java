package org.openmrs.module.epts.etl.etl.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.common.model.SyncImportInfoVO;
import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.AppInfo;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.SrcConf;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.dbquickmerge.model.ParentInfo;
import org.openmrs.module.epts.etl.etl.engine.EtlEngine;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParentException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.AbstractDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseOperationHeaderResult;
import org.openmrs.module.epts.etl.model.pojo.generic.Oid;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class LoadRecord {
	
	protected static CommonUtilities utilities = CommonUtilities.getInstance();
	
	protected EtlDatabaseObject record;
	
	protected DstConf dstConf;
	
	protected List<ParentInfo> parentsWithDefaultValues;
	
	protected boolean writeOperationHistory;
	
	protected long destinationRecordId;
	
	protected EtlEngine engine;
	
	private SrcConf srcConf;
	
	public LoadRecord(EtlDatabaseObject record, SrcConf srcConf, DstConf dstConf, EtlEngine engine,
	    boolean writeOperationHistory) {
		this.record = record;
		this.srcConf = srcConf;
		this.dstConf = dstConf;
		
		this.engine = engine;
		
		this.writeOperationHistory = writeOperationHistory;
		
		this.parentsWithDefaultValues = new ArrayList<ParentInfo>();
		
		this.record.setUniqueKeysInfo(UniqueKeyInfo.cloneAllAndLoadValues(this.dstConf.getUniqueKeys(), this.record));
	}
	
	public boolean isWriteOperationHistory() {
		return writeOperationHistory;
	}
	
	public EtlEngine getEngine() {
		return engine;
	}
	
	public AppInfo getSrcApp() {
		return getEngine().getSrcApp();
	}
	
	public AppInfo getDstApp() {
		return getEngine().getDstApp();
	}
	
	public SrcConf getSrcConf() {
		return srcConf;
	}
	
	public EtlConfiguration getEtlConfiguration() {
		return getDstConf().getRelatedSyncConfiguration();
	}
	
	public EtlDatabaseObject getRecord() {
		return record;
	}
	
	public DstConf getDstConf() {
		return dstConf;
	}
	
	public void setDestinationRecordId(long destinationRecordId) {
		this.destinationRecordId = destinationRecordId;
	}
	
	public long getDestinationRecordId() {
		return destinationRecordId;
	}
	
	public List<ParentInfo> getParentsWithDefaultValues() {
		return parentsWithDefaultValues;
	}
	
	public boolean hasParentsWithDefaultValues() {
		return utilities.arrayHasElement(getParentsWithDefaultValues());
	}
	
	public void consolidateAndSaveData(boolean create, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		if (!getDstConf().isFullLoaded()) {
			getDstConf().fullLoad();
		}
		
		loadDstParentInfo(srcConn, dstConn);
		
		try {
			
			if (create) {
				getRecord().save(getDstConf(), dstConn);
			} else {
				getRecord().update(getDstConf(), dstConn);
			}
			
			if (getDstConf().useSimpleNumericPk()) {
				this.setDestinationRecordId(getRecord().getObjectId().getSimpleValueAsInt());
			}
		}
		catch (DBException e) {
			if (e.isDuplicatePrimaryOrUniqueKeyException()) {
				
				boolean existWinningRecInfo = utilities.arrayHasElement(getDstConf().getWinningRecordFieldsInfo());
				boolean existObservationDateFields = utilities.arrayHasElement(getDstConf().getObservationDateFields());
				
				if (existObservationDateFields || existWinningRecInfo) {
					List<EtlDatabaseObject> recs = DatabaseObjectDAO.getByUniqueKeys(this.getDstConf(), this.getRecord(),
					    dstConn);
					
					EtlDatabaseObject recordOnDB = utilities.arrayHasElement(recs) ? recs.get(0) : null;
					
					((AbstractDatabaseObject) getRecord()).resolveConflictWithExistingRecord(recordOnDB, this.getDstConf(),
					    dstConn);
					
					if (getDstConf().useSimpleNumericPk()) {
						this.setDestinationRecordId(getRecord().getObjectId().getSimpleValueAsInt());
					}
				}
				
			} else if (e.isIntegrityConstraintViolationException()) {
				determineMissingMetadataParent(this, srcConn, dstConn);
				
				//If there is no missing metadata parent, throw exception
				throw e;
			} else
				throw e;
		}
		
		if (this.hasParentsWithDefaultValues()) {
			reloadParentsWithDefaultValues(srcConn, dstConn);
		}
	}
	
	public void resolveConflict(Connection srcConn, Connection dstConn) throws ParentNotYetMigratedException, DBException {
		if (!getDstConf().isFullLoaded())
			dstConf.fullLoad();
		
		loadDstParentInfo(srcConn, dstConn);
		
		List<EtlDatabaseObject> recs = DatabaseObjectDAO.getByUniqueKeys(this.getDstConf(), this.getRecord(), dstConn);
		
		EtlDatabaseObject recordOnDB = utilities.arrayHasElement(recs) ? recs.get(0) : null;
		
		((AbstractDatabaseObject) record).resolveConflictWithExistingRecord(recordOnDB, this.getDstConf(), dstConn);
		
		if (!this.getParentsWithDefaultValues().isEmpty()) {
			reloadParentsWithDefaultValues(srcConn, dstConn);
		}
	}
	
	public void reloadParentsWithDefaultValues(Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		for (ParentInfo parentInfo : this.getParentsWithDefaultValues()) {
			
			List<SrcConf> avaliableSrcForCurrParent = parentInfo.getParentTableConfInDst()
			        .findRelatedSrcConfWhichAsAtLeastOnematchingDst();
			
			if (utilities.arrayHasNoElement(avaliableSrcForCurrParent)) {
				throw new ForbiddenOperationException(
				        "There are relashioship which cannot auto resolved as there is no configured etl for "
				                + parentInfo.getParentTableConfInDst().getTableName() + " as source and destination!");
			}
			
			EtlDatabaseObject dstParent = null;
			DstConf dstConf = null;
			
			for (SrcConf src : avaliableSrcForCurrParent) {
				DstConf dst = src.getParentConf().findDstTable(parentInfo.getParentTableConfInDst().getTableName());
				
				EtlDatabaseObject recordAsSrc = src.createRecordInstance();
				recordAsSrc.setRelatedConfiguration(src);
				
				recordAsSrc.copyFrom(parentInfo.getParentRecordInOrigin());
				
				dstParent = dst.transform(recordAsSrc, srcConn, getSrcApp(), getDstApp());
				
				if (dstParent != null) {
					LoadRecord parentData = new LoadRecord(dstParent,
					        parentInfo.getParentTableConfInDst().findRelatedSrcConf(), dst, getEngine(),
					        this.isWriteOperationHistory());
					
					parentData.load(srcConn, dstConn);
					
					dstConf = dst;
					
					break;
				}
			}
			
			List<EtlDatabaseObject> recs = DatabaseObjectDAO.getByUniqueKeys(dstConf, dstParent, dstConn);
			
			EtlDatabaseObject parent = utilities.arrayHasElement(recs) ? recs.get(0) : null;
			
			getRecord().changeParentValue(parentInfo.getParentTableConfInDst(), parent);
		}
	}
	
	protected void loadDstParentInfo(Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, MissingParentException, DBException {
		
		if (!utilities.arrayHasElement(getDstConf().getParentRefInfo()))
			return;
		
		for (ParentTable refInfo : getDstConf().getParentRefInfo()) {
			if (refInfo.isMetadata())
				continue;
			
			if (!refInfo.isFullLoaded()) {
				refInfo.tryToGenerateTableAlias(this.getEtlConfiguration());
				
				refInfo.fullLoad(dstConn);
			}
			
			if (!getRecord().hasAllPerentFieldsFilled(refInfo)) {
				continue;
			}
			
			if (refInfo.useSharedPKKey()) {
				if (!refInfo.getSharedKeyRefInfo().isFullLoaded()) {
					refInfo.getSharedKeyRefInfo().tryToGenerateTableAlias(this.getEtlConfiguration());
					
					refInfo.getSharedKeyRefInfo().fullLoad(dstConn);
				}
			}
			
			if (refInfo.hasConditionalFields()) {
				if (refInfo.hasMoreThanOneConditionalFields()) {
					throw new ForbiddenOperationException("Currently not supported multiple conditional fields");
				}
				
				String conditionalFieldName = refInfo.getConditionalFields().get(0).getName();
				Object conditionalvalue = refInfo.getConditionalFields().get(0).getValue();
				
				if (!conditionalvalue.equals(getRecord().getFieldValue(conditionalFieldName).toString()))
					continue;
			}
			
			Oid key = refInfo.generateParentOidFromChild(getRecord());
			
			TableConfiguration tabConfInSrc = this.getSrcConf().findFullConfiguredConfInAllRelatedTable(
			    refInfo.generateFullTableNameOnSchema(getSrcConf().getSchema()));
			
			if (tabConfInSrc == null) {
				tabConfInSrc = new GenericTableConfiguration(this.getSrcConf());
				tabConfInSrc.setTableName(refInfo.getTableName());
				tabConfInSrc.setRelatedSyncConfiguration(this.getEtlConfiguration());
				tabConfInSrc.fullLoad(srcConn);
			}
			
			EtlDatabaseObject parentInOrigin = DatabaseObjectDAO.getByOid(tabConfInSrc, key, srcConn);
			
			if (parentInOrigin == null) {
				throw new MissingParentException(getRecord(), key, refInfo.getTableName(),
				        this.getDstConf().getOriginAppLocationCode(), refInfo, null);
			}
			
			EtlDatabaseObject parent;
			
			if (getEngine().getRelatedEtlConfiguration().isDoNotTransformsPrimaryKeys()) {
				parent = retrieveParentByOid(refInfo, parentInOrigin, dstConn);
			} else {
				parent = retrieveParentByUnikeKeys(refInfo, parentInOrigin, dstConn);
			}
			
			if (parent == null) {
				parent = refInfo.getDefaultObject(dstConn);
				
				if (parent == null) {
					parent = getDstConf().generateAndSaveDefaultObject(dstConn);
				}
				this.getParentsWithDefaultValues().add(new ParentInfo(refInfo, parentInOrigin));
			}
			
			getRecord().changeParentValue(refInfo, parent);
			
		}
	}
	
	private EtlDatabaseObject retrieveParentByOid(ParentTable refInfo, EtlDatabaseObject parentInOrigin, Connection dstConn)
	        throws DBException {
		
		return DatabaseObjectDAO.getByOid(refInfo, parentInOrigin.getObjectId(), dstConn);
	}
	
	private EtlDatabaseObject retrieveParentByUnikeKeys(ParentTable refInfo, EtlDatabaseObject parentInOrigin,
	        Connection dstConn) throws DBException {
		
		TableConfiguration parentTabConf = refInfo;
		
		List<EtlDatabaseObject> recs = DatabaseObjectDAO.getByUniqueKeys(parentTabConf, parentInOrigin, dstConn);
		
		return utilities.arrayHasElement(recs) ? recs.get(0) : null;
	}
	
	/**
	 * @param loadRecord
	 * @param srcConn
	 * @param destConn
	 * @throws DBException
	 * @throws ParentNotYetMigratedException
	 * @throws SQLException
	 */
	public static void determineMissingMetadataParent(LoadRecord loadRecord, Connection srcConn, Connection destConn)
	        throws MissingParentException, DBException {
		TableConfiguration dstConf = loadRecord.getDstConf();
		
		EtlDatabaseObject record = loadRecord.getRecord();
		
		for (ParentTable refInfo : dstConf.getParentRefInfo()) {
			if (!refInfo.isMetadata())
				continue;
			
			Object oParentId = record.getParentValue(refInfo);
			
			if (oParentId != null) {
				Integer parentId = (Integer) oParentId;
				
				EtlDatabaseObject parent = DatabaseObjectDAO.getByOid(refInfo,
				    Oid.fastCreate(refInfo.getParentColumnOnSimpleMapping(), parentId), destConn);
				
				if (parent == null)
					throw new MissingParentException(record, parentId, refInfo.getTableName(),
					        loadRecord.getDstConf().getOriginAppLocationCode(), refInfo, null);
			}
		}
	}
	
	public void save(Connection conn) throws DBException {
		SyncImportInfoVO syncInfo = SyncImportInfoVO.generateFromSyncRecord(getRecord(),
		    getDstConf().getOriginAppLocationCode(), false);
		
		syncInfo.setDestinationId((int) this.destinationRecordId);
		
		syncInfo.save(getDstConf(), conn);
	}
	
	public void load(Connection srcConn, Connection destConn) throws DBException {
		consolidateAndSaveData(true, srcConn, destConn);
		
		if (writeOperationHistory) {
			save(srcConn);
		}
	}
	
	public void reLoad(Connection srcConn, Connection destConn) throws ParentNotYetMigratedException, DBException {
		consolidateAndSaveData(false, srcConn, destConn);
	}
	
	public static DatabaseOperationHeaderResult loadAll(List<LoadRecord> mergingRecs, Connection srcConn, Connection dstConn)
	        throws ParentNotYetMigratedException, DBException {
		
		AbstractTableConfiguration config = mergingRecs.get(0).dstConf;
		
		if (!config.isFullLoaded()) {
			config.fullLoad();
		}
		
		List<EtlDatabaseObject> objects = new ArrayList<EtlDatabaseObject>(mergingRecs.size());
		
		DatabaseOperationHeaderResult currResult = new DatabaseOperationHeaderResult();
		
		for (LoadRecord loadRecord : mergingRecs) {
			try {
				loadRecord.loadDstParentInfo(srcConn, dstConn);
				
				objects.add(loadRecord.getRecord());
				
			}
			catch (DBException e) {
				currResult.addToRecordsWithUnresolvedErrors(loadRecord.getRecord(), e);
			}
			
		}
		
		currResult.addAllFromOtherResult(DatabaseObjectDAO.insertAll(objects, config, config.getOriginAppLocationCode(), dstConn));
		
		if (config.hasParentRefInfo()) {
			
			for (LoadRecord r : mergingRecs) {
				if (r.hasParentsWithDefaultValues()) {
					r.reloadParentsWithDefaultValues(srcConn, dstConn);
					
					r.getRecord().update(r.getDstConf(), dstConn);
				}
			}
		}

		return currResult;
	}
	
	public static DatabaseOperationHeaderResult loadAll(Map<String, List<LoadRecord>> mergingRecs, Connection srcConn,
	        OpenConnection dstConn) throws ParentNotYetMigratedException, DBException {
		
		DatabaseOperationHeaderResult result = new DatabaseOperationHeaderResult();
		
		for (String key : mergingRecs.keySet()) {
			DatabaseOperationHeaderResult currresult = loadAll(mergingRecs.get(key), srcConn, dstConn);
			
			result.addAllFromOtherResult(currresult);
		}
		
		return result;
	}
}