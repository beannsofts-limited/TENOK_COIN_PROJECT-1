package org.tenok.coin.data.entity.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.CoinDataAccessable;
import org.tenok.coin.data.entity.Backtestable;
import org.tenok.coin.data.entity.InstrumentInfo;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.WalletAccessable;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.data.BybitRestDAO;

public class BacktestDAO implements CoinDataAccessable, Backtestable {

    private Map<String, String> myPosition = new HashMap<String, String>();
    private double walletBalance = 1000000;
    private double profit = 0;
    private BybitRestDAO restDAO = new BybitRestDAO();


    /**
     * implNote 데이터가 담긴 json 파일 업데이트
     * json 파일 파싱 후 candle 데이터 return
     * @return candleList
     * 
     */
    @Override
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval) {
        // loadCandleList(coinType, interval);

        try {
            updateCandleList(coinType, interval);
            String path = "./../";
            String filePath = path.concat(coinType.name() + "_" + interval.getApiString() + ".txt");
            JSONParser parser = new JSONParser();
            Object obj;
            obj = parser.parse(new FileReader(filePath));
            JSONArray jsonObject = (JSONArray) obj;
            String load = jsonObject.toString();
            Object kLineobj;
            kLineobj = parser.parse(load);
            JSONArray kLineArray = (JSONArray) kLineobj;
    
                CandleList candleList = new CandleList(coinType, interval);
    
                Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
                    return (JSONObject) kLineObject;
                });
                map.forEachOrdered((JSONObject kLineObject) -> {
                    double open = ((Number) kLineObject.get("open")).doubleValue();
                    double high = ((Number) kLineObject.get("high")).doubleValue();
                    double low = ((Number) kLineObject.get("low")).doubleValue();
                    double close = ((Number) kLineObject.get("close")).doubleValue();
                    double volume = ((Number) kLineObject.get("volume")).doubleValue();
                    Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
                    Candle candle = (new Candle(startAt, volume, open, high, low, close));
                    candleList.registerNewCandle(candle);
                });
                return candleList;
        } catch (ParseException e) {
            
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 
     *  candle 데이터가 담긴 json 파일 update
     *  캐시된 데이터가 없을 경우 최신부터 처음까지 데이터 load
     *  캐시된 데이터가 있을 경우 최신부터 기록된 데이터 까지 load
     */
    public void updateCandleList(CoinEnum coinType, IntervalEnum interval) {

        try {
            long intervalSec = interval.getSec();
            Scanner scan;
            String candleData = null;
            String path = "./../";
            String filePath = path.concat(coinType.name() + "_" + interval.getApiString() + ".txt");
            JSONParser parser = new JSONParser();
            Object obj;
            File file = new File(filePath);
            scan = new Scanner(file);
            Date prevFrom = null;
            String load ;
            if (!scan.hasNextLine()) { // 캐시된 데이터가 비어 있을 경우 현재 데이터 부터 처음 데이터까지 불러오기
                //최신의 200개 가져오기
                
                JSONObject firstCandleObject = restDAO.requestKline(coinType, interval, 200, new Date(System.currentTimeMillis() - intervalSec * 200000L));
                JSONArray kLineArray = (JSONArray) firstCandleObject.get("result");
                BufferedWriter bufferedWriter;
                
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                    // 쓰기
                bufferedWriter.write(kLineArray.toString());
                bufferedWriter.close();
                
                while (true) { // 그다음 반복해서 가져오기  날짜 이어서 불러오기

                    // obj = parser.parse(new FileReader(filePath));
                    // JSONArray jsonObject = (JSONArray) obj;
                    file = new File(filePath);
                    scan = new Scanner(file);
                    load = scan.nextLine();
                    Object kLineobj = parser.parse(load);
                    kLineArray = (JSONArray) kLineobj;
    
                    CandleList candleList = new CandleList(coinType, interval);

                    Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
                        return (JSONObject) kLineObject;
                    });
                    map.forEachOrdered((JSONObject kLineObject) -> {
                        double open = ((Number) kLineObject.get("open")).doubleValue();
                        double high = ((Number) kLineObject.get("high")).doubleValue();
                        double low = ((Number) kLineObject.get("low")).doubleValue();
                        double close = ((Number) kLineObject.get("close")).doubleValue();
                        double volume = ((Number) kLineObject.get("volume")).doubleValue();
                        Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
                        Candle candle = (new Candle(startAt, volume, open, high, low, close));
                        candleList.registerNewCandle(candle);
                    });
                    
                    Date newFrom = candleList.get(candleList.size()-1).getStartAt();
                    System.out.println(newFrom);
                    if(newFrom.equals(prevFrom) ){
                        System.out.println("파일의 끝");
                        break;
                        
                    }
                    prevFrom = newFrom;

                    JSONObject jsonObj = restDAO.requestKline(coinType, interval, 200,
                            new Date(newFrom.getTime() - intervalSec * 200000L));
                    JSONArray newKlineArray = (JSONArray) jsonObj.get("result");
                    
                    // 한번에 합치기
                    candleData = newKlineArray.toString();
                    load = load.substring(1, load.length() - 1);
                    candleData = candleData.substring(0, candleData.length() - 1);
                    candleData = candleData.concat(",");
                    candleData = candleData.concat(load);
                    candleData = candleData.concat("]");
                    
                    file = new File(filePath);
                    bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(candleData);
                    bufferedWriter.close();
                    
                }

            } else {// 캐시된 데이터가 있을 경우

            

            }
            scan.close();


        } catch (FileNotFoundException e1) {
            
            e1.printStackTrace();
        } catch (IOException e1) {
            
            e1.printStackTrace();
        } catch (ParseException e1) {
            
            e1.printStackTrace();
        }
      
    }

    public void orderCoin(Orderable order, Candle candle) {

        if (order.getSide().getKorean().equals("매수/오픈") || order.getSide().getKorean().equals("매도/오픈")) {
            // 포지션 오픈 -> 진입가, 주문시간 등록
            myPosition.put("open", Double.toString(candle.getClose()));
            myPosition.put("startAt", candle.getStartAt().toString());
            myPosition.put("qty", Double.toString(order.getQty()));

        } else {
            // 포지션 청산 -> myPosition 초기화/ 총수익률 계산
            calProfit(candle);
            myPosition.clear();

        }

    }

    /**
     * 
     * @return 수익률 총합 계산
     */
    private void calProfit(Candle candle) {
        double nowProfit = (Double.parseDouble(myPosition.get("qty")) * candle.getClose()
                - Double.parseDouble(myPosition.get("qty")) * Double.parseDouble(myPosition.get("open")));
        profit = profit + (Math.round(nowProfit * 100) / 100);

    }

    public Map<String, String> getMyPosition() {
        return this.myPosition;
    }

    /**
     * 
     * @return 실시간 수익률
     */
    public double getRealtimeProfit(Candle candle) {
        return (Double.parseDouble(myPosition.get("qty")) * candle.getClose()
                - Double.parseDouble(myPosition.get("qty")) * Double.parseDouble(myPosition.get("open")));
    }

    /**
     * 
     * 수익률 총합 호출
     * 
     * @return 수익률 총합
     */
    public double getProfit() {
        return profit;
    }

    /**
     * 
     * 계좌에 있는 돈 호출 return 계좌 잔액
     */
    public double getWalletBalance() {
        return walletBalance;
    }

    /**
     * 
     * 살수 있는 코인의 개수를 계산해 준다.
     * 
     * return qty 개수
     */
    public double calQTY(Candle candle) {
        // 포지션 오픈 -> order qty에 저장시켜준다.

        return (Math.round(walletBalance / candle.getClose() * 1000) / 1000);

    }

    @Override
    @Deprecated
    public OrderedList getOrderList() {

        return null;
    }

    @Override
    public InstrumentInfo getInstrumentInfo(CoinEnum coinType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public WalletAccessable getWalletInfo() {
        return null;
    }

    @Override
    @Deprecated
    public void orderCoin(Orderable order) {

    }

    @Override
    @Deprecated
    public void getPaidLimit(CoinEnum coinType) {

    }

    @Override
    @Deprecated
    public PositionList getPositionList() {

        return null;
    }
    public double getCurrentPrice(CoinEnum coinType) {
        // TODO Auto-generated method stub
        return 0;
    }

    
    
}

