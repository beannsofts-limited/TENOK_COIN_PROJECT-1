package org.tenok.coin.strategy.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.extern.log4j.Log4j;

@Log4j
public class LongStrategy implements Strategy {
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;
    CandleList candleList;

    public LongStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
    }

    @Override
    public double testOpenRBI() {
        synchronized(this) {
            // candleList = coinDAO.getCandleList(coinType, IntervalEnum.THREE);
            // System.out.print("\r"+candleList.getReversed(0).getMa5() + " " + candleList.getReversed(0).getMa10());
            // if (candleList.getReversed(1).getMa5() != 0 && candleList.getReversed(1).getMa10() != 0 &&  candleList.getReversed(1).getMa5() < candleList.getReversed(1).getMa10()
            //         && candleList.getReversed(0).getMa5() > candleList.getReversed(0).getMa10()) {
            //     return 1;
            // }
            // return 0;
            return 1;
        }
    }

    @Override
    public boolean testCloseRBI() {
        candleList = coinDAO.getCandleList(coinType, IntervalEnum.HUNDREDTWENTY);
        // if (candleList.getReversed(0).getMa5() < candleList.getReversed(0).getMa10()) {
        //     return true;
        // }
        return false;
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
