package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.InstrumentInfo;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderedData;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.impl.AuthDecryptor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;
import org.tenok.coin.type.TickDirectionEnum;

/**
 * Websocket에서 할 수 있는 요청들(예: kline 실시간, wallet 실시간) 들을 처리 해줄 수 있는 클래스.
 */
public class BybitWebsocketProcessor implements Closeable {
    private static Logger logger = Logger.getLogger(BybitWebsocketProcessor.class);
    private static final String BYBIT_PUBLIC = "wss://stream.bybit.com/realtime_public";
    private static final String BYBIT_PRIVATE = "wss://stream.bybit.com/realtime_private";
    private Session websocketPublicSession;
    private Session websocketPrivateSession;
    private BybitWebsocket websocketPublicInstance;
    private BybitWebsocket websocketPrivateInstance;
    private Thread heartBeatThread;

    public BybitWebsocketProcessor() {
    }

    /**
     * 웹소켓 연결 및 heart deat 스레드 등록.
     */
    public void init() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.websocketPublicInstance = new BybitWebsocket();
        this.websocketPrivateInstance = new BybitWebsocket();

        try {
            // public, private websocket에 연결
            this.websocketPublicSession = container.connectToServer(websocketPublicInstance, new URI(BYBIT_PUBLIC));
            this.websocketPrivateSession = container.connectToServer(websocketPrivateInstance, new URI(BYBIT_PRIVATE));
            // 웹소켓 서버에 로그인 시도
            sendAuthQeury();
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("websocket connect failed");
        }

        heartBeatThread = new Thread(() -> {
            try {
                while (true) {
                    websocketPublicSession.getAsyncRemote().sendText("{\"op\":\"ping\"}");
                    websocketPrivateSession.getAsyncRemote().sendText("{\"op\":\"ping\"}");
                    Thread.sleep(30000);
                }
            } catch (InterruptedException e) {
                logger.info("heart beat thread interrupted!");
                Thread.currentThread().interrupt();
            }
        }, "HeartBeatThread");

        heartBeatThread.start();

    }

    /**
     * Websocket 서버에 auth qeury를 전송한다.
     * 
     * @throws IOException     .
     * @throws EncodeException .
     */
    @SuppressWarnings("unchecked")
    private void sendAuthQeury() throws IOException, EncodeException {
        JSONObject authObject = new JSONObject();
        authObject.put("op", "auth");
        long expires = AuthDecryptor.getInstance().generateExpire();
        authObject.put("args", Arrays.asList(AuthDecryptor.getInstance().getApiKey(), Long.toString(expires),
                AuthDecryptor.getInstance().generateSignature(expires)));
        websocketPrivateSession.getBasicRemote().sendObject(authObject);
    }

    Integer count = 0;
    /**
     * kLine 실시간 처리 등록
     * 
     * @param coinType   coin type
     * @param interval   interval
     * @param candleList CandleList instance
     */
    public void subscribeCandle(CoinEnum coinType, IntervalEnum interval, CandleList candleList) {
        this.websocketPublicInstance.registerKLineCallback(coinType, interval, data -> {
            double open = ((Number) data.get("open")).doubleValue();
            double close = ((Number) data.get("close")).doubleValue();
            double high = ((Number) data.get("high")).doubleValue();
            double low = ((Number) data.get("low")).doubleValue();
            double volume = Double.parseDouble((String) data.get("volume"));
            Date startAt = new Date(((Number) data.get("start")).longValue() * 1000L);
            boolean confirm = (boolean) data.get("confirm");

            if (confirm) {
                System.out.println("confirm");
                Candle candle = new Candle(startAt, volume, open, high, low, close);
                candleList.registerNewCandle(candle);
            } else {
                Candle candle = new Candle(startAt, volume, open, high, low, close);
                candleList.updateCurrentCandle(candle);
            }
        });
    }

    public void subscribeInsturmentInfo(CoinEnum coinType, InstrumentInfo instrumentInfo) {
        this.websocketPublicInstance.registerInsturmentInfo(coinType, data -> {
            if (data.containsKey("last_tick_direction")) {
                instrumentInfo.lastTickDirection(
                        TickDirectionEnum.valueOfApiString((String) data.get("last_tick_direction")));
            }
            if (data.containsKey("last_price_e4")) {
                instrumentInfo.lastPriceE4(Long.valueOf((String) data.get("last_price_e4")));
            }
            if (data.containsKey("price_24h_pcnt_e6")) {
                instrumentInfo.price24hPcntE6(Long.valueOf((String) data.get("price_24h_pcnt_e6")));
            }
            if (data.containsKey("high_price_24h_e4")) {
                instrumentInfo.price24hPcntE6(Long.valueOf((String) data.get("high_price_24h_e4")));
            }
            if (data.containsKey("low_price_24h_e4")) {
                instrumentInfo.lowPrice24hE4(Long.valueOf((String) data.get("low_price_24h_e4")));
            }
            if (data.containsKey("price_1h_pcnt_e6")) {
                instrumentInfo.price1hPcntE6(Long.valueOf((String) data.get("price_1h_pcnt_e6")));
            }
        });
    }

    public void subscribePosition() {

    }

    public void subscribeWalletInfo(WalletAccessable walletInfo) {
        this.websocketPrivateInstance.registerWalletInfo(data -> {
            double walletBalance = (double) data.get("wallet_balance");
            double availableBalance = (double) data.get("available_balance");
            walletInfo.setWalletBalance(walletBalance).setWalletAvailableBalance(availableBalance);
        });
    }

    public void subscribeOrder(OrderedList orderedList) {
        this.websocketPrivateInstance.registerOrder(data -> {
            CoinEnum coin = CoinEnum.valueOf((String) data.get("symbol"));
            TIFEnum tif = TIFEnum.valueOfApiString((String) data.get("time_in_force"));
            double qty = ((Number) data.get("qty")).doubleValue();
            OrderTypeEnum orderType = OrderTypeEnum.valueOfApiString((String) data.get("order_type"));
            SideEnum sideEnum = null;
            boolean reduceOnly = (boolean) data.get("reduce_only");
            String side = (String) data.get("side");
            if (reduceOnly && side.equals("Buy")) {
                // 공매수
                sideEnum = SideEnum.OPEN_BUY;
            } else if (reduceOnly && side.equals("Sell")) {
                // 공매도
                sideEnum = SideEnum.OPEN_SELL;
            } else if ((!reduceOnly) && side.equals("Buy")) {
                // 매수로 청산
                sideEnum = SideEnum.CLOSE_BUY;
            } else if ((!reduceOnly) && side.equals("Sell")) {
                // 매도로 청산
                sideEnum = SideEnum.CLOSE_SELL;
            }
            orderedList.add(
                    OrderedData.builder().coinType(coin).tif(tif).qty(qty).side(sideEnum).orderType(orderType).build());
        });
    }

    @Override
    public void close() throws IOException {
        this.heartBeatThread.interrupt(); // heart beat 세션 유지용 스레드 인터럽트
        this.websocketPublicInstance.close();
        this.websocketPrivateInstance.close();
    }

}
