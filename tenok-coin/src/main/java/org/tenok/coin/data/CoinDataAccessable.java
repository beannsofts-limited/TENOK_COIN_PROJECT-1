package org.tenok.coin.data;

import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.OrderList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public interface CoinDataAccessable {
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval);
    public OrderList getOrderList();
    public PositionList getPositionList();
    public InstrumentInfo getInsturmentInfo(CoinEnum coinType);
    public WalletAccessable getWalletInfo();
    public void orderCoin(Orderable order);
    public void getPaidLimit(CoinEnum coinType);
}
