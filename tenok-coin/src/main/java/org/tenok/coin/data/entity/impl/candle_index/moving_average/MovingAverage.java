package org.tenok.coin.data.entity.impl.candle_index.moving_average;

import org.tenok.coin.data.entity.impl.candle_index.BasicIndexAbstract;

@SuppressWarnings("serial")
public class MovingAverage extends BasicIndexAbstract<MAObject> {

    @Override
    protected MAObject calculate() {
        double ma2 = calMA(2);
        double ma5 = calMA(5);
        double ma10 = calMA(10);
        double ma20 = calMA(20);
        double ma60 = calMA(60);
        double ma120 = calMA(120);
        return new MAObject(ma2, ma5, ma10, ma20, ma60, ma120);
    }

    private double calMA(int period) {
        double closeSum = 0;
        double ma = 0;

        if (period > reference.size() - 1) {
            return 0;
        } else {
            for (int i = 0; i < period; i++) {
                closeSum += reference.getReversed(i).getClose();
            }
            ma = closeSum / period;

            return ma;
        }
    }
}
