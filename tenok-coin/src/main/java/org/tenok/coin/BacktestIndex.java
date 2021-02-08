package org.tenok.coin;

import org.tenok.coin.data.impl.BacktestDAO;
import org.tenok.coin.strategy.StrategyConfig;
import org.tenok.coin.strategy.StrategyHandler;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.LongStrategy;
import org.tenok.coin.type.CoinEnum;

public class BacktestIndex {
    public static void main(String[] args) throws InterruptedException {
        StrategyRunner runner = new StrategyRunner();

        StrategyConfig config = new StrategyConfig(CoinEnum.BTCUSDT, BacktestDAO.class, LongStrategy.class, 1, 1.0);
        StrategyHandler handler = runner.runStrategy(config);

        handler.start();

        handler.join();

        System.out.printf("%f", BacktestDAO.getWholeThreadProfit());
    }
}
