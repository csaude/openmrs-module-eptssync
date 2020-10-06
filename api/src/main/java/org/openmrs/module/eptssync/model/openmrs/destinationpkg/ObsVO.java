package org.openmrs.module.eptssync.model.openmrs.destinationpkg; 
 
import org.openmrs.module.eptssync.model.openmrs.generic.*; 
 
import org.openmrs.module.eptssync.utilities.DateAndTimeUtilities; 
 
import org.openmrs.module.eptssync.utilities.db.conn.DBException; 
import org.openmrs.module.eptssync.utilities.AttDefinedElements; 
import org.openmrs.module.eptssync.exceptions.ParentNotYetMigratedException; 
 
import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class ObsVO extends AbstractOpenMRSObject implements OpenMRSObject { 
	private int obsId;
	private int personId;
	private int conceptId;
	private int encounterId;
	private int orderId;
	private java.util.Date obsDatetime;
	private int locationId;
	private int obsGroupId;
	private String accessionNumber;
	private int valueGroupId;
	private byte valueBoolean;
	private int valueCoded;
	private int valueCodedNameId;
	private int valueDrug;
	private java.util.Date valueDatetime;
	private double valueNumeric;
	private String valueModifier;
	private String valueText;
	private String comments;
	private int creator;
	private java.util.Date dateCreated;
	private byte voided;
	private int voidedBy;
	private java.util.Date dateVoided;
	private String voidReason;
	private String valueComplex;
	private String uuid;
	private int previousVersion;
	private String formNamespaceAndPath;
	private int consistent;
	private java.util.Date lastSyncDate;
	private int originRecordId;
	private java.util.Date dateChanged;
	private String originAppLocationCode;
 
	public ObsVO() { 
		this.metadata = false;
	} 
 
	public void setObsId(int obsId){ 
	 	this.obsId = obsId;
	}
 
	public int getObsId(){ 
		return this.obsId;
	}
 
	public void setPersonId(int personId){ 
	 	this.personId = personId;
	}
 
	public int getPersonId(){ 
		return this.personId;
	}
 
	public void setConceptId(int conceptId){ 
	 	this.conceptId = conceptId;
	}
 
	public int getConceptId(){ 
		return this.conceptId;
	}
 
	public void setEncounterId(int encounterId){ 
	 	this.encounterId = encounterId;
	}
 
	public int getEncounterId(){ 
		return this.encounterId;
	}
 
	public void setOrderId(int orderId){ 
	 	this.orderId = orderId;
	}
 
	public int getOrderId(){ 
		return this.orderId;
	}
 
	public void setObsDatetime(java.util.Date obsDatetime){ 
	 	this.obsDatetime = obsDatetime;
	}
 
	public java.util.Date getObsDatetime(){ 
		return this.obsDatetime;
	}
 
	public void setLocationId(int locationId){ 
	 	this.locationId = locationId;
	}
 
	public int getLocationId(){ 
		return this.locationId;
	}
 
	public void setObsGroupId(int obsGroupId){ 
	 	this.obsGroupId = obsGroupId;
	}
 
	public int getObsGroupId(){ 
		return this.obsGroupId;
	}
 
	public void setAccessionNumber(String accessionNumber){ 
	 	this.accessionNumber = accessionNumber;
	}
 
	public String getAccessionNumber(){ 
		return this.accessionNumber;
	}
 
	public void setValueGroupId(int valueGroupId){ 
	 	this.valueGroupId = valueGroupId;
	}
 
	public int getValueGroupId(){ 
		return this.valueGroupId;
	}
 
	public void setValueBoolean(byte valueBoolean){ 
	 	this.valueBoolean = valueBoolean;
	}
 
	public byte getValueBoolean(){ 
		return this.valueBoolean;
	}
 
	public void setValueCoded(int valueCoded){ 
	 	this.valueCoded = valueCoded;
	}
 
	public int getValueCoded(){ 
		return this.valueCoded;
	}
 
	public void setValueCodedNameId(int valueCodedNameId){ 
	 	this.valueCodedNameId = valueCodedNameId;
	}
 
	public int getValueCodedNameId(){ 
		return this.valueCodedNameId;
	}
 
	public void setValueDrug(int valueDrug){ 
	 	this.valueDrug = valueDrug;
	}
 
	public int getValueDrug(){ 
		return this.valueDrug;
	}
 
	public void setValueDatetime(java.util.Date valueDatetime){ 
	 	this.valueDatetime = valueDatetime;
	}
 
	public java.util.Date getValueDatetime(){ 
		return this.valueDatetime;
	}
 
	public void setValueNumeric(double valueNumeric){ 
	 	this.valueNumeric = valueNumeric;
	}
 
	public double getValueNumeric(){ 
		return this.valueNumeric;
	}
 
	public void setValueModifier(String valueModifier){ 
	 	this.valueModifier = valueModifier;
	}
 
	public String getValueModifier(){ 
		return this.valueModifier;
	}
 
	public void setValueText(String valueText){ 
	 	this.valueText = valueText;
	}
 
	public String getValueText(){ 
		return this.valueText;
	}
 
	public void setComments(String comments){ 
	 	this.comments = comments;
	}
 
	public String getComments(){ 
		return this.comments;
	}
 
	public void setCreator(int creator){ 
	 	this.creator = creator;
	}
 
	public int getCreator(){ 
		return this.creator;
	}
 
	public void setDateCreated(java.util.Date dateCreated){ 
	 	this.dateCreated = dateCreated;
	}
 
	public java.util.Date getDateCreated(){ 
		return this.dateCreated;
	}
 
	public void setVoided(byte voided){ 
	 	this.voided = voided;
	}
 
	public byte getVoided(){ 
		return this.voided;
	}
 
	public void setVoidedBy(int voidedBy){ 
	 	this.voidedBy = voidedBy;
	}
 
	public int getVoidedBy(){ 
		return this.voidedBy;
	}
 
	public void setDateVoided(java.util.Date dateVoided){ 
	 	this.dateVoided = dateVoided;
	}
 
	public java.util.Date getDateVoided(){ 
		return this.dateVoided;
	}
 
	public void setVoidReason(String voidReason){ 
	 	this.voidReason = voidReason;
	}
 
	public String getVoidReason(){ 
		return this.voidReason;
	}
 
	public void setValueComplex(String valueComplex){ 
	 	this.valueComplex = valueComplex;
	}
 
	public String getValueComplex(){ 
		return this.valueComplex;
	}
 
	public void setUuid(String uuid){ 
	 	this.uuid = uuid;
	}
 
	public String getUuid(){ 
		return this.uuid;
	}
 
	public void setPreviousVersion(int previousVersion){ 
	 	this.previousVersion = previousVersion;
	}
 
	public int getPreviousVersion(){ 
		return this.previousVersion;
	}
 
	public void setFormNamespaceAndPath(String formNamespaceAndPath){ 
	 	this.formNamespaceAndPath = formNamespaceAndPath;
	}
 
	public String getFormNamespaceAndPath(){ 
		return this.formNamespaceAndPath;
	}
 
	public void setConsistent(int consistent){ 
	 	this.consistent = consistent;
	}
 
	public int getConsistent(){ 
		return this.consistent;
	}
 
	public void setLastSyncDate(java.util.Date lastSyncDate){ 
	 	this.lastSyncDate = lastSyncDate;
	}
 
	public java.util.Date getLastSyncDate(){ 
		return this.lastSyncDate;
	}
 
	public void setOriginRecordId(int originRecordId){ 
	 	this.originRecordId = originRecordId;
	}
 
	public int getOriginRecordId(){ 
		return this.originRecordId;
	}
 
	public void setDateChanged(java.util.Date dateChanged){ 
	 	this.dateChanged = dateChanged;
	}
 
	public java.util.Date getDateChanged(){ 
		return this.dateChanged;
	}
 
	public void setOriginAppLocationCode(String originAppLocationCode){ 
	 	this.originAppLocationCode = originAppLocationCode;
	}


 
	public String getOriginAppLocationCode(){ 
		return this.originAppLocationCode;
	}
 
	public int getObjectId() { 
 		return this.obsId; 
	} 
 
	public void setObjectId(int selfId){ 
		this.obsId = selfId; 
	} 
 
	public void load(ResultSet rs) throws SQLException{ 
		this.obsId = rs.getInt("obs_id");
		this.personId = rs.getInt("person_id");
		this.conceptId = rs.getInt("concept_id");
		this.encounterId = rs.getInt("encounter_id");
		this.orderId = rs.getInt("order_id");
		this.obsDatetime =  rs.getTimestamp("obs_datetime") != null ? new java.util.Date( rs.getTimestamp("obs_datetime").getTime() ) : null;
		this.locationId = rs.getInt("location_id");
		this.obsGroupId = rs.getInt("obs_group_id");
		this.accessionNumber = rs.getString("accession_number") != null ? rs.getString("accession_number").trim() : null;
		this.valueGroupId = rs.getInt("value_group_id");
		this.valueBoolean = rs.getByte("value_boolean");
		this.valueCoded = rs.getInt("value_coded");
		this.valueCodedNameId = rs.getInt("value_coded_name_id");
		this.valueDrug = rs.getInt("value_drug");
		this.valueDatetime =  rs.getTimestamp("value_datetime") != null ? new java.util.Date( rs.getTimestamp("value_datetime").getTime() ) : null;
		this.valueNumeric = rs.getDouble("value_numeric");
		this.valueModifier = rs.getString("value_modifier") != null ? rs.getString("value_modifier").trim() : null;
		this.valueText = rs.getString("value_text") != null ? rs.getString("value_text").trim() : null;
		this.comments = rs.getString("comments") != null ? rs.getString("comments").trim() : null;
		this.creator = rs.getInt("creator");
		this.dateCreated =  rs.getTimestamp("date_created") != null ? new java.util.Date( rs.getTimestamp("date_created").getTime() ) : null;
		this.voided = rs.getByte("voided");
		this.voidedBy = rs.getInt("voided_by");
		this.dateVoided =  rs.getTimestamp("date_voided") != null ? new java.util.Date( rs.getTimestamp("date_voided").getTime() ) : null;
		this.voidReason = rs.getString("void_reason") != null ? rs.getString("void_reason").trim() : null;
		this.valueComplex = rs.getString("value_complex") != null ? rs.getString("value_complex").trim() : null;
		this.uuid = rs.getString("uuid") != null ? rs.getString("uuid").trim() : null;
		this.previousVersion = rs.getInt("previous_version");
		this.formNamespaceAndPath = rs.getString("form_namespace_and_path") != null ? rs.getString("form_namespace_and_path").trim() : null;
		this.consistent = rs.getInt("consistent");
		this.lastSyncDate =  rs.getTimestamp("last_sync_date") != null ? new java.util.Date( rs.getTimestamp("last_sync_date").getTime() ) : null;
		this.originRecordId = rs.getInt("origin_record_id");
		this.dateChanged =  rs.getTimestamp("date_changed") != null ? new java.util.Date( rs.getTimestamp("date_changed").getTime() ) : null;
	} 
 
	@JsonIgnore
	public String generateDBPrimaryKeyAtt(){ 
 		return "obs_id"; 
	} 
 
	@JsonIgnore
	public Object[]  getInsertParams(){ 
 		Object[] params = {this.personId == 0 ? null : this.personId, this.conceptId == 0 ? null : this.conceptId, this.encounterId == 0 ? null : this.encounterId, this.orderId == 0 ? null : this.orderId, this.obsDatetime, this.locationId == 0 ? null : this.locationId, this.obsGroupId == 0 ? null : this.obsGroupId, this.accessionNumber, this.valueGroupId, this.valueBoolean, this.valueCoded == 0 ? null : this.valueCoded, this.valueCodedNameId == 0 ? null : this.valueCodedNameId, this.valueDrug == 0 ? null : this.valueDrug, this.valueDatetime, this.valueNumeric, this.valueModifier, this.valueText, this.comments, this.creator == 0 ? null : this.creator, this.dateCreated, this.voided, this.voidedBy == 0 ? null : this.voidedBy, this.dateVoided, this.voidReason, this.valueComplex, this.uuid, this.previousVersion == 0 ? null : this.previousVersion, this.formNamespaceAndPath, this.consistent, this.lastSyncDate, this.originRecordId, this.dateChanged, this.originAppLocationCode};		return params; 
	} 
 
	@JsonIgnore
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.personId == 0 ? null : this.personId, this.conceptId == 0 ? null : this.conceptId, this.encounterId == 0 ? null : this.encounterId, this.orderId == 0 ? null : this.orderId, this.obsDatetime, this.locationId == 0 ? null : this.locationId, this.obsGroupId == 0 ? null : this.obsGroupId, this.accessionNumber, this.valueGroupId, this.valueBoolean, this.valueCoded == 0 ? null : this.valueCoded, this.valueCodedNameId == 0 ? null : this.valueCodedNameId, this.valueDrug == 0 ? null : this.valueDrug, this.valueDatetime, this.valueNumeric, this.valueModifier, this.valueText, this.comments, this.creator == 0 ? null : this.creator, this.dateCreated, this.voided, this.voidedBy == 0 ? null : this.voidedBy, this.dateVoided, this.voidReason, this.valueComplex, this.uuid, this.previousVersion == 0 ? null : this.previousVersion, this.formNamespaceAndPath, this.consistent, this.lastSyncDate, this.originRecordId, this.dateChanged, this.originAppLocationCode, this.obsId};		return params; 
	} 
 
	@JsonIgnore
	public String getInsertSQL(){ 
 		return "INSERT INTO obs(person_id, concept_id, encounter_id, order_id, obs_datetime, location_id, obs_group_id, accession_number, value_group_id, value_boolean, value_coded, value_coded_name_id, value_drug, value_datetime, value_numeric, value_modifier, value_text, comments, creator, date_created, voided, voided_by, date_voided, void_reason, value_complex, uuid, previous_version, form_namespace_and_path, consistent, last_sync_date, origin_record_id, date_changed, origin_app_location_code) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	public String getUpdateSQL(){ 
 		return "UPDATE obs SET person_id = ?, concept_id = ?, encounter_id = ?, order_id = ?, obs_datetime = ?, location_id = ?, obs_group_id = ?, accession_number = ?, value_group_id = ?, value_boolean = ?, value_coded = ?, value_coded_name_id = ?, value_drug = ?, value_datetime = ?, value_numeric = ?, value_modifier = ?, value_text = ?, comments = ?, creator = ?, date_created = ?, voided = ?, voided_by = ?, date_voided = ?, void_reason = ?, value_complex = ?, uuid = ?, previous_version = ?, form_namespace_and_path = ?, consistent = ?, last_sync_date = ?, origin_record_id = ?, date_changed = ?, origin_app_location_code = ? WHERE obs_id = ?;"; 
	} 
 
	@JsonIgnore
	public String generateInsertValues(){ 
 		return ""+(this.personId == 0 ? null : this.personId) + "," + (this.conceptId == 0 ? null : this.conceptId) + "," + (this.encounterId == 0 ? null : this.encounterId) + "," + (this.orderId == 0 ? null : this.orderId) + "," + (this.obsDatetime != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(obsDatetime)  +"\"" : null) + "," + (this.locationId == 0 ? null : this.locationId) + "," + (this.obsGroupId == 0 ? null : this.obsGroupId) + "," + (this.accessionNumber != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(accessionNumber))  +"\"" : null) + "," + (this.valueGroupId) + "," + (this.valueBoolean) + "," + (this.valueCoded == 0 ? null : this.valueCoded) + "," + (this.valueCodedNameId == 0 ? null : this.valueCodedNameId) + "," + (this.valueDrug == 0 ? null : this.valueDrug) + "," + (this.valueDatetime != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(valueDatetime)  +"\"" : null) + "," + (this.valueNumeric) + "," + (this.valueModifier != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(valueModifier))  +"\"" : null) + "," + (this.valueText != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(valueText))  +"\"" : null) + "," + (this.comments != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(comments))  +"\"" : null) + "," + (this.creator == 0 ? null : this.creator) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateCreated)  +"\"" : null) + "," + (this.voided) + "," + (this.voidedBy == 0 ? null : this.voidedBy) + "," + (this.dateVoided != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateVoided)  +"\"" : null) + "," + (this.voidReason != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(voidReason))  +"\"" : null) + "," + (this.valueComplex != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(valueComplex))  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(uuid))  +"\"" : null) + "," + (this.previousVersion == 0 ? null : this.previousVersion) + "," + (this.formNamespaceAndPath != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(formNamespaceAndPath))  +"\"" : null) + "," + (this.consistent) + "," + (this.lastSyncDate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(lastSyncDate)  +"\"" : null) + "," + (this.originRecordId) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateChanged)  +"\"" : null) + "," + (this.originAppLocationCode != null ? "\""+ utilities.scapeQuotationMarks(AttDefinedElements.removeStrangeCharactersOnString(originAppLocationCode))  +"\"" : null); 
	} 
 
	@Override
	public boolean isGeneratedFromSkeletonClass() {
		return false;
	}

	@Override
	public boolean hasParents() {
		if (this.valueCoded != 0) return true;
		if (this.valueDrug != 0) return true;
		if (this.encounterId != 0) return true;
		if (this.conceptId != 0) return true;
		if (this.creator != 0) return true;
		if (this.obsGroupId != 0) return true;
		if (this.locationId != 0) return true;
		if (this.valueCodedNameId != 0) return true;
		if (this.orderId != 0) return true;
		if (this.personId != 0) return true;
		if (this.previousVersion != 0) return true;
		if (this.voidedBy != 0) return true;
		return false;
	}

	@Override
	public int retrieveSharedPKKey(Connection conn) throws ParentNotYetMigratedException, DBException {
		throw new RuntimeException("No PKSharedInfo defined!");	}

	@Override
	public void loadDestParentInfo(Connection conn) throws ParentNotYetMigratedException, DBException {
		OpenMRSObject parentOnDestination = null;
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.EncounterVO.class, this.encounterId, true, conn); 
		this.encounterId = 0;
		if (parentOnDestination  != null) this.encounterId = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.UsersVO.class, this.creator, false, conn); 
		this.creator = 0;
		if (parentOnDestination  != null) this.creator = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.ObsVO.class, this.obsGroupId, true, conn); 
		this.obsGroupId = 0;
		if (parentOnDestination  != null) this.obsGroupId = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.LocationVO.class, this.locationId, true, conn); 
		this.locationId = 0;
		if (parentOnDestination  != null) this.locationId = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.OrdersVO.class, this.orderId, true, conn); 
		this.orderId = 0;
		if (parentOnDestination  != null) this.orderId = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.PersonVO.class, this.personId, false, conn); 
		this.personId = 0;
		if (parentOnDestination  != null) this.personId = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.ObsVO.class, this.previousVersion, true, conn); 
		this.previousVersion = 0;
		if (parentOnDestination  != null) this.previousVersion = parentOnDestination.getObjectId();
 
		parentOnDestination = loadParent(org.openmrs.module.eptssync.model.openmrs.destinationpkg.UsersVO.class, this.voidedBy, true, conn); 
		this.voidedBy = 0;
		if (parentOnDestination  != null) this.voidedBy = parentOnDestination.getObjectId();
 
	}

	@Override
	public int getParentValue(String parentAttName) {		
		if (parentAttName.equals("valueCoded")) return this.valueCoded;		
		if (parentAttName.equals("valueDrug")) return this.valueDrug;		
		if (parentAttName.equals("encounterId")) return this.encounterId;		
		if (parentAttName.equals("conceptId")) return this.conceptId;		
		if (parentAttName.equals("creator")) return this.creator;		
		if (parentAttName.equals("obsGroupId")) return this.obsGroupId;		
		if (parentAttName.equals("locationId")) return this.locationId;		
		if (parentAttName.equals("valueCodedNameId")) return this.valueCodedNameId;		
		if (parentAttName.equals("orderId")) return this.orderId;		
		if (parentAttName.equals("personId")) return this.personId;		
		if (parentAttName.equals("previousVersion")) return this.previousVersion;		
		if (parentAttName.equals("voidedBy")) return this.voidedBy;

		throw new RuntimeException("No found parent for: " + parentAttName);	}


}