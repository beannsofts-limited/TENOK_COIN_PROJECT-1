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
        System.out.println(findPivotCandle(CoinEnum.BTCUSDT, IntervalEnum.ONE).getStartAt().getTime());
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
