package org.tenok.coin.data.entity.impl.candle_index;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;

public interface Indexable<T> {
    void injectReference(CandleList candleList);
    void calculateNewCandle(Candle item);
    void calculateCurrentCandle(Candle item);
    T getReversed(int index);
}
