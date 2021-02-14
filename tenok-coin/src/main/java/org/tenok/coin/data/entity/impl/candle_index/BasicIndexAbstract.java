package org.tenok.coin.data.entity.impl.candle_index;

import java.util.Stack;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

@SuppressWarnings("serial")
public abstract class BasicIndexAbstract<T> extends Stack<T> implements Indexable<T> {
    private CoinEnum coinType;
    private IntervalEnum interval;
    protected CandleList reference;

    protected BasicIndexAbstract(CoinEnum coinType, IntervalEnum interval, CandleList reference) {
        this.coinType = coinType;
        this.interval = interval;
        this.reference = reference;

        if (!reference.isEmpty()) {
            reference.stream().forEachOrdered(this::calculateNewCandle);
        }
    }

    public CoinEnum getCoinType() {
        return coinType;
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public CandleList getReference() {
        return reference;
    }

    protected abstract T calculate(Candle item);

    @Override
    public final void calculateNewCandle(Candle item) {
        push(calculate(item));
    }

    @Override
    public final void calculateCurrentCandle(Candle item) {
        pop();
        push(calculate(item));
    }

    @Override
    public final T getReversed(int index) {
        return get(size() - index - 1);
    }
}
