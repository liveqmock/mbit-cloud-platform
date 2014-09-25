/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xd.dirt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.springframework.xd.dirt.core.Job;
import org.springframework.xd.dirt.core.Stream;
//import org.springframework.xd.module.ModuleDeploymentProperties;
//import org.springframework.xd.module.ModuleDescriptor;


/**
 * Class that provides utility methods to handle module deployment properties.
 *
 * @author Patrick Peralta
 * @author Mark Fisher
 * @author Ilayaperumal Gopinathan
 * @author Eric Bottard
 */
public class DeploymentPropertiesUtility {

	/**
	 * Pattern used for parsing a String of comma-delimited key=value pairs.
	 */
	private static final Pattern DEPLOYMENT_PROPERTIES_PATTERN = Pattern.compile(",\\s*module\\.[^\\.]+\\.[^=]+=");


	/**
	 * Based on the deployment properties for a {@link Stream}/{@link Job}, create an instance of
	 * {@link org.springframework.xd.dirt.core.ModuleDeploymentProperties} for a specific module
	 * in the {@link Stream}/{@link Job}.
	 *
	 * @param deploymentProperties deployment properties for a stream/job
	 * @param descriptor descriptor for module in the stream for which to create the properties
	 * @return deployment properties for the module
	 */
//	public static ModuleDeploymentProperties createModuleDeploymentProperties(
//			Map<String, String> deploymentProperties, ModuleDescriptor descriptor) {
//		ModuleDeploymentProperties moduleDeploymentProperties = new ModuleDeploymentProperties();
//		// first add properties that should apply to all modules unless overridden
//		String wildcardPrefix = "module.*.";
//		for (Map.Entry<String, String> prop : deploymentProperties.entrySet()) {
//			String key = prop.getKey();
//			if (key.startsWith(wildcardPrefix)) {
//				moduleDeploymentProperties.put(key.substring(wildcardPrefix.length()), prop.getValue());
//			}
//		}
//		// now add properties that are designated for this module explicitly
//		String modulePrefix = String.format("module.%s.", descriptor.getModuleName());
//		for (Map.Entry<String, String> prop : deploymentProperties.entrySet()) {
//			String key = prop.getKey();
//			if (key.startsWith(modulePrefix)) {
//				moduleDeploymentProperties.put(key.substring(modulePrefix.length()), prop.getValue());
//			}
//		}
//		return moduleDeploymentProperties;
//	}

	/**
	 * Parses a String comprised of 0 or more comma-delimited key=value pairs where
	 * each key has the format:
	 * {@code module.[modulename].[key]}.
	 * Values may themselves contain commas, since the split points will be based upon the key pattern.
	 *
	 * @param s the string to parse
	 * @return the Map of parsed key value pairs
	 */
	public static Map<String, String> parseDeploymentProperties(String s) {
		Map<String, String> deploymentProperties = new HashMap<String, String>();
		if (!StringUtils.isEmpty(s)) {
			Matcher matcher = DEPLOYMENT_PROPERTIES_PATTERN.matcher(s);
			int start = 0;
			while (matcher.find()) {
				addKeyValuePairAsProperty(s.substring(start, matcher.start()), deploymentProperties);
				start = matcher.start() + 1;
			}
			addKeyValuePairAsProperty(s.substring(start), deploymentProperties);
		}
		return deploymentProperties;
	}

	/**
	 * Returns a String representation of deployment properties as a comma separated list of key=value pairs.
	 * @param properties the properties to format
	 * @return the properties formatted as a String
	 */
	public static String formatDeploymentProperties(Map<String, String> properties) {
		StringBuilder sb = new StringBuilder(15 * properties.size());
		for (Map.Entry<String, String> pair : properties.entrySet()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(pair.getKey()).append("=").append(pair.getValue());
		}
		return sb.toString();
	}

	/**
	 * Adds a String of format key=value to the provided Map as a key/value pair.
	 *
	 * @param pair the String representation
	 * @param properties the Map to which the key/value pair should be added
	 */
	private static void addKeyValuePairAsProperty(String pair, Map<String, String> properties) {
		int firstEquals = pair.indexOf('=');
		if (firstEquals != -1) {
			// todo: should key only be a "flag" as in: put(key, true)?
			properties.put(pair.substring(0, firstEquals).trim(), pair.substring(firstEquals + 1).trim());
		}
	}

}
