package org.tenok.coin.strategy;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.type.CoinEnum;

public abstract class BasicStrategyAbstract implements Strategy {
    protected CoinDataAccessable coinDAO;
    protected CoinEnum coinType;
    protected boolean isOpened;

    protected BasicStrategyAbstract(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
    }

    @Override
    public CoinEnum getCoinType() {
        return coinType;
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public boolean isNotOpened() {
        return !isOpened;
    }

    @Override
    public void setIsopened(boolean isOpened) {
        this.isOpened = isOpened;
    }
}
