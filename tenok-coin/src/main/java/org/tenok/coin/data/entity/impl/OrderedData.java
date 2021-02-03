package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.OrderDataAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class OrderedData implements OrderDataAccessable {
    @NonNull
    private SideEnum side;
    @NonNull
    private CoinEnum coinType;
    @NonNull
    private OrderTypeEnum orderType;
    @NonNull
    private TIFEnum tif;
    private double qty;

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
        return null;
    }

    @Override
    @Deprecated
    public boolean isOpen() {
        return false;
    }

    @Override
    @Deprecated
    public double getEntryPrice() {
        return 0;
    }

    @Override
    @Deprecated
    public double getExitPrice() {
        return 0;
    }

    @Override
    public int getLeverage() {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
