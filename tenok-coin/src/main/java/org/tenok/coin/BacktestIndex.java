package org.tenok.coin;

import java.util.concurrent.ExecutionException;

import org.tenok.coin.data.entity.impl.BacktestDAO;
import org.tenok.coin.strategy.StrategyConfig;
import org.tenok.coin.strategy.StrategyHandler;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.LongStrategy;
import org.tenok.coin.type.CoinEnum;

public class BacktestIndex {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        StrategyRunner runner = new StrategyRunner();

        StrategyConfig config = new StrategyConfig(CoinEnum.BTCUSDT, BacktestDAO.class, LongStrategy.class, 1, 0.5);

        StrategyHandler handler = runner.runStrategy(config);

        Thread.sleep(500000000000L);
        handler.updateLeverage(5);

    }
}
