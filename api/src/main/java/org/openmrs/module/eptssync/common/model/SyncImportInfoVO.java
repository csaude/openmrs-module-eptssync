package org.openmrs.module.eptssync.common.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.eptssync.controller.conf.SyncOperationType;
import org.openmrs.module.eptssync.controller.conf.SyncTableConfiguration;
import org.openmrs.module.eptssync.exceptions.MetadataInconsistentException;
import org.openmrs.module.eptssync.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.eptssync.model.base.BaseVO;
import org.openmrs.module.eptssync.model.base.SyncRecord;
import org.openmrs.module.eptssync.model.pojo.generic.OpenMRSObject;
import org.openmrs.module.eptssync.model.pojo.generic.OpenMRSObjectDAO;
import org.openmrs.module.eptssync.utilities.CommonUtilities;
import org.openmrs.module.eptssync.utilities.DateAndTimeUtilities;
import org.openmrs.module.eptssync.utilities.concurrent.TimeCountDown;
import org.openmrs.module.eptssync.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represent the information to be imported from the source to destination data base.
 * This information are first saved on sync stage area
 * 
 * @author jpboane
 *
 */
public class SyncImportInfoVO extends BaseVO implements SyncRecord{
	private static CommonUtilities utilities = CommonUtilities.getInstance();
	
	public static final int MIGRATION_STATUS_PENDING = 1;
	public static final int MIGRATION_STATUS_INCOMPLETE = 0;
	public static final int MIGRATION_STATUS_FAILED = -1;
	
	private int id;
	private int recordOriginId;
	private String recordUuid;
	private String recordOriginLocationCode;
	
	private String json;
	
	private Date lastSyncDate;
	private String lastSyncTryErr;
		
	private Date lastUpdateDate;
	
	private int consistent;
	private int migrationStatus;
	
	public SyncImportInfoVO(){
		this.migrationStatus = MIGRATION_STATUS_PENDING;
	}
	
	@Override
	public void load(ResultSet resultSet) throws SQLException {
		super.load(resultSet);
		
		try {this.dateCreated = resultSet.getDate("record_date_created");} catch (SQLException e) {}
		try {this.dateChanged = resultSet.getDate("record_date_changed");} catch (SQLException e) {}
		try {this.dateVoided = resultSet.getDate("record_date_voided");} catch (SQLException e) {}
	}
	
	@JsonIgnore
	public int getConsistent() {
		return consistent;
	}
	
	public int getRecordOriginId() {
		return recordOriginId;
	}

	public void setRecordOriginId(int recordOriginId) {
		this.recordOriginId = recordOriginId;
	}
	
	public String getRecordUuid() {
		return recordUuid;
	}

	public void setRecordUuid(String recordUuid) {
		this.recordUuid = recordUuid;
	}

	public String getRecordOriginLocationCode() {
		return recordOriginLocationCode;
	}

	public void setRecordOriginLocationCode(String recordOriginLocationCode) {
		this.recordOriginLocationCode = recordOriginLocationCode;
	}

	@JsonIgnore
	public int isConsistent() {
		return consistent;
	}

	public void setConsistent(int consistent) {
		this.consistent = consistent;
	}

	@JsonIgnore
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@JsonIgnore
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@JsonIgnore
	public Date getLastSyncDate() {
		return lastSyncDate;
	}

	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	
	@JsonIgnore
	public String getLastSyncTryErr() {
		return lastSyncTryErr;
	}

	public void setLastSyncTryErr(String lastSyncTryErr) {
		this.lastSyncTryErr = lastSyncTryErr;
	}

	public void setMigrationStatus(int migrationStatus) {
		this.migrationStatus = migrationStatus;
	}
	
	public void markAsPartialMigrated() {
		this.migrationStatus = MIGRATION_STATUS_INCOMPLETE;
	}

	public void markAsConsistent(SyncTableConfiguration tableInfo, Connection conn) throws DBException {
		SyncImportInfoDAO.markAsConsistent(tableInfo, this, conn);
	}
	
	public void markAsInconsistent(SyncTableConfiguration tableInfo, Connection conn) throws DBException {
		SyncImportInfoDAO.markAsInconsistent(tableInfo, this, conn);
	}
	
	public void markAsPartialMigrated(SyncTableConfiguration tableInfo, String errMsg, Connection conn) throws DBException {
		this.migrationStatus = MIGRATION_STATUS_INCOMPLETE;
		this.lastSyncTryErr = errMsg;
		
		SyncImportInfoDAO.updateMigrationStatus(tableInfo, this, conn);
	}

	public void markAsSyncFailedToMigrate(SyncTableConfiguration tableInfo, String errMsg, Connection conn) throws DBException {
		this.migrationStatus = MIGRATION_STATUS_FAILED;
		this.lastSyncTryErr = errMsg;
		
		SyncImportInfoDAO.updateMigrationStatus(tableInfo, this, conn);
	}

