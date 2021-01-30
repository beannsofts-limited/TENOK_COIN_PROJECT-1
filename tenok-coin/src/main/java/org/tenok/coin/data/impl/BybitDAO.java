package org.tenok.coin.data.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.BybitInstrumentInfo;
import org.tenok.coin.data.entity.impl.BybitWalletInfo;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderedData;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.websocket.impl.BybitWebsocketProcessor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;
import org.tenok.coin.type.TickDirectionEnum;

/**
 * 바이비트 데이터 엑세스&주문 용도 DAO 클래스
 */
public class BybitDAO implements CoinDataAccessable, Closeable {
    private static Logger logger = Logger.getLogger(BybitDAO.class);
    private boolean isLoggedIn;

    // cached data field
    private Map<CoinEnum, Map<IntervalEnum, CandleList>> candleListIsCachedMap;
    private WalletAccessable walletInfo;
    private OrderedList orderList;
    private PositionList positionList;
    private Map<CoinEnum, InstrumentInfo> instrumentInfo;

    // data accessable instance field
    private BybitWebsocketProcessor websocketProcessor = new BybitWebsocketProcessor();
    private BybitRestDAO restDAO;
    private AuthDecryptor auth;

    private BybitDAO() {
        candleListIsCachedMap = new HashMap<>();
        instrumentInfo = new HashMap<>();

        Arrays.asList(CoinEnum.values()).parallelStream().forEach((coinType) -> {
            candleListIsCachedMap.put(coinType, new HashMap<>());
        });
        restDAO = new BybitRestDAO();
    }

    private static class BybitLazyLoader {
        public static final BybitDAO INSTANCE = new BybitDAO();
    }

    public static BybitDAO getInstance() {
        return BybitLazyLoader.INSTANCE;
    }

    /**
     * rest api & websocket에 로그인 요청. BybitDAO 기능 사용을 위해서는 <strong>필수적으로</strong> 가장
     * 먼저 호출되어야 함.
     * 
     * @param password 비밀번호
     * @throws LoginException 로그인 실패 시 발생
     */
    public void login(String password) throws LoginException {
        this.auth = AuthDecryptor.getInstance();
        this.auth.setPassword(password);
        if (!auth.validate()) {
            throw new LoginException("로그인 실패");
        }
        logger.info(String.format("login success with pw: %s", password));
        this.websocketProcessor.init();
        this.isLoggedIn = true;
    }

