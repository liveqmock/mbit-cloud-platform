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

package org.springframework.xd.dirt.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.xd.dirt.cluster.ContainerAttributes;
import org.springframework.xd.dirt.container.store.ContainerRepository;
import org.springframework.xd.dirt.job.JobFactory;
//import org.springframework.xd.dirt.module.ModuleDefinitionRepository;
//import org.springframework.xd.dirt.module.ModuleDeployer;
import org.springframework.xd.dirt.stream.JobDefinitionRepository;
import org.springframework.xd.dirt.stream.StreamDefinitionRepository;
import org.springframework.xd.dirt.stream.StreamFactory;
import org.springframework.xd.dirt.util.XdConfigLoggingInitializer;
//import org.springframework.xd.dirt.zookeeper.ZooKeeperConnection;
//import org.springframework.xd.dirt.zookeeper.ZooKeeperConnectionConfigurer;
//import org.springframework.xd.module.options.ModuleOptionsMetadataResolver;

/**
 * Container Application Context
 *
 * @author David Turanski
 * @author Ilayaperumal Gopinathan
 */
@Configuration
@EnableAutoConfiguration(exclude = { BatchAutoConfiguration.class, JmxAutoConfiguration.class })
class ContainerConfiguration {

	@Autowired
	private ContainerAttributes containerAttributes;

	@Autowired
	private ContainerRepository containerRepository;

	@Autowired
	private StreamDefinitionRepository streamDefinitionRepository;

	@Autowired
	private JobDefinitionRepository jobDefinitionRepository;

//	@Autowired
//	private ModuleDefinitionRepository moduleDefinitionRepository;
//
//	@Autowired
//	private ModuleOptionsMetadataResolver moduleOptionsMetadataResolver;
//
//	@Autowired
//	private ModuleDeployer moduleDeployer;
//
//	@Autowired
//	private ZooKeeperConnection zooKeeperConnection;
//
	/*
	 * An optional bean to configure the ZooKeeperConnection. XD by default does not provide this bean but it may be
	 * added via an extension. This is also effected by the boolean property value ${zk.client.connection.configured}
	 * which if set, defers the start of the ZooKeeper connection until now.
	 */
//	@Autowired(required = false)
//	ZooKeeperConnectionConfigurer zooKeeperConnectionConfigurer;

	@Bean
	public ApplicationListener<?> xdInitializer(ApplicationContext context) {
		XdConfigLoggingInitializer delegate = new XdConfigLoggingInitializer(true);
		delegate.setEnvironment(context.getEnvironment());
		return new SourceFilteringListener(context, delegate);
	}

//	@Bean(name = "moduleDeployer")
//	public ModuleDeployer moduleDeployer() {
//		return new ModuleDeployer();
//	}
//
//	@Bean
//	public ContainerRegistrar containerRegistrar() {
//		if (zooKeeperConnectionConfigurer != null) {
//			zooKeeperConnectionConfigurer.configureZooKeeperConnection(zooKeeperConnection);
//			zooKeeperConnection.start();
//		}
//
//		StreamFactory streamFactory = new StreamFactory(streamDefinitionRepository, moduleDefinitionRepository,
//				moduleOptionsMetadataResolver);
//
//		JobFactory jobFactory = new JobFactory(jobDefinitionRepository, moduleDefinitionRepository,
//				moduleOptionsMetadataResolver);
//
//		return new ContainerRegistrar(zooKeeperConnection, containerAttributes,
//				containerRepository,
//				streamFactory,
//				jobFactory,
//				moduleOptionsMetadataResolver,
//				moduleDeployer);
//	}
}
