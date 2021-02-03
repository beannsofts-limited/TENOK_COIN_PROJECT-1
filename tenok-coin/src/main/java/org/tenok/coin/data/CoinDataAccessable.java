package org.tenok.coin.data;

import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
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

    public InstrumentInfo getInstrumentInfo(CoinEnum coinType);

    public WalletAccessable getWalletInfo();

    public void orderCoin(Orderable order);

    public void getPaidLimit(CoinEnum coinType);

    public double getCurrentPrice(CoinEnum coinType);
}
