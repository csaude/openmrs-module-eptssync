package org.openmrs.module.epts.etl.controller.conf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObject;
import org.openmrs.module.epts.etl.utilities.AttDefinedElements;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jpboane
 */
public class UniqueKeyInfo {
	
	public static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private String keyName;
	
	private List<Key> fields;
	
	public UniqueKeyInfo() {
	}
	
	public static UniqueKeyInfo init(String keyName) {
		UniqueKeyInfo uk = new UniqueKeyInfo();
		uk.keyName = keyName;
		
		return uk;
	}
	
	public boolean isCompositeKey() {
		return utilities.arrayHasMoreThanOneElements(this.fields);
	}
	
	/**
	 * Retrieves this unique key as simple key
	 * 
	 * @return the simple key for this unique key
	 * @throws ForbiddenOperationException if this unique key is composite
	 */
	public Key retrieveSimpleKey() throws ForbiddenOperationException {
		if (isCompositeKey())
			throw new ForbiddenOperationException("The key is composite, you cannot retrive the SimpleKey");
		
		return this.fields.get(0);
	}
	
	@JsonIgnore
	public List<String> generateListFromFieldsNames() {
		List<String> fieldsAsName = new ArrayList<String>();
		
		for (Field field : this.fields) {
			fieldsAsName.add(field.getName());
		}
		
		return fieldsAsName;
	}
	
	public String getKeyName() {
		return keyName;
	}
	
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	public List<Key> getFields() {
		return fields;
	}
	
	public void setFields(List<Key> fields) {
		this.fields = fields;
	}
	
	public void loadValuesToFields(DatabaseObject object) {
		for (Field field : this.fields) {
			String attClasse = AttDefinedElements.convertTableAttNameToClassAttName(field.getName());
			
			Object[] fieldValues = object.getFieldValues(attClasse);
			
			Object value = fieldValues != null ? fieldValues[0] : null;
			
			field.setValue(value);
		}
	}
	
