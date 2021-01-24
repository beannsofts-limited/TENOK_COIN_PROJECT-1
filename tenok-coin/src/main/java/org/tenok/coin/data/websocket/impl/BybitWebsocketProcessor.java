package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BybitWebsocketProcessor implements Closeable {
    private Session websocketSession = null;

    public BybitWebsocketProcessor() { }

    public void init() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            this.websocketSession = container.connectToServer(BybitWebsocket.class, new URI("wss://"));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void subscirbeKLine(CoinEnum coinType, IntervalEnum interval, CandleList candleList) {
        
    }

    public void subscribeInsturmentInfo() {

    }

    public void subscribePosition() {

    }

    public void subscribeOrder() {

    }

    public void processKLine() {

    }

    public void processPosition() {

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
