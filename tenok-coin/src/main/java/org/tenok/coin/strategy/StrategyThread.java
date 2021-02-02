package org.tenok.coin.strategy;

import java.lang.reflect.InvocationTargetException;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.BacktestOrderable;
import org.tenok.coin.data.entity.WalletAccessable;

import lombok.extern.log4j.Log4j;

@Log4j
class StrategyThread implements Runnable {
    private CoinDataAccessable coinDAOInstance;
    private StrategyConfig config;
    private Strategy strategyInstance;
    private WalletAccessable wallet;
    private Thread thisThread;

    public StrategyThread(StrategyConfig config) {
        try {
            coinDAOInstance = (CoinDataAccessable) config.getCoinDataAccessableClass().getDeclaredMethod("getInstance", (Class<?>[]) null)
                    .invoke((Object) null, (Object) null);
            strategyInstance = config.getStrategyClass().getDeclaredConstructor(CoinDataAccessable.class)
                    .newInstance(coinDAOInstance);
            this.config = config;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        wallet = coinDAOInstance.getWalletInfo();
    }

    @Override
    public void run() {
        thisThread = Thread.currentThread();
        while (true) {
            if (strategyInstance.isNotOpened()) {
                double openRBI = strategyInstance.testOpenRBI();
                double currentAvailable = wallet.getWalletAvailableBalance();
                coinDAOInstance.orderCoin(null);    // TODO
            } else {
                coinDAOInstance.orderCoin(null);
            }

            if (coinDAOInstance instanceof BacktestOrderable) {
                ((BacktestOrderable) coinDAOInstance).nextSeq();
            }

            if (thisThread.isInterrupted()) {
                log.info(String.format("%s strategy thread interrupted", thisThread.getName()));
                return; // TODO instrrupted 되면 그대로 끝낼지. 아님 자동매도 할지.
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info(String.format("%s strategy thread interrupted", thisThread.getName()));
                return;
            }
        }
    }

    public void updateLeverage(int leverage) {
        config.updateLeverage(leverage);
    }

    public void updateAvailableRate(double availableRate) {
        config.updateAvailableRate(availableRate);
    }
}
