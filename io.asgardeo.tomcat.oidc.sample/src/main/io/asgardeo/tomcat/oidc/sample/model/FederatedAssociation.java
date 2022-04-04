package io.asgardeo.tomcat.oidc.sample.model;

/**
 * Model class for federated user association.
 */
public class FederatedAssociation {

    private String userId;
    private String idp;
    private String federatedUserId;

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public String getIdp() {

        return idp;
    }

    public void setIdp(String idp) {

        this.idp = idp;
    }

    public String getFederatedUserId() {

        return federatedUserId;
    }

    public void setFederatedUserId(String federatedUserId) {

        this.federatedUserId = federatedUserId;
    }
}
