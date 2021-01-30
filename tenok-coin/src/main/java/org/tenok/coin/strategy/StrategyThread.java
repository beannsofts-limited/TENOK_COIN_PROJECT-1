package org.tenok.coin.strategy;

import java.lang.reflect.InvocationTargetException;

import org.tenok.coin.data.CoinDataAccessable;

public class StrategyThread implements Runnable {
    private CoinDataAccessable coinDAOInstance;
    private Strategy strategyInstance;

    public StrategyThread(Class<CoinDataAccessable> coinDaoClass, Class<? extends Strategy> strategyClass) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        coinDAOInstance = coinDaoClass.getDeclaredConstructor().newInstance();
        strategyInstance = strategyClass.getDeclaredConstructor(CoinDataAccessable.class).newInstance(coinDAOInstance);
    }

    @Override
    public void run() {
        if (strategyInstance.isNotOpened()) {
            double openRBI = strategyInstance.testOpenRBI();
            coinDAOInstance.orderCoin(null);
        } else {
            coinDAOInstance.orderCoin(null);
        }
    }
}
