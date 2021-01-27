package org.tenok.coin.type;

public enum IntervalEnum {
    /**
     * 1
     */
    ONE("1", 60),

    /**
     * 3
     */
    THREE("3", 180),

    /**
     * 5
     */
    FIVE("5", 300),

    /**
     * 15
     */
    FIFTEEN("15", 900),

    /**
     * 30
     */
    THIRTY("30", 1800),

    /**
     * 60
     */
    SIXTY("60", 3600),

    /**
     * 120
     */
    HUNDREDTWENTY("120", 7200),

    /**
     * 240
     */
    TWOHUNDREDFORTY("240", 14400),

    /**
     * 360
     */
    THREEHOUNDREDSIXTY("360", 21600),

    /**
     * D
     */
    DAY("D", 86400),

    /**
     * W
     */
    WEEK("W", 604800),

    /**
     * M: 초 데이터 정확한지 확인 필요
     */
    @Deprecated
    MONTH("M", 24190200);

    private String apiString;
    private long sec;

    private IntervalEnum(String apiString, long sec) {
        this.apiString = apiString;
        this.sec = sec;
    }

    public String getApiString() {
        return apiString;
    }

    public long getSec() {
        return sec;
    }

    public static IntervalEnum valueOfApiString(String literal) {
        for (var interval: IntervalEnum.values()) {
            if (interval.getApiString().equalsIgnoreCase(literal)) {
                return interval;
            }
        }
        return null;
    }
}
