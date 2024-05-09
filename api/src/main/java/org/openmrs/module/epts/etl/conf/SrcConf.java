package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SrcConf extends AbstractTableConfiguration implements EtlDataSource, TableAliasesGenerator {
	
	private List<TableDataSourceConfig> extraTableDataSource;
	
	private List<QueryDataSourceConfig> extraQueryDataSource;
	
	private List<String> autoGeneratedAliases;
	
	private boolean fullLoaded;
	
	public List<QueryDataSourceConfig> getExtraQueryDataSource() {
		return extraQueryDataSource;
	}
	
	public void setExtraQueryDataSource(List<QueryDataSourceConfig> extraQueryDataSource) {
		this.extraQueryDataSource = extraQueryDataSource;
	}
	
	public List<TableDataSourceConfig> getExtraTableDataSource() {
		return extraTableDataSource;
	}
	
	public void setExtraTableDataSource(List<TableDataSourceConfig> extraTableDataSource) {
		this.extraTableDataSource = extraTableDataSource;
	}
	
	@Override
	public String getName() {
		return getTableName();
	}
	
	@Override
	public boolean isGeneric() {
		return false;
	}
	
	@Override
	public synchronized String generateAlias(AbstractTableConfiguration tabConfig) {
		if (this.autoGeneratedAliases == null) {
			this.autoGeneratedAliases = new ArrayList<>();
		}
		
		int i = 1;
		
		String tableName = DBUtilities.extractTableNameFromFullTableName(tabConfig.getTableName());
		
		String alias = tableName + "_" + i;
		
		while (this.autoGeneratedAliases.contains(alias)) {
			alias = tableName + "_" + ++i;
		}
		
		this.autoGeneratedAliases.add(alias);
		
		return alias;
	}
	
	public synchronized void fullLoad() throws DBException {
		
		if (this.fullLoaded) {
			return;
		}
		if (!utilities.stringHasValue(this.getTableAlias())) {
			this.setTableAlias(generateAlias(this));
		}
		
		super.fullLoad();
		
		if (this.hasParentRefInfo()) {
			for (ParentTable ref : this.getParentRefInfo()) {
				ref.setTableAlias(generateAlias(ref));
				
				ref.fullLoad();
			}
		}
		
		OpenConnection srcConn = this.getRelatedAppInfo().openConnection();
		
		try {
			
			if (utilities.arrayHasElement(this.extraTableDataSource)) {
				for (TableDataSourceConfig t : this.extraTableDataSource) {
					t.setRelatedSrcConf(this);
					
					t.fullLoad(srcConn);
				}
				
			}
			
			if (utilities.arrayHasElement(this.extraQueryDataSource)) {
				for (QueryDataSourceConfig query : this.extraQueryDataSource) {
					query.setRelatedSrcConf(this);
					query.fullLoad(srcConn);
				}
			}
		}
		catch (Exception e) {
			srcConn.finalizeConnection();
			
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
		
		this.fullLoaded = true;
	}
	
	public void setFullLoaded(boolean fullLoaded) {
		this.fullLoaded = fullLoaded;
	}
	
	public QueryDataSourceConfig findAdditionalDataSrc(String dsName) {
		if (!utilities.arrayHasElement(this.extraQueryDataSource)) {
			return null;
		}
		
		for (QueryDataSourceConfig src : this.extraQueryDataSource) {
			if (src.getName().equals(dsName)) {
				return src;
			}
		}
		
		throw new ForbiddenOperationException("The table '" + dsName + "'cannot be foud on the mapping src tables");
	}
	
	public boolean isFullLoaded() {
		return this.fullLoaded;
	}
	
	public static SrcConf fastCreate(AbstractTableConfiguration tableConfig) {
		SrcConf src = new SrcConf();
		
		src.clone(src);
		
		return src;
	}
	
	@Override
	public void setParentConf(EtlDataConfiguration parent) {
		super.setParentConf((EtlItemConfiguration) parent);
	}
	
	@Override
	@JsonIgnore
	public EtlItemConfiguration getParentConf() {
		return (EtlItemConfiguration) super.getParentConf();
	}
	
	@Override
	public AppInfo getRelatedAppInfo() {
		return getMainApp();
	}
	
	@Override
	protected void tryToDiscoverySharedKeyInfo(Connection conn) throws DBException {
		super.tryToDiscoverySharedKeyInfo(conn);
		
		if (useSharedPKKey()) {
			//Parce the shared parent to datasource
			utilities.updateOnArray(this.getParentRefInfo(), getSharedKeyRefInfo(),
			    SharedPkDataSource.generateFromSrcConfSharedPkParent(this));
		}
	}
	
	@JsonIgnore
	public List<EtlAdditionalDataSource> getAvaliableExtraDataSource() {
		List<EtlAdditionalDataSource> ds = new ArrayList<>();
		
		if (useSharedPKKey()) {
			ds.add((EtlAdditionalDataSource) getSharedKeyRefInfo());
		}
		
		if (utilities.arrayHasElement(this.extraTableDataSource)) {
			ds.addAll(utilities.parseList(this.extraTableDataSource, EtlAdditionalDataSource.class));
		}
		
		if (utilities.arrayHasElement(this.extraQueryDataSource)) {
			ds.addAll(utilities.parseList(this.extraQueryDataSource, EtlAdditionalDataSource.class));
		}
		
		return ds;
	}
	
	/**
	 * Generate all avaliable fields on this srcConf, this fields will include all field from
	 * {@link #getFields()} and the fields from all {@link #extraTableDataSource} Note that the
	 * duplicated fields will only be included once
	 * 
	 * @return
	 */
	public List<Field> generateAllAvaliableFields() {
		List<Field> fields = new ArrayList<>();
		
		for (Field f : this.getFields()) {
			fields.add(f);
		}
		
		if (this.extraTableDataSource != null) {
			
			for (EtlAdditionalDataSource ds : this.extraTableDataSource) {
				for (Field f : ds.getFields()) {
					
					if (!fields.contains(f)) {
						fields.add(f);
					}
				}
			}
		}
		
		return fields;
	}
	
}
