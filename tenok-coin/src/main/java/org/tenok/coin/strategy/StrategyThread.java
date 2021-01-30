package org.tenok.coin.strategy;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.impl.BybitDAO;

public class StrategyThread implements Runnable {
    private CoinDataAccessable daoInstance;
    private List<Strategy> strategyList;

    public StrategyThread(Class<CoinDataAccessable> daoClass) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        daoInstance = daoClass.getDeclaredConstructor().newInstance();
    }

    @Override
    public void run() {
        strategyList.parallelStream().filter(Strategy::isNotOpened).forEach(strategy -> {
            double openRBI = strategy.testOpenRBI();

            if (openRBI != 0.0) {
                BybitDAO.getInstance().orderCoin(null);
            }
        });
        strategyList.parallelStream().filter(Strategy::isOpened).forEach(strategy -> {
            if (strategy.testCloseRBI()) {
                BybitDAO.getInstance().orderCoin(null);
            }
        });
    }

}
