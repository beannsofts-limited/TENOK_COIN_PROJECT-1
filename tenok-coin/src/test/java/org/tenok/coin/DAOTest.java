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
    public void candleListTest() {
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        assertEquals(candleList.size(), 200);

        candleList.stream().forEachOrdered(System.out::println);
    }

    @Test
    public void sendMessage(){
        try {
            
            WebhookResponse response = SlackDAO.getInstance().sendTradingMessage(CoinEnum.BTCUSDT, SideEnum.BUY, 1);
            assertSame(response.getCode(), 200);
        } catch (NoSuchFieldException e) {
            
            e.printStackTrace();
        }
    public void orderTest() throws LoginException {
        BybitDAO.getInstance().login("password");
        Orderable order = ActiveOrder.builder()
                                     .coinType(CoinEnum.BTCUSDT)
                                     .orderType(OrderTypeEnum.MARKET)
                                     .qty(0.00001)
                                     .tif(TIFEnum.GTC)
                                     .build();
                                     
        BybitDAO.getInstance().orderCoin(order);
    }
}
