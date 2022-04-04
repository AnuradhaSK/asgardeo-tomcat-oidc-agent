package io.asgardeo.tomcat.oidc.sample.model;

public class APIResponse {
    private int statusCode;
    private String locationHeader;
    private String body;

    public int getStatusCode() {

        return statusCode;
    }

    public void setStatusCode(int statusCode) {

        this.statusCode = statusCode;
    }

    public String getLocationHeader() {

        return locationHeader;
    }

    public void setLocationHeader(String locationHeader) {

        this.locationHeader = locationHeader;
    }

    public String getBody() {

        return body;
    }

    public void setBody(String body) {

        this.body = body;
    }
}
