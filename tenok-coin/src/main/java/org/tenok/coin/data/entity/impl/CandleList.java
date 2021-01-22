package org.tenok.coin.data.entity.impl;

import java.util.Stack;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;


@SuppressWarnings("serial")
public class CandleList extends Stack<Candle>{
    private CoinEnum coinType = null;
    private IntervalEnum interval = null;

    public CandleList(CoinEnum coinType, IntervalEnum interval) {
        this.coinType = coinType;
        this.interval = interval;
    }

    public CoinEnum getCoinType() {
        return this.coinType;
    }

    public IntervalEnum getInterval() {
        return this.interval;
    }

    @Override
    @Deprecated
    public synchronized Candle pop() {
        throw new RuntimeException("호출하지 마세요.");
    }
}
