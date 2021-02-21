package org.tenok.coin.data.entity.impl.candle_index.moving_average;

import org.tenok.coin.data.entity.impl.candle_index.IndexObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class MAObject implements IndexObject {
    private double ma2;
    private double ma5;
    private double ma10;
    private double ma20;
    private double ma60;
    private double ma120;

    @Override
    public void updateData(Object obj) {
        MAObject maObject = (MAObject) obj;
        this.ma2 = maObject.ma2;
        this.ma5 = maObject.ma5;
        this.ma10 = maObject.ma10;
        this.ma20 = maObject.ma20;
        this.ma60 = maObject.ma60;
        this.ma120 = maObject.ma120;
    }
}
