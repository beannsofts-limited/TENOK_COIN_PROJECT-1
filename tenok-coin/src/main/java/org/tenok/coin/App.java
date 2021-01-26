package org.tenok.coin;

import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class App {
    public static void main(String[] args) throws InterruptedException {
        BybitDAO.getInstance().init();
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);

        // candleList.stream().forEachOrdered(System.out::println);

        while (true) {
            // System.out.println(candleList.get(0));
            Thread.sleep(1000);
        }
    }
}






