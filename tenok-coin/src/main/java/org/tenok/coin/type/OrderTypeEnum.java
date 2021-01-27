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
}
