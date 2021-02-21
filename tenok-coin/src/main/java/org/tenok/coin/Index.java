package org.tenok.coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.strategy.StrategyConfig;
import org.tenok.coin.strategy.StrategyHandler;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.LongStrategy;
import org.tenok.coin.strategy.impl.ShortStrategy;
import org.tenok.coin.strategy.impl.TrendLongStrategy;
import org.tenok.coin.strategy.impl.TrendShortStrategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

public class Index {
    public static void main(String[] args) throws LoginException {
        BybitDAO.getInstance().login(args[0]);

        StrategyRunner runner = new StrategyRunner();

        StrategyConfig TrendlongConfig = new StrategyConfig(CoinEnum.BCHUSDT, SideEnum.OPEN_BUY, BybitDAO.class, TrendLongStrategy.class, 1, 0.9);
        StrategyConfig TrendshortConfig = new StrategyConfig(CoinEnum.BCHUSDT,SideEnum.OPEN_SELL, BybitDAO.class, TrendShortStrategy.class, -1, 0.9);
        StrategyHandler longHandler = runner.runStrategy(TrendlongConfig);
        StrategyHandler shortHandler = runner.runStrategy(TrendshortConfig);

        
        longHandler.start();
        shortHandler.start();
    }
}
