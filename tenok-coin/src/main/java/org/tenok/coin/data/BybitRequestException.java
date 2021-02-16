package org.tenok.coin.data;


public class BybitRequestException extends Exception {
    private static final long serialVersionUID = -3865742622315033325L;

    public BybitRequestException() {
    }

    public BybitRequestException(String msg) {
        super(msg);
    }

    public BybitRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
