package org.tenok.coin;

import static org.junit.Assert.assertNotNull;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.commodity_channel.CommidityChannelIndex;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MAObject;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class CandleIndexTest {
    @Test
    public void maTest() throws LoginException {
        BybitDAO.getInstance().login("tenokMDC2021");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        MovingAverage ma = candleList.createIndex(new MovingAverage());
        assertNotNull(ma);
        long startTime = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - startTime > (long) 1000*60*2) {
                break;
            }
            MAObject maObj1 = ma.getReversed(0);
            MAObject maObj2 = ma.getReversed(1);
            MAObject maObj3 = ma.getReversed(2);

            System.out.printf("\r%f %f %f %f %f\n", maObj1.getMa5(), maObj1.getMa10(), maObj1.getMa20(), maObj1.getMa60(), maObj1.getMa120());
            System.out.printf("\r%f %f %f %f %f\n", maObj2.getMa5(), maObj2.getMa10(), maObj2.getMa20(), maObj2.getMa60(), maObj2.getMa120());
            System.out.printf("\r%f %f %f %f %f\n", maObj3.getMa5(), maObj3.getMa10(), maObj3.getMa20(), maObj3.getMa60(), maObj3.getMa120());

        }
    }
}
