package org.openmrs.module.epts.etl.data.validation.missingrecords.engine;

import java.sql.Connection;

import org.openmrs.module.epts.etl.data.validation.missingrecords.controller.DetectMissingRecordsController;
import org.openmrs.module.epts.etl.data.validation.missingrecords.model.DetectMissingRecordsSearchParams;
import org.openmrs.module.epts.etl.engine.RecordLimits;
import org.openmrs.module.epts.etl.engine.SyncSearchParams;
import org.openmrs.module.epts.etl.etl.engine.EtlEngine;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.monitor.EngineMonitor;

public class DetectMIssingRecordsEngine extends EtlEngine {
	
	public DetectMIssingRecordsEngine(EngineMonitor monitor, RecordLimits limits) {
		super(monitor, limits);
	}
	
	@Override
	public DetectMissingRecordsController getRelatedOperationController() {
		return (DetectMissingRecordsController) super.getRelatedOperationController();
	}
	
	@Override
	protected SyncSearchParams<? extends EtlObject> initSearchParams(RecordLimits limits, Connection conn) {
		return new DetectMissingRecordsSearchParams(getEtlConfiguration(), limits, getRelatedOperationController());
	}
}
