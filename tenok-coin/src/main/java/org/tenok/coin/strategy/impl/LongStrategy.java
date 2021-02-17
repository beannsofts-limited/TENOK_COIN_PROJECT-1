package org.tenok.coin.strategy.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.extern.log4j.Log4j;

@Log4j
public class LongStrategy implements Strategy {
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;
    private CandleList candleList;
    private MovingAverage ma;
    private double entryPrice;

    public LongStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
        candleList = this.coinDAO.getCandleList(coinType, IntervalEnum.FIVE);
        ma = candleList.createIndex(new MovingAverage());
    }

    @Override
    public double testOpenRBI() {
        if (ma.getReversed(2).getMa5() < ma.getReversed(2).getMa20()
                && ma.getReversed(1).getMa5() > ma.getReversed(1).getMa20()) {
            entryPrice = coinDAO.getCurrentPrice(coinType);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        if (getProfitPercent() >= 0.5 || getProfitPercent() <= -1.0) {
            log.info(String.format("%s %s 도달", coinType.getKorean(), (getProfitPercent() > 0) ? "익절가" : "손절가"));
            return true;
        }

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

    private double getProfitPercent() {
        return ((coinDAO.getCurrentPrice(coinType) / entryPrice) - 1.0) * 100;
    }

}
