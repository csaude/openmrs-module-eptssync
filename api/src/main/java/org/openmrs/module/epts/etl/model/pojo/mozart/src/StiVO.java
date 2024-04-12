package org.openmrs.module.epts.etl.model.pojo.mozart.src;

import org.openmrs.module.epts.etl.model.pojo.generic.*; 
 
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities; 
 
import org.openmrs.module.epts.etl.utilities.AttDefinedElements; 
 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class StiVO extends AbstractDatabaseObject implements DatabaseObject { 
	private Integer id;
	private String encounterUuid;
	private Integer stiConceptId;
	private java.util.Date stiDate;
	private Integer stiValue;
	private String stiUuid;
 
	public StiVO() { 
		this.metadata = false;
	} 
 
	public void setId(Integer id){ 
	 	this.id = id;
	}
 
	public Integer getId(){ 
		return this.id;
	}
 
	public void setEncounterUuid(String encounterUuid){ 
	 	this.encounterUuid = encounterUuid;
	}
 
	public String getEncounterUuid(){ 
		return this.encounterUuid;
	}
 
	public void setStiConceptId(Integer stiConceptId){ 
	 	this.stiConceptId = stiConceptId;
	}
 
	public Integer getStiConceptId(){ 
		return this.stiConceptId;
	}
 
	public void setStiDate(java.util.Date stiDate){ 
	 	this.stiDate = stiDate;
	}
 
	public java.util.Date getStiDate(){ 
		return this.stiDate;
	}
 
	public void setStiValue(Integer stiValue){ 
	 	this.stiValue = stiValue;
	}
 
	public Integer getStiValue(){ 
		return this.stiValue;
	}
 
	public void setStiUuid(String stiUuid){ 
	 	this.stiUuid = stiUuid;
	}


 
	public String getStiUuid(){ 
		return this.stiUuid;
	}
 
	public Integer getObjectId() { 
 		return this.id; 
	} 
 
	public void setObjectId(Integer selfId){ 
		this.id = selfId; 
	} 
 
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
		if (rs.getObject("id") != null) this.id = rs.getInt("id");
		this.encounterUuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("encounter_uuid") != null ? rs.getString("encounter_uuid").trim() : null);
		if (rs.getObject("sti_concept_id") != null) this.stiConceptId = rs.getInt("sti_concept_id");
		this.stiDate =  rs.getTimestamp("sti_date") != null ? new java.util.Date( rs.getTimestamp("sti_date").getTime() ) : null;
		if (rs.getObject("sti_value") != null) this.stiValue = rs.getInt("sti_value");
		this.stiUuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("sti_uuid") != null ? rs.getString("sti_uuid").trim() : null);
	} 
 
	@JsonIgnore
	public String generateDBPrimaryKeyAtt(){ 
 		return "id"; 
	} 
 
	@JsonIgnore
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO sti(encounter_uuid, sti_concept_id, sti_date, sti_value, sti_uuid) VALUES( ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.encounterUuid, this.stiConceptId, this.stiDate, this.stiValue, this.stiUuid};		return params; 
	} 
 
	@JsonIgnore
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO sti(id, encounter_uuid, sti_concept_id, sti_date, sti_value, sti_uuid) VALUES(?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.id, this.encounterUuid, this.stiConceptId, this.stiDate, this.stiValue, this.stiUuid};		return params; 
	} 
 
	@JsonIgnore
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.encounterUuid, this.stiConceptId, this.stiDate, this.stiValue, this.stiUuid, this.id};		return params; 
	} 
 
	@JsonIgnore
	public String getUpdateSQL(){ 
 		return "UPDATE sti SET encounter_uuid = ?, sti_concept_id = ?, sti_date = ?, sti_value = ?, sti_uuid = ? WHERE id = ?;"; 
	} 
 
	@JsonIgnore
	public String generateInsertValues(){ 
 		return ""+(this.encounterUuid != null ? "\""+ utilities.scapeQuotationMarks(encounterUuid)  +"\"" : null) + "," + (this.stiConceptId) + "," + (this.stiDate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(stiDate)  +"\"" : null) + "," + (this.stiValue) + "," + (this.stiUuid != null ? "\""+ utilities.scapeQuotationMarks(stiUuid)  +"\"" : null); 
	} 
 
	@Override
	public boolean hasParents() {
		return false;
	}

	@Override
	public Integer getParentValue(String parentAttName) {

		throw new RuntimeException("No found parent for: " + parentAttName);	}

	@Override
	public void changeParentValue(String parentAttName, DatabaseObject newParent) {

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public void setParentToNull(String parentAttName) {

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "sti";
	}


}