package cn.cjx913.httpdiffy.exception;

public class HttpDiffyException extends RuntimeException{
    public HttpDiffyException() {
    }

    public HttpDiffyException(String message) {
        super(message);
    }

    public HttpDiffyException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpDiffyException(Throwable cause) {
        super(cause);
    }

    public HttpDiffyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
