package org.tenok.coin.config;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConfigParser {
    private static JSONObject configJson;

    private ConfigParser() {
    }

    static {
        try {
            configJson = (JSONObject) new JSONParser().parse(new FileReader("./../resources/config/tenok_config.json"));
        } catch (Exception e) {
            try {
                configJson = (JSONObject) new JSONParser().parse(new FileReader(new File("./resources/config/tenok_config.json")));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Strategy Update Rate 리턴
     * @return strategy update rate in millis
     */
    public static int getUpdateRate() {
        return ((Number) configJson.get("update_rate")).intValue();
    }    
}
