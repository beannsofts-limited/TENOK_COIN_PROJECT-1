package org.tenok.coin.strategy;

import java.util.Map;

import org.apache.log4j.Logger;

public class StrategyRunner {
    private static Logger logger = Logger.getLogger(StrategyRunner.class);
    Map<StrategyHandler, StrategyThread> strategyThreadMap;

    public StrategyRunner() {
    }

    public StrategyHandler runStrategy(StrategyConfig config) {
        StrategyThread strategy = new StrategyThread(config.clone());
        StrategyHandler handler = new StrategyHandler(config, strategy);

        logger.info(
                String.format("Run Strategy Thread [%s, %s, %s]", config.getCoinDataAccessableClass().getSimpleName(),
                        config.getStrategyClass().getSimpleName(), config.getCoinType().getKorean()));

        strategyThreadMap.put(handler, strategy);

        Thread strategyThread = new Thread(strategy);
        strategyThread.start();
        return handler;
    }

    public void stopStrategy(StrategyHandler handler) {

    }
}
