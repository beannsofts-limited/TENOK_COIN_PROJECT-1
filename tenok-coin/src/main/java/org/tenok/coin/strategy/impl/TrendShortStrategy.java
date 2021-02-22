package org.tenok.coin.strategy.impl;



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
    private double entryPrice=0.0;
 
    private long standardDate = 0;

    public TrendShortStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        this.coinDAO = coinDAO;
        this.coinType = coinType;
        if(coinType == CoinEnum.BTCUSDT || coinType == CoinEnum.ETHUSDT){
            candleList = this.coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);

        }else{

            candleList = this.coinDAO.getCandleList(coinType, IntervalEnum.FIVE);
        }
        ma = candleList.createIndex(new MovingAverage());
    }

    @Override
    public double testOpenRBI() {
        //새로운 캔들이 갱신될때마다 봉초가에 open
        //만약 2분선이 20분 선 아래에 있을 경우에는 무조건 숏 포지션
        if(standardDate != candleList.getReversed(0).getStartAt().getTime()){
            
            if(candleList.getReversed(0).getOpen() <= ma.getReversed(1).getMa10()){
               
                standardDate = candleList.getReversed(0).getStartAt().getTime();
                System.out.println("매매 시간" + standardDate);
                entryPrice = coinDAO.getCurrentPrice(coinType);
                return 1; 
            }
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
         //15분봉의 종가에 청산
         if(coinType == CoinEnum.BTCUSDT || coinType == CoinEnum.ETHUSDT){
          
            if((standardDate + 895000) <= System.currentTimeMillis() || getProfitPercent() <= -0.7 ){   
                return true;
            }

        }else{

            if((standardDate + 297000) <= System.currentTimeMillis() || getProfitPercent() <= -0.7 ){   
                return true;
            }
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
        return (1.0 - (coinDAO.getCurrentPrice(coinType) / entryPrice)) * 100;
    }

    @Override
    public String getStrategyName() {
        // TODO Auto-generated method stub
        return "추세 숏 전략";
    }
}
