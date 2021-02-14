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

        StrategyConfig config = new StrategyConfig(CoinEnum.LINKUSDT, BybitDAO.class, LongStrategy.class, 1, 0.1);
        StrategyHandler handler = runner.runStrategy(config);

        handler.start();

        while (!br.readLine().equals("quit")) { }

        br.close();

    }
}
