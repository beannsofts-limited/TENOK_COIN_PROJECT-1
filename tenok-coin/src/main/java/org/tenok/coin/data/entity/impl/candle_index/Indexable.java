package org.tenok.coin.data.entity.impl.candle_index;

import org.tenok.coin.data.entity.impl.CandleList;

public interface Indexable<T> {
    void injectReference(CandleList candleList);
    void calculateNewCandle();
    void calculateCurrentCandle();

    /**
     * <code>index</code>봉 전 캔들의 지표 데이터를 불러온다.
     * @param index 봉 전
     * @return 지표 데이터
     */
    T getReversed(int index);
}
