package org.tenok.coin.strategy;

import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.InsufficientCostException;
import org.tenok.coin.data.entity.BackTestable;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.Position;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.extern.log4j.Log4j;

@Log4j
class StrategyThread implements Runnable {
    private CoinDataAccessable coinDAOInstance;
    private StrategyConfig config;
    private Strategy strategyInstance;
    private WalletAccessable wallet;
    private Position myPosition;

    public StrategyThread(StrategyConfig config) {
        try {
            if (BackTestable.class.isAssignableFrom(config.getCoinDataAccessableClass())) {
                // backtestable 클래스 일 경우 Runnable 객체 집어넣고 getInstance 호출
                coinDAOInstance = (CoinDataAccessable) config.getCoinDataAccessableClass()
                        .getDeclaredMethod("getInstance", Runnable.class).invoke((Object) null, this);
                log.info("get instance from Backtestable DAO Object");
            } else {
                coinDAOInstance = (CoinDataAccessable) config.getCoinDataAccessableClass()
                        .getDeclaredMethod("getInstance", (Class<?>[]) null).invoke((Object) null);
                log.info("get instance from Bybit DAO Object");
            }
            strategyInstance = config.getStrategyClass()
                    .getDeclaredConstructor(CoinDataAccessable.class, CoinEnum.class)
                    .newInstance(coinDAOInstance, config.getCoinType());
            this.config = config;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        wallet = coinDAOInstance.getWalletInfo();
    }

    @Override
    public void run() {

        while (true) {
            if (strategyInstance.isNotOpened()) {
                double openRBI = strategyInstance.testOpenRBI();
                if (openRBI != 0.0) {
                    double currentAvailable = wallet.getWalletAvailableBalance();
                    SideEnum side;
                    if (config.getLeverage() > 0) {
                        side = SideEnum.OPEN_BUY;
                    } else {
                        side = SideEnum.OPEN_SELL;
                    }

                    // 주문하고자 하는 코인의 현재가
                    double currentPrice = coinDAOInstance.getCurrentPrice(config.getCoinType());

                    // 예수금 / 현재가
                    double qty = currentAvailable / currentPrice;

                    if (config.getCoinType() == CoinEnum.BTCUSDT) {
                        qty = Math.floor(qty * 1000) / 1000.0;  // 비트코인은 세자리 까지
                        qty = qty-0.001;
                    } else if (config.getCoinType() == CoinEnum.ETHUSDT || config.getCoinType() == CoinEnum.BCHUSDT) {
                        qty = Math.floor(qty * 100) / 100.0;  // 두자리 까지
                        qty = qty-0.01;
                    } else {
                        qty = Math.floor(qty * 10) / 10.0;  // 한 자리 까지
                        qty = qty-0.1;
                    }
                    log.info(String.format("예수금: %f 시가: %f 개수: %.1f", currentAvailable, currentPrice, qty));
                    Orderable order = ActiveOrder.builder().coinType(config.getCoinType())
                            .orderType(OrderTypeEnum.MARKET).qty(qty).side(side).tif(TIFEnum.IOC).leverage(config.getLeverage()).build();
                    try {
                        coinDAOInstance.orderCoin(order);
                    } catch (InsufficientCostException e) {
                        throw new RuntimeException(e);
                    }

                    myPosition = Position.builder().coinType(config.getCoinType())
                            .entryPrice(coinDAOInstance.getCurrentPrice(config.getCoinType()))
                            .leverage(config.getLeverage()).liqPrice(0).qty(qty).side(side).build();

                    strategyInstance.setIsopened(true);
                }

            } else {
                if (strategyInstance.testCloseRBI()) {
                    SideEnum side;

                    if (config.getLeverage() > 0) {
                        side = SideEnum.CLOSE_SELL;
                    } else {
                        side = SideEnum.CLOSE_BUY;
                    }

                    Orderable order = ActiveOrder.builder().coinType(config.getCoinType())
                            .orderType(OrderTypeEnum.MARKET).qty(myPosition.getQty()).side(side).leverage(config.getLeverage()).tif(TIFEnum.GTC)
                            .build();
                    try {
                        coinDAOInstance.orderCoin(order);
                    } catch (InsufficientCostException e) {
                        throw new RuntimeException(e);
                    }
                    strategyInstance.setIsopened(false);
                }
            }

            if (coinDAOInstance instanceof BackTestable) {
                // Next seq 호출하여 다음 시간으로 이동
                boolean isEnd = ((BackTestable) coinDAOInstance).nextSeq(config.getCoinType());
                if (isEnd) {
                    log.info("Backtest exceed");
                    break;
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                log.info(String.format("%s strategy thread interrupted", Thread.currentThread().getName()));
                return; // TODO instrrupted 되면 그대로 끝낼지. 아님 자동매도 할지.
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.info(String.format("%s strategy thread interrupted", Thread.currentThread().getName()));
                Thread.currentThread().interrupt();
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

    public boolean isOpened() {
        return strategyInstance.isOpened();
    }
}
