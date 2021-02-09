package org.tenok.coin;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;
import javax.swing.plaf.ColorUIResource;

import com.slack.api.webhook.WebhookResponse;

import org.junit.Test;
import org.knowm.xchart.OHLCChart;
import org.knowm.xchart.OHLCChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.Candle;
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
            BybitDAO.getInstance().login("tenok2019");
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
        assertEquals(candleList.size(), 200);
        
        candleList.stream().forEachOrdered(cl -> {
            System.out.println(String.format("u bb: %f\nm bb: %f\nl bb: %f", cl.getUpperBB(), cl.getMiddleBB(), cl.getLowerBB()));
            System.out.println(String.format("ma5: %f ma10: %f ma20: %f ma60: %f ma120: %f\nstart at: %s\n", cl.getMa5(), cl.getMa10(), cl.getMa20(), cl.getMa60(), cl.getMa120(), cl.getStartAt().toString()));
        });
        for (int i = 0; i < 0; i++) {
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
                                     .side(SideEnum.CLOSE_SELL)
                                     .qty(0.1)
                                     .tif(TIFEnum.GTC)
                                     .build();
                                     
        BybitDAO.getInstance().orderCoin(order);
    }

    @Test
    public void excpetionTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");
        
        // WebhookResponse res = SlackDAO.getInstance().sendException(new RuntimeException("something went wrong!"));
        // assertEquals(res.getCode().intValue(), 200);
    }

    @Test
    public void BacktestCandleTest() {
        // BacktestDAO back = new BacktestDAO();
        //back.inputTest(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
        // back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);  
        // CandleList candle = back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.DAY);
        // for (int i = 0; i<1000; i++){
        //     System.out.println(candle.get(i));
            
        // }
    }

    @Test
    public void getOrderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        // BybitDAO.getInstance().getOrderList();
    }

    @Test
    public void getInstrumentInfoTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("tenok2019");
        // var inst = BybitDAO.getInstance().getInstrumentInfo(CoinEnum.BTCUSDT);

        // for (int i = 0; i < 20; i++) {
        //     System.out.println(inst);
        //     Thread.sleep(1000);
        // }
    }
}
