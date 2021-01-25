package org.tenok.coin.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.tenok.coin.data.entity.impl.Candle;
import org.tenok.coin.data.impl.BybitDAO;
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
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klineJson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: 캔들차트 불러오기\n");

        return klineJson;

    }

    public JSONObject getActiveOrder(CoinEnum coinType)
            throws ParseException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        request.put("symbol", coinType.toString());
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/order/list?");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject ActiveOrderListJson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: active Order List 불러오기\n");

        return ActiveOrderListJson;

    }

    public JSONObject getConditionalOrder()
            throws ParseException, IOException {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("https://api.bybit.com/private/linear/stop-order/list?");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject ConditionalOrderListJson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: Conditional Order List 불러오기\n");

        return ConditionalOrderListJson;

    }

    public JSONObject placeActiveOrder(SideEnum side, CoinEnum coinType, OrderTypeEnum oderType, int qty, TIFEnum tif) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", BybitDAO.getInstance().getApiKey());
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", oderType.toString());
        request.put("qty", Integer.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", Long.toString(System.currentTimeMillis()));
        request.put("sign", BybitDAO.getInstance().getSign());
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: active order 주문\n");

        return klinejson;
    }

    public JSONObject placeConditionalOrder() {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("side", side.toString());
        request.put("symbol", coinType.toString());
        request.put("order_type", oderType.toString());
        request.put("qty", Integer.toString(qty));
        request.put("time_in_force", tif.toString());
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: conditional order 주문\n");

        return klinejson;
    }

    public JSONObject cancelActiveOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: active order 취소\n");

        return klinejson;
    }

    public JSONObject cancelConditionalOrder( CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: conditional order 취소\n");

        return klinejson;
    }

    public JSONObject cancelAllActiveOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: 모든 active order 취소\n");

        return klinejson;
    }

    public JSONObject cancelAllConditionalOrder(CoinEnum coinType, String orderID) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("symbol", coinType.toString());
        request.put("order_id", orderID);
        request.put("timestamp", "{" + (timeStamp.toString()) + "}");
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: 모든 conditional order 취소\n");

        return klinejson;
    }

    public JSONObject setLeverage(CoinEnum coinType, int buyLeverage, int sellLeverage) {
        Map<String, String> request = new HashMap<>();
        request.put("api_key", "{" + apiKey + "}");
        request.put("symbol", coinType.toString());
        request.put("buy_leverage", Integer.toString(buyLeverage));
        request.put("sell_leverage", Integer.toString(sellLeverage));
        request.put("sign", "{" + sign + "}");
        StringBuilder url = new StringBuilder("");
        StringBuilder loadData = getRestApi(request, url);
        JSONObject klinejson = stringToJSON((loadData.toString()));
        System.out.println("restAPI: 레버리지 설정\n");

        return klinejson;
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
        throw new RuntimeException("RestApi 호출 실패");
    }

    private void postRestApi() {

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