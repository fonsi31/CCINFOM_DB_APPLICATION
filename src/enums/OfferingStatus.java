package enums;

public enum OfferingStatus {
	PLANNED("Planned"),
	OPEN("Open"),
	CLOSED("Closed"),
	CANCELED("Cancelled"); //the database spells it 'Cancelled'
	
	private final String databaseValue;
	
	OfferingStatus(String databaseValue){
		this.databaseValue = databaseValue;
	}
	
	public String toDatabaseValue() {
		return databaseValue;
	}
	
	//accepts the database value and both spellings of cancelled
	public static OfferingStatus fromDatabaseValue(String value) {
		if(value != null && value.trim().equalsIgnoreCase("Canceled")) {
			return CANCELED;
		}
		
		for(OfferingStatus type : values()) {
			if(type.databaseValue.equalsIgnoreCase(value == null ? null : value.trim())) {
				return type;
			}
		}
		
		throw new IllegalArgumentException("Unknown Offering Status: " + value);
	}
}
