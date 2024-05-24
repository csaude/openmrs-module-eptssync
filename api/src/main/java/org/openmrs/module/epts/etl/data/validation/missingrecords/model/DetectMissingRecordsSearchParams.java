package org.openmrs.module.epts.etl.data.validation.missingrecords.model;

import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.data.validation.missingrecords.controller.DetectMissingRecordsController;
import org.openmrs.module.epts.etl.engine.RecordLimits;
import org.openmrs.module.epts.etl.etl.model.EtlSearchParams;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class DetectMissingRecordsSearchParams extends EtlSearchParams {
	
	DstConf relatedDstConf;
	
	public DetectMissingRecordsSearchParams(EtlItemConfiguration config, RecordLimits limits,
	    DetectMissingRecordsController relatedController) {
		super(config, limits, relatedController);
		
		this.excludedRecords = null;
		
		this.relatedDstConf = new DstConf();
		this.relatedDstConf.setParentConf(config);
		
		OpenConnection dstConn = null;
		
		try {
			dstConn = relatedController.openDstConnection();
			
			this.relatedDstConf.clone(getSrcConf(), dstConn);
			
			this.relatedDstConf.tryToGenerateTableAlias(config.getRelatedSyncConfiguration());
		}
		catch (DBException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (dstConn != null)
				dstConn.finalizeConnection();
		}
		
	}
	
	@Override
	public DetectMissingRecordsController getRelatedController() {
		return (DetectMissingRecordsController) super.getRelatedController();
	}
	
	@Override
	public SearchClauses<EtlDatabaseObject> generateSearchClauses(Connection conn) throws DBException {
		SearchClauses<EtlDatabaseObject> clauses = super.generateSearchClauses(conn);
		
		OpenConnection dstConn = getRelatedController().openDstConnection();
		
		try {
			//To avoid slowness, don't exclude if there is no limit
			if (hasLimits()) {
				String exclusionCondition = " NOT EXISTS (" + generateDestinationJoinSubquery(dstConn) + ")";
				clauses.addToClauses(exclusionCondition);
			}
		}
		finally {
			dstConn.finalizeConnection();
		}
		
		return clauses;
	}
	
	private String generateDestinationJoinSubquery(Connection dstConn) throws DBException {
		
		AbstractTableConfiguration srcTabConf = getSrcTableConf();
		
		String fromClause = relatedDstConf.generateSelectFromClauseContent();
		
		String dstJoinSubquery = "";
		String joinCondition = relatedDstConf.generateJoinConditionWithSrc();
		
		if (utilities.stringHasValue(joinCondition)) {
			dstJoinSubquery += " SELECT 1 \n";
			dstJoinSubquery += " FROM    " + fromClause + "\n";
			dstJoinSubquery += " WHERE " + joinCondition + "\n";
		} else {
			throw new ForbiddenOperationException("There is no join condition between the src [" + srcTabConf.getTableName()
			        + "] and it destination table [" + relatedDstConf.getTableName() + "]");
		}
		
		return dstJoinSubquery;
	}
	
	@Override
	public int countAllRecords(Connection conn) throws DBException {
		// TODO Auto-generated method stub
		return super.countAllRecords(conn);
	}
}
