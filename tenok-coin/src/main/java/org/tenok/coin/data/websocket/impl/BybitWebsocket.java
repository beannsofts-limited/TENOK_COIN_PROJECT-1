package org.tenok.coin.data.websocket.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.websocket.WebsocketResponseEnum;
import org.tenok.coin.slack.SlackDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

@ClientEndpoint(encoders = { BybitEncoder.class }, decoders = { BybitDecoder.class })
public class BybitWebsocket implements Closeable {
    private static Logger logger = Logger.getLogger(BybitWebsocket.class);
    Map<CoinEnum, Map<IntervalEnum, Consumer<JSONObject>>> kLineConsumerMap;
    Consumer<JSONObject> walletInfoConsumer;
    Consumer<JSONObject> orderConsumer;
    Map<CoinEnum, Consumer<JSONObject>> instrumentInfoConsumerMap;

    private Session session;

    public BybitWebsocket() {
        this.kLineConsumerMap = new EnumMap<>(CoinEnum.class);
        this.instrumentInfoConsumerMap = new EnumMap<>(CoinEnum.class);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info(String.format("%s에 websocket 연결", session.getRequestURI().toString()));
    }

    @OnMessage
    public void onMessage(JSONObject response) {
        logger.debug(response);
        WebsocketResponseEnum resType = (WebsocketResponseEnum) response.get("response_type");
        if (resType.equals(WebsocketResponseEnum.PING) || resType.equals(WebsocketResponseEnum.SUBSCRIPTION)) {
            boolean success = (boolean) response.get("success"); // TODO ping??
            if (!success) {
                logger.fatal("Websocket Ping or Subscription failed");
                logger.fatal(response.toJSONString());
            } else {
                logger.debug(String.format("Websocket %s success", resType.name()));
            }
            return;
        }

        String topic = (String) response.get("topic");
        String[] topicParsed = topic.split("\\.");

        CoinEnum coinType;
        switch (topicParsed[0]) {
            case "orderBookL2_25":
                break;

            case "orderBook_200":
                break;

            case "trade":
                break;

            case "instrument_info":
                coinType = CoinEnum.valueOf(topicParsed[2]);
                instrumentInfoConsumerMap.get(coinType).accept((JSONObject) response.get("data"));
                break;

            case "candle":
                IntervalEnum interval = IntervalEnum.valueOfApiString(topicParsed[1]);
                coinType = CoinEnum.valueOf(topicParsed[2]);

                kLineConsumerMap.get(coinType).get(interval)
                        .accept((JSONObject) ((JSONArray) response.get("data")).get(0));
                break;

            case "wallet":
                this.walletInfoConsumer.accept((JSONObject) ((JSONArray) response.get("data")).get(0));
                break;

            case "order":
                this.orderConsumer.accept((JSONObject) ((JSONArray) response.get("data")).get(0));
                break;

            default:
                throw new RuntimeException("Websocket Topic Parse Failed" + Arrays.toString(topicParsed));
        }
    }

    @OnClose
    public void onClose() {
        logger.info("websocket 연결 종료");
    }

    @OnError
    public void onError(Throwable t) {
        logger.error("Websocket Error", t);
        SlackDAO.getInstance().sendException(t);
    }

    /**
     * websocket kline 실시간 처리
     * 
     * @param coinType 코인 종류
     * @param interval 캔들 간격
     * @param consumer 콜백
     */
    public void registerKLineCallback(CoinEnum coinType, IntervalEnum interval, Consumer<JSONObject> consumer) {
        kLineConsumerMap.putIfAbsent(coinType, new EnumMap<>(IntervalEnum.class));
        var previousValue = kLineConsumerMap.get(coinType).get(interval);
        if (previousValue != null) {
            throw new RuntimeException("RegisterKLineCallback request called twice by the homogeneous parameter");
        }
        kLineConsumerMap.get(coinType).put(interval, consumer);
        JSONObject requestJson = getSubscriptionJSONObject(
                Arrays.asList(String.format("%s.%s.%s", "candle", interval.getApiString(), coinType.name())));
        session.getAsyncRemote().sendObject(requestJson);
    }

    /**
     * 예수금 상황 실시간 처리 등록
     * 
     * @param consumer 콜백
     */
    public void registerWalletInfo(Consumer<JSONObject> consumer) {
        this.walletInfoConsumer = consumer;
        JSONObject requestJson = getSubscriptionJSONObject(Arrays.asList("wallet"));
        session.getAsyncRemote().sendObject(requestJson);
    }

    /**
     * 거래내역 실시간 처리 등록
     * 
     * @param consumer 콜백
     */
    public void registerOrder(Consumer<JSONObject> consumer) {
        this.orderConsumer = consumer;
        JSONObject requestJson = getSubscriptionJSONObject(Arrays.asList("order"));
        session.getAsyncRemote().sendObject(requestJson);
    }

    /**
     * instrument info 실시간 처리 등록
     * 
     * @param coinType 코인
     * @param consumer 콜백
     */
    public void registerInsturmentInfo(CoinEnum coinType, Consumer<JSONObject> consumer) {
        var previousValue = instrumentInfoConsumerMap.putIfAbsent(coinType, consumer);
        if (previousValue != null) {
            throw new RuntimeException("RegisterInsturmentInfo request called twice by the homogeneous parameter");
        }
        JSONObject requestJson = getSubscriptionJSONObject(
                Arrays.asList(String.format("instrument_info.100ms.%s", coinType.name())));
        session.getAsyncRemote().sendObject(requestJson);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JSONObject getSubscriptionJSONObject(List args) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("op", "subscribe");
        requestJson.put("args", args);
        return requestJson;
    }

    @Override
    public void close() throws IOException {
        session.close();
    }
}
