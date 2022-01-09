package cn.cjx913.httpdiffy.exception;

public class HttpDiffyPropertiesException extends RuntimeException{
    public HttpDiffyPropertiesException() {
    }

    public HttpDiffyPropertiesException(String message) {
        super(message);
    }

    public HttpDiffyPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpDiffyPropertiesException(Throwable cause) {
        super(cause);
    }

    public HttpDiffyPropertiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
