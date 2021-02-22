package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.OrderDataAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.Builder;
import lombok.Setter;

/**
 * Backtest 할 때. 진입가격, 수익률 등을 기록하기 위한 클래스
 */
@Builder
@Setter
public class BacktestOrder implements OrderDataAccessable {
    private SideEnum side;
    private CoinEnum coinType;
    private OrderTypeEnum orderType;
    private TIFEnum tif;
    private double qty;
    private Date timestamp;
    private double entryPrice;
    private double exitPrice;
    private Date exitDate;
    private int leverage;

    @Override
    public SideEnum getSide() {
        return side;
    }

    @Override
    public CoinEnum getCoinType() {
        return coinType;
    }

    @Override
    public OrderTypeEnum getOrderType() {
        return orderType;
    }

    @Override
    public TIFEnum getTIF() {
        return tif;
    }

    @Override
    public double getQty() {
        return qty;
    }

    @Override
    public Date getTimeStamp() {
        return timestamp;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public Date getEntryDate() {
        return getTimeStamp();
    }

    public Date getExitDate() {
        return exitDate;
    }

    @Override
    public int getLeverage() {
        return Math.abs(leverage);
    }

    public double getProfit() throws IllegalAccessException {
        if (exitDate == null) {
            throw new IllegalAccessException("거래가 종료되지 않음.");
        }
        return ((exitPrice / entryPrice) - 1) * 100 * leverage;
    }

    public double getEarnUSDT() throws IllegalAccessException {
        if (exitDate == null) {
            throw new IllegalAccessException("거래가 종료되지 않음.");
        }
        return (exitPrice - entryPrice) * qty * leverage;
    }

}
