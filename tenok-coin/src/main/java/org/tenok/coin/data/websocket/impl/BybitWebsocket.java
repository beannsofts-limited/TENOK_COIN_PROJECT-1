package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

@ClientEndpoint(encoders = { BybitEncoder.class }, decoders = { BybitDecoder.class })
class BybitWebsocket implements Closeable {
    Map<CoinEnum, Map<IntervalEnum, Consumer<JSONObject>>> kLineCallbackMap = null;

    public BybitWebsocket() {
        this.kLineCallbackMap = new HashMap<>();
    }

    @OnOpen
    public void onOpen(Session session) {

    }

    @OnMessage
    public void onMessage(JSONObject response) {

    }

    @OnClose
    public void onClose() {

    }

    @OnError
    public void onError() {

    }

    /**
     * websocket kline 실시간 처리
     * 
     * @param coinType
     * @param interval
     * @param consumer
     */
    public void registerKLineCallback(CoinEnum coinType, IntervalEnum interval, Consumer<JSONObject> consumer) {
        kLineCallbackMap.putIfAbsent(coinType, new HashMap<>());
        var previousValue = kLineCallbackMap.get(coinType).putIfAbsent(interval, consumer);
        if (previousValue != null) {
            throw new RuntimeException("RegisterKLineCallback request called twice by the equal parameter");
        }
    }

    public void unregisterKLine(CoinEnum coinType, IntervalEnum interval) {

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
