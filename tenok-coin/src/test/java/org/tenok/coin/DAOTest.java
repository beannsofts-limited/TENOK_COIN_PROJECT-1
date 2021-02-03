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
        assertEquals(candleList.size(), 200);
        
        candleList.stream().forEachOrdered(cl -> {
            System.out.println(String.format("u bb: %f\nm bb: %f\nl bb: %f", cl.getUpperBB(), cl.getMiddleBB(), cl.getLowerBB()));
            System.out.println(String.format("ma5: %f ma10: %f ma20: %f ma60: %f ma120: %f\nstart at: %s\n", cl.getMa5(), cl.getMa10(), cl.getMa20(), cl.getMa60(), cl.getMa120(), cl.getStartAt().toString()));
        });
        for (int i = 0; i < 50; i++) {
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
    public void BacktestCandleTest() {
        // BacktestDAO back = new BacktestDAO();
        //back.inputTest(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
        // back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);  
        // CandleList candle = back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.DAY);
        // for (int i = 0; i<1000; i++){
        //     System.out.println(candle.get(i));
            
        // }
    }

       
        // CandleList candle = back.test(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
        // System.out.println(candle.size());
        // CandleList candle = back.getCandleList(CoinEnum.BTCUSDT,IntervalEnum.FIFTEEN);
        // System.out.println(candle.size());
        // for(int i =0; i<candle.size(); i++){
        //     System.out.println(candle.elementAt(i).getStartAt());
        //     System.out.println("\n");
        //     // System.out.println(System.currentTimeMillis() - IntervalEnum.FIFTEEN.getSec()*200000L+"\n");

        // }

    public void getOrderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        BybitDAO.getInstance().getOrderList();
    }

    @Test
    public void getInstrumentInfoTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("tenok2019");
        var inst = BybitDAO.getInstance().getInstrumentInfo(CoinEnum.BTCUSDT);

        for (int i = 0; i < 20; i++) {
            System.out.println(inst);
            Thread.sleep(1000);
        }
    }

    @Test
    public void ohlcTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("tenok2019");
        boolean loop = true;
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        var chartBuilder = new OHLCChartBuilder();
        OHLCChart chart = chartBuilder.xAxisTitle("시간")
                                      .yAxisTitle("가격")
                                      .title("비트코인 가격")
                                      .build();

        List<Double> openList = candleList.stream().map(Candle::getOpen).collect(Collectors.toList()).subList(150, 199);
        List<Double> highList = candleList.stream().map(Candle::getHigh).collect(Collectors.toList()).subList(150, 199);
        List<Double> lowList = candleList.stream().map(Candle::getLow).collect(Collectors.toList()).subList(150, 199);
        List<Double> closeList = candleList.stream().map(Candle::getClose).collect(Collectors.toList()).subList(150, 199);
        List<Date> startAt = candleList.stream().map(Candle::getStartAt).collect(Collectors.toList()).subList(150, 199);
        chart.addSeries("seriesName", startAt, openList, highList, lowList, closeList).setDownColor(new ColorUIResource(0, 0, 255));
        new SwingWrapper<>(chart).displayChart();
        long startTime = System.currentTimeMillis();
        
        while (loop) {
            // Candle candle = candleList.getReversed(0);

            // if (candle.isConfirmed()) {
            //     openList.remove(openList.size()-1);
            //     highList.remove(highList.size()-1);
            //     lowList.remove(lowList.size()-1);
            //     closeList.remove(closeList.size()-1);
            //     startAt.remove(startAt.size()-1);
            // } else {
            //     openList.remove(0);
            //     highList.remove(0);
            //     lowList.remove(0);
            //     closeList.remove(0);
            //     startAt.remove(0);
            // }

            // openList.add(candle.getOpen());
            // highList.add(candle.getHigh());
            // lowList.add(candle.getLow());
            // closeList.add(candle.getClose());
            // startAt.add(candle.getStartAt());

            // chart.updateOHLCSeries("seriesName", startAt, openList, highList, lowList, closeList);

            if (System.currentTimeMillis() - startTime > 10 * 1000) {
                break;
            }
        }
    }
}
