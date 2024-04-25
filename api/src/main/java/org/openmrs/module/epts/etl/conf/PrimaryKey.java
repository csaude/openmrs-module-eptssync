package org.openmrs.module.epts.etl.conf;

import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.Oid;

public class PrimaryKey extends UniqueKeyInfo {
	
	public boolean isSimpleNumericKey() {
		return !isCompositeKey() && retrieveSimpleKey().isNumericColumnType();
	}
	
	public Oid generateOid(DatabaseObject obj) {
		Oid oid = new Oid();
		
		for (Key key : this.getFields()) {
			Object keyValue;
			
			try {
				keyValue = obj.getFieldValue(key.getName());
			}
			catch (ForbiddenOperationException e) {
				keyValue = obj.getFieldValue(key.getNameAsClassAtt());
			}
			
			oid.addKey(new Key(key.getName(), key.getType(), keyValue));
		}
		
		return oid;
	}
	
}