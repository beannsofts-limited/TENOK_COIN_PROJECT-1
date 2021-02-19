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
        File configFile = new File("./../resources/config/tenok_config.json");
        try {
            configJson = (JSONObject) new JSONParser().parse(new FileReader(configFile));
        } catch (Exception e) {
            e.printStackTrace();
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
