package org.tenok.coin.strategy;

import java.lang.reflect.InvocationTargetException;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.BacktestOrderable;
import org.tenok.coin.data.entity.WalletAccessable;

public class StrategyThread implements Runnable {
    private CoinDataAccessable coinDAOInstance;
    private Strategy strategyInstance;
    private WalletAccessable wallet;

    public StrategyThread(Class<? extends CoinDataAccessable> coinDaoClass, Class<? extends Strategy> strategyClass)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {

        coinDAOInstance = (CoinDataAccessable) coinDaoClass.getDeclaredMethod("getInstance", (Class<?>[]) null)
                .invoke((Object) null, (Object) null);

        strategyInstance = strategyClass.getDeclaredConstructor(CoinDataAccessable.class).newInstance(coinDAOInstance);
        wallet = coinDAOInstance.getWalletInfo();
    }

    @Override
    public void run() {
        if (strategyInstance.isNotOpened()) {
            double openRBI = strategyInstance.testOpenRBI();
            double currentAvailable = wallet.getWalletAvailableBalance();
            coinDAOInstance.orderCoin(null);
        } else {
            coinDAOInstance.orderCoin(null);
        }

        if (coinDAOInstance instanceof BacktestOrderable) {
            ((BacktestOrderable) coinDAOInstance).nextSeq();
        }
    }
}
