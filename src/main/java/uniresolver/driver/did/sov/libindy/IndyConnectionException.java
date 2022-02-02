package uniresolver.driver.did.sov.libindy;

public class IndyConnectionException extends Exception {

    public IndyConnectionException() {
    }

    public IndyConnectionException(String message) {
        super(message);
    }

    public IndyConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndyConnectionException(Throwable cause) {
        super(cause);
    }

    public IndyConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
