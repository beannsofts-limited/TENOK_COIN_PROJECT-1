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
        // return (getMeanPrice(item) - calMA(item, length))/(getMeanDeviation(item, length) * DIVISOR);
        return null;
    }

    /**
     * mean price의 이동평균을 계산한다.
     */
    private double calMA(Candle item, int period) {
        double meanPriceSum = 0;   // mean price: (High + Low + Close) / 3
        double ma = 0;

        if (period > reference.size()) {
            return 0;
        } else {
            for (int i = reference.size() - 1; i >= reference.size() - period + 1; i--) {
                meanPriceSum += getMeanPrice(reference.get(i));
            }
            meanPriceSum += getMeanPrice(item);
            ma = meanPriceSum / period;

            return ma;
        }
    }

    private double getMeanPrice(Candle item) {
        return (item.getHigh() + item.getLow() + item.getClose()) / 3.0;
    }

    private double getMeanDeviation(Candle item, int period) {
        double meanDeviationSum = 0;   // mean price: (High + Low + Close) / 3
        double ma = 0;

        if (period > reference.size()) {
            return 0;
        } else {
            for (int i = reference.size() - 1; i >= reference.size() - period + 1; i--) {
                meanDeviationSum += Math.abs(calMA(reference.get(i), period) - getMeanPrice(reference.get(i)));
            }
            meanDeviationSum += calMA(item, period);
            ma = meanDeviationSum / period;

            return ma;
        }
    }
    
}
