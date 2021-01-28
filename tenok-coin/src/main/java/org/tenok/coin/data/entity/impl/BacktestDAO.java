package org.tenok.coin.data.entity.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.Backtestable;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BacktestDAO implements CoinDataAccessable, Backtestable {

    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrderedList getOrderList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PositionList getPositionList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InstrumentInfo getInsturmentInfo(CoinEnum coinType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WalletAccessable getWalletInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public void orderCoin(Orderable order) {
        // TODO Auto-generated method stub

    }

    public void orderCoin(Orderable order, Candle candle) {
        // 진입가. 청산가. 주문 시간.
    }

    @Override
    public void getPaidLimit(CoinEnum coinType) {
        // TODO Auto-generated method stub

    }

    
    
}
