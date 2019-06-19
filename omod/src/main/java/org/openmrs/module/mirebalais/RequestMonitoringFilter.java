/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mirebalais;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.util.OpenmrsUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

/**
 * Basic servlet filter to log page requests, utilization, and performance
 */
public class RequestMonitoringFilter implements Filter {

	private static Logger logger = Logger.getLogger(RequestMonitoringFilter.class);

	private static final String NAME = "RequestMonitoringFileAppender";
	private static final String LOG_FILE_DIR_NAME = "activitylog";
	private static final String LOG_FILE_NAME = "activity.log";
	private static final String ENABLED_PROPERTY = "activitylog_enabled";

	private boolean enabled = false;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			enabled = "true".equalsIgnoreCase(Context.getRuntimeProperties().getProperty(ENABLED_PROPERTY, "false"));
			logger.setAdditivity(false);
			File directory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(LOG_FILE_DIR_NAME);
			directory.mkdirs();
			File outputFile = new File(directory, LOG_FILE_NAME);
			Layout layout = new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN);
			DailyRollingFileAppender appender = new DailyRollingFileAppender(layout, outputFile.getAbsolutePath(), "-yyyy-MM-dd");
			appender.setName(NAME);
			logger.setLevel(Level.INFO);
			logger.addAppender(appender);
		}
		catch (Exception e) {
			throw new ServletException("Unable to initialize RequestMonitoringFilter", e);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long startTime = System.currentTimeMillis();
		chain.doFilter(request, response);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		if (enabled && request instanceof HttpServletRequest) {
			try {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				HttpSession session = httpRequest.getSession();
				ISO8601DateFormat dateFormat = new ISO8601DateFormat();
				SimpleObject logLine = new SimpleObject();
				logLine.put("timestamp", dateFormat.format(new Date(startTime)));
				logLine.put("sessionId", session.getId());
				logLine.put("user", session.getAttribute("username"));
				logLine.put("loadTime", totalTime);
				logLine.put("method", httpRequest.getMethod());
				logLine.put("requestPath", httpRequest.getRequestURI());
				logLine.put("queryParams", formatRequestParams(httpRequest));
				logger.info(logLine.toJson());
			}
			catch (Exception e) {
				// Do nothing, we don't want this filter to cause any adverse behavior
			}
		}
	}

	@Override
	public void destroy() {
		logger.removeAppender(NAME);
	}

	private String formatRequestParams(HttpServletRequest request) {
		StringBuilder ret = new StringBuilder();
		if (request.getMethod().equalsIgnoreCase("GET")) {
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements(); ) {
				String paramName = e.nextElement();
				String[] paramValues = request.getParameterValues(paramName);
				if (paramValues != null) {
					for (String paramValue : paramValues) {
						if (StringUtils.isNotBlank(paramValue)) {
							if (ret.length() > 0) {
								ret.append("&");
							}
							if (paramName.toUpperCase().contains("PASSWORD")) {
								paramValue = "********";
							}
							ret.append(paramName).append("=").append(paramValue);
						}
					}
				}
			}
		}
		return ret.toString();
	}
}
