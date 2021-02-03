package org.tenok.coin.type;

import lombok.Getter;

@Getter
public enum SideEnum {
    OPEN_BUY("Buy", "매수/오픈"), OPEN_SELL("Sell", "매도/오픈"), CLOSE_BUY("Buy", "매수/청산"), CLOSE_SELL("Sell", "매도/청산");

    private String apiString;
    private String korean;

    private SideEnum(String apiString, String korean) {
        this.apiString = apiString;
        this.korean = korean;
    }
}
