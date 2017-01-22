package com.android.philip.photoapp.api.auth;

/**
 * Created by Philip Bao on 2017-01-15.
 */

public class FiveHundredException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1755802476069592558L;

    private int statusCode = 0 ;

    public int getStatusCode() {
        return statusCode;
    }

    public FiveHundredException() {
        super();
    }

    public FiveHundredException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FiveHundredException(String detailMessage) {
        super(detailMessage);
    }

    public FiveHundredException(Throwable throwable) {
        super(throwable);
    }

    public FiveHundredException(int status) {
        super();
        this.statusCode = status;
    }

}
