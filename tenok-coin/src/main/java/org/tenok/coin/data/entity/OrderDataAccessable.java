package org.tenok.coin.data.entity;

import java.util.Date;

public interface OrderDataAccessable extends Orderable {
    /**
     * 
     * @return timestamp
     */
    public Date getTimeStamp();
}
