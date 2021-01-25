package org.tenok.coin;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.tenok.coin.data.impl.BybitDAO;

public class DAOText {
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
}
