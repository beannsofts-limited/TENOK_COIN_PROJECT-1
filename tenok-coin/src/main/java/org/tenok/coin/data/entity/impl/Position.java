package org.tenok.coin.data.entity.impl;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Position {
    private CoinEnum coinType = null;
    private SideEnum side = null;
    private double qty = 0.0;
    private double entryPrice = 0.0;
    private double liqPrice = 0.0;
    private int leverage = 0;
    private double todayProfit = 0.0;
    private double wholeProfit = 0.0;    
}
