package cn.cjx913.httpdiffy.exception;

public class HttpDiffRequestContentException extends HttpDiffyException{
    public HttpDiffRequestContentException() {
    }

    public HttpDiffRequestContentException(String message) {
        super(message);
    }

    public HttpDiffRequestContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpDiffRequestContentException(Throwable cause) {
        super(cause);
    }

    public HttpDiffRequestContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
