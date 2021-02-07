package org.tenok.coin.data.impl;

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
    private long currentIndex = 0;
    // private List<BacktestOrder> orderedList;
    private double wholeProfit = 0;
    private double realTimeProfit = 0;

    private static double wholeThreadProfit = 0.0;

    private BacktestDAO() {
        candleListCachedMap = new HashMap<>();
        candleListWholeCachedMap = new HashMap<>();

        for (var coinType : CoinEnum.values()) {
            candleListCachedMap.put(coinType, new HashMap<>());
            candleListWholeCachedMap.put(coinType, new HashMap<>());
            for (var interval : IntervalEnum.values()) {
                candleListCachedMap.get(coinType).put(interval, new CandleList(coinType, interval));
                candleListWholeCachedMap.get(coinType).put(interval, new CandleList(coinType, interval));
            }
        }

    }

    private static class BacktestLazyLoader {
        public static final Map<Runnable, BacktestDAO> INSTANCE = new HashMap<>();
    }

    public static BacktestDAO getInstance(Runnable thread) {
        if (!BacktestLazyLoader.INSTANCE.containsKey(thread)) {
            BacktestLazyLoader.INSTANCE.put(thread, new BacktestDAO());
        }
        return BacktestLazyLoader.INSTANCE.get(thread);
    }

    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        if (candleListCachedMap.get(coinType).get(interval).size() == 0) {
            candleListCachedMap.get(coinType).put(interval, CoinMapper.getInstance().getWholeCandleList(coinType, interval));
        }
        return candleListCachedMap.get(coinType).get(interval);
    }

    @Override
    public void orderCoin(Orderable order) {

        if (order.getSide() == SideEnum.OPEN_BUY || order.getSide() == SideEnum.OPEN_SELL) {
            // 포지션 오픈 arrayList에 등록

            Position pos = Position.builder().coinType(order.getCoinType())
                    .entryPrice(getCurrentPrice(order.getCoinType())).side(order.getSide()).liqPrice(0)
                    .qty(order.getQty()).leverage(1).build();
            // positionList에 여러코인에 대한 정보를 저장해야하나????
            myPosition.add(pos);
            wallet.setWalletAvailableBalance(
                    wallet.getWalletAvailableBalance() - (order.getQty() * getCurrentPrice(order.getCoinType())));
            logger.info(String.format("orderCoin : Open Position(coin: %s, entryPrice: %lf, side: %s, qty: %lf)",
                    pos.getCoinType().getKorean(), pos.getEntryPrice(), pos.getSide().getKorean(), pos.getQty()));
        } else {
            // 포지션 청산 -> close 값 업데이트

            myPosition.parallelStream().filter(pred -> {
                return pred.getCoinType().equals(order.getCoinType()) && pred.getSide().equals(order.getSide());
            }).peek(action -> {
                double profit = (((getCurrentPrice(order.getCoinType()) / action.getEntryPrice()) - 1) * 100);
                if (action.getSide() == SideEnum.OPEN_SELL) {
                    wholeProfit = wholeProfit - profit;
                    wholeThreadProfit = wholeThreadProfit - profit;
                    wallet.setWalletBalance(
                            wallet.getWalletBalance() - ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                    - (action.getEntryPrice() * action.getQty())));
                    wallet.setWalletAvailableBalance(wallet.getWalletAvailableBalance()
                            - ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                    - (action.getEntryPrice() * action.getQty())));

                } else {
                    wholeProfit = wholeProfit + profit;
                    wholeThreadProfit = wholeThreadProfit + profit;
                    wallet.setWalletBalance(
                            wallet.getWalletBalance() + ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                    - (action.getEntryPrice() * action.getQty())));
                    wallet.setWalletAvailableBalance(wallet.getWalletAvailableBalance()
                            + ((getCurrentPrice(order.getCoinType()) * order.getQty())
                                    - (action.getEntryPrice() * action.getQty())));
                }

                logger.info(String.format(
                        "orderCoin : Close Position(coin: %s, entryPrice: %lf closePrice: %lf, side: %s, qty: %lf, profit : %lf)",
                        action.getCoinType().getKorean(), action.getEntryPrice(), getCurrentPrice(order.getCoinType()),
                        order.getSide().getKorean(), order.getQty(), getRealtimeProfit(action.getCoinType(), order)));
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
        myPosition.parallelStream().filter(pred -> {
            return pred.getCoinType().equals(order.getCoinType()) && pred.getSide().equals(order.getSide());
        }).peek(action -> {
            realTimeProfit = realTimeProfit + ((getCurrentPrice(coinType) / action.getEntryPrice()) - 1) * 100;

        });
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
     */
    @Override
    public boolean nextSeq() {
        if (currentIndex++ == 0L) {
            return false;
        }
        for (var coinType : CoinEnum.values()) {

            for (var interval : IntervalEnum.values()) {
                if (currentIndex % interval.getBacktestNumber() == 0) {
                    if (candleListWholeCachedMap.get(coinType).get(interval).size() == 0) {
                        continue;
                    }
                    candleListCachedMap.get(coinType).get(interval)
                            .add(candleListWholeCachedMap.get(coinType).get(interval).get((int) currentIndex - 1));
                }
            }

        }

        // TODO MAXIMUM of Current Index
        if (currentIndex > 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 초기환경으로 돌아간 것 처럼 행동.
     */
    public void resetAll() {
        currentIndex = 0;
        candleListCachedMap.entrySet().parallelStream().forEach(action -> {
            action.getValue().entrySet().stream().forEach(aaa -> {
                aaa.getValue().clear();
            });
        });
    }

    @Override
    @Deprecated
    public OrderedList getOrderList() {

        return null;
    }

    @Override
    @Deprecated
    public InstrumentInfo getInstrumentInfo(CoinEnum coinType) {
        return null;
    }

    @Override
    @Deprecated
    public void getPaidLimit(CoinEnum coinType) {

    }

}
