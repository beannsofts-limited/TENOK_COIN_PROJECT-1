package org.tenok.coin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.extern.log4j.Log4j;

@Log4j
public class CandleCache {
    public static final String rootPath = "./candle_cached";
    public static final BybitRestDAO restDAO = new BybitRestDAO();

    public static void main(String[] args)
            throws InterruptedException, JsonGenerationException, JsonMappingException, IOException {
        log.info(String.format("MAX Mem: %d", Runtime.getRuntime().maxMemory()));
        for (var coinType : CoinEnum.values()) {
            for (var interval : IntervalEnum.values()) {
                cacheKLine(coinType, interval);
            }
        }
        // cacheKLine(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
    }

    @SuppressWarnings("unchecked")
    public static void cacheKLine(CoinEnum coinType, IntervalEnum interval)
            throws InterruptedException, JsonGenerationException, JsonMappingException, IOException {
        log.info(String.format("start cache %s %s", coinType.getLiteral(), interval.getApiString()));
        Date currentDate = new Date();
        new File(rootPath + "/" + coinType.getLiteral() + "/").mkdirs();
        new File(rootPath + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json").createNewFile();

        int i = 0;
        Stack<JSONArray> responseStack = new Stack<>();
        long previousId = 0, currentId = 0;
        while (true) {
            JSONObject responseJson = null;
            try {
                responseJson = restDAO.requestKline(coinType, interval, 200, new Date(
                    currentDate.getTime() - (200000L * ((long) i) * interval.getSec() + interval.getSec() * 1000L)));
            } catch (Exception e) {
                log.info(e);
                Runtime.getRuntime().runFinalization();

                Thread.sleep(20000);
                
                continue;
            }

            // System.out.println(responseJson);
            JSONObject currentResponse = (JSONObject) responseStack.push((JSONArray) responseJson.get("result")).get(0);
            currentId = ((long) currentResponse.get("id"));
            if (previousId == currentId) {
                break;
            } else if (i % 10 == 0) {
                log.info(String.format("candle list loading epoch %d", i));
                log.info(String.format("free space: %d", Runtime.getRuntime().freeMemory()));
            }
            previousId = currentId;
            i++;
        }
        log.info("loading complete");
        CandleList candleList = new CandleList(coinType, interval);
        responseStack.parallelStream().flatMap(inner -> (Stream<JSONObject>) inner.stream()).forEach(kLineObject -> {
            double open = ((Number) kLineObject.get("open")).doubleValue();
            double high = ((Number) kLineObject.get("high")).doubleValue();
            double low = ((Number) kLineObject.get("low")).doubleValue();
            double close = ((Number) kLineObject.get("close")).doubleValue();
            double volume = ((Number) kLineObject.get("volume")).doubleValue();
            Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
            Candle candle = (new Candle(startAt, volume, open, high, low, close));
            candleList.registerNewCandle(candle);
        });
        log.info("parse success");
        candleList.sort((candle1, candle2) -> {
            return (-1) * candle1.getStartAt().compareTo(candle2.getStartAt());
        });

        List<Integer> indexList = new ArrayList<>();

        Candle indexCandle = candleList.get(0);
        for (int j = 1; j < candleList.size(); j++) {
            if (candleList.get(j).getStartAt().getTime() == indexCandle.getStartAt().getTime()) {
                indexList.add(j);
            } else {
                indexCandle = candleList.get(j);
            }
        }

        for (int j = indexList.size() - 1; j > -1; j--) {
            candleList.remove((int) indexList.get(j));
        }

        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(new File(rootPath + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json"),
                candleList);
    }
}
