package org.tenok.coin;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.commodity_channel.CommidityChannelIndex;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class IndexCandleTest {
    @Test
    public void cciTest() throws IOException, LoginException {
        try {

            BybitDAO.getInstance().login("");
        } catch (Exception e) {

        }
        CandleList cl = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        CommidityChannelIndex cci = cl.createIndex(new CommidityChannelIndex(9));

        for (Double double1 : cci) {
            System.out.println(double1);
        }
        BybitDAO.getInstance().close();
        assertNotNull(cci);
    }
}
