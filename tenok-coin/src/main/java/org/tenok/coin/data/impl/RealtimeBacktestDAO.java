package org.tenok.coin.data.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.InsufficientCostException;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.data.entity.impl.BacktestOrder;
import org.tenok.coin.data.entity.impl.BybitWalletInfo;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.InstrumentInfo;
import org.tenok.coin.data.entity.impl.OrderedList;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.SideEnum;

public class RealtimeBacktestDAO implements CoinDataAccessable {
    private static Logger logFile = Logger.getLogger("bybit.realtimeDAO.logger");
    private WalletAccessable myWallet;
    private Map<Orderable, BacktestOrder> orderListMap;

    private RealtimeBacktestDAO() {
        myWallet = new BybitWalletInfo(50, 50);
        orderListMap = new HashMap<>();
    }

    private static class Holder {
        static final RealtimeBacktestDAO INSTANCE = new RealtimeBacktestDAO();
    }

    public static RealtimeBacktestDAO getInstance() {
        return Holder.INSTANCE;
    }

    public void login(String password) throws LoginException {
        BybitDAO.getInstance().login(password);
    }

    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        return BybitDAO.getInstance().getCandleList(coinType, interval);
    }

    @Override
    public OrderedList getOrderList() {
        return null;
    }

    @Override
    public PositionList getPositionList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WalletAccessable getWalletInfo() {
        return myWallet;
    }

    @Override
    public void orderCoin(Orderable order) throws InsufficientCostException {
        for (var entry : orderListMap.entrySet()) {
                if (entry.getKey().getCoinType() == order.getCoinType() && entry.getKey().getQty() == order.getQty()) {
                    if (entry.getKey().getSide() == SideEnum.OPEN_BUY && order.getSide() == SideEnum.CLOSE_SELL) {
                        // 롱 포지션 청산
                        entry.getValue().setExitDate(new Date());
                        entry.getValue().setExitPrice(getCurrentPrice(entry.getKey().getCoinType()));
                    } else if (entry.getKey().getSide() == SideEnum.OPEN_SELL && order.getSide() == SideEnum.CLOSE_BUY) {
                        // 숏 포지션 청산
                        entry.getValue().setExitDate(new Date());
                        entry.getValue().setExitPrice(getCurrentPrice(entry.getKey().getCoinType()));
                        entry.getValue().setLeverage(entry.getValue().getLeverage() * -1);
                    } else {
                        continue;
                    }
                    return;
                }
            }
        BacktestOrder backtestOrder = BacktestOrder.builder().coinType(order.getCoinType())
                .entryPrice(getCurrentPrice(order.getCoinType())).leverage(order.getLeverage())
                .orderType(order.getOrderType()).qty(order.getQty()).side(order.getSide()).tif(order.getTIF())
                .timestamp(new Date()).build();
        orderListMap.put(order, backtestOrder);
        logFile.info(String.format("%s 코인 %d 레버리지로 %f 개 %s", order.getCoinType(), order.getLeverage(), order.getQty(), order.getSide()));
    }

    @Override
    public double getCurrentPrice(CoinEnum coinType) {
        return BybitDAO.getInstance().getCurrentPrice(coinType);
    }

    @Override
    public InstrumentInfo getInstrumentInfo(CoinEnum coinType) {
        return BybitDAO.getInstance().getInstrumentInfo(coinType);
    }

    public List<BacktestOrder> getBacktestOrderList() {
        List<BacktestOrder> backtestOrders = new ArrayList<>();
        for (var entry : orderListMap.entrySet()) {
            if (entry.getValue().getExitDate() != null) {
                backtestOrders.add(entry.getValue());
            }
        }
        return backtestOrders;
    }

}
