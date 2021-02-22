package org.tenok.coin;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.impl.RealtimeBacktestDAO;

public class RealtimeBacktestIndex {
    public static void main(String[] args) throws LoginException {
        RealtimeBacktestDAO.getInstance().login(args[0]);

        
    }
}
