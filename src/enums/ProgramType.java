package enums;

public enum ProgramType {
    FIRSTAWARD("FirstAward"),
    RENEWAL("Renewal");

    private final String databaseValue;

    ProgramType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String toDatabaseValue() {
        return databaseValue;
    }

    public static ProgramType fromDatabaseValue(String value) {
        for (ProgramType type : values()) {
            if (type.databaseValue.equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown program type: " + value);
    }
}