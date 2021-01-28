package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.data.impl.AuthDecryptor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;


public class BybitWebsocketProcessor implements Closeable {
    private static Logger logger = Logger.getLogger(BybitWebsocketProcessor.class);
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
    @SuppressWarnings("unchecked")
    public void init() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.websocketPublicInstance = new BybitWebsocket();
        this.websocketPrivateInstance = new BybitWebsocket();

        try {
            this.websocketPublicSession = container.connectToServer(websocketPublicInstance, new URI("wss://stream.bybit.com/realtime_public"));
            this.websocketPrivateSession = container.connectToServer(websocketPrivateInstance, new URI("wss://stream.bybit.com/realtime_private"));
            JSONObject authObject = new JSONObject();
            authObject.put("op", "auth");
            authObject.put("args", Arrays.asList(new String[] {AuthDecryptor.getInstance().getApiKey(),
                                                               Long.toString(System.currentTimeMillis()+1000L),
                                                               AuthDecryptor.getInstance().generate_signature()}));
            websocketPrivateSession.getBasicRemote().sendObject(authObject);
        } catch (DeploymentException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } catch (URISyntaxException e) {
            logger.error(e);
        } catch (EncodeException e) {
            logger.error(e);
        }
        heartBeatThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    websocketPublicSession.getAsyncRemote().sendText("{\"op\":\"ping\"}");
                    websocketPrivateSession.getAsyncRemote().sendText("{\"op\":\"ping\"}");
                    if (interrupted()) {
                        break;
                    }
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        heartBeatThread.start();
    }

    /**
     * kLine 실시간 처리 등록
     * @param coinType coin type
     * @param interval interval
     * @param candleList CandleList instance
     */
    public void subscribeCandle(CoinEnum coinType, IntervalEnum interval, CandleList candleList) {
        this.websocketPublicInstance.registerKLineCallback(coinType, interval, (data) -> {
            double open = ((Number) data.get("open")).doubleValue();
            double close = ((Number) data.get("close")).doubleValue();
            double high = ((Number) data.get("high")).doubleValue();
            double low = ((Number) data.get("low")).doubleValue();
            double volume = Double.valueOf((String) data.get("volume"));
            Date startAt = new Date(((Number) data.get("start")).longValue() * 1000L);
            boolean confirm = (boolean) data.get("confirm");

            if (confirm) {
                Candle candle = new Candle(startAt, volume, open, high, low, close);
                candleList.registerNewCandle(candle);
            } else {
                Candle candle = new Candle(startAt, volume, open, high, low, close);
                candleList.updateCurrentCandle(candle);
            }
        });
    }

    public void subscribeInsturmentInfo() {

    }

    public void subscribePosition() {

    }

    public void subscribeWalletInfo(WalletAccessable walletInfo) {
        this.websocketPrivateInstance.registerWalletInfo(data -> {
            double walletBalance = (double) data.get("wallet_balance");
            double availableBalance = (double) data.get("available_balance");
            walletInfo.setWalletBalance(walletBalance)
                      .setWalletAvailableBalance(availableBalance);
        });
    }

    public void subscribeOrder(CoinEnum coinType, IntervalEnum interval, OrderList orderList) {

    }

    public void unsubscribeKLine(CoinEnum coinType, IntervalEnum interval) {
        this.websocketPublicInstance.unregisterKLine(coinType, interval);
    }

    @Override
    public void close() throws IOException {
        this.heartBeatThread.interrupt();   // heart beat 세션 유지용 스레드 인터럽트
        this.websocketPublicInstance.close();
    }

}
