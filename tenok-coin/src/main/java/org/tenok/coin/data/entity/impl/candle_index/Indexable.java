package org.tenok.coin.data.entity.impl.candle_index;

import org.tenok.coin.data.entity.impl.CandleList;

public interface Indexable<T> {
    void injectReference(CandleList candleList);
    void calculateNewCandle();
    void calculateCurrentCandle();
    T getReversed(int index);
}
