package org.tenok.coin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.tenok.coin.data.entity.impl.BacktestDAO;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.LongStrategy;
import org.tenok.coin.strategy.impl.ShortStrategy;
import org.tenok.coin.type.CoinEnum;

public class BacktestIndex {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        StrategyRunner strategyRunner = new StrategyRunner(BacktestDAO.class);
        Future a = strategyRunner.runStrategy(LongStrategy.class, CoinEnum.BTCUSDT);
        a.get();
        Future b = strategyRunner.runStrategy(ShortStrategy.class, CoinEnum.BTCUSDT);
        b.get();
        
        double profit = BacktestDAO.getInstance(null).getWholeProfit();

        System.out.println(profit);
    }
}
