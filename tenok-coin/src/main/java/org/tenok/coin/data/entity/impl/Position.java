package org.tenok.coin.data.entity.impl;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Position {
    private CoinEnum coinType;
    private SideEnum side;
    private double qty;
    private double entryPrice;
    private double liqPrice;
    private int leverage;
}
