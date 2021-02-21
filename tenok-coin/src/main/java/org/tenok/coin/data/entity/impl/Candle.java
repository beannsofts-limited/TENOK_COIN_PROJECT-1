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

    public void updateData(Candle obj) {
        this.startAt = obj.startAt;
        this.volume = obj.volume;
        this.open = obj.open;
        this.high = obj.high;
        this.low = obj.low;
        this.close = obj.close;
        this.isConfirmed = obj.isConfirmed;
    }

    /**
     * @return open 시간
     */
    public Date getStartAt() {
        return this.startAt;
    }

    /**
     * 
     * @return 거래량
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * 
     * @return 시가
     */
    public double getOpen() {
        return this.open;
    }

    /**
     * @return 고가
     */
    public double getHigh() {
        return this.high;
    }

    /**
     * 
     * @return 저가
     */
    public double getLow() {
        return this.low;
    }

    /**
     * @return 종가
     */
    public double getClose() {
        return this.close;
    }

    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    @Override
    public String toString() {
        return String.format("open: %f, high:%f, close: %f, low: %f", open, high, close, low);
    }
}
