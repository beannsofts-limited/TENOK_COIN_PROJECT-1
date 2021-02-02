package org.tenok.coin.strategy;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.type.CoinEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class StrategyConfig implements Cloneable {
    @NonNull
    private CoinEnum coinType;
    @NonNull
    private Class<CoinDataAccessable> coinDataAccessableClass;
    @NonNull
    private Class<Strategy> strategyClass;

    private int leverage;
    private double availableRate;

    @Override
    public StrategyConfig clone() {
        return new StrategyConfig(coinType, coinDataAccessableClass, strategyClass, leverage, availableRate);
    }

    void updateLeverage(int leverage) {
        this.leverage = leverage;
    }

    void updateAvailableRate(double availableRate) {
        this.availableRate = availableRate;
    }
}
