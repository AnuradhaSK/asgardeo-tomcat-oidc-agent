package io.asgardeo.tomcat.oidc.sample.exception;

/**
 * Exception class defined for PreRegistration exceptions.
 */
public class RegistrationException extends Exception {

    private static final long serialVersionUID = -9149427058480393410L;

    private String errorCode;

    public RegistrationException(String message) {

        super(message);
    }

    public RegistrationException(String message, String errorCode) {

        super(message);
        this.errorCode = errorCode;
    }

    public RegistrationException(String message, Throwable cause) {

        super(message, cause);
    }

    public RegistrationException(String message, String errorCode, Throwable cause) {

        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
    }
}
