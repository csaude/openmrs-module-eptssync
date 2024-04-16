package org.openmrs.module.epts.etl.model.pojo.openmrs._default;

import org.openmrs.module.epts.etl.model.pojo.generic.*; 
 
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities; 
 
import org.openmrs.module.epts.etl.utilities.AttDefinedElements; 
 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class PersonVO extends AbstractDatabaseObject implements DatabaseObject { 
	private Integer personId;
	private String gender;
	private java.util.Date birthdate;
	private Byte birthdateEstimated;
	private Byte dead;
	private java.util.Date deathDate;
	private Integer causeOfDeath;
	private Integer creator;
	private Integer changedBy;
	private Byte voided;
	private Integer voidedBy;
	private java.util.Date dateVoided;
	private String voidReason;
	private Byte deathdateEstimated;
	private java.util.Date birthtime;
	private String causeOfDeathNonCoded;
 
	public PersonVO() { 
		this.metadata = false;
	} 
 
	public void setPersonId(Integer personId){ 
	 	this.personId = personId;
	}
 
	public Integer getPersonId(){ 
		return this.personId;
	}
 
	public void setGender(String gender){ 
	 	this.gender = gender;
	}
 
	public String getGender(){ 
		return this.gender;
	}
 
	public void setBirthdate(java.util.Date birthdate){ 
	 	this.birthdate = birthdate;
	}
 
	public java.util.Date getBirthdate(){ 
		return this.birthdate;
	}
 
	public void setBirthdateEstimated(Byte birthdateEstimated){ 
	 	this.birthdateEstimated = birthdateEstimated;
	}
 
	public Byte getBirthdateEstimated(){ 
		return this.birthdateEstimated;
	}
 
	public void setDead(Byte dead){ 
	 	this.dead = dead;
	}
 
	public Byte getDead(){ 
		return this.dead;
	}
 
	public void setDeathDate(java.util.Date deathDate){ 
	 	this.deathDate = deathDate;
	}
 
	public java.util.Date getDeathDate(){ 
		return this.deathDate;
	}
 
	public void setCauseOfDeath(Integer causeOfDeath){ 
	 	this.causeOfDeath = causeOfDeath;
	}
 
	public Integer getCauseOfDeath(){ 
		return this.causeOfDeath;
	}
 
	public void setCreator(Integer creator){ 
	 	this.creator = creator;
	}
 
	public Integer getCreator(){ 
		return this.creator;
	}
 
	public void setChangedBy(Integer changedBy){ 
	 	this.changedBy = changedBy;
	}
 
	public Integer getChangedBy(){ 
		return this.changedBy;
	}
 
	public void setVoided(Byte voided){ 
	 	this.voided = voided;
	}
 
	public Byte getVoided(){ 
		return this.voided;
	}
 
	public void setVoidedBy(Integer voidedBy){ 
	 	this.voidedBy = voidedBy;
	}
 
	public Integer getVoidedBy(){ 
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
 
	public void setDeathdateEstimated(Byte deathdateEstimated){ 
	 	this.deathdateEstimated = deathdateEstimated;
	}
 
	public Byte getDeathdateEstimated(){ 
		return this.deathdateEstimated;
	}
 
	public void setBirthtime(java.util.Date birthtime){ 
	 	this.birthtime = birthtime;
	}
 
	public java.util.Date getBirthtime(){ 
		return this.birthtime;
	}
 
	public void setCauseOfDeathNonCoded(String causeOfDeathNonCoded){ 
	 	this.causeOfDeathNonCoded = causeOfDeathNonCoded;
	}


 
	public String getCauseOfDeathNonCoded(){ 
		return this.causeOfDeathNonCoded;
	}
 
	@Override
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
 
if (rs.getObject("person_id") != null) this.personId = rs.getInt("person_id");
this.gender = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("gender") != null ? rs.getString("gender").trim() : null);
this.birthdate =  rs.getTimestamp("birthdate") != null ? new java.util.Date( rs.getTimestamp("birthdate").getTime() ) : null;
this.birthdateEstimated = rs.getByte("birthdate_estimated");
this.dead = rs.getByte("dead");
this.deathDate =  rs.getTimestamp("death_date") != null ? new java.util.Date( rs.getTimestamp("death_date").getTime() ) : null;
if (rs.getObject("cause_of_death") != null) this.causeOfDeath = rs.getInt("cause_of_death");
if (rs.getObject("creator") != null) this.creator = rs.getInt("creator");
this.dateCreated =  rs.getTimestamp("date_created") != null ? new java.util.Date( rs.getTimestamp("date_created").getTime() ) : null;
if (rs.getObject("changed_by") != null) this.changedBy = rs.getInt("changed_by");
this.dateChanged =  rs.getTimestamp("date_changed") != null ? new java.util.Date( rs.getTimestamp("date_changed").getTime() ) : null;
this.voided = rs.getByte("voided");
if (rs.getObject("voided_by") != null) this.voidedBy = rs.getInt("voided_by");
this.dateVoided =  rs.getTimestamp("date_voided") != null ? new java.util.Date( rs.getTimestamp("date_voided").getTime() ) : null;
this.voidReason = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("void_reason") != null ? rs.getString("void_reason").trim() : null);
this.uuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("uuid") != null ? rs.getString("uuid").trim() : null);
this.deathdateEstimated = rs.getByte("deathdate_estimated");
this.birthtime =  rs.getTimestamp("birthtime") != null ? new java.util.Date( rs.getTimestamp("birthtime").getTime() ) : null;
this.causeOfDeathNonCoded = AttDefinedElements.removeStrangeCharactersOnString(rs.getString("cause_of_death_non_coded") != null ? rs.getString("cause_of_death_non_coded").trim() : null);
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO person(gender, birthdate, birthdate_estimated, dead, death_date, cause_of_death, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid, deathdate_estimated, birthtime, cause_of_death_non_coded) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO person(person_id, gender, birthdate, birthdate_estimated, dead, death_date, cause_of_death, creator, date_created, changed_by, date_changed, voided, voided_by, date_voided, void_reason, uuid, deathdate_estimated, birthtime, cause_of_death_non_coded) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.gender, this.birthdate, this.birthdateEstimated, this.dead, this.deathDate, this.causeOfDeath, this.creator, this.dateCreated, this.changedBy, this.dateChanged, this.voided, this.voidedBy, this.dateVoided, this.voidReason, this.uuid, this.deathdateEstimated, this.birthtime, this.causeOfDeathNonCoded};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.personId, this.gender, this.birthdate, this.birthdateEstimated, this.dead, this.deathDate, this.causeOfDeath, this.creator, this.dateCreated, this.changedBy, this.dateChanged, this.voided, this.voidedBy, this.dateVoided, this.voidReason, this.uuid, this.deathdateEstimated, this.birthtime, this.causeOfDeathNonCoded};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.personId, this.gender, this.birthdate, this.birthdateEstimated, this.dead, this.deathDate, this.causeOfDeath, this.creator, this.dateCreated, this.changedBy, this.dateChanged, this.voided, this.voidedBy, this.dateVoided, this.voidReason, this.uuid, this.deathdateEstimated, this.birthtime, this.causeOfDeathNonCoded, this.personId};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getUpdateSQL(){ 
 		return "UPDATE person SET person_id = ?, gender = ?, birthdate = ?, birthdate_estimated = ?, dead = ?, death_date = ?, cause_of_death = ?, creator = ?, date_created = ?, changed_by = ?, date_changed = ?, voided = ?, voided_by = ?, date_voided = ?, void_reason = ?, uuid = ?, deathdate_estimated = ?, birthtime = ?, cause_of_death_non_coded = ? WHERE person_id = ? "; 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId(){ 
 		return ""+(this.gender != null ? "\""+ utilities.scapeQuotationMarks(gender)  +"\"" : null) + "," + (this.birthdate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(birthdate)  +"\"" : null) + "," + (this.birthdateEstimated) + "," + (this.dead) + "," + (this.deathDate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(deathDate)  +"\"" : null) + "," + (this.causeOfDeath) + "," + (this.creator) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateCreated)  +"\"" : null) + "," + (this.changedBy) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateChanged)  +"\"" : null) + "," + (this.voided) + "," + (this.voidedBy) + "," + (this.dateVoided != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateVoided)  +"\"" : null) + "," + (this.voidReason != null ? "\""+ utilities.scapeQuotationMarks(voidReason)  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(uuid)  +"\"" : null) + "," + (this.deathdateEstimated) + "," + (this.birthtime != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(birthtime)  +"\"" : null) + "," + (this.causeOfDeathNonCoded != null ? "\""+ utilities.scapeQuotationMarks(causeOfDeathNonCoded)  +"\"" : null); 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId(){ 
 		return ""+(this.personId) + "," + (this.gender != null ? "\""+ utilities.scapeQuotationMarks(gender)  +"\"" : null) + "," + (this.birthdate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(birthdate)  +"\"" : null) + "," + (this.birthdateEstimated) + "," + (this.dead) + "," + (this.deathDate != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(deathDate)  +"\"" : null) + "," + (this.causeOfDeath) + "," + (this.creator) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateCreated)  +"\"" : null) + "," + (this.changedBy) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateChanged)  +"\"" : null) + "," + (this.voided) + "," + (this.voidedBy) + "," + (this.dateVoided != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(dateVoided)  +"\"" : null) + "," + (this.voidReason != null ? "\""+ utilities.scapeQuotationMarks(voidReason)  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(uuid)  +"\"" : null) + "," + (this.deathdateEstimated) + "," + (this.birthtime != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS(birthtime)  +"\"" : null) + "," + (this.causeOfDeathNonCoded != null ? "\""+ utilities.scapeQuotationMarks(causeOfDeathNonCoded)  +"\"" : null); 
	} 
 
	@Override
	public boolean hasParents() {
		if (this.causeOfDeath != null) return true;

		if (this.changedBy != null) return true;

		if (this.creator != null) return true;

		if (this.voidedBy != null) return true;

		return false;
	}

	@Override
	public Integer getParentValue(String parentAttName) {		
		if (parentAttName.equals("causeOfDeath")) return this.causeOfDeath;		
		if (parentAttName.equals("changedBy")) return this.changedBy;		
		if (parentAttName.equals("creator")) return this.creator;		
		if (parentAttName.equals("voidedBy")) return this.voidedBy;

		throw new RuntimeException("No found parent for: " + parentAttName);	}

	@Override
	public String generateTableName() {
		return "person";
	}


}