package org.tenok.coin.type;

import lombok.Getter;

@Getter
public enum SideEnum {
    BUY("Buy", "매수"),
    Sell("Sell", "매도");

    private String apiString;
    private String korean;

    private SideEnum(String apiString, String korean) {
        this.apiString = apiString;
        this.korean = korean;
    }
}
