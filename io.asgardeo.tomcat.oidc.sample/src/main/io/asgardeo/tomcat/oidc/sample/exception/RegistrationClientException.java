package io.asgardeo.tomcat.oidc.sample.exception;

/**
 * Exception class defined for client side registration exceptions.
 */
public class RegistrationClientException extends RegistrationException {

    private static final long serialVersionUID = -9149427058480393411L;

    private String code;

    public RegistrationClientException(String message) {

        super(message);
    }

    public RegistrationClientException(String message, String code) {

        super(message, code);
        this.code = code;
    }

    public RegistrationClientException(String message, Throwable cause) {

        super(message, cause);
    }

    public RegistrationClientException(String message, String errorCode, Throwable cause) {

        super(message, cause);
        this.code = errorCode;
    }

    public String getErrorCode() {

        return code;
    }

    public void setErrorCode(String errorCode) {

        this.code = errorCode;
    }

}
