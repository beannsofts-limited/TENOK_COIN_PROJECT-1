package org.tenok.coin;

import static org.junit.Assert.assertEquals;

import javax.security.auth.login.LoginException;

import com.slack.api.webhook.WebhookResponse;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.slack.SlackDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.TIFEnum;

public class DAOTest {
    @Test
    public void loginTest() {
        try {
            BybitDAO.getInstance().login("");
        } catch (LoginException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void candleListTest() throws InterruptedException {
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        assertEquals(candleList.size(), 200);

        candleList.stream().forEachOrdered(System.out::println);
        for (int i = 0; i < 50; i++) {
            System.out.println(candleList.get(0).toString());
            Thread.sleep(1000);
        }
    }

    @Test
    public void sendMessage(){
        
        try {
            BybitDAO.getInstance().login("tenok2019");
        } catch (LoginException e1) {
            e1.printStackTrace();
        }
        WebhookResponse response = SlackDAO.getInstance().sendTradingMessage(CoinEnum.BTCUSDT, SideEnum.BUY, 1);
        assertEquals(response.getCode().intValue(), 200);
    }

    @Test
    public void orderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");
        Orderable order = ActiveOrder.builder()
                                     .coinType(CoinEnum.LTCUSDT)
                                     .orderType(OrderTypeEnum.MARKET)
                                     .side(SideEnum.BUY)
                                     .qty(0.1)
                                     .tif(TIFEnum.GTC)
                                     .build();
                                     
        BybitDAO.getInstance().orderCoin(order);
    }

    @Test
    public void excpetionTest() throws LoginException {
        BybitDAO.getInstance().login("");
        
        WebhookResponse res = SlackDAO.getInstance().sendException(new RuntimeException("something went wrong!"));
        assertEquals(res.getCode().intValue(), 200);
    }
}
