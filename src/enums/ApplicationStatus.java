package enums;

public enum ApplicationStatus {
	SUBMITTED("Submitted"),
	EVALUATED("Evaluated"),
	APPROVED("Approved"),
	REJECTED("Rejected"),
	WITHDRAWN("Withdrawn");
	
	private final String databaseValue;
	
	ApplicationStatus(String databaseValue){
		this.databaseValue = databaseValue;
	}
	
	public String toDatabaseValue() {
		return databaseValue;
	}
	
	public static ApplicationStatus fromDatabaseValue(String value) {
		for(ApplicationStatus type : values()) {
			if(type.databaseValue.equalsIgnoreCase(value)) {
				return type;
			}
		}
		
		throw new IllegalArgumentException("Unknown Application Status: " + value);
	}
}
