package org.tenok.coin.strategy;

import org.tenok.coin.type.CoinEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StrategyHandler {
    @Getter
    private CoinEnum coinType;
    private StrategyThread strategyInstance;
    private Thread strategyThread;

    public StrategyHandler(StrategyConfig config, StrategyThread strategyInstance, Thread strategyThread) {
        this.coinType = config.getCoinType();
        this.strategyInstance = strategyInstance;
        this.strategyThread = strategyThread;
    }

    /**
     * Strarts the Strategy Thread
     */
    public void start() {
        strategyThread.start();
    }

    /**
     * Stop the Strategy Thread
     */
    public void stop() {
        strategyThread.interrupt();
    }

    public void join() throws InterruptedException {
        strategyThread.join();
    }

    public void join(long millis) throws InterruptedException {
        strategyThread.join(millis);
    }

    public boolean isOpened() {
        return strategyInstance.isOpened();
    }

    /**
     * 레버리지를 업데이트한다.
     * <ul>
     * <li><strong><code>leverage > 0</code></strong>
     * <ul>
     * <li><em>롱 포지션</em> 전용 strategy</li>
     * </ul>
     * </li>
     * <li><strong><code>leverage < 0</code></strong>
     * <ul>
     * <li><em>숏 포지션</em> 전용 strategy</li>
     * </ul>
     * </li>
     * </ul>
     * </br>
     * 
     * @param leverage 거래 레버리지
     */
    public void updateLeverage(int leverage) {
        if (leverage == 0) {
            throw new IllegalArgumentException("Leverage cannot be 0.");
        }
        strategyInstance.updateLeverage(leverage);
    }

    /**
     * 해당 strategy가 사용할 수 있는 예수금의 비율을 업데이트한다.
     * <p>
     * ex) <code>availableRate = 0.5</code> is equal to <code>50%</code>
     * </p>
     * 
     * @param availableRate 사용가능한 예수금 비율
     */
    public void updateAvailableRate(double availableRate) {
        if (availableRate <= 0.0 || availableRate > 1.0) {
            throw new IllegalArgumentException("available rate must be in range of (0, 1]");
        }
        strategyInstance.updateAvailableRate(availableRate);
    }

    public String getStrategyName() {
        return strategyInstance.getStrategyName();
    }
}
