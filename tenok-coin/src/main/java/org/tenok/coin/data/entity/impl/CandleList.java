package org.tenok.coin.data.entity.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.tenok.coin.data.RealtimeAccessable;
import org.tenok.coin.data.entity.impl.candle_index.Indexable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class CandleList extends Stack<Candle> implements RealtimeAccessable {
    private CoinEnum coinType;
    private IntervalEnum interval;
    private transient List<Indexable<?>> indexList;

    public CandleList(CoinEnum coinType, IntervalEnum interval) {
        super();
        this.coinType = coinType;
        this.interval = interval;
        this.indexList = new ArrayList<>();
    }

    public CandleList() {
        super();
    }

    /**
     * 처음 open 된 캔들 등록
     */
    public void registerNewCandle(Candle item) {
        indexList.parallelStream().forEach(index -> index.calculateNewCandle(item));
        item.setConfirmed(false);

        super.push(item);
    }

    /**
     * 현재 confirm 되지 않은 캔들 업데이트
     */
    public void updateCurrentCandle(Candle item) {
        super.pop();
        indexList.parallelStream().forEach(index -> index.calculateCurrentCandle(item));
        item.setConfirmed(false);

        super.push(item);
    }

    /**
     * 지표를 등록한다.
     * 
     * @param indexClass 지표 클래스
     */
    public <E extends Indexable<?>> E createIndex(E indexObject) {
        indexList.add(indexObject);
        indexObject.injectReference(this);
        return indexObject;
    }

    /**
     * 지표를 삭제한다.
     * 
     * @param indexClass 지표 클래스
     */
    public void removeIndex(Indexable<?> indexClass) {
        indexList.remove(indexClass);
        indexClass.injectReference(null);
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = false)
    public Candle push(Candle item) {
        return super.push(item);
    }

    /**
     * @deprecated
     */
    @Override
    @Deprecated(forRemoval = false)
    public synchronized Candle pop() {
        throw new RuntimeException("호출하지 마세요.");
    }

    /**
     * 역배열 순서로 캔들을 가져온다. ex) HTS의 0봉전, 1봉전
     * 
     * @param index index of the element to return in reversed order
     * @return 역배열된 index 번 째의 candle
     */
    public Candle getReversed(int index) {
        return super.get(this.size() - index - 1);
    }
}
