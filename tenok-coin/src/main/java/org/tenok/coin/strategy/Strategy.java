package org.tenok.coin.strategy;

public interface Strategy {
    /**
     * 포지션 오픈 조건에 해당하는 지를 반환.
     * 0일 경우 오픈 불가.
     * 0 이상일 경우, 오픈 비율.
     * @return 포지션 오픈 비율
     */
    public double testOpenRBI();

    /**
     * 포지션 청산 조건에 해당하는 지를 반환
     * @return true: 청산, false: 관망
     */
    public boolean testCloseRBI();

    public boolean isOpened();
}
