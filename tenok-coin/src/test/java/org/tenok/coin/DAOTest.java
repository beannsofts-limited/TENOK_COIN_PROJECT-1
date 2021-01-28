package org.tenok.coin;

import static org.junit.Assert.assertEquals;

import javax.security.auth.login.LoginException;

import com.slack.api.webhook.WebhookResponse;

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
    public void candleListTest() throws InterruptedException, LoginException {
        BybitDAO.getInstance().login("tenok2019");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        CandleList candleLidst = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.TWOHUNDREDFORTY);
        assertEquals(candleList.size(), 200);

        candleList.stream().forEachOrdered(System.out::println);
        for (int i = 0; i < 20; i++) {
            var cl = candleList.get(0);
            System.out.println(String.format("u bb: %f\nm bb: %f\nl bb: %f", cl.getUpperBB(), cl.getMiddleBB(), cl.getLowerBB()));
            System.out.println(String.format("ma5: %f ma10: %f ma20: %f ma60: %f ma120: %f\n", cl.getMa5(), cl.getMa10(), cl.getMa20(), cl.getMa60(), cl.getMa120()));
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
        WebhookResponse response = SlackDAO.getInstance().sendTradingMessage(CoinEnum.BTCUSDT, SideEnum.OPEN_BUY, 1);
        assertEquals(response.getCode().intValue(), 200);
    }

    @Test
    public void orderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        Orderable order = ActiveOrder.builder()
                                     .coinType(CoinEnum.LTCUSDT)
                                     .orderType(OrderTypeEnum.MARKET)
                                     .side(SideEnum.CLOSE_BUY)
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

    @Test
    public void getOrderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        BybitDAO.getInstance().getOrderList();
    }
}
