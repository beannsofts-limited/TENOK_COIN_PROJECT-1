package org.tenok.coin.data.impl;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.BacktestOrderable;
import org.tenok.coin.data.entity.Backtestable;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.BybitWalletInfo;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.entity.impl.Position;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.util.CoinMapper;

/**
 * this class aint thread safe
 */
public class BacktestDAO implements CoinDataAccessable, Backtestable, BacktestOrderable {
    private static Logger logger = Logger.getLogger(BacktestDAO.class);
    private PositionList myPosition = new PositionList();
    private Map<CoinEnum, Map<IntervalEnum, CandleList>> candleListCachedMap; // 실시간 처럼 보이는 기만용 캔들
    private Map<CoinEnum, Map<IntervalEnum, CandleList>> candleListWholeCachedMap; // 전체 캔들 데이터
    private WalletAccessable wallet = new BybitWalletInfo(1000000, 1000000);

    /**
     * 현재 시간을 표현한다. 정수 1 하나가 1분을 의미.
     */
    private int currentIndex = -1;
    private double wholeProfit = 0;
    private double realTimeProfit = 0;

    private static double wholeThreadProfit = 0.0;

    private BacktestDAO() {
        candleListCachedMap = new EnumMap<>(CoinEnum.class);
        candleListWholeCachedMap = new EnumMap<>(CoinEnum.class);

        for (var coinType : CoinEnum.values()) {
            candleListCachedMap.put(coinType, new EnumMap<>(IntervalEnum.class));
            candleListWholeCachedMap.put(coinType, new EnumMap<>(IntervalEnum.class));
            for (var interval : IntervalEnum.values()) {
                candleListCachedMap.get(coinType).put(interval, new CandleList(coinType, interval));
                candleListWholeCachedMap.get(coinType).put(interval, new CandleList(coinType, interval));
            }
        }

    }

    private static class BacktestLazyLoader {
        protected static final Map<Runnable, BacktestDAO> INSTANCE = new HashMap<>();
    }

