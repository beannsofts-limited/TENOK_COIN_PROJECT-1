package org.tenok.coin.data.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.data.websocket.impl.BybitWebsocketProcessor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class BybitDAO implements CoinDataAccessable, Closeable {
    private Map<CoinEnum, Map<IntervalEnum, Boolean>> candleListIsCachedMap;
    private BybitWebsocketProcessor websocketProcessor;
    private AuthDecryptor auth;

    private BybitDAO() {
        candleListIsCachedMap = new HashMap<>();
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
        try {
            this.auth = new AuthDecryptor(new File("./secret.auth"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new LoginException("login failed");
    }

    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        if (!this.candleListIsCachedMap.get(coinType).get(interval).booleanValue()) {
            // 해당 캔들 리스트가 캐시되어 있지 않을 경우.
        }
        return null;
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

    public String getApiKey() {
        return null;
    }

    public String getSign() {
        return null;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
