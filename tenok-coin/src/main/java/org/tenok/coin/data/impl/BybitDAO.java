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
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.data.websocket.impl.BybitWebsocketProcessor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BybitDAO implements CoinDataAccessable, Closeable {
    private Map<CoinEnum, Map<IntervalEnum, CandleList>> candleListIsCachedMap;
    private BybitWebsocketProcessor websocketProcessor;
    private BybitRestDAO restDAO;   // TODO 상호의존(순환참조) 해결
    private AuthDecryptor auth;

    private BybitDAO() {
        candleListIsCachedMap = new HashMap<>();

        Arrays.asList(CoinEnum.values()).parallelStream().forEach((coinType) -> {
            candleListIsCachedMap.put(coinType, new HashMap<>());
        });
        restDAO = new BybitRestDAO();
    }

    private static class BybitLazyLoader {
        public static BybitDAO INSTANCE = new BybitDAO();
    }

    public static BybitDAO getInstance() {
        return BybitLazyLoader.INSTANCE;
    }

    public void init() {

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

            JSONObject jsonObject = restDAO.requestKline(coinType, interval, 200, new Date(System.currentTimeMillis()));
            JSONArray kLineArray = (JSONArray) jsonObject.get("result");
            CandleList candleList = new CandleList(coinType, interval);
            candleListIsCachedMap.get(coinType).put(interval, candleList);

            Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
                return (JSONObject) kLineObject;
            });
            map.forEachOrdered((JSONObject kLineObject) -> {
                double open = (double) kLineObject.get("open");
                double high = (double) kLineObject.get("high");
                double low = (double) kLineObject.get("low");
                double close = (double) kLineObject.get("close");
                double volume = (double) kLineObject.get("volume");
                Date startAt = new Date((long) kLineObject.get("start_at"));
                candleList.add(new Candle(startAt, volume, open, high, low, close));
            });
            // 실시간 kLine에 등록
            websocketProcessor.subscribeKLine(coinType, interval, candleList);
        }
        return candleListIsCachedMap.get(coinType).get(interval);
    }

    @Override
    public OrderList getOrderList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PositionList getPositionList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InstrumentInfo getInsturmentInfo(CoinEnum coinType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WalletAccessable getWalletInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void orderCoin(Orderable order) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPaidLimit(CoinEnum coinType) {
        // TODO Auto-generated method stub

    }

    @Deprecated(forRemoval = false)
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
