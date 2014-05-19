/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */

package org.constellation.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bgarcia
 * @author Olivier NOUGUIER
 */
public class CorsFilter implements Filter {

    private Pattern EXCUSION_PATTERN;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        String exclude = filterConfig.getInitParameter("exclude");
        if (exclude != null)
            EXCUSION_PATTERN = Pattern.compile(filterConfig.getServletContext().getContextPath() +  exclude);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (EXCUSION_PATTERN == null || !EXCUSION_PATTERN.matcher(httpServletRequest.getRequestURI()).matches())
            httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");

        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(httpServletRequest.getMethod()))
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        else
            chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
        // do nothing
    }
}