	public void markAsSyncFailedToMigrate() {
		this.migrationStatus = MIGRATION_STATUS_FAILED;
	}

	@JsonIgnore
	public int getMigrationStatus() {
		return migrationStatus;
	}
	
	public static List<SyncImportInfoVO> generateFromSyncRecord(List<OpenMRSObject> syncRecords, String recordOriginLocationCode, boolean generateRecordJSON) throws DBException {
		List<SyncImportInfoVO> importInfo = new ArrayList<SyncImportInfoVO>();
	
		for (OpenMRSObject syncRecord : syncRecords) {
			importInfo.add(generateFromSyncRecord(syncRecord, recordOriginLocationCode, generateRecordJSON));
		}
		
		return importInfo;
	}
	
	public static SyncImportInfoVO generateFromSyncRecord(OpenMRSObject syncRecord, String recordOriginLocationCode, boolean generateRecordJSON) throws DBException {
		SyncImportInfoVO syncInfo = new SyncImportInfoVO();
			
		syncInfo.setRecordUuid(syncRecord.getUuid());
		syncInfo.setRecordOriginId(syncRecord.getObjectId());
		syncInfo.setRecordOriginLocationCode (recordOriginLocationCode);
		
		syncInfo.setDateChanged(syncRecord.getDateChanged());
		syncInfo.setDateCreated(syncRecord.getDateCreated());
		syncInfo.setDateVoided(syncRecord.getDateVoided());
		syncInfo.setJson(generateRecordJSON ? utilities.parseToJSON(syncRecord) : null);
		syncInfo.setLastUpdateDate(syncRecord.getDateChanged());
		
		return syncInfo;
	}

	public static SyncImportInfoVO retrieveFromSyncRecord(SyncTableConfiguration tableConfiguration, OpenMRSObject syncRecord, String recordOriginLocationCode, Connection conn) throws DBException {
		SyncImportInfoVO syncInfo = SyncImportInfoDAO.retrieveFromOpenMRSObject(tableConfiguration, syncRecord, recordOriginLocationCode, conn);
		syncRecord.setRelatedSyncInfo(syncInfo);
		
		return syncInfo;
	}
	
	public void sync(SyncTableConfiguration tableInfo, Class<OpenMRSObject> objectClass, Connection conn) throws DBException {
		OpenMRSObject source = utilities.loadObjectFormJSON(objectClass, this.json);
		source.setRelatedSyncInfo(this);
		try {
				
			if (tableInfo.isDoIntegrityCheckInTheEnd(SyncOperationType.DB_MERGE_FROM_JSON)) {
				if (source.hasParents()) {
					this.markAsConsistent(tableInfo, conn);
				}
				
				if (tableInfo.useSharedPKKey()) {
					refrieveSharedPKKey(tableInfo, source, 0, conn);
				}
				else {
					source.setObjectId(0);
				}
				
				//Migrate now and ajust later
				SyncImportInfoDAO.refreshLastMigrationTrySyncDate(tableInfo, this, conn);
			}
			else {
				source.loadDestParentInfo(tableInfo, this.recordOriginLocationCode, conn);
				
				if (source.hasIgnoredParent()) {
					markAsToBeCompletedInFuture(tableInfo, conn);
				}
				else {
					markAsMigrated(tableInfo, conn);
				}
				
				if (!tableInfo.useSharedPKKey()) {
					source.setObjectId(0);
				}
			}
			
			source.save(tableInfo, conn);
		} catch (ParentNotYetMigratedException e) {
			markAsFailedToMigrate(tableInfo, e.getLocalizedMessage(), conn);
		} catch (MetadataInconsistentException e) {
			markAsFailedToMigrate(tableInfo, e.getLocalizedMessage(), conn);
		} 
		catch (Exception e) {
			markAsFailedToMigrate(tableInfo, e.getLocalizedMessage(), conn);
		}
	}

	/**
	 * 
	 * @param source
	 * @param qtyTry number of attempts before "give up" 
	 * @param conn
	 * @throws ParentNotYetMigratedException
	 * @throws DBException
	 */
	private void refrieveSharedPKKey(SyncTableConfiguration tableConfiguration, OpenMRSObject source, int qtyTry, Connection conn) throws ParentNotYetMigratedException, DBException {
		try {
			OpenMRSObject obj = OpenMRSObjectDAO.getByUuid(tableConfiguration.getSharedKeyRefInfo(conn).getRefObjectClass(tableConfiguration.getMainApp()), this.getRecordUuid(), conn);
			
			if (obj != null) {
				source.setObjectId(obj.getObjectId());
			}
			else throw new ParentNotYetMigratedException();
			
		} catch (ParentNotYetMigratedException e) {
			
			if (qtyTry > 0) {
				//Wait 10 seconds before try again
				TimeCountDown.sleep(1);
				refrieveSharedPKKey(tableConfiguration, source, --qtyTry, conn);
			}
			else throw e;
		}
	}
	
