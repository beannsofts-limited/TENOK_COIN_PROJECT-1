package org.tenok.coin.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.impl.AuthDecryptor;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

public class BybitRestDAO {
    private static Logger logger = Logger.getLogger(BybitRestDAO.class);

    public JSONObject requestKline(CoinEnum coinType, IntervalEnum interval, int limit, Date from) {
        Map<String, Object> request = new TreeMap<>();
        request.put("symbol", coinType.name());
        request.put("interval", interval.getApiString());
        request.put("limit", Integer.toString(limit));
        request.put("from", Long.toString(from.getTime() / 1000L));
        return requestByGet(request, "https://api.bybit.com/public/linear/kline?");

    }

    public CompletableFuture<HttpResponse<String>> requestKlineHttp2(CoinEnum coinType, IntervalEnum interval, int limit, Date from) {
        Map<String, Object> request = new TreeMap<>();
        request.put("symbol", coinType.name());
        request.put("interval", interval.getApiString());
        request.put("limit", Integer.toString(limit));
        request.put("from", Long.toString(from.getTime() / 1000L));
        StringBuilder requestParam = new StringBuilder();
        request.entrySet().stream().forEachOrdered(requestSet -> {
            requestParam.append(String.format("%s=%s&", requestSet.getKey(), (String) requestSet.getValue()));
        });
        requestParam.deleteCharAt(requestParam.length()-1);
        try {
            HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
            HttpRequest httpRequest = HttpRequest
                    .newBuilder(new URI(
                            String.format("http://api.bybit.com/public/linear/kline?%s", requestParam.toString())))
                    .GET().build();

            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    public JSONObject getActiveOrder(CoinEnum coinType) {
        logger.debug("getActiveOrder: active Order List 불러오기");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("symbol", coinType.name());
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        return requestByGet(request, "https://api.bybit.com/private/linear/order/list?");

    }

    public JSONObject getConditionalOrder() {
        logger.debug("getConditionalOrder: Conditional Order List 불러오기");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        return requestByGet(request, "https://api.bybit.com/private/linear/stop-order/list?");

    }

    public JSONObject getMyPositionList(CoinEnum coinType) {
        logger.debug("getMyPositionList: My Position 불러오기");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        return requestByGet(request, "https://api.bybit.com/private/linear/position/list?");
    }

    /**
     * Query Symbol
     * 
     * @param coinType coin Enum
     * @return response json object
     */
    public JSONObject getInstrumentInfo(CoinEnum coinType) {
        Map<String, Object> request = new TreeMap<>();
        return requestByGet(request, "https://api.bybit.com/v2/public/symbols");
    }

    public JSONObject placeActiveOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum oderType, double qty,
            TIFEnum tif) {
        Boolean reduceOnly = null;
        switch (side) {
            case OPEN_BUY:
            case OPEN_SELL:
                reduceOnly = false;
                break;
            case CLOSE_BUY:
            case CLOSE_SELL:
                reduceOnly = true;
                break;
            default:
                break;
        }
        Map<String, Object> request = new TreeMap<>();
        logger.debug("placeActiveOrder: active order 주문");
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("side", side.getApiString());
        request.put("symbol", coinType.name());
        request.put("order_type", oderType.getApiString());
        request.put("qty", Double.toString(qty));
        request.put("time_in_force", tif.getApiString());
        request.put("reduce_only", Boolean.valueOf(reduceOnly));
        request.put("close_on_trigger", Boolean.valueOf(false));
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/create");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("place active order 실패");
    }

    public JSONObject placeActiveOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum oderType, double qty,
            TIFEnum tif, int leverage) {
        setLeverage(coinType, leverage, leverage);
        return placeActiveOrder(side, coinType, oderType, qty, tif);
    }

    public JSONObject placeConditionalOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum orderType, double qty,
            TIFEnum tif) {
        logger.debug("placeConditionalOrder: condition order 주문");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("side", side.getApiString());
        request.put("symbol", coinType.name());
        request.put("order_type", orderType.getApiString());
        request.put("qty", Double.toString(qty));
        request.put("time_in_force", tif.getApiString());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/create");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("place conditional order 실패");
    }

    public JSONObject cancelActiveOrder(CoinEnum coinType, String orderID) {
        logger.debug("cancelActiveOrder: active order 주문 취소");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/cancel");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("active order cancel 실패");
    }

    public JSONObject cancelConditionalOrder(CoinEnum coinType, String orderID) {
        logger.debug("cancelConditionalOrder: conditional order 주문 취소");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/cancel");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("conditional order cancel 실패");
    }

    public JSONObject cancelAllActiveOrder(CoinEnum coinType, String orderID) {
        logger.debug("cancelAllActiveOrder: 모든 active order 취소");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/cancel-all");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("active order all cancel 실패");
    }

    public JSONObject cancelAllConditionalOrder(CoinEnum coinType, String orderID) {
        logger.debug("cancelAllConditionalOrder: 모든 conditional order 취소");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/cancel-all");
            return requestByPost(request, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("condtional oder all cancel 실패");
    }

    public JSONObject setLeverage(CoinEnum coinType, int buyLeverage, int sellLeverage) {
        logger.debug("setLeverage: 레버리지 설정");
        Map<String, Object> request = new TreeMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.name());
        request.put("buy_leverage", Integer.toString(buyLeverage));
        request.put("sell_leverage", Integer.toString(sellLeverage));
        request.put("sign", AuthDecryptor.getInstance().generate_signature(request));
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/position/set-leverage");

            return requestByPost(request, url);
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("leverage setting 실패");
    }

    private JSONObject requestByGet(Map<String, Object> request, String uri) {
        StringBuilder urlBuilder = new StringBuilder(uri);
        request.forEach((k, v) -> {
            urlBuilder.append(String.format("%s=%s&", k, v));
        });
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            return (JSONObject) new JSONParser().parse(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("RestApi get 호출 실패");
    }

    private JSONObject requestByPost(Map<String, Object> request, URL url) {
        JSONObject jsonInputObject = new JSONObject(request);

        HttpsURLConnection conn;
        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputObject.toJSONString().getBytes());
            conn.connect();
            JSONObject response = (JSONObject) new JSONParser().parse(new InputStreamReader(conn.getInputStream()));
            os.flush();

            conn.disconnect();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("RestApi post 호출 실패");
    }
}
