package org.tenok.coin.strategy;

import org.apache.log4j.Logger;

/**
 * Strategy Factory
 */
public class StrategyRunner {
    private static Logger logger = Logger.getLogger(StrategyRunner.class);

    /**
     * Create Strategy Handler
     * 
     * @param config Strategy config
     * @return Strategy Handler
     */
    public StrategyHandler runStrategy(StrategyConfig config) {
        StrategyThread strategy = new StrategyThread(config.copy());

        logger.info(
                String.format("Run Strategy Thread [%s, %s, %s]", config.getCoinDataAccessableClass().getSimpleName(),
                        config.getStrategyClass().getSimpleName(), config.getCoinType().getKorean()));

        return new StrategyHandler(config, strategy, new Thread(strategy));
    }
}
