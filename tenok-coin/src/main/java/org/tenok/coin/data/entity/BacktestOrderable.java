package org.tenok.coin.data.entity;

import org.tenok.coin.type.CoinEnum;

public interface BacktestOrderable {
    public double getRealtimeProfit(CoinEnum coinType, Orderable order);

    public double getWholeProfit();

    public void nextSeq();
}
