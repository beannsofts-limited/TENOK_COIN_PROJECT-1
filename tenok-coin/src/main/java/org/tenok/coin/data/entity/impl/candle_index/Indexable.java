package org.tenok.coin.data.entity.impl.candle_index;

import org.tenok.coin.data.entity.impl.Candle;

public interface Indexable<T> {
    void calculateNewCandle(Candle item);
    void calculateCurrentCandle(Candle item);
    T getReversed(int index);
}
