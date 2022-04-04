package io.asgardeo.tomcat.oidc.sample;

/**
 * Constant class.
 */
public class Constants {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String SCIM_USERNAME = "userName";
    public static final String EMAIL = "email";
    public static final String EMAILS = "emails";
    public static final String PASSWORD = "password";
    public static final String LOCAL_CREDENTIAL_EXISTS = "localCredentialExists";
    public static final String USER_SOURCE_ID = "userSourceId";
    public static final String SCIM_CUSTOM_SCHEMA = "urn:scim:wso2:schema";
    public static final String SCHEMA = "schema";
    public static final String CUSTOMER_USERSTORE_DOMAIN = "CUSTOMER-DEFAULT";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String GOOGLE = "Google";
    public static final String GIVEN_NAME = "givenName";
    public static final String FAMILY_NAME = "familyName";
    public static final String NAME = "name";

    public static final String IDENTITY_PROVIDER_API = "api/server/v1/identity-providers";
    public static final String IDENTITY_PROVIDERS = "identityProviders";
    public static final String TOTAL_RESULTS = "totalResults";
    public static final String SCIM_USERS_API = "scim2/Users";
    public static final String USER_ID_SEPARATOR = "Users/";
    public static final String OAUTH2_TOKEN_API = "oauth2/token";
    public static final String FEDERATED_ASSOCIATION_API =
            "api/asgardeo-federated-user-association/v1/federated-associations";
    public static final String LOCAL_SIGNUP_ENDPOINT = "/local";
    public static final String SOCIAL_SIGNUP_ENDPOINT = "/federated";
    public static final String USERSTORE_DOMAIN_APPENDER = "/";

    // Scopes
    public static final String INTERNAL_USER_MGT_CREATE = "internal_user_mgt_create";
    public static final String INTERNAL_USER_MGT_LIST = "internal_user_mgt_list";
    public static final String INTERNAL_USER_MGT_DELETE = "internal_user_mgt_delete";
    public static final String INTERNAL_USER_ASSOCIATION_CREATE = "internal_user_association_create";
    public static final String INTERNAL_IDP_VIEW = "internal_idp_view";

    public static class APIClientConstants {

        public static final String JSON_MEDIA_TYPE = "application/json; charset=utf-8";
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
        public static final String AUTHORIZATION = "Authorization";
        public static final String GRANT_TYPE = "grant_type";
        public static final String CLIENT_CREDENTIAL = "client_credentials";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String SCOPE = "scope";
    }

    /**
     * The Constants using in auto login flows are defined here.
     */
    public static class AutoLoginConstants {

        public static final String USERNAME = "username";
        public static final String CREATED_TIME = "createdTime";
        public static final String TENANT_DOMAIN_APPENDER = "@";
        public static final String USERSTORE_DOMAIN_APPENDER = "/";
        public static final String FLOW_TYPE = "flowType";
        public static final String SIGNATURE = "signature";
        public static final String DOMAIN = "domain";
        public static final String SET_COOKIE = "Set-Cookie";
        public static final String CONTENT = "content";
    }
}
