package io.asgardeo.tomcat.oidc.sample.servlet;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import io.asgardeo.java.oidc.sdk.SSOAgentConstants;
import io.asgardeo.java.oidc.sdk.bean.SessionContext;
import io.asgardeo.java.oidc.sdk.config.model.OIDCAgentConfig;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentServerException;
import io.asgardeo.java.oidc.sdk.validators.IDTokenValidator;
import io.asgardeo.tomcat.oidc.sample.ClientHolder;
import io.asgardeo.tomcat.oidc.sample.exception.RegistrationException;
import io.asgardeo.tomcat.oidc.sample.exception.RegistrationServerException;
import io.asgardeo.tomcat.oidc.sample.model.APIResponse;
import io.asgardeo.tomcat.oidc.sample.Constants;
import io.asgardeo.tomcat.oidc.sample.model.UserRegistrationObject;
import io.asgardeo.tomcat.oidc.sample.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.asgardeo.tomcat.oidc.sample.util.Utils.generateRandomPassword;

public class UserRegistrationServlet extends HttpServlet {

    private static final String AUTO_LOGIN_FLOW_TYPE = "SIGNUP";
    private static final String API_KEY = "<choreo API key>";
    private static final String REG_ENDPOINT =  "registration";

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        doPost(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

            try {
                if (Constants.LOCAL_SIGNUP_ENDPOINT.equals(httpServletRequest.getPathInfo())) {
                    String email = httpServletRequest.getParameter(Constants.EMAIL);
                    String password = httpServletRequest.getParameter(Constants.PASSWORD);
                    JSONObject userObject = buildUser(email, password);
                    UserRegistrationObject userRegistrationObject = new UserRegistrationObject(userObject, false, "", "");
                    // TODO use choreo app credentails instead API key, since API KEY expires in few days
                    APIResponse apiResponse = ClientHolder.getDefaultApiClient()
                            .makeHTTPPostToChoreo(REG_ENDPOINT, userRegistrationObject, API_KEY);
                    if (apiResponse.getStatusCode() == HttpServletResponse.SC_CREATED) {
                        httpServletResponse.sendRedirect("/oidc-sample-app/home.jsp");
                    }
                    return;
                } else if (Constants.SOCIAL_SIGNUP_ENDPOINT.equals(httpServletRequest.getPathInfo())) {
                    SessionContext sessionContext = Utils.getSessionContext(httpServletRequest);
                    if (sessionContext != null) {
                        String idToken = sessionContext.getIdToken();
                        if (StringUtils.isNotEmpty(idToken)) {
                            JWT idTokenJWT = JWTParser.parse(idToken);
                            // TODO validate ID token
                            JWTClaimsSet jwtClaimsSet = idTokenJWT.getJWTClaimsSet();
                            if (jwtClaimsSet != null) {
                                // TODO resole amr and get the IDP name . now hardcoding idp name
                                // Create user if not.
                                String fidp = Constants.GOOGLE;
                                JSONObject userObject = buildUser(jwtClaimsSet);
                                String fidpUserName = jwtClaimsSet.getStringClaim(Constants.EMAIL);
                                UserRegistrationObject userRegistrationObject =
                                        new UserRegistrationObject(userObject, true, fidp, fidpUserName);
                                APIResponse apiResponse = ClientHolder.getDefaultApiClient()
                                        .makeHTTPPostToChoreo(REG_ENDPOINT, userRegistrationObject, API_KEY);
                                if (apiResponse.getStatusCode() == HttpServletResponse.SC_CREATED) {
                                    if (httpServletRequest.getSession(false) != null) {
                                        httpServletRequest.getSession(false).invalidate();
                                    }
                                    httpServletResponse.sendRedirect("/oidc-sample-app/home.jsp");
                                }
                            }
                        }
                    }
                    return;
                }
            } catch (SSOAgentServerException e) {
                //TODO
            } catch (ParseException e) {
                //TODO
            } catch (IOException e) {
                //TODO
            } catch (RegistrationException e) {
                //TODO
            }
    }

