package org.tenok.coin.strategy;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.type.CoinEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class StrategyConfig {
    @NonNull
    private CoinEnum coinType;
    @NonNull
    private Class<? extends CoinDataAccessable> coinDataAccessableClass;
    @NonNull
    private Class<? extends Strategy> strategyClass;

    private int leverage;
    private double availableRate;

    public StrategyConfig copy() {
        return new StrategyConfig(coinType, coinDataAccessableClass, strategyClass, leverage, availableRate);
    }

    void updateLeverage(int leverage) {
        this.leverage = leverage;
    }

    void updateAvailableRate(double availableRate) {
        this.availableRate = availableRate;
    }
}
