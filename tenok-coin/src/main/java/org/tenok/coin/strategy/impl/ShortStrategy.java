package org.tenok.coin.strategy.impl;

import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;

public class ShortStrategy implements Strategy {

    @Override
    public double testOpenRBI() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOpened() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNotOpened() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CoinEnum getCoinType() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
