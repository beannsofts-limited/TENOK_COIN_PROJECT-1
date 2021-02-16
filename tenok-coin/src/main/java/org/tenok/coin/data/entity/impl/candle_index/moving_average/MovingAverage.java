package org.tenok.coin.data.entity.impl.candle_index.moving_average;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.candle_index.BasicIndexAbstract;

@SuppressWarnings("serial")
public class MovingAverage extends BasicIndexAbstract<MAObject> {

    @Override
    protected MAObject calculate(Candle item) {
        double ma5 = calMA(item, 5);
        double ma10 = calMA(item, 10);
        double ma20 = calMA(item, 20);
        double ma60 = calMA(item, 60);
        double ma120 = calMA(item, 120);
        return new MAObject(ma5, ma10, ma20, ma60, ma120);
    }

    private double calMA(Candle item, int period) {
        double closeSum = 0;
        double ma = 0;

        if (period > reference.size()) {
            return 0;

        } else {
            for (int i = reference.size() - 1; i > reference.size() - period; i--) {
                closeSum = closeSum + reference.elementAt(i).getClose();
            }
            closeSum = item.getClose() + closeSum;
            ma = closeSum / period;

            return ma;
        }
    }
}
