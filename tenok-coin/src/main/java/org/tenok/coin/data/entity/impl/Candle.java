package org.tenok.coin.data.entity.impl;

import java.util.Date;

import lombok.Setter;


@Setter
public class Candle {
    private Date startAt;
    private double volume;
    private double open;
    private double high;
    private double low;
    private double close;
    private double ma5;
    private double ma10;
    private double ma20;
    private double ma60;
    private double ma120;
    private double upperBB;
    private double middleBB;
    private double lowerBB;
    private boolean isConfirmed;

    public Candle(Date startAt, double volume, double open, double high, double low, double close) {
        this.startAt = startAt;
        this.volume = volume;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public Candle() {
        
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

    public double getMa5() {
        return this.ma5;
    }

    public double getMa10() {
        return this.ma10;
    }

    public double getMa20() {
        return this.ma20;
    }

    public double getMa60() {
        return this.ma60;
    }

    public double getMa120() {
        return this.ma120;
    }

    public double getUpperBB() {
        return this.upperBB;
    }

    public double getMiddleBB() {
        return this.middleBB;
    }

    public double getLowerBB() {
        return this.lowerBB;
    }

    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    @Override
    public String toString() {
        return String.format("open: %f, high:%f, close: %f, low: %f", open, high, close, low);
    }
}
