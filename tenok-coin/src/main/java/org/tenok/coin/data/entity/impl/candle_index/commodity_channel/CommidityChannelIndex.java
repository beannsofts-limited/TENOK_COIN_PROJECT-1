package org.tenok.coin.data.entity.impl.candle_index.commodity_channel;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.candle_index.BasicIndexAbstract;

/**
 * CCI index
 */
public class CommidityChannelIndex extends BasicIndexAbstract<Double> {
    private static final long serialVersionUID = 4871857368700216513L;
    private int length;
    public static final double DIVISOR = 0.015;

    public CommidityChannelIndex(int length) {
        super();
        this.length = length;
    }

    @Override
    protected Double calculate() {
        return (getMeanPrice(reference.getReversed(0)) - calMA(length)) / (getMeanDeviation(length) * DIVISOR);
    }

    /**
     * mean price의 이동평균을 계산한다.
     */
    private double calMA(int period) {
        double meanPriceSum = 0;    // mean price = (high + low + close) / 3
        double ma = 0;

        if (period > reference.size() - 1) {
            return 0;
        } else {
            for (int i = 0; i < period; i++) {
                meanPriceSum += getMeanPrice(reference.getReversed(i));
            }
            ma = meanPriceSum / period;

            return ma;
        }
    }

    private double getMeanPrice(Candle item) {
        return (item.getHigh() + item.getLow() + item.getClose()) / 3.0;
    }

    private double getMeanDeviation(int period) {
        double meanDeviationSum = 0;   // mean price: (High + Low + Close) / 3
        double ma = 0;

        if (period > reference.size()) {
            return 0;
        } else {
            for (int i = 0; i < period; i++) {
                meanDeviationSum += Math.abs(calMA(period) - getMeanPrice(reference.getReversed(i)));
            }
            ma = meanDeviationSum / period;

            return ma;
        }
    }
    
}
