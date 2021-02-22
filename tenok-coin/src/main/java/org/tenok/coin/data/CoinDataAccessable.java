package org.tenok.coin.data;

import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.InstrumentInfo;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

/**
 * Coin Data Accessable: DAO sepcification
 */
public interface CoinDataAccessable {
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval);

    public OrderedList getOrderList();

    public PositionList getPositionList();

    /**
     * 현재 잔고 현황 리턴.
     * @return 잔고현황 객체
     */
    public WalletAccessable getWalletInfo();

    /**
     * 코인을 주문한다.
     * @param order 주문 상세 내용
     * @throws InsufficientCostException 주문가능 금액 부족
     */
    public void orderCoin(Orderable order) throws InsufficientCostException;

    /**
     * 해당 코인의 현재 시가를 받아온다.
     * @param coinType 조회할 코인
     * @return 시가
     */
    public double getCurrentPrice(CoinEnum coinType);

    public InstrumentInfo getInstrumentInfo(CoinEnum coinType);
}
