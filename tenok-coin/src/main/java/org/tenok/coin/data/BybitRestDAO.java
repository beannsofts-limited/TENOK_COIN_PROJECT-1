package org.tenok.coin.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

public class BybitRestDAO {
    
    public JSONObject requestKline(CoinEnum coinType, String interval, int limit, Date from)
            throws ParseException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("symbol", coinType.toString());
        request.put("interval", interval.toString());
        request.put("limit", Integer.toString(limit));
        request.put("from", from.toString());
        StringBuilder url = new StringBuilder("https://api.bybit.com/public/linear/kline?");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klineJson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: 캔들차트 불러오기\n");

        return klineJson;

    }


    public JSONObject getActiveOrder(String apiKey, Date timeStamp, String sign, CoinEnum coinType)
            throws ParseException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        request.put("symbol", coinType.toString());
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/order/list?");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject ActiveOrderListJson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: active Order List 불러오기\n");

        return ActiveOrderListJson;

    }
    
    public JSONObject getConditionalOrder(String apiKey, Date timeStamp, String sign)
            throws ParseException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/stop-order/list?");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject ConditionalOrderListJson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: Conditional Order List 불러오기\n");

        return ConditionalOrderListJson;

    }







    public JSONObject placeActiveOrder(String apiKey, SideEnum side, CoinEnum coinType, OrderTypeEnum oderType, int qty, TIFEnum tif, Date timeStamp, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", oderType.toString());
        request.put("qty", Integer.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: active order 주문\n");

        return klinejson;
    }

    public JSONObject placeConditionalOrder(){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", oderType.toString());
        request.put("qty", Integer.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: conditional order 주문\n");

        return klinejson;
    }

    public JSONObject cancelActiveOrder(String apiKey, CoinEnum coinType, String orderID,  Date timeStamp, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: active order 취소\n");

        return klinejson;
    }

    public JSONObject cancelConditionalOrder(String apiKey, CoinEnum coinType, String orderID,  Date timeStamp, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: conditional order 취소\n");

        return klinejson;
    }


    public JSONObject cancelAllActiveOrder(String apiKey, CoinEnum coinType, String orderID,  Date timeStamp, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: 모든 active order 취소\n");

        return klinejson;
    }
    
    public JSONObject cancelAllConditionalOrder(String apiKey, CoinEnum coinType, String orderID,  Date timeStamp, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{"+(timeStamp.toString())+"}");
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: 모든 conditional order 취소\n");

        return klinejson;
    }

    public JSONObject setLeverage(String apiKey, CoinEnum coinType, int buyLeverage,  int sellLeverage, String sign){
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{"+apiKey+"}");
        request.put("symbol", coinType.toString());
        request.put("buy_leverage", Integer.toString(buyLeverage));
        request.put("sell_leverage", Integer.toString(sellLeverage));
        request.put("sign", "{"+sign+"}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = LoadRestAPI(request, url);
        JSONObject klinejson = StringToJSON((loadData.toString()));
        System.out.println("restAPI: 레버리지 설정\n");

        return klinejson;
    }

    public StringBuilder LoadRestAPI(Map<String, String> request, StringBuilder url)
            throws MalformedURLException, IOException {
        request.forEach((k, v) -> {
            url.append(String.format("%s=%s&", k, v));
        });
        url.deleteCharAt(url.length() - 1);

        HttpsURLConnection conn = (HttpsURLConnection) new URL(url.toString()).openConnection();
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

    }

    public JSONObject StringToJSON(String restSTR) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(restSTR);
        JSONObject jsonObj = (JSONObject) obj;
        return jsonObj;
    }
}