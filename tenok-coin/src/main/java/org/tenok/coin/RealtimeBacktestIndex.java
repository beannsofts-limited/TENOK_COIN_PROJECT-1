package org.tenok.coin;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.entity.impl.BacktestOrder;
import org.tenok.coin.data.impl.RealtimeBacktestDAO;
import org.tenok.coin.slack.SlackSender;
import org.tenok.coin.strategy.StrategyConfig;
import org.tenok.coin.strategy.StrategyRunner;
import org.tenok.coin.strategy.impl.CciExitLongStrategy;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

public class RealtimeBacktestIndex {
    public static void main(String[] args) throws LoginException, InterruptedException, IllegalAccessException {
        RealtimeBacktestDAO.getInstance().login(args[0]);

        StrategyRunner runner = new StrategyRunner();
        var handler = runner.runStrategy(new StrategyConfig(CoinEnum.LINKUSDT, SideEnum.OPEN_BUY, RealtimeBacktestDAO.class, CciExitLongStrategy.class, 1, 1));

        handler.start();

        Thread.sleep(1000L * 60L * 60L * 30L);   // 30시간

        handler.stop();

        var orderList = RealtimeBacktestDAO.getInstance().getBacktestOrderList();
        double earnUSDT = 0;
        double profit = 0;

        for (BacktestOrder backtestOrder : orderList) {
            earnUSDT += backtestOrder.getEarnUSDT();
            profit += backtestOrder.getProfit();
        }
        SlackSender.getInstance().sendText(String.format("%s 검증 완료", handler.getStrategyName()));
        SlackSender.getInstance().sendText(String.format("수익: %f USDT%n수익률: %f %%n", earnUSDT, profit / orderList.size()));
    }
}
