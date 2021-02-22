package org.tenok.coin.strategy.impl;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.commodity_channel.CommidityChannelIndex;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.strategy.BasicStrategyAbstract;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class CciExitLongStrategy extends BasicStrategyAbstract {
    private CandleList candleList4h;
    private CandleList candleList5m;
    private CommidityChannelIndex cci4h;
    private CommidityChannelIndex cci5m;
    private MovingAverage ma5m;
    private double entryPrice;

    public CciExitLongStrategy(CoinDataAccessable coinDAO, CoinEnum coinType) {
        super(coinDAO, coinType);
        candleList4h = coinDAO.getCandleList(coinType, IntervalEnum.TWOHUNDREDFORTY);
        cci4h = candleList4h.createIndex(new CommidityChannelIndex(9));
        candleList5m = coinDAO.getCandleList(coinType, IntervalEnum.FIVE);
        cci5m = candleList5m.createIndex(new CommidityChannelIndex(20));
        ma5m = candleList5m.createIndex(new MovingAverage());
    }

    @Override
    public double testOpenRBI() {
        if (isExiting() && isMaRising()) {
            entryPrice = coinDAO.getCurrentPrice(coinType);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        if (getProfitPercent() >= 5.0) {
            /*
             * 5프로 넘기면 익절
             */
            return true;
        }
        if (getProfitPercent() <= -3.0) {
            /*
             * -3프로 될 시 손절
             */
            return true;
        }
        if (isCciFalling()) {
            // CCI 하락시 매도
            return true;
        }
        return false;
    }

    @Override
    public String getStrategyName() {
        return "CCI 탈출 롱 전략";
    }

    /**
     * 과매도 구간인지
     * @return 과매도 여부
     */
    private boolean isOverSelled(int index) {
        return cci4h.getReversed(index).doubleValue() <= -150;
    }

    private boolean isExiting() {
        /*
         * 4시간 봉 기준으로, 1 봉전 CCI가 과매도 구간에 위치해 있고
         * 0 봉전 CCI가 탈출의 기미가 보일 때 true를 리턴한다.
         */
        return isOverSelled(1) && cci5m.getReversed(0).doubleValue() > -120;
    }

    private boolean isMaRising() {
        return ma5m.getReversed(0).getMa5() - ma5m.getReversed(1).getMa5() > 0;
    }

    private boolean isCciFalling() {
        return cci5m.getReversed(0) - cci5m.getReversed(1) < 0;
    }
    
    private double getProfitPercent() {
        return ((coinDAO.getCurrentPrice(coinType) / entryPrice) - 1.0) * 100;
    }
}
