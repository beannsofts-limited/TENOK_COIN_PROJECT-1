package org.tenok.coin.data.entity;

public interface WalletAccessable {
    public double getWalletBalance();
    public double getWalletAvailableBalance();
    public WalletAccessable setWalletBalance(double arg);
    public WalletAccessable setWalletAvailableBalance(double arg);
}
