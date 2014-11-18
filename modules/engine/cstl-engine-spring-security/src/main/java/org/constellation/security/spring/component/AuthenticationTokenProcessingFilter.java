package org.constellation.security.spring.component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.constellation.security.spring.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    
    private static final Logger LOGGER = LoggerFactory.getLogger("token");
    
    @Autowired
    private UserDetailsService userService;

    @Autowired
    private TokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = this.getAsHttpRequest(request);

        String authToken = this.extractAuthTokenFromRequest(httpRequest);
        String userName = tokenService.getUserNameFromToken(authToken);

        if (userName != null) {

            UserDetails userDetails = this.userService.loadUserByUsername(userName);

            if (tokenService.validateToken(authToken, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private HttpServletRequest getAsHttpRequest(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new RuntimeException("Expecting an HTTP request");
        }

        return (HttpServletRequest) request;
    }

    private String extractAuthTokenFromRequest(HttpServletRequest httpRequest) {
        /* Get token from header */
        String authToken = httpRequest.getHeader("X-Auth-Token");
        if (authToken != null) {
            LOGGER.debug("Header: " + authToken + " (" + httpRequest.getRequestURI() + ")");
            return authToken;
        }

        /* Extract from cookie */
        Cookie[] cookies = httpRequest.getCookies();
        if(cookies!=null)
        for (Cookie cookie : cookies) {
            if("authToken".equals(cookie.getName())) {
                try {
                    LOGGER.debug("Cookie: " + authToken + " (" + httpRequest.getRequestURI() + ")");
                    return URLDecoder.decode(cookie.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        
        /* If token not found get it from request query string 'token' parameter */
        String queryString = httpRequest.getQueryString();
        if (StringUtils.hasText(queryString)) {
            int tokenIndex = queryString.indexOf("token=");
            if (tokenIndex != -1) {
                tokenIndex += "token=".length();
                int tokenEndIndex = queryString.indexOf('&', tokenIndex);
                if (tokenEndIndex == -1)
                    authToken = queryString.substring(tokenIndex);
                else
                    authToken = queryString.substring(tokenIndex, tokenEndIndex);
                LOGGER.debug("QueryString: " + authToken + " (" + httpRequest.getRequestURI() + ")");
            }
        }
        // authToken = httpRequest.getParameter("token");

        return authToken;
    }
}