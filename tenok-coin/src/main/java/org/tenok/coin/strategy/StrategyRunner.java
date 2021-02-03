package org.tenok.coin.strategy;


import org.apache.log4j.Logger;

/**
 * Strategy Factory
 */
public class StrategyRunner {
    private static Logger logger = Logger.getLogger(StrategyRunner.class);

    public StrategyRunner() {
    }

    /**
     * Create Strategy Handler
     * @param config Strategy config
     * @return Strategy Handler
     */
    public StrategyHandler runStrategy(StrategyConfig config) {
        StrategyThread strategy = new StrategyThread(config.clone());
        
        logger.info(
            String.format("Run Strategy Thread [%s, %s, %s]", config.getCoinDataAccessableClass().getSimpleName(),
            config.getStrategyClass().getSimpleName(), config.getCoinType().getKorean()));
            
            StrategyHandler handler = new StrategyHandler(config, strategy, new Thread(strategy));
            
        return handler;
    }
}
