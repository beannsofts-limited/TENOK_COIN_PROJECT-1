package org.tenok.coin;

import java.io.File;
import java.io.IOException;

import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class App {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println(new File("./secret.auth").getCanonicalPath());
    }
}






