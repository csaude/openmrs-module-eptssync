package org.openmrs.module.eptssync.model.pojo.source.eip_change_dectetor; 
 
import org.openmrs.module.eptssync.model.pojo.generic.*; 
 
import org.openmrs.module.eptssync.utilities.DateAndTimeUtilities; 
 
import org.openmrs.module.eptssync.utilities.AttDefinedElements; 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class ProviderAttributeTypeVO extends AbstractOpenMRSObject implements OpenMRSObject { 
	private int providerAttributeTypeId;
	private String name;
	private String description;
	private String datatype;
	private String datatypeConfig;
	private String preferredHandler;
	private String handlerConfig;
	private int minOccurs;
	private int maxOccurs;
	private int creator;
	private int changedBy;
	private byte retired;
	private int retiredBy;
	private java.util.Date dateRetired;
	private String retireReason;
 
	public ProviderAttributeTypeVO() { 
		this.metadata = true;
	} 
 
	public void setProviderAttributeTypeId(int providerAttributeTypeId){ 
	 	this.providerAttributeTypeId = providerAttributeTypeId;
	}
 
	public int getProviderAttributeTypeId(){ 
		return this.providerAttributeTypeId;
	}
 
	public void setName(String name){ 
	 	this.name = name;
	}
 
	public String getName(){ 
		return this.name;
	}
 
	public void setDescription(String description){ 
	 	this.description = description;
	}
 
	public String getDescription(){ 
		return this.description;
	}
 
	public void setDatatype(String datatype){ 
	 	this.datatype = datatype;
	}
 
	public String getDatatype(){ 
		return this.datatype;
	}
 
	public void setDatatypeConfig(String datatypeConfig){ 
	 	this.datatypeConfig = datatypeConfig;
	}
 
	public String getDatatypeConfig(){ 
		return this.datatypeConfig;
	}
 
	public void setPreferredHandler(String preferredHandler){ 
	 	this.preferredHandler = preferredHandler;
	}
 
	public String getPreferredHandler(){ 
		return this.preferredHandler;
	}
 
	public void setHandlerConfig(String handlerConfig){ 
	 	this.handlerConfig = handlerConfig;
	}
 
	public String getHandlerConfig(){ 
		return this.handlerConfig;
	}
 
	public void setMinOccurs(int minOccurs){ 
	 	this.minOccurs = minOccurs;
	}
 
	public int getMinOccurs(){ 
		return this.minOccurs;
	}
 
	public void setMaxOccurs(int maxOccurs){ 
	 	this.maxOccurs = maxOccurs;
	}
 
	public int getMaxOccurs(){ 
		return this.maxOccurs;
	}
 
	public void setCreator(int creator){ 
	 	this.creator = creator;
	}
 
	public int getCreator(){ 
		return this.creator;
	}
 
	public void setChangedBy(int changedBy){ 
	 	this.changedBy = changedBy;
	}
 
	public int getChangedBy(){ 
		return this.changedBy;
	}
 
	public void setRetired(byte retired){ 
	 	this.retired = retired;
	}
 
	public byte getRetired(){ 
		return this.retired;
	}
 
	public void setRetiredBy(int retiredBy){ 
	 	this.retiredBy = retiredBy;
	}
 
	public int getRetiredBy(){ 
		return this.retiredBy;
	}
 
	public void setDateRetired(java.util.Date dateRetired){ 
	 	this.dateRetired = dateRetired;
	}
 
	public java.util.Date getDateRetired(){ 
		return this.dateRetired;
	}
 
	public void setRetireReason(String retireReason){ 
	 	this.retireReason = retireReason;
	}
 
	public String getRetireReason(){ 
		return this.retireReason;
	}
 

 
	public int getObjectId() { 
 		return this.providerAttributeTypeId; 
	} 
 
	public void setObjectId(int selfId){ 
		this.providerAttributeTypeId = selfId; 
	} 
 
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
		this.providerAttributeTypeId = rs.getInt("provider_attribute_type_id");
		this.name = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("name") != null ? rs.getString("name").trim() : null);
		this.description = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("description") != null ? rs.getString("description").trim() : null);
		this.datatype = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("datatype") != null ? rs.getString("datatype").trim() : null);
		this.datatypeConfig = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("datatype_config") != null ? rs.getString("datatype_config").trim() : null);
		this.preferredHandler = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("preferred_handler") != null ? rs.getString("preferred_handler").trim() : null);
		this.handlerConfig = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("handler_config") != null ? rs.getString("handler_config").trim() : null);
		this.minOccurs = rs.getInt("min_occurs");
		this.maxOccurs = rs.getInt("max_occurs");
		this.creator = rs.getInt("creator");
		this.dateCreated =  rs.getTimestamp("date_created") != null ? new java.util.Date( rs.getTimestamp("date_created").getTime() ) : null;
		this.changedBy = rs.getInt("changed_by");
		this.dateChanged =  rs.getTimestamp("date_changed") != null ? new java.util.Date( rs.getTimestamp("date_changed").getTime() ) : null;
		this.retired = rs.getByte("retired");
		this.retiredBy = rs.getInt("retired_by");
		this.dateRetired =  rs.getTimestamp("date_retired") != null ? new java.util.Date( rs.getTimestamp("date_retired").getTime() ) : null;
		this.retireReason = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("retire_reason") != null ? rs.getString("retire_reason").trim() : null);
		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("uuid") != null ? rs.getString("uuid").trim() : null);
	} 
 
	@JsonIgnore
	public String generateDBPrimaryKeyAtt(){ 
 		return "provider_attribute_type_id"; 
	} 
 
	@JsonIgnore
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO provider_attribute_type(provider_attribute_type_id, name, description, datatype, datatype_config, preferred_handler, handler_config, min_occurs, max_occurs, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.providerAttributeTypeId, this.name, this.description, this.datatype, this.datatypeConfig, this.preferredHandler, this.handlerConfig, this.minOccurs, this.maxOccurs, this.creator == 0 ? null : this.creator, this.dateCreated, this.changedBy == 0 ? null : this.changedBy, this.dateChanged, this.retired, this.retiredBy == 0 ? null : this.retiredBy, this.dateRetired, this.retireReason, this.uuid};		return params; 
	} 
 
	@JsonIgnore
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO provider_attribute_type(provider_attribute_type_id, provider_attribute_type_id, name, description, datatype, datatype_config, preferred_handler, handler_config, min_occurs, max_occurs, creator, date_created, changed_by, date_changed, retired, retired_by, date_retired, retire_reason, uuid) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.providerAttributeTypeId, this.providerAttributeTypeId, this.name, this.description, this.datatype, this.datatypeConfig, this.preferredHandler, this.handlerConfig, this.minOccurs, this.maxOccurs, this.creator == 0 ? null : this.creator, this.dateCreated, this.changedBy == 0 ? null : this.changedBy, this.dateChanged, this.retired, this.retiredBy == 0 ? null : this.retiredBy, this.dateRetired, this.retireReason, this.uuid};		return params; 
	} 
 
	@JsonIgnore
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.providerAttributeTypeId, this.name, this.description, this.datatype, this.datatypeConfig, this.preferredHandler, this.handlerConfig, this.minOccurs, this.maxOccurs, this.creator == 0 ? null : this.creator, this.dateCreated, this.changedBy == 0 ? null : this.changedBy, this.dateChanged, this.retired, this.retiredBy == 0 ? null : this.retiredBy, this.dateRetired, this.retireReason, this.uuid, this.providerAttributeTypeId};		return params; 
	} 
 
	@JsonIgnore
	public String getUpdateSQL(){ 
 		return "UPDATE provider_attribute_type SET provider_attribute_type_id = ?, name = ?, description = ?, datatype = ?, datatype_config = ?, preferred_handler = ?, handler_config = ?, min_occurs = ?, max_occurs = ?, creator = ?, date_created = ?, changed_by = ?, date_changed = ?, retired = ?, retired_by = ?, date_retired = ?, retire_reason = ?, uuid = ? WHERE provider_attribute_type_id = ?;"; 
	} 
 
	@JsonIgnore
	public String generateInsertValues(){ 
 		return ""+(this.providerAttributeTypeId) + "," + (this.name != null ? "\""+ utilities.scapeQuotationMarks(name)  +"\"" : null) + "," + (this.description != null ? "\""+ utilities.scapeQuotationMarks(description)  +"\"" : null) + "," + (this.datatype != null ? "\""+ utilities.scapeQuotationMarks(datatype)  +"\"" : null) + "," + (this.datatypeConfig != null ? "\""+ utilities.scapeQuotationMarks(datatypeConfig)  +"\"" : null) + "," + (this.preferredHandler != null ? "\""+ utilities.scapeQuotationMarks(preferredHandler)  +"\"" : null) + "," + (this.handlerConfig != null ? "\""+ utilities.scapeQuotationMarks(handlerConfig)  +"\"" : null) + "," + (this.minOccurs) + "," + (this.maxOccurs) + "," + (this.creator == 0 ? null : this.creator) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateCreated)  +"\"" : null) + "," + (this.changedBy == 0 ? null : this.changedBy) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateChanged)  +"\"" : null) + "," + (this.retired) + "," + (this.retiredBy == 0 ? null : this.retiredBy) + "," + (this.dateRetired != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateRetired)  +"\"" : null) + "," + (this.retireReason != null ? "\""+ utilities.scapeQuotationMarks(retireReason)  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(uuid)  +"\"" : null); 
	} 
 
	@Override
	public boolean hasParents() {
		if (this.changedBy != 0) return true;

		if (this.creator != 0) return true;

		if (this.retiredBy != 0) return true;

		return false;
	}

	@Override
	public int getParentValue(String parentAttName) {		
		if (parentAttName.equals("changedBy")) return this.changedBy;		
		if (parentAttName.equals("creator")) return this.creator;		
		if (parentAttName.equals("retiredBy")) return this.retiredBy;

		throw new RuntimeException("No found parent for: " + parentAttName);	}

	@Override
	public void changeParentValue(String parentAttName, OpenMRSObject newParent) {		
		if (parentAttName.equals("changedBy")) {
			this.changedBy = newParent.getObjectId();
			return;
		}		
		if (parentAttName.equals("creator")) {
			this.creator = newParent.getObjectId();
			return;
		}		
		if (parentAttName.equals("retiredBy")) {
			this.retiredBy = newParent.getObjectId();
			return;
		}

		throw new RuntimeException("No found parent for: " + parentAttName);
	}


}