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

public class Index {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("enter pw: ");
        while (true) {
            try {
                BybitDAO.getInstance().login(br.readLine());
                System.out.println("login success");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LoginException e) {
                System.out.println("retry");
            }
        }

        StrategyRunner runner = new StrategyRunner();

        StrategyConfig longConfig = new StrategyConfig(CoinEnum.XTZUSDT, BybitDAO.class, LongStrategy.class, 1, 0.3);
        StrategyConfig shortConfig = new StrategyConfig(CoinEnum.XTZUSDT, BybitDAO.class, ShortStrategy.class, -1, 0.3);
        StrategyHandler longHandler = runner.runStrategy(longConfig);
        StrategyHandler shortHandler = runner.runStrategy(shortConfig);

        longHandler.start();
        shortHandler.start();

        while (!br.readLine().equals("quit"));

        br.close();
        longHandler.stop();
        shortHandler.stop();
        BybitDAO.getInstance().close();

    }
}
