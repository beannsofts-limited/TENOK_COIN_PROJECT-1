package org.tenok.coin.data.entity.impl;

import java.util.Stack;

import javax.swing.plaf.TreeUI;

import org.tenok.coin.data.RealtimeAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.Getter;
import lombok.experimental.SuperBuilder;



@Getter
@SuppressWarnings("serial")
public class CandleList extends Stack<Candle> implements RealtimeAccessable {
    private CoinEnum coinType = null;
    private IntervalEnum interval = null;

    private Candle currentCandle = null;

    public CandleList(CoinEnum coinType, IntervalEnum interval) {
        this.coinType = coinType;
        this.interval = interval;
    }

    public void registerNewCandle(Candle item) {
        super.push(item);
    }

    /**
     * 현재 confirm 되지 않은 캔들 업데이트
     */
    public void updateCurrentCandle(Candle item) {
        super.pop();

        super.push(currentCandle);
      //0봉전 pop -> 0봉전 실시간데이터 변경 볼린저 ,ma 계산 -> 다시  0봉전 push 

    }

    @Override
    public Candle push(Candle item) {
        
        return super.push(item);
    }

    


    @Override
    @Deprecated
    public synchronized Candle pop() {
        throw new RuntimeException("호출하지 마세요.");
    }


    public double CalMA5(){
        
    }
}
