package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BybitWebsocketProcessor implements Closeable {
    private Session websocketSession = null;
    private BybitWebsocket websocketInstance = null;
    private Thread heartBeatThread = null;

    public BybitWebsocketProcessor() {
    }

    public void init() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.websocketInstance = new BybitWebsocket();

        try {
            this.websocketSession = container.connectToServer(websocketInstance, new URI("wss://stream.bybit.com/realtime_public"));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        heartBeatThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    websocketSession.getAsyncRemote().sendText("{\"op\":\"ping\"}");
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
     * @param coinType
     * @param interval
     * @param candleList
     */
    public void subscribeCandle(CoinEnum coinType, IntervalEnum interval, CandleList candleList) {
        this.websocketInstance.registerKLineCallback(coinType, interval, (data) -> {
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
        this.websocketInstance.registerWalletInfo(data -> {
            double walletBalance = (double) data.get("wallet_balance");
            double availableBalance = (double) data.get("available_balance");
            walletInfo.setWalletBalance(walletBalance)
                      .setWalletAvailableBalance(availableBalance);
        });
    }

    public void subscribeOrder(CoinEnum coinType, IntervalEnum interval, OrderList orderList) {

    }

    public void unsubscribeKLine(CoinEnum coinType, IntervalEnum interval) {
        this.websocketInstance.unregisterKLine(coinType, interval);
    }

    @Override
    public void close() throws IOException {
        this.heartBeatThread.interrupt();   // heart beat 세션 유지용 스레드 인터럽트
        this.websocketInstance.close();
    }

}
