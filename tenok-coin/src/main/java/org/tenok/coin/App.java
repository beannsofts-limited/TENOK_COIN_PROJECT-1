package org.tenok.coin;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.commodity_channel.CommidityChannelIndex;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MAObject;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.extern.log4j.Log4j;

@Log4j
public class App {
    public static void main(String[] args) throws LoginException, InterruptedException {
        candleDateTest();
    }

    public static void maTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        MovingAverage ma = candleList.createIndex(new MovingAverage());

        while (true) {
            MAObject maObj1 = ma.getReversed(0);
            MAObject maObj2 = ma.getReversed(1);
            MAObject maObj3 = ma.getReversed(2);

            System.out.printf("%f %f %f %f %f%n", maObj1.getMa5(), maObj1.getMa10(), maObj1.getMa20(), maObj1.getMa60(), maObj1.getMa120());
            System.out.printf("%f %f %f %f %f%n", maObj2.getMa5(), maObj2.getMa10(), maObj2.getMa20(), maObj2.getMa60(), maObj2.getMa120());
            System.out.printf("%f %f %f %f %f%n", maObj3.getMa5(), maObj3.getMa10(), maObj3.getMa20(), maObj3.getMa60(), maObj3.getMa120());
            Thread.sleep(500);
            if (false) {
                break;
            }
        }
    }

    public static void candleConfirmTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);

        while (true) {
            if (false) {
                break;
            }
        }
    }

    public static void candleDateTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("");

        CandleList cl = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);

        Thread.sleep(1000L*60L*2L);

        for (Candle candle : cl) {
            System.out.println(candle.getStartAt());
        }
    }
}
