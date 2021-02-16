package org.tenok.coin.data.entity.impl.candle_index;

import java.util.ArrayList;

import org.tenok.coin.data.entity.impl.CandleList;

@SuppressWarnings("serial")
public abstract class BasicIndexAbstract<T> extends ArrayList<T> implements Indexable<T> {
    protected CandleList reference;

    protected BasicIndexAbstract() {

    }

    public CandleList getReference() {
        return reference;
    }

    protected abstract T calculate();

    @Override
    public final void injectReference(CandleList candleList) {
        this.reference = new CandleList();
        for (int i = 0; i < candleList.size(); i++) {
            this.reference.add(candleList.get(i));
            calculateNewCandle();
        }

        this.reference = candleList;
    }

    @Override
    public final void calculateNewCandle() {
        super.add(calculate());
    }

    @Override
    public final void calculateCurrentCandle() {
        ((IndexObject) super.get(super.size() - 1)).updateData(calculate());
    }

    @Override
    public final T getReversed(int index) {
        if (reference == null) {
            throw new IllegalStateException("CandleList::createIndex를 호출하여 사용해야 합니다.");
        }
        return get(size() - index - 1);
    }
}
