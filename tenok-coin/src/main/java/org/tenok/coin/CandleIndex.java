package org.tenok.coin;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.BybitRestDAO;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;

import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;

@Log4j
public class CandleIndex {
    static BybitRestDAO restDAO = new BybitRestDAO();
    static final String rootPath = "./candle_cached";

    public static void main(String[] args)
            throws JsonGenerationException, JsonMappingException, InterruptedException, IOException, ParseException {
        for (var coinType : CoinEnum.values()) {
            for (var interval : IntervalEnum.values()) {
                cacheKLine(coinType, interval);
            }
        }
        // cacheKLine(CoinEnum.BTCUSDT, IntervalEnum.THREE);
    }

    public static void cacheKLine(CoinEnum coinType, IntervalEnum interval)
            throws InterruptedException, JsonGenerationException, JsonMappingException, IOException, ParseException {
        log.info(String.format("start cache %s %s", coinType.getLiteral(), interval.getApiString()));
        new File(rootPath + "/" + coinType.getLiteral() + "/").mkdirs();
        new File(rootPath + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json").createNewFile();

        // Bybit 서버로 부터 전체 캔들 받아옴.
        List<Candle> tempCandleList = requestAllCandleList(coinType, interval);
        log.info(String.format("size of Loaded list: %d", tempCandleList.size()));

        // 중복된 캔들 리스트 삭제
        removeDuplicatCandle(tempCandleList);
        log.info(String.format("removed duplicate candles. size is %d", tempCandleList.size()));

        // data loss 검증 및 data loss 메꿔질 때 까지 반복
        while (checkDataLossParallel(tempCandleList, coinType, interval) != 0) {
        }
        log.info(String.format("data loss check complete. size is %d", tempCandleList.size()));

        // CandleList 객체 생성하여, register한다.
        CandleList candleList = new CandleList(coinType, interval);
        tempCandleList.stream().forEachOrdered(candle -> {
            candleList.registerNewCandle(candle);
        });

        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        log.info(String.format("Final size of candle list: %d", candleList.size()));

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(new File(rootPath + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json"),
                candleList);

        log.info(String.format("Successfully cached candle List. size is %dkb",
                new File(rootPath + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json").length()
                        / 1024L));
    }

    /**
     * Bybit API를 통해 현재부터, 과거까지 전체의 캔들 리스트를 요청하여 받아온다.
     * 
     * @param coinType CoinEnum
     * @param interval IntervalEnum
     * @return Whole Candle List
     */
    @SuppressWarnings("unchecked")
    public static List<Candle> requestAllCandleList(CoinEnum coinType, IntervalEnum interval) {
        Candle pivotCandle = findPivotCandle(coinType, interval);
        long pivotTime = pivotCandle.getStartAt().getTime() / 1000L;
        List<JSONArray> responseList = new ArrayList<>();

        int requestIter = 1;
        long previousId = 0, currentId = 0;
        while (true) {
            JSONObject responseJson = null;
            responseJson = restDAO.requestKline(coinType, interval, 200,
                    new Date(pivotTime * 1000L - 200L * ((long) interval.getSec()) * 1000L * ((long) requestIter)));

            JSONObject currentResponse = (JSONObject) ((JSONArray) responseJson.get("result")).get(0);
            responseList.add((JSONArray) responseJson.get("result"));

            currentId = ((long) currentResponse.get("id"));
            if (previousId == currentId) {
                break;
            } else if (requestIter % 10 == 0) {
                log.info(String.format("candle list loading epoch %d", requestIter));
            }
            previousId = currentId;
            requestIter++;
        }
        log.info("loading complete");
        List<Candle> tempCandleList = new ArrayList<>();

        responseList.stream().flatMap(inner -> {
            return (Stream<JSONObject>) inner.stream();
        }).forEach(kLineObject -> {
            double open = ((Number) kLineObject.get("open")).doubleValue();
            double high = ((Number) kLineObject.get("high")).doubleValue();
            double low = ((Number) kLineObject.get("low")).doubleValue();
            double close = ((Number) kLineObject.get("close")).doubleValue();
            double volume = ((Number) kLineObject.get("volume")).doubleValue();
            Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
            tempCandleList.add(new Candle(startAt, volume, open, high, low, close));
        });
        tempCandleList.add(pivotCandle);

        log.info("parse success");
        tempCandleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });

        return tempCandleList;
    }

    /**
     * Candle List 중에서 loss 된 데이터가 있는지 확인. 만약 loss 된 데이터가 있다면 메꿈.
     * 
     * @param candleList 데이터 loss 검증 대상
     * @param coinType   코인 종류
     * @param interval   봉 간격
     * @return 데이터 loss 개수
     */
    public static int checkDataLoss(List<Candle> candleList, CoinEnum coinType, IntervalEnum interval) {
        List<Long> dataLossList = new ArrayList<>();
        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        long listSize = (candleList.get(candleList.size() - 1).getStartAt().getTime()
                - candleList.get(0).getStartAt().getTime()) / ((long) interval.getSec() * 1000);
        long baseTime = candleList.get(0).getStartAt().getTime();
        int index = -1;
        List<Candle> temp = new ArrayList<>();
        for (Candle candleTemp : candleList) {
            index++;
            while (candleTemp.getStartAt().getTime() != baseTime + interval.getSec() * ((long) index) * 1000L) {
                dataLossList.add(baseTime + interval.getSec() * ((long) index) * 1000L);
                temp.add(requestCandle(coinType, interval,
                        new Date(baseTime + interval.getSec() * ((long) index) * 1000L)));

                if (index % 10 == 0) {
                    log.info(String.format("loss list loading %.2f%%", (index / (double) listSize * 100)));
                }
                if (baseTime + interval.getSec() * ((long) index) * 1000L > System.currentTimeMillis()) {
                    throw new RuntimeException("se");
                }
                index++;
            }
        }
        candleList.addAll(temp);
        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        log.info(String.format("loss data: %d", dataLossList.size()));
        return dataLossList.size();
    }

    /**
     * Candle List 중에서 loss 된 데이터가 있는지 확인. 만약 loss 된 데이터가 있다면 메꿈.
     * 
     * @param candleList 데이터 loss 검증 대상
     * @param coinType   코인 종류
     * @param interval   봉 간격
     * @return 데이터 loss 개수
     * @throws ParseException
     */
    public static int checkDataLossParallel(List<Candle> candleList, CoinEnum coinType, IntervalEnum interval)
            throws ParseException {
        int dataLossCount = 0;
        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        // 총 candleList의 사이즈
        long listSize = (candleList.get(candleList.size() - 1).getStartAt().getTime()
                - candleList.get(0).getStartAt().getTime()) / ((long) interval.getSec() * 1000);
        // 0번째 캔들의 start at
        long baseTime = candleList.get(0).getStartAt().getTime();
        int index = -1;
        List<CompletableFuture<HttpResponse<String>>> temp = new LinkedList<>();
        List<Candle> candleTempList = new ArrayList<>();
        for (Candle candleTemp : candleList) {
            index++;
            while (candleTemp.getStartAt().getTime() != baseTime + interval.getSec() * ((long) index) * 1000L) {
                dataLossCount++;
                temp.add(requestCandleParallel(coinType, interval,
                        new Date(baseTime + interval.getSec() * ((long) index) * 1000L)));

                if (index % 100 == 0) {
                    log.info(String.format("loss list loading %.2f%%", (index / (double) listSize * 100)));
                }
                if (index % 1000 == 0) {
                    for(var future : temp) {
                        JSONObject jsonObj = (JSONObject) new JSONParser().parse(future.join().body());
                        JSONArray resArr = (JSONArray) jsonObj.get("result");
                        JSONObject kLineObject = (JSONObject) resArr.get(0);
                        double open = ((Number) kLineObject.get("open")).doubleValue();
                        double high = ((Number) kLineObject.get("high")).doubleValue();
                        double low = ((Number) kLineObject.get("low")).doubleValue();
                        double close = ((Number) kLineObject.get("close")).doubleValue();
                        double volume = ((Number) kLineObject.get("volume")).doubleValue();
                        Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
                        candleTempList.add(new Candle(startAt, volume, open, high, low, close));
                    }
                    temp.clear();
                }
                if (baseTime + interval.getSec() * ((long) index) * 1000L > System.currentTimeMillis()) {
                    throw new RuntimeException("se");
                }
                index++;
            }
        }
        for(var future : temp) {
            JSONObject jsonObj = (JSONObject) new JSONParser().parse(future.join().body());
            JSONArray resArr = (JSONArray) jsonObj.get("result");
            JSONObject kLineObject = (JSONObject) resArr.get(0);
            double open = ((Number) kLineObject.get("open")).doubleValue();
            double high = ((Number) kLineObject.get("high")).doubleValue();
            double low = ((Number) kLineObject.get("low")).doubleValue();
            double close = ((Number) kLineObject.get("close")).doubleValue();
            double volume = ((Number) kLineObject.get("volume")).doubleValue();
            Date startAt = new Date(((long) kLineObject.get("start_at")) * 1000L);
            candleTempList.add(new Candle(startAt, volume, open, high, low, close));
        }

        candleList.addAll(candleTempList);
        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
        log.info(String.format("loss data: %d", dataLossCount));
        return dataLossCount;
    }

    /**
     * cache 과정에서 중복된 캔들 리스트를 삭제.
     * 
     * @param candleList 캔들 리스트
     */
    public static void removeDuplicatCandle(List<Candle> candleList) {
        candleList.sort((candle1, candle2) -> {
            return (-1) * candle1.getStartAt().compareTo(candle2.getStartAt());
        });

        List<Integer> duplicateIndexList = new ArrayList<>();

        Candle indexCandle = candleList.get(0);
        for (int j = 1; j < candleList.size(); j++) {
            if (candleList.get(j).getStartAt().getTime() == indexCandle.getStartAt().getTime()) {
                duplicateIndexList.add(j);
            } else {
                indexCandle = candleList.get(j);
            }
        }

        for (int j = duplicateIndexList.size() - 1; j > -1; j--) {
            candleList.remove((int) duplicateIndexList.get(j));
        }

        candleList.sort((candle1, candle2) -> {
            return candle1.getStartAt().compareTo(candle2.getStartAt());
        });
    }

    @SuppressWarnings("unchecked")
    public static void updateCandle(CoinEnum coinType, IntervalEnum interval)
            throws JsonParseException, JsonMappingException, IOException {

        CandleList list = new ObjectMapper().readValue(new File("./candle_cached_final/bitcoin/15.json"),
                CandleList.class);
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

    public static long findPivotTime(JSONObject response) {
        return (long) ((JSONObject) ((JSONArray) response.get("result")).get(0)).get("start_at");
    }

    public static Candle findPivotCandle(CoinEnum coinType, IntervalEnum interval) {
        JSONObject response = restDAO.requestKline(coinType, interval, 1,
                new Date(System.currentTimeMillis() - interval.getSec() * 1000L));
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

    public static CompletableFuture<HttpResponse<String>> requestCandleParallel(CoinEnum coinType,
            IntervalEnum interval, Date startAt) {
        return restDAO.requestKlineHttp2(coinType, interval, 1, startAt);
    }
}
