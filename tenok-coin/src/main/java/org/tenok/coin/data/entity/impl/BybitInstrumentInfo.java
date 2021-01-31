package org.tenok.coin.data.entity.impl;

import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.TickDirectionEnum;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Setter
@Accessors(fluent = true)
@ToString
public class BybitInstrumentInfo implements InstrumentInfo {
    @NonNull
    private CoinEnum coinType;
    private Long lastPriceE4;
    private TickDirectionEnum lastTickDirection;
    private Long price24hPcntE6;
    private Long highPrice24hE4;
    private Long lowPrice24hE4;
    private Long price1hPcntE6;
    private Long highPrice1hE4;
    private Long lowPrice1hE4;

    @Override
    public CoinEnum getCoinType() {
        return this.coinType;
    }

    @Override
    public long getLastPriceE4() {
        return lastPriceE4;
    }

    @Override
    public TickDirectionEnum getLastTickDirection() {
        return lastTickDirection;
    }

    @Override
    public long getPrice24hPcntE6() {
        return price24hPcntE6;
    }

    @Override
    public long getHighPrice24hE4() {
        return highPrice24hE4;
    }

    @Override
    public long getLowPrice24hE4() {
        return lowPrice24hE4;
    }

    @Override
    public long getPrice1hPcntE6() {
        return price1hPcntE6;
    }

    @Override
    public long getHighPrice1hE4() {
        return highPrice1hE4;
    }

    @Override
    public long getLowPrice1hE4() {
        return lowPrice1hE4;
    }   
}
