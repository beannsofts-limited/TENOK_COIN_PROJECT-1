package org.tenok.coin.type;

import lombok.Getter;

@Getter
public enum SideEnum {
    BUY("Buy", "매수"),
    Sell("Sell", "매도");

    private String literal;
    private String korean;

    private SideEnum(String literal, String korean) {
        this.literal = literal;
        this.korean = korean;
    }
}
