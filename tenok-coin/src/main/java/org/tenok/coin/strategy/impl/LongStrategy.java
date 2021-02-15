package org.tenok.coin.strategy.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class LongStrategy implements Strategy {
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;
    CandleList candleList;
    MovingAverage ma;

    public LongStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
        candleList = this.coinDAO.getCandleList(coinType, IntervalEnum.ONE);
        ma = candleList.createIndex(new MovingAverage());
    }

    @Override
    public double testOpenRBI() {
        if (ma.getReversed(2).getMa5() < ma.getReversed(2).getMa20()
                && ma.getReversed(1).getMa5() > ma.getReversed(1).getMa20()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        return !(ma.getReversed(2).getMa5() < ma.getReversed(2).getMa20()
                && ma.getReversed(1).getMa5() > ma.getReversed(1).getMa20());
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
    public CoinEnum getCoinType() {
        return coinType;
    }

    @Override
    public void setIsopened(boolean isOpened) {
        this.isOpened = isOpened;
    }

}
