package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;


@ClientEndpoint(encoders = { BybitEncoder.class }, decoders = { BybitDecoder.class })
public class BybitWebsocket implements Closeable {
    Map<CoinEnum, Map<IntervalEnum, Consumer<JSONObject>>> kLineCallbackMap;
    Consumer<JSONObject> walletInfoConsumer;

    private Session session;

    public BybitWebsocket() {
        this.kLineCallbackMap = new HashMap<>();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(JSONObject response) {
        JSONObject request = (JSONObject) response.get("request");
        String op = (String) request.get("op");
        if (op.equals("ping")) {
            return;
        }
        System.out.println(request.toJSONString());

        String topic = (String) response.get("topic");
        String[] topicParsed = topic.split(".");

        if (topicParsed.length == 0) {
            // . 구분자가 없는 topic

        } else {
            switch (topicParsed[0]) {
                case "orderBookL2_25":
                    break;

                case "orderBook_200":
                    break;

                case "trade":
                    break;

                case "instrument_info":
                    break;

                case "candle":
                    IntervalEnum interval = IntervalEnum.valueOfLiteral(topicParsed[1]);
                    CoinEnum coinType = CoinEnum.valueOf(topicParsed[2]);

                    kLineCallbackMap.get(coinType).get(interval).accept((JSONObject) ((JSONArray) response.get("data")).get(0));
                    break;

                default:
                    throw new RuntimeException("Websocket Topic Parse Failed" + topicParsed.toString());
            }
        }
    }

    @OnClose
    public void onClose() {

    }

    // @OnError
    // public void onError(Throwable t) {

    // }

    /**
     * websocket kline 실시간 처리
     * 
     * @param coinType 코인 종류
     * @param interval 캔들 간격
     * @param consumer 콜백
     */
    public void registerKLineCallback(CoinEnum coinType, IntervalEnum interval, Consumer<JSONObject> consumer) {
        kLineCallbackMap.putIfAbsent(coinType, new HashMap<>());
        var previousValue = kLineCallbackMap.get(coinType).get(interval);
        if (previousValue != null) {
            throw new RuntimeException("RegisterKLineCallback request called twice by the homogeneous parameter");
        }
        kLineCallbackMap.get(coinType).put(interval, consumer);
        JSONObject requestJson = getSubscriptionJSONObject(Arrays.asList(new String[] {String.format("%s.%s.%s", "candle",
                                                           interval.getLiteral(), coinType.name())}));
        session.getAsyncRemote().sendObject(requestJson);
    }

    /**
     * 예수금 상황 실시간 처리 등록
     * @param consumer 콜백
     */
    public void registerWalletInfo(Consumer<JSONObject> consumer) {
        this.walletInfoConsumer = consumer;
        JSONObject requestJson = getSubscriptionJSONObject(Arrays.asList("wallet"));
        session.getAsyncRemote().sendObject(requestJson);
    }

    public void unregisterKLine(CoinEnum coinType, IntervalEnum interval) {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private JSONObject getSubscriptionJSONObject(List args) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("op", "subscribe");
        requestJson.put("args", args);
        return requestJson;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