    public static BacktestDAO getInstance(Runnable thread) {
        return BacktestLazyLoader.INSTANCE.computeIfAbsent(thread, k -> new BacktestDAO());
    }

    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        if (candleListCachedMap.get(coinType).get(interval).isEmpty()) {
            // 전체 캔들리스트를 먼저 불러온다.
            candleListWholeCachedMap.get(coinType).get(interval)
                    .addAll(CoinMapper.getInstance().getWholeCandleList(coinType, interval));

            if (currentIndex == 0L) {
                // 5개 캔들을 집어넣고 리턴
                candleListCachedMap.get(coinType).get(interval)
                        .addAll(candleListWholeCachedMap.get(coinType).get(interval).subList(0, 5));
            } else {
                // current index가 0이 아닌 상황 = 이미 Backtest 상에서 시간이 흐른 상황에서 새로운 캔들을 불러올 경우,
                // nextSeq에서 문제가 생길 수 있으므로 이와 같이 currentIndex를 포함하는 서브리스트를 리턴
                candleListCachedMap.get(coinType).get(interval)
                        .addAll(candleListWholeCachedMap.get(coinType).get(interval).subList(0, 5 + currentIndex));
            }

        }
        return candleListCachedMap.get(coinType).get(interval);
    }

    @Override
    public synchronized void orderCoin(Orderable order) {

        if (order.getSide() == SideEnum.OPEN_BUY || order.getSide() == SideEnum.OPEN_SELL) {
            // 포지션 오픈 arrayList에 등록

            Position pos = Position.builder().coinType(order.getCoinType())
                    .entryPrice(getCurrentPrice(order.getCoinType())).side(order.getSide()).liqPrice(0)
                    .qty(order.getQty()).leverage(1).build();
            // positionList에 여러코인에 대한 정보를 저장해야하나????
            myPosition.add(pos);
            wallet.setWalletAvailableBalance(
                    wallet.getWalletAvailableBalance() - (order.getQty() * getCurrentPrice(order.getCoinType())));
            logger.debug(String.format("orderCoin : Open Position(coin: %s, entryPrice: %f, side: %s, qty: %f)",
                    pos.getCoinType().getKorean(), pos.getEntryPrice(), pos.getSide().getKorean(), pos.getQty()));
        } else {
            // 포지션 청산 -> close 값 업데이트

            myPosition.parallelStream().filter(
                    pred -> pred.getCoinType().equals(order.getCoinType()) && pred.getSide().equals(order.getSide()))
                    .forEach(action -> {
                        double profit = (((getCurrentPrice(order.getCoinType()) / action.getEntryPrice()) - 1) * 100);
                        if (action.getSide() == SideEnum.OPEN_SELL) {
                            wholeProfit = wholeProfit - profit;
                            BacktestDAO.wholeThreadProfit -= profit;
                            wallet.setWalletBalance(
                                    wallet.getWalletBalance() - ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                            - (action.getEntryPrice() * action.getQty())));
                            wallet.setWalletAvailableBalance(wallet.getWalletAvailableBalance()
                                    - ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                            - (action.getEntryPrice() * action.getQty())));

                        } else {
                            wholeProfit = wholeProfit + profit;
                            BacktestDAO.wholeThreadProfit += profit;
                            wallet.setWalletBalance(
                                    wallet.getWalletBalance() + ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                            - (action.getEntryPrice() * action.getQty())));
                            wallet.setWalletAvailableBalance(wallet.getWalletAvailableBalance()
                                    + ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                            - (action.getEntryPrice() * action.getQty())));
                        }

                        logger.info(String.format(
                                "orderCoin : Close Position(coin: %s, entryPrice: %lf closePrice: %lf, side: %s, qty: %lf, profit : %lf)",
                                action.getCoinType().getKorean(), action.getEntryPrice(),
                                getCurrentPrice(order.getCoinType()), order.getSide().getKorean(), order.getQty(),
                                getRealtimeProfit(action.getCoinType(), order)));
                    });
        }
    }

    /**
     * 
     * @return 현재 오픈중인 코인의 실시간 수익률
     */
    @Override
    public double getRealtimeProfit(CoinEnum coinType, Orderable order) {
        realTimeProfit = 0;
        // myPosition 에서 현재 close가 0인 coinType을 가진 position
        myPosition.parallelStream().filter(
                pred -> pred.getCoinType().equals(order.getCoinType()) && pred.getSide().equals(order.getSide()))
                .forEach(action -> realTimeProfit = realTimeProfit
                        + ((getCurrentPrice(coinType) / action.getEntryPrice()) - 1) * 100);
        return realTimeProfit;
    }

    @Override
    public WalletAccessable getWalletInfo() {
        return wallet;
    }

    @Override
    public PositionList getPositionList() {

        return myPosition;
    }

    @Override
    public double getCurrentPrice(CoinEnum coinType) {
        Candle currentCandle = this.getCandleList(coinType, IntervalEnum.ONE).get(0);
        return currentCandle.getClose();
    }

    @Override
    public double getWholeProfit() {
        return wholeProfit;
    }

    public static double getWholeThreadProfit() {
        return wholeThreadProfit;
    }

    /**
     * 시간 축이 1칸 우측으로.
     * 
     * @return 더이상 nextSeq할 수 없으면 <code>true</code>리턴
     */
    @Override
    public boolean nextSeq(CoinEnum coinType) {
        // 1분봉이 캐싱되어 있어야, 하단에서 isEnd를 계산할 수 있음.
        getCandleList(coinType, IntervalEnum.ONE);

        for (var interval : IntervalEnum.values()) {
            if (currentIndex % interval.getBacktestNumber() == 0) {
                if (candleListWholeCachedMap.get(coinType).get(interval).isEmpty()) {
                    // 아직 캐싱되지 않은 캔들일 경우, 패싱
                    continue;
                }
                candleListCachedMap.get(coinType).get(interval)
                        .add(candleListWholeCachedMap.get(coinType).get(interval).get(currentIndex + 6));
            }
        }

        return currentIndex++ >= candleListWholeCachedMap.get(coinType).get(IntervalEnum.ONE).size() - 7;
    }

    /**
     * 초기환경으로 돌아간 것 처럼 행동.
     */
    public void resetAll() {
        currentIndex = 0;
        candleListCachedMap.entrySet().parallelStream().forEach(
                entSet -> entSet.getValue().entrySet().stream().forEach(allCandle -> allCandle.getValue().clear()));
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = false)
    public OrderedList getOrderList() {

        return null;
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = false)
    public InstrumentInfo getInstrumentInfo(CoinEnum coinType) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = false)
    public void getPaidLimit(CoinEnum coinType) {
        throw new UnsupportedOperationException();
    }

}
