package org.tenok.coin.type;

public enum IntervalEnum {
    /**
     * 1
     */
    ONE("1", 60, 1),

    /**
     * 3
     */
    THREE("3", 180, 3),

    /**
     * 5
     */
    FIVE("5", 300, 5),

    /**
     * 15
     */
    FIFTEEN("15", 900, 15),

    /**
     * 30
     */
    THIRTY("30", 1800, 30),

    /**
     * 60
     */
    ONE_HOUR("60", 3600, 60),

    /**
     * 120
     */
    TWO_HOUR("120", 7200, 120),

    /**
     * 240
     */
    FOUR_HOUR("240", 14400, 240),

    /**
     * 360
     */
    SIX_HOUR("360", 21600, 360),

    /**
     * D
     */
    DAY("D", 86400, 1440),

    /**
     * W
     */
    WEEK("W", 604800, 10080),

    /**
     * M: 초 데이터 정확한지 확인 필요
     */
    @Deprecated
    MONTH("M", 24190200, 43200);

    private String apiString;
    private long sec;
    private long backtestNumber;

    private IntervalEnum(String apiString, long sec, long backtestNumber) {
        this.apiString = apiString;
        this.sec = sec;
        this.backtestNumber = backtestNumber;

    }

    public String getApiString() {
        return apiString;
    }

    public long getSec() {
        return sec;
    }

    public long getBacktestNumber() {
        return backtestNumber;
    }

    public static IntervalEnum valueOfApiString(String literal) {
        for (var interval : IntervalEnum.values()) {
            if (interval.getApiString().equalsIgnoreCase(literal)) {
                return interval;
            }
        }
        return null;
    }
}
