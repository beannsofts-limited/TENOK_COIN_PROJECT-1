package org.tenok.coin.data.entity;

import java.util.Date;

public interface OrderDataAccessable extends Orderable {
    public double getEntryPrice();

    public double getExitPrice();

    /**
     * 
     * @return timestamp
     */
    public Date getTimeStamp();
}
