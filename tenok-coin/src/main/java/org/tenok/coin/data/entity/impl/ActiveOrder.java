package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class ActiveOrder implements Orderable {
    @NonNull
    private SideEnum side;

    @NonNull
    private CoinEnum coinType;

    @NonNull
    private OrderTypeEnum orderType;

    @NonNull
    private TIFEnum tif;

    @NonNull
    private double qty;

    private Date timeStamp;

    @Override
    public SideEnum getSide() {
        return side;
    }

    @Override
    public CoinEnum getCoin() {
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
        return timeStamp;
    }
    
}
