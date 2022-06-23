package io.asgardeo.tomcat.oidc.sample.model;

import org.json.simple.JSONObject;

public class UserRegistrationObject {

    private JSONObject userObject;
    private boolean isFederatedUserSignup;
    private String federatedIdp;
    private String federatedSubjectID;

    public UserRegistrationObject(JSONObject userObject, boolean isFederatedUserSignup, String federatedIdp,
                                  String federatedSubjectID) {

        this.userObject = userObject;
        this.isFederatedUserSignup = isFederatedUserSignup;
        this.federatedIdp = federatedIdp;
        this.federatedSubjectID = federatedSubjectID;
    }

    public JSONObject getUserObject() {

        return userObject;
    }

    public boolean isFederatedUserSignup() {

        return isFederatedUserSignup;
    }

    public String getFederatedIdp() {

        return federatedIdp;
    }

    public String getFederatedSubjectID() {

        return federatedSubjectID;
    }

    public void setUserObject(JSONObject userObject) {

        this.userObject = userObject;
    }

    public void setIsFederatedUserSignup(boolean isFederatedUserSignup) {

        this.isFederatedUserSignup = isFederatedUserSignup;
    }

    public void setFederatedIdp(String federatedIdp) {

        this.federatedIdp = federatedIdp;
    }

    public void setFederatedSubjectID(String federatedSubjectID) {

        this.federatedSubjectID = federatedSubjectID;
    }
}
