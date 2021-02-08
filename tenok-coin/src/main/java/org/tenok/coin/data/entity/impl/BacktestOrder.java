package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.OrderDataAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.Builder;

/**
 * Backtest 할 때. 진입가격, 수익률 등을 기록하기 위한 클래스
 */
@Builder
public class BacktestOrder implements OrderDataAccessable {
    @Override
    public SideEnum getSide() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CoinEnum getCoinType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrderTypeEnum getOrderType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIFEnum getTIF() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getQty() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Date getTimeStamp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getEntryPrice() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getExitPrice() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Date getEntryDate() {
        return null;
    }

    public Date getExitDate() {
        return null;
    }

    @Override
    public int getLeverage() {
        // TODO Auto-generated method stub
        return 0;
    }

}
