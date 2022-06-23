package io.asgardeo.tomcat.oidc.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.asgardeo.tomcat.oidc.sample.model.APIResponse;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class APIClient {

    public static final MediaType JSON = MediaType.parse(Constants.APIClientConstants.JSON_MEDIA_TYPE);
    public static final String BASE_URL = "https://dev.api.asgardeo.io/t/anuradha/";
    public static final String CHOREO_API_BASE_URL = "https://00d50a2e-a547-499b-9386-cdf5d319155e-dev.e1-us-east-azure.choreoapis.dev/xcrh/registration35gq/1.0.0/";

    public APIResponse makeHTTPPostToChoreo(String urlContext, Object body, String apiKey) {

        APIResponse apiResponse = null;
        try {
            OkHttpClient okHttpClient = getUnsafeOkHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(body);
            RequestBody requestBody = RequestBody.create(jsonString, JSON);

            Request request = new Request.Builder()
                    .url(CHOREO_API_BASE_URL + urlContext)
                    .header(Constants.APIClientConstants.API_KEY, apiKey)
                    .header(Constants.APIClientConstants.ACCEPT, Constants.APIClientConstants.APPLICATION_JSON)
                    .header(Constants.APIClientConstants.CONTENT_TYPE, Constants.APIClientConstants.APPLICATION_JSON)
                    .method("POST", requestBody)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                apiResponse = buildApiResponse(response);
            }
        } catch (IOException e) {
            // TODO
        }
        return apiResponse;
    }

//    public APIResponse makeHTTPGet(String urlContext, String header) {
//
//        try {
//            OkHttpClient okHttpClient = getUnsafeOkHttpClient();
//            APIResponse apiResponse;
//            Request request = new Request.Builder().url(BASE_URL + urlContext)
//                    .header(Constants.APIClientConstants.AUTHORIZATION, "Bearer " + header)
//                    .header(Constants.APIClientConstants.ACCEPT, Constants.APIClientConstants.APPLICATION_JSON)
//                    .build();
//            try (Response response = okHttpClient.newCall(request).execute()) {
//                apiResponse = buildApiResponse(response);
//                return apiResponse;
//            }
//        } catch (IOException e) {
//            // TODO
//        }
//        return null;
//    }
//
//    public APIResponse makeHTTPPostToken(String clientKey, String clientSecret, String scope) {
//
//        APIResponse apiResponse = null;
//        try {
//            OkHttpClient okHttpClient = getUnsafeOkHttpClient();
//
//            RequestBody requestBody = new FormBody.Builder()
//                    .add(Constants.APIClientConstants.GRANT_TYPE, Constants.APIClientConstants.CLIENT_CREDENTIAL)
//                    .add(Constants.APIClientConstants.SCOPE, scope)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(BASE_URL + Constants.OAUTH2_TOKEN_API)
//                    .header(Constants.APIClientConstants.CONTENT_TYPE,
//                            Constants.APIClientConstants.APPLICATION_FORM_URL_ENCODED)
//                    .header(Constants.APIClientConstants.AUTHORIZATION,
//                            "Basic " + Base64.getEncoder().encodeToString((clientKey + ":" + clientSecret).getBytes()))
//                    .post(requestBody)
//                    .build();
//
//            try (Response response = okHttpClient.newCall(request).execute()) {
//                apiResponse = buildApiResponse(response);
//            }
//        } catch (IOException e) {
//            // TODO
//        }
//        return apiResponse;
//    }

    private OkHttpClient getUnsafeOkHttpClient() {

        try {
            Long callTimeOut = Long.parseLong("30");
            Long connectTimeOut = Long.parseLong("10");
            Long readTimeOut = Long.parseLong("30");
            Long writeTimeOut = Long.parseLong("30");

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts;
            trustAllCerts = new TrustManager[]{new TrustAllManager()};

            final SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .callTimeout(callTimeOut, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .hostnameVerifier((hostname, session) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    private APIResponse buildApiResponse(Response response) throws IOException {

        int statusCode = response.code();
        String locationHeader = null;
        APIResponse apiResponse = new APIResponse();

        if (response.body() != null) {
            apiResponse.setBody(response.body().string());
        }
        if (StringUtils.isNotEmpty(response.header("Location"))) {
            locationHeader = response.header("Location");
        }
        apiResponse.setLocationHeader(locationHeader);
        apiResponse.setStatusCode(statusCode);
        return apiResponse;
    }

    /**
     * Allowing to trust everything.
     */
    static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {

            return new X509Certificate[0];
        }
    }
}
