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
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
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
    
}
