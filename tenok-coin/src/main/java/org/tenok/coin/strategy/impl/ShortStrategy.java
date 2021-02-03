package org.tenok.coin.strategy.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class ShortStrategy implements Strategy {
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;

    public ShortStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
    }

    @Override
    public double testOpenRBI() {
        CandleList candleList = coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);
        if (candleList.getReversed(1).getMa5() != 0 && candleList.getReversed(1).getMa10() != 0) {

            if (candleList.getReversed(1).getMa5() > candleList.getReversed(1).getMa10()) {

                if (candleList.getReversed(0).getMa5() < candleList.getReversed(0).getMa10()) {
                    return 1;
                }

            }
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        CandleList candleList = coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);
        if (candleList.getReversed(0).getMa5() > candleList.getReversed(0).getMa10()) {
            return true;
        }
        return false;
    }

    @Override
    public void setIsopened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public boolean isNotOpened() {
        return isOpened;
    }

    @Override
    public CoinEnum getCoinType() {
        return coinType;
    }

}
