package org.tenok.coin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

public class CandleUpdate {
    static BybitRestDAO restDAO = new BybitRestDAO();
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
        updateCandle(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
        

    }

    @SuppressWarnings("unchecked")
    public static void updateCandle(CoinEnum coinType, IntervalEnum interval)
            throws JsonParseException, JsonMappingException, IOException {

        CandleList list = new ObjectMapper().readValue(new File("./candle_cached_final/bitcoin/15.json"), CandleList.class);
        Candle latestCandle = list.get(list.size() - 1);
        
        Date latestDate = latestCandle.getStartAt();
        Date currentDate = new Date();
        int i = 0;
        boolean outFlag = true;

        Stack<JSONObject> responseStack = new Stack<>();
        
        while (outFlag) {
            JSONObject response = restDAO.requestKline(coinType, interval, 200, new Date(
                currentDate.getTime() - (200000L * ((long) i) * interval.getSec() + interval.getSec() * 1000L)));
            JSONArray responseArray = (JSONArray) response.get("result");
            responseStack.push(response);

            for (Object object : responseArray) {
                JSONObject kLine = (JSONObject) object;
                System.out.printf("%d %d\n", (long) kLine.get("start_at"), latestDate.getTime() / 1000L);
                if (((long) kLine.get("start_at")) == latestDate.getTime() / 1000L) {
                    System.out.println("hi");
                    outFlag = false;
                    break;
                }
            }
            System.out.println();
            i++;
        }

        
    }
}
