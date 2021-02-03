package org.tenok.coin;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.tenok.coin.data.impl.BybitDAO;

public class App {
    public static void main(String[] args) throws InterruptedException, IOException, LoginException {
        System.out.println(new File("./secret.auth").getCanonicalPath());
        BybitDAO.getInstance().login("tenok2019");
    }
}
