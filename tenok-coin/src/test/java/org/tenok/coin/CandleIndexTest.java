package org.tenok.coin;

import static org.junit.Assert.assertNotNull;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.commodity_channel.CommidityChannelIndex;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class CandleIndexTest {
    @Test
    public void cciTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        CommidityChannelIndex cci = candleList.createIndex(new CommidityChannelIndex(9));
        assertNotNull(cci);
        long startTime = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - startTime > 1000*60*2) {
                break;
            }
            Double cciValue = cci.getReversed(0);

            System.out.printf("\r%f", cciValue);
        }
    }
}
