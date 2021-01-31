package org.tenok.coin.type;

public enum OrderTypeEnum {
    MARKET("Market"),
    LIMIT("Limit");

    private String apiString;

    private OrderTypeEnum(String apiString) {
        this.apiString = apiString;
    }

    public String getApiString() {
        return this.apiString;
    }

    public static OrderTypeEnum valueOfApiString(String value) {
        for (OrderTypeEnum orderType : OrderTypeEnum.values()) {
            if (orderType.getApiString().equals(value)) {
                return orderType;
            }
        }
        throw new IllegalArgumentException(String.format("No enum constant %s", value));
    }
}
