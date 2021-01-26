package org.tenok.coin.type;

public enum IntervalEnum {
    /**
     * 1
     */
    ONE("1"),

    /**
     * 3
     */
    THREE("3"),

    /**
     * 5
     */
    FIVE("5"),

    /**
     * 15
     */
    FIFTEEN("15"),

    /**
     * 30
     */
    THIRTY("30"),

    /**
     * 60
     */
    SIXTY("60"),

    /**
     * 120
     */
    HUNDREDTWENTY("120"),

    /**
     * 240
     */
    TWOHUNDREDFORTY("240"),

    /**
     * 360
     */
    THREEHOUNDREDSIXTY("360"),

    /**
     * D
     */
    DAY("D"),

    /**
     * W
     */
    WEEK("W"),

    /**
     * M
     */
    MONTH("M");

    private String literal;

    private IntervalEnum(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public static IntervalEnum valueOfLiteral(String literal) {
        for (var interval: IntervalEnum.values()) {
            if (interval.getLiteral().equalsIgnoreCase(literal)) {
                return interval;
            }
        }
        return null;
    }
}
