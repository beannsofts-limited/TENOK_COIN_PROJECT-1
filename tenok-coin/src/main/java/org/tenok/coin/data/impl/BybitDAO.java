package org.tenok.coin.data.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.BybitWalletInfo;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.data.websocket.impl.BybitWebsocketProcessor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BybitDAO implements CoinDataAccessable, Closeable {
    // cached data field
    private Map<CoinEnum, Map<IntervalEnum, CandleList>> candleListIsCachedMap;
    private WalletAccessable walletInfo;
    private OrderList orderList;
    private PositionList positionList;

    // data accessable instance field
    private BybitWebsocketProcessor websocketProcessor = new BybitWebsocketProcessor();
    private BybitRestDAO restDAO;   // TODO 상호의존(순환참조) 해결
    private AuthDecryptor auth;

    private BybitDAO() {
        candleListIsCachedMap = new HashMap<>();

        Arrays.asList(CoinEnum.values()).parallelStream().forEach((coinType) -> {
            candleListIsCachedMap.put(coinType, new HashMap<>());
        });
        restDAO = new BybitRestDAO();
        this.websocketProcessor.init();
    }

    private static class BybitLazyLoader {
        public static BybitDAO INSTANCE = new BybitDAO();
    }

    public static BybitDAO getInstance() {
        return BybitLazyLoader.INSTANCE;
    }

    public void login(String password) throws LoginException {
        this.auth = AuthDecryptor.getInstance();
        this.auth.setPassword(password);
        if (!auth.validate()) {
            throw new LoginException("로그인 실패");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        if (!candleListIsCachedMap.get(coinType).containsKey(interval)) {
            // 해당 캔들 리스트가 캐시되어 있지 않을 경우.
            long intervalSec = interval.getSec();
            JSONObject jsonObject = restDAO.requestKline(coinType, interval, 200, new Date(System.currentTimeMillis() - intervalSec*200000L));
            JSONArray kLineArray = (JSONArray) jsonObject.get("result");
            CandleList candleList = new CandleList(coinType, interval);
            candleListIsCachedMap.get(coinType).put(interval, candleList);

            Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
                return (JSONObject) kLineObject;
            });
            map.forEachOrdered((JSONObject kLineObject) -> {
                double open = ((Number) kLineObject.get("open")).doubleValue();
                double high = ((Number) kLineObject.get("high")).doubleValue();
                double low = ((Number) kLineObject.get("low")).doubleValue();
                double close = ((Number) kLineObject.get("close")).doubleValue();
                double volume = ((Number) kLineObject.get("volume")).doubleValue();
                Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
                candleList.add(new Candle(startAt, volume, open, high, low, close));
            });
            // 실시간 kLine에 등록
            websocketProcessor.subscribeCandle(coinType, interval, candleList);
        }
        return candleListIsCachedMap.get(coinType).get(interval);
    }

    @Override
    public OrderList getOrderList() {
        if (orderList == null) {
            orderList = new OrderList();
            // TODO
        }
        return null;
    }

    @Override
    @Deprecated
    public PositionList getPositionList() {
        if (positionList == null) {
            positionList = new PositionList();

            // restDAO.
        }
        return null;
    }

    @Override
    public InstrumentInfo getInsturmentInfo(CoinEnum coinType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WalletAccessable getWalletInfo() {
        if (walletInfo == null) {
            walletInfo = new BybitWalletInfo(0, 0);
            websocketProcessor.subscribeWalletInfo(walletInfo);
        }
        return walletInfo;
    }

    @Override
    public void orderCoin(Orderable order) {
        // active order 실패 시 exception 뜨게 바꿨으면 좋겠음.
        // restDAO.placeActiveOrder(order.getSide(), order.getCoin(), order.getOrderType(), order.getQty(), order.getTIF());
    }

    @Override
    public void getPaidLimit(CoinEnum coinType) {
        // TODO Auto-generated method stub

    }

    @Deprecated(forRemoval = true)
    public String getApiKey() {
        return null;
    }

    @Deprecated(forRemoval = true)
    public String getSign() {
        return null;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
