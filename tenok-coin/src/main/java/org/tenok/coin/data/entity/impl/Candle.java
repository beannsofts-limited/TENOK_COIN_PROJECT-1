package org.tenok.coin.data.entity.impl;

import java.util.Date;

public class Candle {
    private Date startAt = null;
    private double volume = 0;
    private double open = 0;
    private double high = 0;
    private double low = 0;
    private double close = 0;

    public Candle(Date startAt, double volume, double open, double high, double low, double close) {
        this.startAt = startAt;
        this.volume = volume;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public Date getStartAt() {
        return this.startAt;
    }

    public double getVolume() {
        return this.volume;
    }

    public double getOpen() {
        return this.open;
    }

    public double getHigh() {
        return this.high;
    }

    public double getLow() {
        return this.low;
    }

    public double getClose() {
        return this.close;
    }
}