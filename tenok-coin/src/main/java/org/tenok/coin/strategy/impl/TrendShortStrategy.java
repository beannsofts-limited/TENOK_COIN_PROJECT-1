package org.tenok.coin.strategy.impl;

import java.util.Date;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class TrendShortStrategy implements Strategy{
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;
    private CandleList candleList;
    private MovingAverage ma;
    private double entryPrice;
    private Date standardDate = null;

    public TrendShortStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
        candleList = this.coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);
        ma = candleList.createIndex(new MovingAverage());
    }

    @Override
    public double testOpenRBI() {
        //새로운 캔들이 갱신될때마다 봉초가에 open
        //만약 2분선이 20분 선 아래에 있을 경우에는 무조건 숏 포지션
        if(standardDate != candleList.getReversed(0).getStartAt()){
            if(candleList.getReversed(0).getOpen() <= ma.getReversed(1).getMa10()){
                standardDate = candleList.getReversed(0).getStartAt();
                return 1; 
            }
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
         //15분봉의 종가에 청산
         if(standardDate != candleList.getReversed(0).getStartAt()){   
            return true;
        }

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

    private double getProfitPercent() {
        return (1.0-(coinDAO.getCurrentPrice(coinType) / entryPrice)) * 100;
    }
}
