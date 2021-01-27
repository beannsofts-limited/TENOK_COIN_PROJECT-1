package org.tenok.coin.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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

    public JSONObject requestKline(CoinEnum coinType, IntervalEnum interval, int limit, Date from) {
        Map<String, String> request = new HashMap<>();
        request.put("symbol", coinType.toString());
        request.put("interval", interval.getApiString());
        request.put("limit", Integer.toString(limit));
        request.put("from", Long.toString(from.getTime() / 1000L));
        StringBuilder url = new StringBuilder("https://api.bybit.com/public/linear/kline?");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject jsonResponse = stringToJSON((loadData.toString()));
        System.out.println("restAPI: 캔들차트 불러오기\n");

        return jsonResponse;

    }

    public JSONObject getActiveOrder(CoinEnum coinType) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        request.put("symbol", coinType.toString());
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/order/list?");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject jsonResponse = stringToJSON((loadData.toString()));
        System.out.println("restAPI: active Order List 불러오기\n");

        return jsonResponse;

    }

    public JSONObject getConditionalOrder() {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/stop-order/list?");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject jsonResponse = stringToJSON((loadData.toString()));
        System.out.println("restAPI: Conditional Order List 불러오기\n");

        return jsonResponse;

    }

    public JSONObject placeActiveOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum oderType,  double qty, TIFEnum tif) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", oderType.toString());
        request.put("qty", Double.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/create");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: active order 주문\n");
            return jsonResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("place active order 실패");

    }

    public JSONObject placeConditionalOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum orderType, double qty,
            TIFEnum tif) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", orderType.toString());
        request.put("qty", Double.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/create");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: conditional order 주문\n");
            return jsonResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("place conditional order 실패");
    }

    public JSONObject cancelActiveOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/cancel");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: active order 취소\n");
            return jsonResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("active order cancel 실패");
    }

    public JSONObject cancelConditionalOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/cancel");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: conditional order 취소\n");
            return jsonResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("conditional order cancel 실패");
    }

    public JSONObject cancelAllActiveOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/order/cancel-all");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: 모든 active order 취소\n");
            return jsonResponse;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("active order all cancel 실패");
    }

    public JSONObject cancelAllConditionalOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/stop-order/cancel-all");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: 모든 conditional order 취소\n");
            return jsonResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("condtional oder all cancel 실패");
    }

    public JSONObject setLeverage(CoinEnum coinType, int buyLeverage, int sellLeverage) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", AuthDecryptor.getInstance().getApiKey());
        request.put("symbol", coinType.toString());
        request.put("buy_leverage", Integer.toString(buyLeverage));
        request.put("sell_leverage", Integer.toString(sellLeverage));
        request.put("sign", AuthDecryptor.getInstance().generate_signature());
        URL url;
        try {
            url = new URL("https://api.bybit.com/private/linear/position/set-leverage");
            StringBuilder loadData = postRestApi(request, url);
            JSONObject jsonResponse = stringToJSON((loadData.toString()));
            System.out.println("restAPI: 레버리지 설정\n");
            return jsonResponse;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("leverage setting 실패");
    }

    private StringBuilder getRestApi(Map<String, String> request, StringBuilder url) {
        request.forEach((k, v) -> {
            url.append(String.format("%s=%s&", k, v));
        });
        url.deleteCharAt(url.length() - 1);

        HttpsURLConnection conn;
        try {
            conn = (HttpsURLConnection) new URL(url.toString()).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");

            }
            br.close();
            return sb;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        throw new RuntimeException("RestApi get 호출 실패");
    }

    private StringBuilder postRestApi(Map<String, String> request, URL url) {
        StringBuilder jsonInputString = new StringBuilder("{");
        request.forEach((k, v) -> {
            jsonInputString.append(String.format("\"%s\":\"%s\",", k, v));
        });
        jsonInputString.deleteCharAt(jsonInputString.length() - 1);
        jsonInputString.append("}");
        HttpsURLConnection conn;
        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.toString().getBytes());
            conn.connect();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            os.flush();
            conn.disconnect();
            return sb;

        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("RestApi post 호출 실패");
    }

    private JSONObject stringToJSON(String restSTR) {
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(restSTR);
            JSONObject jsonObj = (JSONObject) obj;
            return jsonObj;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("ReatApi JSON 파싱 실패");
    }
}
