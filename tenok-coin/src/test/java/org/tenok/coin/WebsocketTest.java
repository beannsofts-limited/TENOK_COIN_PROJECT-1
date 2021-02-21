package org.tenok.coin;

import static org.junit.Assert.assertNotNull;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.entity.impl.PositionList;
import org.tenok.coin.data.impl.BybitDAO;

public class WebsocketTest {
    @Test
    public void PositionTest() throws LoginException {
        BybitDAO.getInstance().login("tenokMDC2021");
        PositionList pl = BybitDAO.getInstance().getPositionList();
        assertNotNull(pl);
        while (true);
    }
    
}
