package org.tenok.coin.strategy;

import java.util.List;

public class StrategyRunner {
    private List<Strategy> strategyList;
    private Thread strategyTestThread;

    public StrategyRunner() {
        strategyTestThread = new Thread(() -> {
            while (true) {
                strategyList.parallelStream().filter(Strategy::isNotOpened).forEach(Strategy::testOpenRBI);
                strategyList.parallelStream().filter(Strategy::isOpened).filter(Strategy::testCloseRBI).forEach((strategy) -> {

                });;
                if (Thread.interrupted()) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        strategyTestThread.start();
    }

    public void init() {

    }

    public void runStrategy(Strategy strategy) {

    }

    public void stopStrategy() {

    }
}
