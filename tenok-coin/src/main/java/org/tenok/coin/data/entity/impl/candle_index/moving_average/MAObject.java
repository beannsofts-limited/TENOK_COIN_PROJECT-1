package org.tenok.coin.data.entity.impl.candle_index.moving_average;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MAObject {
    private double ma5;
    private double ma10;
    private double ma20;
    private double ma60;
    private double ma120;
}
