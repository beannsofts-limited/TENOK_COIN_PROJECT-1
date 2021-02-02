package org.tenok.coin.strategy.impl;

import java.lang.reflect.InvocationTargetException;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.strategy.Strategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class ShortStrategy implements Strategy {
    private CoinDataAccessable coinDAO;
    private CoinEnum coinType;
    private boolean isOpened = false;

    public ShortStrategy(Class<? extends CoinDataAccessable> coinDAOClass, CoinEnum coinType) {
        try {
            coinDAO = coinDAOClass.getConstructor((Class<?>) null).newInstance((Object) null);
            this.coinType = coinType;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    @Override
    public double testOpenRBI() {
        CandleList candleList = coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);
        if(candleList.getReversed(1).getMa5()!=0 && candleList.getReversed(1).getMa10()!=0){

            if(candleList.getReversed(1).getMa5() > candleList.getReversed(1).getMa10()){

                if(candleList.getReversed(0).getMa5() < candleList.getReversed(0).getMa10() ){
                    return 1;
                }

            }
        }
        return 0;
    }

    @Override
    public boolean testCloseRBI() {
        CandleList candleList = coinDAO.getCandleList(coinType, IntervalEnum.FIFTEEN);
        if(candleList.getReversed(0).getMa5() > candleList.getReversed(0).getMa10()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isOpened() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNotOpened() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setOpenValue() {

    }

    @Override
    public CoinEnum getCoinType() {
        return null;
    }
    
}
