package org.openmrs.module.epts.etl.dbquickload.model;

import java.sql.Connection;

import org.openmrs.module.epts.etl.common.model.SyncImportInfoVO;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.ThreadLimitsManager;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.model.SearchParamsDAO;
import org.openmrs.module.epts.etl.model.base.VOLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class LoadedRecordsSearchParams extends AbstractEtlSearchParams<SyncImportInfoVO> {
	
	private String appOriginLocationCode;
	
	public LoadedRecordsSearchParams(EtlItemConfiguration config, ThreadLimitsManager limits, String appOriginLocationCode) {
		super(config, limits, null);
		
		setOrderByFields("id");
		
		this.appOriginLocationCode = appOriginLocationCode;
	}
	
	@Override
	public SearchClauses<SyncImportInfoVO> generateSearchClauses(Connection conn) throws DBException {
		SearchClauses<SyncImportInfoVO> searchClauses = new SearchClauses<SyncImportInfoVO>(this);
		
		searchClauses.addColumnToSelect(getSrcTableConf().generateFullStageTableName() + ".*");
		
		searchClauses.addToClauseFrom(getSrcTableConf().generateFullStageTableName());
		
		searchClauses.addToClauses("record_origin_location_code = ?");
		searchClauses.addToParameters(this.appOriginLocationCode);
		
		if (utilities.stringHasValue(getExtraCondition())) {
			searchClauses.addToClauses(getExtraCondition());
		}
		
		return searchClauses;
	}
	
	@Override
	public Class<SyncImportInfoVO> getRecordClass() {
		return SyncImportInfoVO.class;
	}
	
	@Override
	public int countAllRecords(Connection conn) throws DBException {
		return SearchParamsDAO.countAll(this, conn);
	}
	
	@Override
	public int countNotProcessedRecords(Connection conn) throws DBException {
		return 0;
	}

	@Override
	protected VOLoaderHelper getLoaderHealper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractEtlSearchParams<SyncImportInfoVO> cloneMe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateDestinationExclusionClause(Connection srcConn, Connection dstConn) throws DBException{
		return null;
	}
}
