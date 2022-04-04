package io.asgardeo.tomcat.oidc.sample.exception;

/**
 * Exception class defined for server side registration exceptions.
 */
public class RegistrationServerException extends RegistrationException {

    private static final long serialVersionUID = -9149427058480393412L;

    private String errorCode;

    public RegistrationServerException(String message) {

        super(message);
    }

    public RegistrationServerException(String message, String errorCode) {

        super(message, errorCode);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
    }
}
