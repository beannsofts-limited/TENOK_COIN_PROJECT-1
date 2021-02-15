package org.tenok.coin.data.entity.impl.candle_index;

import java.util.Stack;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;

@SuppressWarnings("serial")
public abstract class BasicIndexAbstract<T> extends Stack<T> implements Indexable<T> {
    protected CandleList reference;

    protected BasicIndexAbstract() {

    }

    public CandleList getReference() {
        return reference;
    }

    protected abstract T calculate(Candle item);

    @Override
    public void injectReference(CandleList candleList) {
        this.reference = candleList;
        if (!reference.isEmpty()) {
            reference.stream().forEachOrdered(this::calculateNewCandle);
        }
    }

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
        if (reference == null) {
            throw new IllegalStateException("CandleList::createIndex를 호출하여 사용해야 합니다.");
        }
        return get(size() - index - 1);
    }
}
