package org.tenok.coin.data.entity.impl;

import org.tenok.coin.data.entity.WalletAccessable;

public class BybitWalletInfo implements WalletAccessable {

    private double walletBalance = 0.0;
    private double walletAvailableBalance = 0.0;

    public BybitWalletInfo(double walletBalance, double walletAvailableBalance) {
        this.walletBalance = walletBalance;
        this.walletAvailableBalance = walletAvailableBalance;
    }

    @Override
    public double getWalletBalance() {

        return this.walletBalance;
    }

    @Override
    public double getWalletAvailableBalance() {

        return this.walletAvailableBalance;
    }

}
