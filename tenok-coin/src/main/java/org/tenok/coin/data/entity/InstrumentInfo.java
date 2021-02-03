package org.tenok.coin.data.entity;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.TickDirectionEnum;

public interface InstrumentInfo {
    public CoinEnum getCoinType();

    public long getLastPriceE4();

    public TickDirectionEnum getLastTickDirection();

    public long getPrice24hPcntE6();

    public long getHighPrice24hE4();

    public long getLowPrice24hE4();

    public long getPrice1hPcntE6();

    public long getHighPrice1hE4();

    public long getLowPrice1hE4();
}
