package org.tenok.coin.type;

public enum TickDirectionEnum {
    PLUS("PlusTick"),
    ZERO_PLUS("ZeroPlusTick"),
    MINUS("MinusTick"),
    ZERO_MINUS("ZeroMinusTick");

    private String apiString;

    private TickDirectionEnum(String apiString) {
        this.apiString = apiString;
    }

    public String getApiString() {
        return this.apiString;
    }

    public static TickDirectionEnum valueOfApiString(String value) {
        for (TickDirectionEnum tickDirection : TickDirectionEnum.values()) {
            if (tickDirection.getApiString().equals(value)) {
                return tickDirection;
            }
        }
        throw new IllegalArgumentException(String.format("No enum constant %s", value));
    }
}
