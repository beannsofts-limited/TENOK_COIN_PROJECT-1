package org.tenok.coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.impl.BybitDAO;

public class Index {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.printf("enter pw: ");
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

        // StrategyRunner strategyRunner = new StrategyRunner(BybitDAO.class);
        // strategyRunner.runStrategy(LongStrategy.class, CoinEnum.BTCUSDT);
        // strategyRunner.runStrategy(ShortStrategy.class, CoinEnum.BTCUSDT);

        try {
            BybitDAO.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
