/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.asgardeo.tomcat.oidc.agent;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.asgardeo.java.oidc.sdk.HTTPSessionBasedOIDCProcessor;
import io.asgardeo.java.oidc.sdk.SSOAgentConstants;
import io.asgardeo.java.oidc.sdk.bean.RequestContext;
import io.asgardeo.java.oidc.sdk.bean.SessionContext;
import io.asgardeo.java.oidc.sdk.config.model.OIDCAgentConfig;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentClientException;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentException;
import io.asgardeo.java.oidc.sdk.exception.SSOAgentServerException;
import io.asgardeo.java.oidc.sdk.request.OIDCRequestResolver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * OIDCAgentFilter is the Filter class responsible for building
 * requests and handling responses for authentication, SLO and session
 * management for the OpenID Connect flows, using the io-asgardeo-oidc-sdk.
 * It is an implementation of the base class, {@link Filter}.
 * OIDCAgentFilter verifies if:
 * <ul>
 * <li>The request is a URL to skip
 * <li>The request is a Logout request
 * <li>The request is already authenticated
 * </ul>
 * <p>
 * and build and send the request, handle the response,
 * or forward the request accordingly.
 */
public class OIDCAgentFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(OIDCAgentFilter.class);

    protected FilterConfig filterConfig = null;
    OIDCAgentConfig oidcAgentConfig;
    HTTPSessionBasedOIDCProcessor oidcManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        ServletContext servletContext = filterConfig.getServletContext();
        if (servletContext.getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME) instanceof OIDCAgentConfig) {
            this.oidcAgentConfig = (OIDCAgentConfig) servletContext.getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME);
        }
        try {
            this.oidcManager = new HTTPSessionBasedOIDCProcessor(oidcAgentConfig);
        } catch (SSOAgentClientException e) {
            throw new SSOAgentException(e.getMessage(), e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        OIDCRequestResolver requestResolver = new OIDCRequestResolver(request, oidcAgentConfig);

        if (requestResolver.isSkipURI()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (requestResolver.isLogoutURL()) {
            try {
                oidcManager.logout(request, response);
            } catch (SSOAgentException e) {
                handleException(request, response, e);
            }
            return;
        }

        if (requestResolver.isCallbackResponse()) {
            RequestContext requestContext = getRequestContext(request);
            try {
                oidcManager.handleOIDCCallback(request, response);
            } catch (SSOAgentException e) {
                handleException(request, response, e);
                return;
            }
            // Check for logout scenario.
            if (requestResolver.isLogout()) {
                response.sendRedirect(oidcAgentConfig.getIndexPage());
                return;
            }
            String homePage = resolveTargetPage(requestContext);
            response.sendRedirect(homePage);
            return;
        }

        if (!isActiveSessionPresent(request)) {
            try {
                Map<String, String> additionalParams = new HashMap<>();
                if (request.getQueryString() != null) {
                    String[] queryParams = request.getQueryString().split("&");
                    for (String param : queryParams) {
                        String[] keyValuePair = param.split("=");
                        if (keyValuePair.length != 2) {
                            continue;
                        }
                        try {
                            additionalParams.put(keyValuePair[0],
                                    URLDecoder.decode(keyValuePair[1], StandardCharsets.UTF_8.name()));
                        } catch (UnsupportedEncodingException e) {
                            throw new SSOAgentException(e);
                        }
                    }
                }
                oidcAgentConfig.setAdditionalParamsForAuthorizeEndpoint(additionalParams);
                oidcManager.sendForLogin(request, response);
            } catch (SSOAgentException e) {
                handleException(request, response, e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String resolveTargetPage(RequestContext requestContext) {

        String targetPage = "home.jsp";
        if (StringUtils.isNotBlank(oidcAgentConfig.getHomePage())) {
            targetPage = oidcAgentConfig.getHomePage();
        } else if (requestContext != null) {
            if (requestContext.getParameter(SSOAgentConstants.REDIRECT_URI_KEY) != null) {
                targetPage = requestContext.getParameter(SSOAgentConstants.REDIRECT_URI_KEY).toString();
            }
        }
        return targetPage;
    }

    private RequestContext getRequestContext(HttpServletRequest request) throws SSOAgentServerException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SSOAgentConstants.REQUEST_CONTEXT) != null) {
            return (RequestContext) request.getSession(false).getAttribute(SSOAgentConstants.REQUEST_CONTEXT);
        }
        throw new SSOAgentServerException("Request context null.");
    }

    @Override
    public void destroy() {

    }

    boolean isActiveSessionPresent(HttpServletRequest request) {

        HttpSession currentSession = request.getSession(false);

        return currentSession != null
                && currentSession.getAttribute(SSOAgentConstants.SESSION_CONTEXT) != null
                && currentSession.getAttribute(SSOAgentConstants.SESSION_CONTEXT) instanceof SessionContext;
    }

    void clearSession(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    protected void handleException(HttpServletRequest request, HttpServletResponse response, SSOAgentException e)
            throws ServletException, IOException {

        String errorPage = oidcAgentConfig.getErrorPage();
        if (StringUtils.isBlank(errorPage)) {
            errorPage = buildErrorPageURL(oidcAgentConfig, request);
        }
        if (errorPage.trim().charAt(0) != '/') {
            errorPage = "/" + errorPage;
        }
        clearSession(request);
        logger.log(Level.FATAL, e.getMessage());
        request.setAttribute(SSOAgentConstants.AGENT_EXCEPTION, e);
        RequestDispatcher requestDispatcher = request.getServletContext().getRequestDispatcher(errorPage);
        requestDispatcher.forward(request, response);
    }

    private String buildErrorPageURL(OIDCAgentConfig oidcAgentConfig, HttpServletRequest request) {

        if (StringUtils.isNotBlank(oidcAgentConfig.getErrorPage())) {
            return oidcAgentConfig.getErrorPage();
        } else if (StringUtils.isNotBlank(oidcAgentConfig.getIndexPage())) {
            return oidcAgentConfig.getIndexPage();
        }
        return SSOAgentConstants.DEFAULT_CONTEXT_ROOT;
    }
}