// while(true){

// long intervalSec = interval.getSec();

// if (!candleListIsCachedMap.get(coinType).containsKey(interval)) {
// // 해당 캔들 리스트가 캐시되어 있지 않을 경우.
// JSONObject jsonObject = restDAO.requestKline(coinType, interval, 200, new
// Date(System.currentTimeMillis() - intervalSec*200000L));
// JSONArray kLineArray = (JSONArray) jsonObject.get("result");
// CandleList candleList = new CandleList(coinType, interval);

// Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
// return (JSONObject) kLineObject;
// });
// map.forEachOrdered((JSONObject kLineObject) -> {
// double open = ((Number) kLineObject.get("open")).doubleValue();
// double high = ((Number) kLineObject.get("high")).doubleValue();
// double low = ((Number) kLineObject.get("low")).doubleValue();
// double close = ((Number) kLineObject.get("close")).doubleValue();
// double volume = ((Number) kLineObject.get("volume")).doubleValue();
// Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
// Candle candle = (new Candle(startAt, volume, open, high, low, close));
// candleList.registerNewCandle(candle);
// });

// candleListIsCachedMap.get(coinType).put(interval, candleList);

// // return candleListIsCachedMap.get(coinType).get(interval);
// }else{
// CandleList backupCandleList =
// candleListIsCachedMap.get(coinType).get(interval);
// JSONObject tempCandleList = restDAO.requestKline(coinType, interval, 200, new
// Date(backupCandleList.elementAt(0).getStartAt().getTime() -
// intervalSec*200000L));
// JSONArray kLineArray = (JSONArray) tempCandleList.get("result");
// CandleList newCandleList = new CandleList(coinType, interval);

// Stream<JSONObject> map = kLineArray.stream().map((kLineObject) -> {
// return (JSONObject) kLineObject;
// });
// map.forEachOrdered((JSONObject kLineObject) -> {
// double open = ((Number) kLineObject.get("open")).doubleValue();
// double high = ((Number) kLineObject.get("high")).doubleValue();
// double low = ((Number) kLineObject.get("low")).doubleValue();
// double close = ((Number) kLineObject.get("close")).doubleValue();
// double volume = ((Number) kLineObject.get("volume")).doubleValue();
// Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
// Candle candle = (new Candle(startAt, volume, open, high, low, close));
// newCandleList.registerNewCandle(candle);
// });
// if(newCandleList.elementAt(newCandleList.size()-1).getClose()==0){
// //추가되는 최신정보가 비어있으므로 추가할 필요가 없음.
// break;
// }

// else{

// candleListIsCachedMap.get(coinType).put(interval, newCandleList);

// }

// }

// }
// return candleListIsCachedMap.get(coinType).get(interval);