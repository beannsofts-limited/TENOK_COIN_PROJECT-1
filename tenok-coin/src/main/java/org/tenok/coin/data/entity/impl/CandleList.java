package org.tenok.coin.data.entity.impl;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.plaf.TreeUI;

import com.google.gson.JsonObject;

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
    public void updateCurrentCandle(Candle item, JsonObject confirm) {
        
        super.pop();
        double ma5 = calMA(item, 5);
        double ma10 = calMA(item, 10);
        double ma20 = calMA(item, 20);
        double ma60 = calMA(item, 60);
        double ma120 = calMA(item, 120);
        double lowerBB=calLowerBB(item, 20);
        double middleBB=calMiddleBB(item, 20);
        double upperBB=calUpperBB(item, 20);
        
        item.setMa5(ma5);
        item.setMa10(ma10);
        item.setMa20(ma20);
        item.setMa60(ma60);
        item.setMa120(ma120);
        item.setLowerBB(lowerBB);
        item.setMiddleBB(middleBB);
        item.setUpperBB(upperBB);

    
        super.push(item);

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


    private double calMA(Candle item, int period){
        double closeSum=0;

        if(period<super.size()){
            return 0;

        }else{

            for(int i =super.size()-1; i>=super.size()-period+1; i--){
              
               closeSum = closeSum+ super.elementAt(i).getClose();
            }
            closeSum = item.getClose() + closeSum;
            closeSum = closeSum/period;
    
            return closeSum;
        }
        
    }

    private double calUpperBB(Candle item, int period){
        return calMiddleBB(item, period) + calStandardDeviation(item, period)*2; 
    }
    private double calMiddleBB(Candle item, int period){
        return calMA(item, period);
    }
    private double calLowerBB(Candle item, int period ){
        return calMiddleBB(item, period) - calStandardDeviation(item, period)*2;
    }

    private double calStandardDeviation(Candle item, int period){
        double closeSum=0;
        double deviationSum=0;
        ArrayList<Candle> closeArray = new ArrayList<>();
        ArrayList<Double> deviationArray = new ArrayList<>();
        for(int i =super.size()-1; i>=super.size()-period+1; i--){
            closeArray.add(super.elementAt(i));
            closeSum = closeSum+ super.elementAt(i).getClose();
         }
         closeArray.add(item);
         closeSum = item.getClose() + closeSum;
         closeSum = closeSum/period;
         
         for(int i = 0; i<closeArray.size(); i++){
             deviationArray.add(Math.pow(closeSum-closeArray.get(i).getClose(), 2));
         }
         for(int i = 0; i< deviationArray.size(); i++){
             deviationSum = deviationSum + deviationArray.get(i);
         }
         return Math.sqrt(deviationSum/period);
    }
}
