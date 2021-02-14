package org.tenok.coin.data.entity.impl.candle_index.bollinger_band;

import java.util.ArrayList;

import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.BasicIndexAbstract;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

@SuppressWarnings("serial")
public class BollingerBand extends BasicIndexAbstract<BBObject> {

    public BollingerBand(CoinEnum coinType, IntervalEnum interval, CandleList reference) {
        super(coinType, interval, reference);
    }

    @Override
    protected BBObject calculate(Candle item) {
        return new BBObject(calUpperBB(item, 20), calMiddleBB(item, 20), calLowerBB(item, 20));
    }

    private double calMA(Candle item, int period) {
        double closeSum = 0;
        double ma = 0;

        if (period > reference.size()) {
            return 0;

        } else {
            for (int i = reference.size() - 1; i >= reference.size() - period + 1; i--) {
                closeSum = closeSum + reference.elementAt(i).getClose();
            }
            closeSum = item.getClose() + closeSum;
            ma = closeSum / period;

            return ma;
        }
    }

    private double calUpperBB(Candle item, int period) {
        return calMiddleBB(item, period) + calStandardDeviation(item, period) * 2;
    }

    private double calMiddleBB(Candle item, int period) {
        return calMA(item, period);
    }

    private double calLowerBB(Candle item, int period) {
        return calMiddleBB(item, period) - calStandardDeviation(item, period) * 2;
    }

    private double calStandardDeviation(Candle item, int period) {

        if (period > reference.size()) {
            return 0;
        } else {

            double closeSum = 0;
            double deviationSum = 0;
            ArrayList<Candle> closeArray = new ArrayList<>();
            ArrayList<Double> deviationArray = new ArrayList<>();
            for (int i = reference.size() - 1; i >= reference.size() - period + 1; i--) {
                closeArray.add(reference.elementAt(i));
                closeSum = closeSum + reference.elementAt(i).getClose();
            }
            closeArray.add(item);
            closeSum = item.getClose() + closeSum;
            closeSum = closeSum / period;

            for (int i = 0; i < closeArray.size(); i++) {
                deviationArray.add(Math.pow(closeSum - closeArray.get(i).getClose(), 2));
            }
            for (int i = 0; i < deviationArray.size(); i++) {
                deviationSum = deviationSum + deviationArray.get(i);
            }
            return Math.sqrt(deviationSum / period);
        }
    }
}
