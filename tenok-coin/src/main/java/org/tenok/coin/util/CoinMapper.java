package org.tenok.coin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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

import lombok.extern.log4j.Log4j;

/**
 * 로컬에 캔들리스트를 캐싱하고, 불러오는 역할을 수행.
 */
@Log4j
public class CoinMapper {
    BybitRestDAO restDAO = new BybitRestDAO();
    private static final String ROOT_PATH = "./candle_cache";

    private CoinMapper() {
    }

    public static CoinMapper getInstance() {
        return CoinMapperLazyHolder.INSTANCE;
    }

    private static class CoinMapperLazyHolder {
        public static final CoinMapper INSTANCE = new CoinMapper();
    }

    /**
     * 로컬에 캐싱되어 있는 캔들 리스트를 불러온다.
     * 
     * @param coinType 불러올 캔들의 코인
     * @param interval 불러올 캔들의 봉 간격
     * @param start    불러올 캔들의 시작 기간
     * @param end      불러올 캔들의 끝 기간
     * @return 로컬에 캐싱되어있는 캔들 리스트
     */
    public CandleList getCandleList(CoinEnum coinType, IntervalEnum interval, Date start, Date end) {
        ObjectMapper mapper = new ObjectMapper();
        CandleList wholeCandleList = null;
        try {
            wholeCandleList = mapper.readValue(getFile(coinType, interval), CandleList.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int startIndex = -1, endIndex = -1;

        for (int i = 0; i < wholeCandleList.size() - 1; i++) {
            if ((wholeCandleList.get(i).getStartAt().equals(start) || wholeCandleList.get(i).getStartAt().before(start))
                    && wholeCandleList.get(i + 1).getStartAt().after(start)) {
                startIndex = i;
            }
            if ((wholeCandleList.get(i).getStartAt().equals(start) || wholeCandleList.get(i).getStartAt().before(start))
                    && wholeCandleList.get(i + 1).getStartAt().after(end)) {
                endIndex = i;
                break;
            }
        }

        CandleList candleList = new CandleList(coinType, interval);
        candleList.addAll(wholeCandleList.subList(startIndex, endIndex));
        return candleList;
    }

    /**
     * 로컬에 캐싱되어 있는 캔들 리스트를 모든 기간에 대하여 불러온다.
     * 
     * @param coinType 불러올 캔들의 코인
     * @param interval 불러올 캔들의 봉 간격
     * @return 로컬에 캐싱되어있는 캔들 리스트
     */
    public CandleList getWholeCandleList(CoinEnum coinType, IntervalEnum interval) {
        ObjectMapper mapper = new ObjectMapper();
        CandleList wholeCandleList = null;
        try {
            wholeCandleList = mapper.readValue(getFile(coinType, interval), CandleList.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wholeCandleList;
    }

    /**
     * 해당 코인, 봉간격에 해당하는 캔들리스트를 로컬에 캐싱한다. 원래 저장되어 있던 캔들리스트가 있다면, 덮어씌워진다.
     * 
     * @param coinType 캐싱할 캔들의 코인
     * @param interval 캐싱할 캔들의 봉 간격
     */
    public void cacheCandle(CoinEnum coinType, IntervalEnum interval) {
        cacheKLine(coinType, interval);
    }

    /**
     * 로컬에 캐싱되어있는 캔들 리스트를 최신 데이터로 업데이트 시킨다.
     * 
     * @param coinType 업데이트 시킬 캔들의 코인
     * @param interval 업데이트 시킬 캔들의 봉 간격
     */
    public void updateCachedCandle(CoinEnum coinType, IntervalEnum interval) {
        CandleList list = null;
        try {
            list = new ObjectMapper().readValue(getFile(coinType, interval), CandleList.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        Candle latestCandle = list.get(list.size() - 1);

        Date latestDate = latestCandle.getStartAt();

        Candle pivotCandle = findPivotCandle(coinType, interval);
        int i = 0;
        boolean outFlag = true;

        List<JSONObject> responseList = new ArrayList<>();

        while (outFlag) {
            JSONObject response = restDAO.requestKline(coinType, interval, 200,
                    new Date(pivotCandle.getStartAt().getTime()
                            - (200000L * ((long) i) * interval.getSec() + interval.getSec() * 1000L)));
            JSONArray responseArray = (JSONArray) response.get("result");
            responseList.add(response);

            for (Object object : responseArray) {
                JSONObject kLine = (JSONObject) object;
                if (((long) kLine.get("start_at")) == latestDate.getTime() / 1000L) {
                    outFlag = false;
                    break;
                }
            }
            System.out.println();
            i++;
        }
    }

    /**
     * 해당 코인, 봉 간격에 해당 하는 파일 반환
     * 
     * @param coinType 코인
     * @param interval 봉 간격
     * @return 파일 객체
     */
    private File getFile(CoinEnum coinType, IntervalEnum interval) {
        return new File(ROOT_PATH + "/" + coinType.getLiteral() + "/" + interval.getApiString() + ".json");
    }

    private void cacheKLine(CoinEnum coinType, IntervalEnum interval) {
        log.info(String.format("start cache %s %s", coinType.getLiteral(), interval.getApiString()));
        new File(ROOT_PATH + "/" + coinType.getLiteral() + "/").mkdirs();
        File outputFile = getFile(coinType, interval);
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Bybit 서버로 부터 전체 캔들 받아옴.
        List<Candle> tempCandleList = requestAllCandleList(coinType, interval);
        log.info(String.format("size of Loaded list: %d", tempCandleList.size()));

        // 중복된 캔들 리스트 삭제
        removeDuplicateCandle(tempCandleList);
        log.info(String.format("removed duplicate candles. size is %d", tempCandleList.size()));

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

        try {
            mapper.writeValue(outputFile, candleList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info(String.format("Successfully cached candle List. size is %d MB", outputFile.length() / 1024L / 1024L));
    }

    /**
     * Bybit API를 통해 현재부터, 과거까지 전체의 캔들 리스트를 요청하여 받아온다.
     * 
     * @param coinType CoinEnum
     * @param interval IntervalEnum
     * @return Whole Candle List
     */
    @SuppressWarnings("unchecked")
    private List<Candle> requestAllCandleList(CoinEnum coinType, IntervalEnum interval) {
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
     * cache 과정에서 중복된 캔들 리스트를 삭제.
     * 
     * @param candleList 캔들 리스트
     */
    private void removeDuplicateCandle(List<Candle> candleList) {
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

    private Candle findPivotCandle(CoinEnum coinType, IntervalEnum interval) {
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
}
