package org.openmrs.module.eptssync.consolitation.model;

import java.sql.Connection;

import org.openmrs.module.eptssync.controller.conf.SyncTableConfiguration;
import org.openmrs.module.eptssync.engine.RecordLimits;
import org.openmrs.module.eptssync.engine.SyncSearchParams;
import org.openmrs.module.eptssync.model.SearchClauses;
import org.openmrs.module.eptssync.model.SearchParamsDAO;
import org.openmrs.module.eptssync.model.openmrs.generic.OpenMRSObject;
import org.openmrs.module.eptssync.utilities.db.conn.DBException;

public class DatabaseIntegrityConsolidationSearchParams extends SyncSearchParams<OpenMRSObject>{
	private boolean selectAllRecords;
	private String appOriginLocationCode;
	
	public DatabaseIntegrityConsolidationSearchParams(SyncTableConfiguration tableInfo, RecordLimits limits, String appOriginLocationCode, Connection conn) {
		super(tableInfo, limits);
		
		this.appOriginLocationCode = appOriginLocationCode;
		
		setOrderByFields(tableInfo.getPrimaryKey(conn));
	}
	
	@Override
	public SearchClauses<OpenMRSObject> generateSearchClauses(Connection conn) throws DBException {
		SearchClauses<OpenMRSObject> searchClauses = new SearchClauses<OpenMRSObject>(this);
		
		searchClauses.addColumnToSelect("*");
		searchClauses.addToClauseFrom(tableInfo.getTableName());
	
		//Consolidate only records from sync
		searchClauses.addToClauses("origin_app_location_code is not null");
		
		if (!this.selectAllRecords) {
			searchClauses.addToClauses("consistent = -1");
		
			if (limits != null) {
				searchClauses.addToClauses(tableInfo.getPrimaryKey(conn) + " between ? and ?");
				searchClauses.addToParameters(this.limits.getFirstRecordId());
				searchClauses.addToParameters(this.limits.getLastRecordId());
			}
		
			if (this.tableInfo.getExtraConditionForExport() != null) {
				searchClauses.addToClauses(tableInfo.getExtraConditionForExport());
			}
		}
		
		searchClauses.addToClauses("origin_app_location_code = ?");
		searchClauses.addToParameters(this.appOriginLocationCode);
	
		return searchClauses;
	}	
	
	@Override
	public Class<OpenMRSObject> getRecordClass() {
		return this.tableInfo.getRecordClass();
	}

	@Override
	public int countAllRecords(Connection conn) throws DBException {
		DatabaseIntegrityConsolidationSearchParams auxSearchParams = new DatabaseIntegrityConsolidationSearchParams(this.tableInfo, this.limits, this.appOriginLocationCode, conn);
		auxSearchParams.selectAllRecords = true;
		
		return SearchParamsDAO.countAll(auxSearchParams, conn);
	}

	@Override
	public synchronized int countNotProcessedRecords(Connection conn) throws DBException {
		RecordLimits bkpLimits = this.limits;
		
		this.limits = null;
		
		int count = SearchParamsDAO.countAll(this, conn);
		
		this.limits = bkpLimits;
		
		return count;
	}
}