	public static List<UniqueKeyInfo> loadUniqueKeysInfo(SyncTableConfiguration tableConfiguration, Connection conn)
	        throws DBException {
		if (tableConfiguration.getPrimaryKey(conn) == null) {
			throw new ForbiddenOperationException(
			        "The primary key is not defined on table " + tableConfiguration.getTableName() + "!");
		}
		
		List<UniqueKeyInfo> uniqueKeysInfo = new ArrayList<UniqueKeyInfo>();
		
		ResultSet rs = null;
		
		try {
			
			String tableName = DBUtilities.extractTableNameFromFullTableName(tableConfiguration.getTableName());
			
			String schema = DBUtilities.determineSchemaFromFullTableName(tableConfiguration.getTableName());
			
			schema = utilities.stringHasValue(schema) ? schema : conn.getSchema();
			
			String catalog = conn.getCatalog();
			
			if (DBUtilities.isMySQLDB(conn) && utilities.stringHasValue(schema)) {
				catalog = schema;
			}
			
			rs = conn.getMetaData().getIndexInfo(catalog, schema, tableName, true, true);
			
			String prevIndexName = null;
			
			List<Key> keyElements = null;
			
			String indexName = "";
			
			while (rs.next()) {
				indexName = rs.getString("INDEX_NAME");
				
				if (!indexName.equals(prevIndexName)) {
					addUniqueKey(prevIndexName, keyElements, uniqueKeysInfo, tableConfiguration, conn);
					
					prevIndexName = indexName;
					keyElements = new ArrayList<>();
				}
				
				keyElements.add(new Key(rs.getString("COLUMN_NAME")));
			}
			
			addUniqueKey(prevIndexName, keyElements, uniqueKeysInfo, tableConfiguration, conn);
			
			if (tableConfiguration.useSharedPKKey()) {
				SyncTableConfiguration parentTableInfo = new SyncTableConfiguration();
				
				parentTableInfo = new SyncTableConfiguration();
				parentTableInfo.setTableName(tableConfiguration.getSharePkWith());
				parentTableInfo.setParent(tableConfiguration.getParent());
				
				List<UniqueKeyInfo> parentUniqueKeys = loadUniqueKeysInfo(parentTableInfo, conn);
				
				if (utilities.arrayHasElement(parentUniqueKeys)) {
					uniqueKeysInfo.addAll(parentUniqueKeys);
				}
			}
			
			return uniqueKeysInfo;
		}
		catch (SQLException e) {
			if (rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e1) {}
			}
			
			throw new DBException(e);
		}
		
	}
	
	private static boolean addUniqueKey(String keyName, List<Key> keyElements, List<UniqueKeyInfo> uniqueKeys,
	        SyncTableConfiguration config, Connection conn) {
		
		if (keyElements == null || keyElements.isEmpty())
			return false;
		
		if (uniqueKeys == null)
			uniqueKeys = new ArrayList<>();
		
		UniqueKeyInfo uk = UniqueKeyInfo.init(keyName);
		uk.fields = keyElements;
		
		//Don't add PK as uniqueKey
		if (config.getPrimaryKey().equals(uk)) {
			uniqueKeys.add(uk);
			
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof UniqueKeyInfo))
			return false;
		
		UniqueKeyInfo other = (UniqueKeyInfo) obj;
		
		if (utilities.stringHasValue(this.keyName) && this.keyName.equals(other.keyName)) {
			return true;
		}
		
		return this.hasSameFields(other);
	}
	
	public boolean hasSameFields(UniqueKeyInfo other) {
		for (Field field : this.getFields()) {
			if (!other.getFields().contains(field))
				return false;
		}
		
		for (Field field : other.getFields()) {
			if (!this.getFields().contains(field))
				return false;
		}
		
		return true;
	}
	
	@Override
	@JsonIgnore
	public String toString() {
		String toString = keyName + "[";
		int i = 0;
		
		for (Field field : this.fields) {
			String fieldInfo = field.getName() + ": " + field.getValue();
			
			if (i > 0) {
				toString += ",";
			}
			
			toString += fieldInfo;
			i++;
		}
		
		toString += "]";
		
		return toString;
	}
	
	public void addKey(Key key) {
		if (this.fields == null)
			this.fields = new ArrayList<Key>();
		
		if (!this.fields.contains(key)) {
			this.fields.add(key);
		}
	}
	
	public static UniqueKeyInfo generateFromFieldList(List<String> fields) {
		if (!utilities.arrayHasElement(fields))
			throw new ForbiddenOperationException("The list cannot be empty");
		
		UniqueKeyInfo uk = new UniqueKeyInfo();
		
		for (String fieldName : fields) {
			uk.addKey(new Key(fieldName));
		}
		
		return uk;
	}
	
	protected UniqueKeyInfo cloneMe() {
		UniqueKeyInfo uk = UniqueKeyInfo.init(keyName);
		
		for (Field field : fields) {
			uk.addKey(new Key(field.getName()));
		}
		return uk;
	}
	
	public static List<UniqueKeyInfo> cloneAll(List<UniqueKeyInfo> uniqueKeysInfo) {
		
		if (uniqueKeysInfo == null) {
			return null;
		}
		
		List<UniqueKeyInfo> cloned = new ArrayList<>(uniqueKeysInfo.size());
		
		for (UniqueKeyInfo uk : uniqueKeysInfo) {
			cloned.add(uk.cloneMe());
		}
		
		return cloned;
	}
	
	public Object[] parseValuesToArray() {
		Object[] values = new Object[this.getFields().size()];
		
		for (int i = 0; i < this.getFields().size(); i++) {
			Key key = this.getFields().get(i);
			
			values[i] = key.getValue();
		}
		
		return values;
	}
	
	public String[] parseFieldNamesToArray() {
		String[] fields = new String[this.getFields().size()];
		
		for (int i = 0; i < this.getFields().size(); i++) {
			Key key = this.getFields().get(i);
			
			fields[i] = key.getName();
		}
		
		return fields;
	}
	
	public String parseFieldNamesToCommaSeparatedString() {
		String fields = "";
		
		for (int i = 0; i < this.getFields().size(); i++) {
			Key key = this.getFields().get(i);
			
			if (utilities.stringHasValue(fields)) {
				fields += ", ";
			}
			
			fields += key.getName();
		}
		
		return fields;		
	}
	
	
	public String parseToParametrizedStringCondition() {
		String fields = "";
		
		for (int i = 0; i < this.getFields().size(); i++) {
			Key key = this.getFields().get(i);
			
			if (utilities.stringHasValue(fields)) {
				fields += " AND ";
			}
			
			fields += key.getName() + " = ? ";
		}
		
		return fields;			
	}
	
	public Key getKey(String name) {
		if (!utilities.arrayHasElement(this.fields)) {
			return null;
		}
		
		for (Key key: this.fields) {
			if (key.getName().equals(name)) {
				return key;
			}
		}
		
		return null;
	}
}
