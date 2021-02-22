package org.tenok.coin.strategy;

import org.tenok.coin.type.CoinEnum;

public interface Strategy {
    /**
     * 포지션 오픈 조건에 해당하는 지를 반환. 0일 경우 오픈 불가. 0 이상일 경우, 오픈 비율.
     * 
     * @return 포지션 오픈 비율
     */
    public double testOpenRBI();

    /**
     * 해당 전략의 별칭. 또는 이름 제공
     * @return 전략의 이름
     */
    public String getStrategyName();

    /**
     * 포지션 청산 조건에 해당하는 지를 반환
     * 
     * @return true: 청산, false: 관망
     */
    public boolean testCloseRBI();

    public CoinEnum getCoinType();

    public boolean isOpened();

    public boolean isNotOpened();

    public void setIsopened(boolean isOpened);
}
