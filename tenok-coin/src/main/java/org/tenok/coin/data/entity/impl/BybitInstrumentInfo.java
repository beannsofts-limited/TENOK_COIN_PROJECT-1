package org.tenok.coin.data.entity.impl;

import java.util.Date;

import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.type.CoinEnum;


public class BybitInstrumentInfo implements InstrumentInfo {
    private CoinEnum coinType = null;
    private int lastPriceE4 = 0;
    private Date creationTime = null;



    public BybitInstrumentInfo(CoinEnum coinType, int lastPriceE4, Date creationTime) {
        this.coinType = coinType;
        this.lastPriceE4 = lastPriceE4;
        this.creationTime = creationTime;
    }

    public int getLastPriceE4() {
        return this.lastPriceE4;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

	@Override
	public CoinEnum getCoinType() {
		return this.coinType;
	}
    
}