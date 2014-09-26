/*
 * Copyright 2013-2014 the original author or authors.
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

package org.springframework.xd.dirt.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.xd.dirt.cluster.ContainerAttributes;
import org.springframework.xd.dirt.server.options.ContainerOptions;
import org.springframework.xd.dirt.util.BannerUtils;
import org.springframework.xd.dirt.util.RuntimeUtils;
import org.springframework.xd.dirt.util.XdProfiles;

/**
 * The boot application class for a Container server.
 *
 * @author Mark Fisher
 * @author David Turanski
 */
@Configuration
@EnableAutoConfiguration(exclude = { BatchAutoConfiguration.class, JmxAutoConfiguration.class })
public class ContainerServerApplication implements EnvironmentAware {

	private static final Log logger = LogFactory.getLog(ContainerServerApplication.class);

	private static final String CONTAINER_ATTRIBUTES_PREFIX = "xd.container.";

	private ConfigurableApplicationContext containerContext;

	private ConfigurableEnvironment environment;

	public static void main(String[] args) {
		new ContainerServerApplication().run(args);
	}

	public void dumpContextConfiguration() {
		ApplicationUtils.dumpContainerApplicationContextConfiguration(this.containerContext);
	}

	public ContainerServerApplication run(String... args) {
		System.out.println(BannerUtils.displayBanner(getClass().getSimpleName(), null));

		try {
			ContainerBootstrapContext bootstrapContext = new ContainerBootstrapContext(new ContainerOptions());

			this.containerContext = new SpringApplicationBuilder(ContainerOptions.class, ParentConfiguration.class)
					.logStartupInfo(false)
					.profiles(XdProfiles.CONTAINER_PROFILE)
					.listeners(bootstrapContext.commandLineListener())
					.child(SharedServerContextConfiguration.class, ContainerOptions.class)
					.logStartupInfo(false)
					.listeners(bootstrapContext.commandLineListener())
					.child(ContainerServerApplication.class)
					.logStartupInfo(false)
					.listeners(
							ApplicationUtils.mergeApplicationListeners(bootstrapContext.commandLineListener(),
									bootstrapContext.pluginContextInitializers()))
					.child(ContainerConfiguration.class)
					.listeners(bootstrapContext.commandLineListener())
					.initializers(new IdInitializer()).run(args);
		}
		catch (Exception e) {
			handleFieldErrors(e);
		}
		return this;
	}

	@Bean
	public ContainerAttributes containerAttributes() {
		ContainerAttributes containerAttributes = new ContainerAttributes();
		containerAttributes.setHost(RuntimeUtils.getHost()).setIp(RuntimeUtils.getIpAddress()).setPid(
				RuntimeUtils.getPid());
		setConfiguredContainerAttributes(containerAttributes);
		return containerAttributes;
	}

	/**
	 * @param containerAttributes
	 */
	private void setConfiguredContainerAttributes(ContainerAttributes containerAttributes) {
		Map<String, String> attributes = new HashMap<String, String>();
		for (PropertySource<?> propertySource : environment.getPropertySources()) {
			if (propertySource instanceof EnumerablePropertySource) {
				EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) propertySource;
				for (String key : ps.getPropertyNames()) {
					if (key.startsWith(CONTAINER_ATTRIBUTES_PREFIX)) {
						String attributeKey = key.replaceAll(CONTAINER_ATTRIBUTES_PREFIX, "");
						attributes.put(attributeKey, environment.getProperty(key));
					}
				}
			}
		}
		containerAttributes.putAll(attributes);
	}

	@Override
	public void setEnvironment(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment,
				"unsupported environment type. " + environment.getClass());
		this.environment = (ConfigurableEnvironment) environment;
	}

	private void handleFieldErrors(Exception e) {
		if (e.getCause() instanceof BindException) {
			BindException be = (BindException) e.getCause();
			for (FieldError error : be.getFieldErrors()) {
				logger.error(String.format("the value '%s' is not allowed for property '%s'",
						error.getRejectedValue(),
						error.getField()));
			}
		}
		else {
			e.printStackTrace();
		}
		System.exit(1);
	}

	private class IdInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			ContainerAttributes containerAttributes = applicationContext.getParent().getBean(ContainerAttributes.class);
			applicationContext.setId(containerAttributes.getId());
		}
	}
}
