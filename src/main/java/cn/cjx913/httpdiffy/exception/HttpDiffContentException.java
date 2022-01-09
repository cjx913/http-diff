package cn.cjx913.httpdiffy.exception;

public class HttpDiffContentException extends HttpDiffyException{
    public HttpDiffContentException() {
    }

    public HttpDiffContentException(String message) {
        super(message);
    }

    public HttpDiffContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpDiffContentException(Throwable cause) {
        super(cause);
    }

    public HttpDiffContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
