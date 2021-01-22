package org.tenok.coin.data.entity.impl;

import java.util.Stack;

import javax.swing.plaf.TreeUI;

import org.tenok.coin.data.RealtimeAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;


@SuppressWarnings("serial")
public class CandleList extends Stack<Candle> implements RealtimeAccessable {
    private CoinEnum coinType = null;
    private IntervalEnum interval = null;

    private Candle currentCandle = null;

    public CandleList(CoinEnum coinType, IntervalEnum interval) {
        this.coinType = coinType;
        this.interval = interval;
    }

    public CoinEnum getCoinType() {
        return this.coinType;
    }

    public IntervalEnum getInterval() {
        return this.interval;
    }

    public void registerNewCandle(Candle item) {
        super.push(item);
    }

    /**
     * 현재 confirm 되지 않은 캔들 업데이트
     */
    public void updateCurrentCandle(Candle item) {
        super.pop();
        confrim -> 1,2,3,4,5
        0 1 2 3 4 5
      0봉전 pop -> 0봉전 실시간데이터 변경 볼린저 ,ma 계산 -> 다시  0봉전 push 

    }

    @Override
    public Candle push(Candle item) {
        
        return super.push(item);
    }

        True 1봉전
        30초지나도 1봉전 stack 에서는


    @Override
    @Deprecated
    public synchronized Candle pop() {
        throw new RuntimeException("호출하지 마세요.");
    }
}
