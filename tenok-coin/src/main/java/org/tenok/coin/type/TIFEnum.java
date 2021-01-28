package org.tenok.coin.type;

/**
 * Time in Force
 */
public enum TIFEnum {
    GTC("GoodTillCancel"),
    IOC("ImmediateOrCancel"),
    FOK("FillOrKill"),
    PO("PostOnly");

    private String apiString;

    private TIFEnum(String apiString) {
        this.apiString = apiString;
    }

    public String getApiString() {
        return apiString;
    }

    public static TIFEnum valueOfApiString(String value) {
        for (TIFEnum tif : TIFEnum.values()) {
            if (tif.getApiString().equals(value)) {
                return tif;
            }
        }
        throw new IllegalArgumentException(String.format("No enum constant %s", value));
    }
}
