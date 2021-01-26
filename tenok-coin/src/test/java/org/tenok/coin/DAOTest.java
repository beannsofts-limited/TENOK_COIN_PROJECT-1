package org.tenok.coin;

import static org.junit.Assert.assertSame;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class DAOTest {
    @Test
    public void loginTest() {
        try {
            BybitDAO.getInstance().login("");
        } catch (LoginException e) {
            e.printStackTrace();
            assert false;
        }
        assert true;
    }

    @Test
    public void candleListText() {
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        assertSame(candleList.size(), 200);

        candleList.stream().forEachOrdered(System.out::println);
    }
}
