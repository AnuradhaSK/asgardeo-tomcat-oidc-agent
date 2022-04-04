package io.asgardeo.tomcat.oidc.sample;

/**
 * Provide clients.
 */
public class ClientHolder {

    private static APIClient defaultApiClient = new APIClient();

    private ClientHolder() {
        //block initiation.
    }

    /**
     * Get the default API client, which would be used when creating API
     * instances without providing an API client.
     */
    public static APIClient getDefaultApiClient() {

        return defaultApiClient;
    }

    /**
     * Set the default API client, which would be used when creating API
     * instances without providing an API client.
     */
    public static void setDefaultApiClient(APIClient apiClient) {

        defaultApiClient = apiClient;
    }
}
