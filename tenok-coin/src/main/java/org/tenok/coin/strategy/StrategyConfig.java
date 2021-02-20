package org.tenok.coin.strategy;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class StrategyConfig {
    @NonNull
    private CoinEnum coinType;
    private SideEnum openType;
    @NonNull
    private Class<? extends CoinDataAccessable> coinDataAccessableClass;
    @NonNull
    private Class<? extends Strategy> strategyClass;
    private int leverage;
    private double availableRate;

    public StrategyConfig(CoinEnum coinType, SideEnum openType,
            Class<? extends CoinDataAccessable> coinDataAccessableClass, Class<? extends Strategy> strategyClass,
            int leverage, double availableRate) {
        
        if (openType == SideEnum.CLOSE_BUY || openType == SideEnum.CLOSE_SELL) {
            throw new IllegalArgumentException("openType cannot be close side");
        }
        this.coinType = coinType;
        this.openType = openType;
        this.coinDataAccessableClass = coinDataAccessableClass;
        this.strategyClass = strategyClass;
        this.leverage = leverage;
        this.availableRate = availableRate;
    }

    public StrategyConfig copy() {
        return new StrategyConfig(coinType, openType, coinDataAccessableClass, strategyClass, leverage, availableRate);
    }

    void updateLeverage(int leverage) {
        this.leverage = leverage;
    }

    void updateAvailableRate(double availableRate) {
        this.availableRate = availableRate;
    }
}
