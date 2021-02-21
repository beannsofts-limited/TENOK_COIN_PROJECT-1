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
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

public class Index {
    public static void main(String[] args) throws LoginException {
        BybitDAO.getInstance().login(args[0]);

        StrategyRunner runner = new StrategyRunner();

        StrategyConfig longConfig = new StrategyConfig(CoinEnum.XTZUSDT, SideEnum.OPEN_BUY, BybitDAO.class,
                LongStrategy.class, 1, 1);
        StrategyConfig shortConfig = new StrategyConfig(CoinEnum.XTZUSDT, SideEnum.OPEN_SELL, BybitDAO.class,
                ShortStrategy.class, -1, 1);
        StrategyHandler longHandler = runner.runStrategy(longConfig);
        StrategyHandler shortHandler = runner.runStrategy(shortConfig);

        
        longHandler.start();
        shortHandler.start();
    }
}
