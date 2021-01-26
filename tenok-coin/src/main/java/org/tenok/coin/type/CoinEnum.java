package org.tenok.coin.type;

import lombok.Getter;

/**
 * Symbol
 */
@Getter
public enum CoinEnum {
    BTCUSDT("bitcoin", "비트코인"),
    ETHUSDT("ethereum", "이더리움"),
    LTCUSDT("light coin", "라이트코인"),
    LINKUSDT("link coin", "링크코인"),
    XTZUSDT("tezos", "테조스"),
    BCHUSDT("bitcoin cash", "비트코인 캐쉬");

    /**
     * english literal
     */
    private String literal;

    /**
     * korean literal
     */
    private String korean;

    private CoinEnum(String literal, String korean) {
        this.literal = literal;
        this.korean = korean;
    }
}
