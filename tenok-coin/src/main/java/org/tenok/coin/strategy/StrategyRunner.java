package org.tenok.coin.strategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tenok.coin.data.CoinDataAccessable;

public class StrategyRunner {
    ExecutorService threadPool = Executors.newFixedThreadPool(10);
    public StrategyRunner(Class<? extends CoinDataAccessable> coinDaoClass) {

    }

    public void init() {

    }

    public void runStrategy(Class<? extends Strategy> strategyClass) {

    }

    public void stopStrategy() {

    }
}
