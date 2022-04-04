package io.asgardeo.tomcat.oidc.sample.util;

import io.asgardeo.java.oidc.sdk.SSOAgentConstants;
import io.asgardeo.java.oidc.sdk.bean.SessionContext;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentServerException;
import io.asgardeo.tomcat.oidc.sample.ClientHolder;
import io.asgardeo.tomcat.oidc.sample.Constants;
import io.asgardeo.tomcat.oidc.sample.exception.RegistrationServerException;
import io.asgardeo.tomcat.oidc.sample.model.APIResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Utils {

    private static final Random RANDOM = new SecureRandom();

    public static SessionContext getSessionContext(HttpServletRequest request) throws SSOAgentServerException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SSOAgentConstants.SESSION_CONTEXT) != null) {
            return (SessionContext) request.getSession(false).getAttribute(SSOAgentConstants.SESSION_CONTEXT);
        }
        throw new SSOAgentServerException("Session context null.");
    }

    public static String generateAccessToken(String clientId, String clientSecret, String scope)
            throws RegistrationServerException {

        try {
            APIResponse apiResponse =
                    ClientHolder.getDefaultApiClient().makeHTTPPostToken(clientId, clientSecret, scope);
            int statusCode = apiResponse.getStatusCode();
            if (statusCode == 200) {
                JSONParser parser = new JSONParser();
                JSONObject tokenResponse = (JSONObject) parser.parse(apiResponse.getBody());
                return (String) tokenResponse.get(Constants.ACCESS_TOKEN);
            } else {
                throw new RegistrationServerException("Error while obtaining access token");
            }
        } catch (ParseException e) {
            throw new RegistrationServerException("Parsing error");
        }
    }

    /**
     * Generate a random password.
     *
     * @param passwordLength Length of the password.
     * @return Generated password.
     */
    public static String generateRandomPassword(int passwordLength) {

        char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] numbers = "0123456789".toCharArray();
        char[] symbols = "!@#$%*&".toCharArray();
        char[] allChars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%*&"
                        .toCharArray();
        char[] password = new char[passwordLength];

        // Get the requirements of having at least on char of below.
        password[0] = lowercase[RANDOM.nextInt(lowercase.length)];
        password[1] = uppercase[RANDOM.nextInt(uppercase.length)];
        password[2] = numbers[RANDOM.nextInt(numbers.length)];
        password[3] = symbols[RANDOM.nextInt(symbols.length)];

        // Populate rest of the password with random chars.
        for (int i = 4; i < passwordLength; i++) {
            password[i] = allChars[RANDOM.nextInt(allChars.length)];
        }

        // Shuffle characters.
        for (int i = 0; i < password.length; i++) {
            int randomPosition = RANDOM.nextInt(password.length);
            char temp = password[i];
            password[i] = password[randomPosition];
            password[randomPosition] = temp;
        }
        return new String(password);
    }
}