    /**
     * 실시간으로 동기화 되는 캔들리스트 리턴
     * 
     * @param coinType 조회할 코인 타입
     * @param interval 캔들 간격
     * @return CandleList instance which is realtime accessable
     */
    @SuppressWarnings("unchecked")
    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        if (!candleListIsCachedMap.get(coinType).containsKey(interval)) {
            // 해당 캔들 리스트가 캐시되어 있지 않을 경우.
            long intervalSec = interval.getSec();
            JSONObject jsonObject = restDAO.requestKline(coinType, interval, 200,
                    new Date(System.currentTimeMillis() - intervalSec * 200000L));
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
                candleList.registerNewCandle(new Candle(startAt, volume, open, high, low, close));
            });

            // 실시간 kLine에 등록
            websocketProcessor.subscribeCandle(coinType, interval, candleList);
        }
        return candleListIsCachedMap.get(coinType).get(interval);
    }

    /**
     * 주문 내역 조회
     */
    @Override
    @SuppressWarnings("unchecked")
    public OrderedList getOrderList() {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        if (orderList == null) {
            orderList = new OrderedList();
            for (var coinType : CoinEnum.values()) {
                JSONObject jsonObj = restDAO.getActiveOrder(coinType);
                JSONArray dataArray = (JSONArray) ((JSONObject) jsonObj.get("result")).get("data");
                if (dataArray == null) {
                    // 해당 코인으로 거래한 내역이 없을 경우에는 dataArray가 null임.
                    // NPE 막기 위해서 continue 한 뒤, 다음 코인 체크
                    continue;
                }
                dataArray.stream().forEachOrdered(data -> {
                    JSONObject dataObject = (JSONObject) data;
                    CoinEnum coin = CoinEnum.valueOf((String) dataObject.get("symbol"));
                    TIFEnum tif = TIFEnum.valueOfApiString((String) dataObject.get("time_in_force"));
                    double qty = ((Number) dataObject.get("qty")).doubleValue();
                    OrderTypeEnum orderType = OrderTypeEnum.valueOfApiString((String) dataObject.get("order_type"));
                    SideEnum sideEnum = null;
                    boolean reduceOnly = (boolean) dataObject.get("reduce_only");
                    String side = (String) dataObject.get("side");
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
                    orderList.add(OrderedData.builder().coinType(coin).tif(tif).qty(qty).side(sideEnum)
                            .orderType(orderType).build());
                });
            } // #end foreach
            websocketProcessor.subscribeOrder(orderList);
        } // #end if
        return orderList;
    }

    /**
     * position list 조회
     */
    @Override
    @Deprecated
    public PositionList getPositionList() {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        if (positionList == null) {
            positionList = new PositionList();
        }
        return null;
    }

    /**
     * instrument info 조회
     */
    @Override
    public InstrumentInfo getInstrumentInfo(CoinEnum coinType) {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        if (!instrumentInfo.containsKey(coinType)) {
            JSONObject result = (JSONObject) restDAO.getInstrumentInfo(coinType).get("result");
            double lastPrice = ((Number) result.get("last_price")).doubleValue();
            TickDirectionEnum lastTickDirection = TickDirectionEnum
                    .valueOfApiString((String) result.get("last_tick_direction"));
            double price24hPcnt = ((Number) result.get("price_24h_pcnt")).doubleValue();
            double highPrice24h = ((Number) result.get("high_price_24h")).doubleValue();
            double lowPrice24h = ((Number) result.get("low_price_24h")).doubleValue();
            double price1hPcnt = ((Number) result.get("price_1h_pcnt")).doubleValue();
            double highPrice1h = ((Number) result.get("high_price_1h")).doubleValue();
            double lowPrice1h = ((Number) result.get("low_price_1h")).doubleValue();
            var insInfo = BybitInstrumentInfo.builder().coinType(coinType).lastPriceE4((long) lastPrice * 10000)
                    .lastTickDirection(lastTickDirection).price24hPcntE6((long) price24hPcnt * 1000000)
                    .highPrice24hE4((long) highPrice24h * 10000).lowPrice24hE4((long) lowPrice24h * 10000)
                    .price1hPcntE6((long) price1hPcnt * 1000000).highPrice1hE4((long) highPrice1h * 10000)
                    .lowPrice1hE4((long) lowPrice1h * 10000).build();
            instrumentInfo.put(coinType, insInfo);
            websocketProcessor.subscribeInsturmentInfo(coinType, insInfo);
        }
        return instrumentInfo.get(coinType);
    }

    /**
     * 현재가 조회
     * 
     * @param coinType 조회할 코인
     */
    public double getCurrentPrice(CoinEnum coinType) {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        return 0;
    }

    /**
     * wallet 객체 요청. 예수금 조회 가능
     * 
     * @return wallet instance
     */
    @Override
    public WalletAccessable getWalletInfo() {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        if (walletInfo == null) {
            walletInfo = new BybitWalletInfo(0, 0);
            websocketProcessor.subscribeWalletInfo(walletInfo);
        }
        return walletInfo;
    }

    /**
     * 코인 주문
     * 
     * @param order 주문 청구 객체
     */
    @Override
    public void orderCoin(Orderable order) {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }
        // active order 실패 시 exception 뜨게 바꿨으면 좋겠음.
        JSONObject res = restDAO.placeActiveOrder(order.getSide(), order.getCoinType(), order.getOrderType(),
                order.getQty(), order.getTIF());
        System.out.println(res.toJSONString());
    }

    @Override
    @Deprecated
    public void getPaidLimit(CoinEnum coinType) {
        if (!isLoggedIn) {
            throw new RuntimeException("DAO instance is not logged in");
        }

    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    @Override
    public void close() throws IOException {
        websocketProcessor.close();
    }
}
