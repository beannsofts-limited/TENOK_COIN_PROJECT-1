package org.tenok.coin.data.entity.impl;

import org.tenok.coin.data.RealtimeAccessable;
import org.tenok.coin.data.entity.WalletAccessable;

public class BybitWalletInfo implements WalletAccessable, RealtimeAccessable {
    private double walletBalance;
    private double walletAvailableBalance;

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

    @Override
    public WalletAccessable setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
        return this;
    }

    @Override
    public WalletAccessable setWalletAvailableBalance(double walletAvailableBalance) {
        this.walletAvailableBalance = walletAvailableBalance;
        return this;
    }

}
