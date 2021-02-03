package org.tenok.coin.data.entity;

public interface OrderDataAccessable extends Orderable {
    public double getEntryPrice();

    public double getExitPrice();
}
