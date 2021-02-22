package org.tenok.coin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.slack.api.webhook.WebhookResponse;


import org.junit.Test;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.InsufficientCostException;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.entity.impl.candle_index.bollinger_band.BollingerBand;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.slack.SlackSender;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.TIFEnum;

public class DAOTest{
    @Test
    public void loginTest() {
        try {
            BybitDAO.getInstance().login("tenok2019");
        } catch (LoginException e) {
            e.printStackTrace();
            assert false;
        }
        assertEquals(true, BybitDAO.getInstance().isLoggedIn());
    }

    @Test
    public void sendMessage() {

        try {
            BybitDAO.getInstance().login("tenok2019");
        } catch (LoginException e1) {
            e1.printStackTrace();
        }
        WebhookResponse response = SlackSender.getInstance().sendTradingMessage(CoinEnum.BTCUSDT, SideEnum.OPEN_BUY, 1,
                5, TIFEnum.GTC);
        assertEquals(200, response.getCode().intValue());
    }

    @Test
    public void orderTest() throws LoginException, InsufficientCostException {
        BybitDAO.getInstance().login("tenok2019");

        Orderable order = ActiveOrder.builder().coinType(CoinEnum.LTCUSDT).orderType(OrderTypeEnum.MARKET)
                .side(SideEnum.OPEN_BUY).qty(0.1).tif(TIFEnum.GTC).build();

        BybitDAO.getInstance().orderCoin(order);

        assertEquals(1, BybitDAO.getInstance().getOrderList().size());
    }

    @Test
    public void excpetionTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        WebhookResponse res = SlackSender.getInstance().sendException(new RuntimeException("something went wrong!"));
        assertEquals(200, res.getCode().intValue());
    }

    // // @Test
    // // public void BacktestCandleTest() {
    // // BacktestDAO back = new BacktestDAO();
    // //back.inputTest(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
    // // back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
    // // CandleList candle = back.getCandleList(CoinEnum.BTCUSDT,
    // IntervalEnum.DAY);
    // // for (int i = 0; i<1000; i++){
    // // System.out.println(candle.get(i));

    // // }
    // // }

    @Test
    public void getInstrumentInfoTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("tenok2019");
        var inst = BybitDAO.getInstance().getInstrumentInfo(CoinEnum.BTCUSDT);
        Thread.sleep(10000);
        System.out.println(inst.getPrice24hPcntE6());
        assertEquals(CoinEnum.BTCUSDT, inst.getCoinType());
    }

    @Test
    public void indexingCandleTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);

        MovingAverage ma = candleList.createIndex(new MovingAverage());

        BollingerBand bb = candleList.createIndex(new BollingerBand());

        assertEquals(ma.getReversed(0).getMa20(), bb.getReversed(0).getMiddleBB(), 0.1);
    }

    @Test
    public void currentPriceTest() throws LoginException {
        BybitDAO.getInstance().login("");

        double price = BybitDAO.getInstance().getCurrentPrice(CoinEnum.XTZUSDT);
        System.out.println(price);
        assertNotEquals(price, 0.0);
    }

    @Test
    public void orderedListTest() throws LoginException, IOException {
        BybitDAO.getInstance().login("");

        OrderedList ol = BybitDAO.getInstance().getOrderList();
        System.out.println(ol);
        BybitDAO.getInstance().close();

        assertNotNull(ol);
    }
    
}