    public static IDTokenClaimsSet validateIdTokenJWT(JWT idTokenJWT, Nonce nonce, ServletContext servletContext)
            throws SSOAgentServerException {

        if (idTokenJWT != null && nonce != null) {
            if (servletContext.getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME) instanceof OIDCAgentConfig) {
                OIDCAgentConfig oidcAgentConfig =
                        (OIDCAgentConfig) servletContext.getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME);
                IDTokenValidator idTokenValidator = new IDTokenValidator(oidcAgentConfig, idTokenJWT);
                return idTokenValidator.validate(nonce);
            }
        }
        return null;
    }

    private JSONObject buildUser(JWTClaimsSet jwtClaimsSet)
            throws ParseException, RegistrationServerException {

        String email = jwtClaimsSet.getStringClaim(Constants.EMAIL);
        String password = generateRandomPassword(12); // TODO tenant wise password policy change will effect
        String givenName = jwtClaimsSet.getStringClaim(Constants.GIVEN_NAME);
        String familyName = jwtClaimsSet.getStringClaim(Constants.FAMILY_NAME);
        return getUserJsonObject(email, password, givenName, familyName);
    }

    private JSONObject buildUser(String email, String password) throws RegistrationServerException {

        return getUserJsonObject(email, password, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    private JSONObject getUserJsonObject(String email, String password, String givenName, String familyName) {

        String userStoreDomain = Constants.CUSTOMER_USERSTORE_DOMAIN;

        JSONObject rootObject = new JSONObject();
        JSONArray schemas = new JSONArray();
        rootObject.put(Constants.SCHEMA, schemas);

        if (StringUtils.isNotEmpty(familyName) && StringUtils.isNotEmpty(givenName)) {
            JSONObject names = new JSONObject();
            names.put(Constants.GIVEN_NAME, familyName);
            names.put(Constants.FAMILY_NAME, givenName);
            rootObject.put(Constants.NAME, names);
        }

        JSONArray emails = new JSONArray();
        emails.add(email);
        rootObject.put(Constants.EMAILS, emails);
        if (StringUtils.isNotEmpty(userStoreDomain)) {
            rootObject.put(Constants.SCIM_USERNAME,
                    userStoreDomain.concat(Constants.USERSTORE_DOMAIN_APPENDER).concat(email));
        } else {
            rootObject.put(Constants.SCIM_USERNAME, email);
        }
        rootObject.put(Constants.PASSWORD, password);
//        JSONObject enterpriseUser = new JSONObject();
//        if (StringUtils.isNotEmpty(fidpId)) {
//            enterpriseUser.put(Constants.LOCAL_CREDENTIAL_EXISTS, Boolean.FALSE);
//            enterpriseUser.put(Constants.USER_SOURCE_ID, fidpId);
//        } else {
//            enterpriseUser.put(Constants.LOCAL_CREDENTIAL_EXISTS, Boolean.TRUE);
//        }
//        rootObject.put(Constants.SCIM_CUSTOM_SCHEMA, enterpriseUser);
        return rootObject;
    }

//    private static String getFederatedIDPResourceId(String fidp) throws RegistrationServerException{
//
//        String accessToken = generateAccessToken(CLIENT_ID, CLIENT_SECRET, Constants.INTERNAL_IDP_VIEW);
//        APIResponse response =
//                ClientHolder.getDefaultApiClient()
//                        .makeHTTPGet(Constants.IDENTITY_PROVIDER_API + "?filter=name+eq+" + fidp,
//                                accessToken);
//        if (response.getStatusCode() == HttpServletResponse.SC_OK) {
//            String body = response.getBody();
//            return extractIdpId(body);
//        }
//        return null;
//    }

//    private static String extractIdpId(String idpGetResponse) {
//
//        String idpId = null;
//        try {
//            JSONParser jsonParser = new JSONParser();
//            JSONObject idpGetObject = (JSONObject) jsonParser.parse(idpGetResponse);
//            String totalResult = idpGetObject.get(Constants.TOTAL_RESULTS).toString();
//
//            if (!StringUtils.equals("1", totalResult)) {
//                return null;
//            }
//            ArrayList<JSONObject> identityProviders =
//                    (ArrayList<JSONObject>) idpGetObject.get(Constants.IDENTITY_PROVIDERS);
//            if (CollectionUtils.isNotEmpty(identityProviders)) {
//                idpId = identityProviders.get(0).get(Constants.ID).toString();
//            }
//        } catch (org.json.simple.parser.ParseException e) {
//            //TODO
//        }
//        return idpId;
//    }

    private void setAutoLoginCookie(HttpServletRequest request, HttpServletResponse response, String userStoreDomain,
                                    String tenantDomain) {

        String hostDomain = "https://api.asg.io/t/anuradha15";
        if (StringUtils.isEmpty(hostDomain)) {
            return;
        }
        String userName = (String) request.getAttribute(Constants.EMAIL);

        JSONObject cookieValue = new JSONObject();
        cookieValue.put(Constants.AutoLoginConstants.USERNAME,
                userStoreDomain + Constants.AutoLoginConstants.USERSTORE_DOMAIN_APPENDER + userName);
        cookieValue.put(Constants.AutoLoginConstants.CREATED_TIME, System.currentTimeMillis());
        cookieValue.put(Constants.AutoLoginConstants.FLOW_TYPE, AUTO_LOGIN_FLOW_TYPE);
        cookieValue.put(Constants.AutoLoginConstants.DOMAIN, hostDomain);
        String content = cookieValue.toString();

        JSONObject cookieValueInJson = new JSONObject();
        cookieValueInJson.put(Constants.AutoLoginConstants.CONTENT, content);
    }
}
