package org.tenok.coin.data.entity.impl;

import java.util.HashMap;
import java.util.Map;
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
    private transient Map<Class<? extends Indexable<?>>, Indexable<?>> indexMap;

    public CandleList(CoinEnum coinType, IntervalEnum interval) {
        super();
        this.coinType = coinType;
        this.interval = interval;
        this.indexMap = new HashMap<>();
    }

    public CandleList() {
        super();
    }

    /**
     * 
     * confirm 이 true 일때 캔들을 확정지음 or kline으로 ??개 캔들 불러올때 계산해서 push 해줌
     */
    public void registerNewCandle(Candle item) {
        indexMap.values().parallelStream().forEach(index -> index.calculateNewCandle(item));
        item.setConfirmed(true);

        super.push(item);
    }

    /**
     * 현재 confirm 되지 않은 캔들 업데이트
     */
    public void updateCurrentCandle(Candle item) {

        super.pop();
        indexMap.values().parallelStream().forEach(index -> index.calculateCurrentCandle(item));
        item.setConfirmed(false);

        super.push(item);

        // 0봉전 pop -> 0봉전 실시간데이터 변경 볼린저 ,ma 계산 -> 다시 0봉전 push

    }

    public void addIndex(Class<? extends Indexable<?>> indexClass) {
        try {
            indexMap.put(indexClass, indexClass.getConstructor(CoinEnum.class, IntervalEnum.class, CandleList.class)
                    .newInstance(coinType, interval, this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeIndex(Class<? extends Indexable<?>> indexClass) {
        indexMap.remove(indexClass);
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

    public Object getIndexReversed(Class<? extends Indexable<?>> indexClass, int index) {
        return indexMap.get(indexClass).getReversed(index);
    }
}
