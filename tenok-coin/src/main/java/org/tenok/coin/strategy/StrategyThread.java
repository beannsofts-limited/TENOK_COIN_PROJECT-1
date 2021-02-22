package org.tenok.coin.strategy;

import org.tenok.coin.config.ConfigParser;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.InsufficientCostException;
import org.tenok.coin.data.entity.BackTestable;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.Position;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.data.impl.RealtimeBacktestDAO;
import org.tenok.coin.slack.SlackSender;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

import lombok.extern.log4j.Log4j;

@Log4j(topic = "bybit.strategy.logger")
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
                log.info("got instance from Backtestable DAO Class");
            } else if (BybitDAO.class.isAssignableFrom(config.getCoinDataAccessableClass())) {
                coinDAOInstance = (CoinDataAccessable) config.getCoinDataAccessableClass()
                        .getDeclaredMethod("getInstance", (Class<?>[]) null).invoke((Object) null);
                log.info("got instance from Bybit DAO Class");
            } else if (RealtimeBacktestDAO.class.isAssignableFrom(config.getCoinDataAccessableClass())) {
                coinDAOInstance = (CoinDataAccessable) config.getCoinDataAccessableClass()
                        .getDeclaredMethod("getInstance", (Class<?>[]) null).invoke((Object) null);
                log.info("got instance from realtime DAO Class");
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

                    SideEnum side = config.getOpenType();

                    double qty = getAvailable() * openRBI;

                    log.info(String.format("%s %s 전략: 포지션 오픈 신호 포착", config.getCoinType().getKorean(),
                            strategyInstance.getStrategyName()));
                    log.debug(String.format("예수금: %f USDT 시가: %f 개수: %f", wallet.getWalletAvailableBalance(),
                            coinDAOInstance.getCurrentPrice(config.getCoinType()), qty));
                    Orderable order = ActiveOrder.builder().coinType(config.getCoinType())
                            .orderType(OrderTypeEnum.MARKET).qty(qty).side(side).tif(TIFEnum.IOC)
                            .leverage(config.getLeverage()).build();
                    try {
                        coinDAOInstance.orderCoin(order);
                    } catch (InsufficientCostException e) {
                        String errMsg = String.format("%s %s전략: 예수금 부족", config.getCoinType().getKorean(),
                                strategyInstance.getStrategyName());
                        SlackSender.getInstance().sendText(errMsg);
                        log.warn(errMsg);
                        return;
                    }

                    myPosition = Position.builder().coinType(config.getCoinType())
                            .entryPrice(coinDAOInstance.getCurrentPrice(config.getCoinType()))
                            .leverage(config.getLeverage()).liqPrice(0).qty(qty).side(side).build();

                    strategyInstance.setIsopened(true);
                }

            } else {
                if (strategyInstance.testCloseRBI()) {
                    SideEnum side;

                    if (config.getOpenType() == SideEnum.OPEN_BUY) {
                        side = SideEnum.CLOSE_SELL;
                    } else {
                        side = SideEnum.CLOSE_BUY;
                    }

                    log.info(String.format("%s %s 전략: 청산 신호 포착", config.getCoinType().getKorean(),
                            strategyInstance.getStrategyName()));
                    Orderable order = ActiveOrder.builder().coinType(config.getCoinType())
                            .orderType(OrderTypeEnum.MARKET).qty(myPosition.getQty()).side(side)
                            .leverage(config.getLeverage()).tif(TIFEnum.GTC).build();
                    try {
                        coinDAOInstance.orderCoin(order);
                    } catch (InsufficientCostException e) {
                        String errMsg = String.format("%s %s전략: 예수금 부족", config.getCoinType().getKorean(),
                                strategyInstance.getStrategyName());
                        SlackSender.getInstance().sendText(errMsg);
                        log.warn(errMsg);
                        return;
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
                Thread.sleep(ConfigParser.getUpdateRate());
            } catch (InterruptedException e) {
                log.info(String.format("%s strategy thread interrupted", Thread.currentThread().getName()));
                Thread.currentThread().interrupt();
                return;
            }
        }

        log.info(String.format("%s %s전략: 전략 스레드 종료", config.getCoinType().getKorean(),
                strategyInstance.getStrategyName()));
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

    /**
     * this strategy thread에서 현재 구매 가능한 코인 개수
     * 
     * @return 현재 구매 가능한 코인 개수
     */
    private double getAvailable() {
        double currentAvailable = wallet.getWalletAvailableBalance();
        // 주문하고자 하는 코인의 현재가
        double currentPrice = coinDAOInstance.getCurrentPrice(config.getCoinType());

        // 예수금 / 현재가
        double qty = currentAvailable / currentPrice * config.getAvailableRate();
        if (config.getCoinType() == CoinEnum.BTCUSDT) {
            qty = Math.floor(qty * 1000) / 1000.0; // 비트코인은 세자리 까지
        } else if (config.getCoinType() == CoinEnum.ETHUSDT || config.getCoinType() == CoinEnum.BCHUSDT) {
            qty = Math.floor(qty * 100) / 100.0; // 두자리 까지
        } else {
            qty = Math.floor(qty * 10) / 10.0; // 한 자리 까지
        }
        return qty;
    }

    public String getStrategyName() {
        return strategyInstance.getStrategyName();
    }
}
