package org.tenok.coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.LongStrategy;
import org.tenok.coin.strategy.impl.ShortStrategy;
import org.tenok.coin.type.CoinEnum;

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
        
        StrategyRunner strategyRunner = new StrategyRunner(BybitDAO.class);
        strategyRunner.runStrategy(LongStrategy.class, CoinEnum.BTCUSDT);
        strategyRunner.runStrategy(ShortStrategy.class, CoinEnum.BTCUSDT);


        try {
            BybitDAO.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
