package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

public class ActiveOrder implements Orderable {

    @Override
    public SideEnum getSide() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CoinEnum getCoin() {
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
    
}
