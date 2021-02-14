package org.tenok.coin.data.entity.impl;

import java.lang.reflect.InvocationTargetException;
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
     * 처음 open 된 캔들 등록
     */
    public void registerNewCandle(Candle item) {
        indexMap.values().parallelStream().forEach(index -> index.calculateNewCandle(item));
        item.setConfirmed(false);

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
    }

    /**
     * 지표를 등록한다.
     * 
     * @param indexClass 지표 클래스
     */
    public void addIndex(Class<? extends Indexable<?>> indexClass) {
        indexMap.put(indexClass, (Indexable<?>) instantiateIndexClass(indexClass));
    }

    /**
     * 지표를 삭제한다.
     * 
     * @param indexClass 지표 클래스
     */
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

    /**
     * 역배열 순서로 캔들의 지표를 가져온다. ex) HTS의 0봉전, 1봉전
     * 
     * 만약 해당 지표가 addIndex() 메소드로 add 되지 않았다면, 내부적으로 add 및 지표를 계산한다.
     * 지표를 LazyLoad 할 수 있는 방법이나, 성능저하가 있을 수 있으니 주의
     * <p>
     * <strong>사용례)</strong>
     * </p>
     * <pre>
     * {@code
     * BBObject bbObject = (BBObject) candleList.getIndexReversed(BollingerBand.class, 0);
     * boolean upperBB = bbObject.getUpperBB();
     * boolean middleBB = bbObject.getMiddleBB();
     * }
     * </pre>
     * 
     * @param indexClass 불러올 지표
     * @param index      index of the element to return in reversed order
     * @return 지표 Object
     */
    public Object getIndexReversed(Class<? extends Indexable<?>> indexClass, int index) {
        indexMap.computeIfAbsent(indexClass, param -> (Indexable<?>) instantiateIndexClass(param));
        return indexMap.get(indexClass).getReversed(index);
    }

    private Object instantiateIndexClass(Class<? extends Indexable<?>> indexClass) {
        try {
            return indexClass.getConstructor(CoinEnum.class, IntervalEnum.class, CandleList.class).newInstance(coinType,
                    interval, this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Index class cannot be instantiated");
        }
    }
}