	public void markAsToBeCompletedInFuture(SyncTableConfiguration tableInfo, Connection conn) throws DBException {
		SyncImportInfoDAO.markAsToBeCompletedInFuture(this, tableInfo, conn);
	}

	public void markAsFailedToMigrate(SyncTableConfiguration tableInfo, String errMsg, Connection conn) throws DBException {
		SyncImportInfoDAO.markAsFailedToMigrate(this, tableInfo, errMsg, conn);
	}
	
	public void markAsMigrated(SyncTableConfiguration tableInfo, Connection conn) throws DBException {
		SyncImportInfoDAO.remove(this, tableInfo, conn);
	}
	
	public static List<OpenMRSObject> convertAllToOpenMRSObject(SyncTableConfiguration tableInfo, Class<OpenMRSObject> objectClass, List<SyncImportInfoVO> toParse, Connection conn) {
		List<OpenMRSObject> records = new ArrayList<OpenMRSObject>();
		
		for (SyncImportInfoVO imp : toParse) {
			String modifiedJSON = imp.getJson();
			
			//String modifiedJSON = imp.getJson().replaceFirst(imp.retrieveSourcePackageName(tableInfo), tableInfo.getClasspackage());
			
			OpenMRSObject rec = null;
			
			try {
				rec = utilities.loadObjectFormJSON(objectClass, modifiedJSON );
			} catch (Exception e) {
				
				//try to resolve pathern problems
				modifiedJSON = utilities.resolveScapeCharacter(modifiedJSON);
				rec = utilities.loadObjectFormJSON(objectClass, modifiedJSON);
			}
			
			if (!tableInfo.isMetadata()) {
				rec.setObjectId(0);
			}
			
			rec.setRelatedSyncInfo(imp);
			
			imp.setConsistent(OpenMRSObject.INCONSISTENCE_STATUS);
			records.add(rec);
		}
		
		return records;
	}
	
	public OpenMRSObject convertToOpenMRSObject(SyncTableConfiguration tableInfo,  Connection conn) {
		String modifiedJSON = this.getJson();
			
		OpenMRSObject rec = null;
			
		try {
			rec = utilities.loadObjectFormJSON(tableInfo.getSyncRecordClass(tableInfo.getMainApp()), modifiedJSON );
		} catch (Exception e) {
			
			//try to resolve pathern problems
			modifiedJSON = utilities.resolveScapeCharacter(modifiedJSON);
			rec = utilities.loadObjectFormJSON(tableInfo.getSyncRecordClass(tableInfo.getMainApp()), modifiedJSON);
		}
		
		if (!tableInfo.isMetadata()) {
			rec.setObjectId(0);
		}
			
		this.setConsistent(OpenMRSObject.INCONSISTENCE_STATUS);
		
		return rec;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		if (!(obj instanceof SyncImportInfoVO)) return false;
		
		SyncImportInfoVO otherObj = (SyncImportInfoVO)obj;
		
		return this.getRecordOriginLocationCode().equalsIgnoreCase(otherObj.getRecordOriginLocationCode()) && this.getRecordUuid().equalsIgnoreCase(otherObj.getRecordUuid());
	}

	public void delete(SyncTableConfiguration tableInfo, Connection conn) throws DBException {
		SyncImportInfoDAO.remove(this, tableInfo, conn);
	}

	public void save(SyncTableConfiguration tableConfiguration, Connection conn) throws DBException {
		if (getId() > 0) {
			SyncImportInfoDAO.update(this, tableConfiguration, conn);
		}
		else {
			SyncImportInfoDAO.insert(this, tableConfiguration, conn);
		}
	}

	@Override
	public String toString() {
		return "recordUuid: " + recordUuid + ", recordOriginId: " + recordOriginId + ", recordOriginLocationCode: " + recordOriginLocationCode;
	}
	
	public static SyncImportInfoVO chooseMostRecent(List<SyncImportInfoVO> records) {
		SyncImportInfoVO mostRecent = records.get(0);
		
		for (SyncImportInfoVO rec : records) {
			if (rec.getDateChanged() != null) {
				if (mostRecent.getDateChanged() == null) {
					mostRecent = rec;
				}
				else if (DateAndTimeUtilities.compareTo(rec.getDateChanged(), mostRecent.getDateChanged()) > 0) {
					mostRecent = rec;
				}
			}
			
			if (rec.getDateVoided() != null) {
				if (mostRecent.getDateVoided() == null) {
					mostRecent = rec;
				}
				else if (DateAndTimeUtilities.compareTo(rec.getDateVoided(), mostRecent.getDateVoided()) > 0) {
					mostRecent = rec;
				}
			}
		}
		
		return mostRecent;
	}
}