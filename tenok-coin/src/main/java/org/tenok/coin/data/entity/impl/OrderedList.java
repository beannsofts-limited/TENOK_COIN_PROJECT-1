package org.tenok.coin.data.entity.impl;

import java.util.ArrayList;

import org.tenok.coin.data.entity.Orderable;

@SuppressWarnings("serial")
public class OrderedList extends ArrayList<OrderedData> {
    public OrderedData findLatestMatching(Orderable order) {
        return this.stream().sorted((obj1, obj2) -> obj2.getTimeStamp().compareTo(obj1.getTimeStamp())).filter(pred -> {
            return pred.getCoinType() == order.getCoinType() && pred.getQty() == order.getQty()
                    && pred.getSide() == order.getSide();
        }).findFirst().get();
    }
}
