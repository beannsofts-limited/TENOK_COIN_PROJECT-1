package org.tenok.coin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class CandleTest {
    static BybitRestDAO restDAO = new BybitRestDAO();
    static Candle previous = null;
    static int count = 0;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        CoinEnum coinType = CoinEnum.BTCUSDT;
        IntervalEnum interval = IntervalEnum.ONE;

        CandleList candleList = new CandleList(coinType, interval);
        Candle pivotCandle = findPivotCandle(coinType, interval);
        long pivotTime = pivotCandle.getStartAt().getTime() / 1000L;


        List<Candle> candleTempList = new ArrayList<>();
        candleTempList.add(pivotCandle);
        for (int i = 1; i < 5; i++) {
            JSONObject response = restDAO.requestKline(coinType, interval, 200, new Date(pivotTime * 1000L - 200L * (long) interval.getSec() * 1000L * ((long) i)));
            JSONArray array = (JSONArray) response.get("result");
            array.parallelStream().forEach(obj -> {
                JSONObject kLineObject = (JSONObject) obj;
                double open = ((Number) kLineObject.get("open")).doubleValue();
                double high = ((Number) kLineObject.get("high")).doubleValue();
                double low = ((Number) kLineObject.get("low")).doubleValue();
                double close = ((Number) kLineObject.get("close")).doubleValue();
                double volume = ((Number) kLineObject.get("volume")).doubleValue();
                Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
                candleTempList.add(new Candle(startAt, volume, open, high, low, close));
            });
        }
        candleTempList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });

        List<Long> dataLossList = new ArrayList<>();
        long baseTime = candleTempList.get(0).getStartAt().getTime();
        int index = -1;
        List<Candle> temp = new ArrayList<>();
        for (Candle candleTemp : candleTempList) {
            index++;
            while (candleTemp.getStartAt().getTime() != baseTime + interval.getSec() * ((long) index) * 1000L) {
                dataLossList.add(baseTime + interval.getSec() * ((long) index) * 1000L);
                temp.add(requestCandle(coinType, interval, new Date(baseTime + interval.getSec() * ((long) index) * 1000L)));
                index++;
            }
        }
        candleTempList.addAll(temp);
        candleTempList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });

        candleTempList.stream().forEachOrdered(action -> {
            // System.out.println(action.getStartAt());
            if (previous != null) {
                if ((previous.getStartAt().getTime() + 60000L) != action.getStartAt().getTime()) {
                    count++;
                }
            }
            previous = action;
        });
        System.out.println(dataLossList.size());
        System.out.println(count);

    }

    public static long findPivotTime(JSONObject response) {
        return (long) ((JSONObject) ((JSONArray) response.get("result")).get(0)).get("start_at");
    }

    public static Candle findPivotCandle(CoinEnum coinType, IntervalEnum interval) {
        JSONObject response = restDAO.requestKline(coinType, interval, 1, new Date(System.currentTimeMillis() - interval.getSec() * 1000L));
        JSONArray resArr = (JSONArray) response.get("result");
        JSONObject kLineObject = (JSONObject) resArr.get(0);
        double open = ((Number) kLineObject.get("open")).doubleValue();
        double high = ((Number) kLineObject.get("high")).doubleValue();
        double low = ((Number) kLineObject.get("low")).doubleValue();
        double close = ((Number) kLineObject.get("close")).doubleValue();
        double volume = ((Number) kLineObject.get("volume")).doubleValue();
        Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
        return new Candle(startAt, volume, open, high, low, close);
    }

    public static Candle requestCandle(CoinEnum coinType, IntervalEnum interval, Date startAt) {
        JSONObject response = restDAO.requestKline(coinType, interval, 1, startAt);
        JSONArray resArr = (JSONArray) response.get("result");
        JSONObject kLineObject = (JSONObject) resArr.get(0);
        double open = ((Number) kLineObject.get("open")).doubleValue();
        double high = ((Number) kLineObject.get("high")).doubleValue();
        double low = ((Number) kLineObject.get("low")).doubleValue();
        double close = ((Number) kLineObject.get("close")).doubleValue();
        double volume = ((Number) kLineObject.get("volume")).doubleValue();
        Date start = new Date(((long) kLineObject.get("start_at")) * 1000L);
        return new Candle(start, volume, open, high, low, close);
    }
}
