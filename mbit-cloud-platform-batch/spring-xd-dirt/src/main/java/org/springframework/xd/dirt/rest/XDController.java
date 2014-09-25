/*
 * Copyright 2013 the original author or authors.
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
 * limitations under the License.
 */

package org.springframework.xd.dirt.rest;

import java.util.Collections;
import java.util.Iterator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.xd.dirt.core.BaseDefinition;
import org.springframework.xd.dirt.core.DeploymentUnitStatus;
import org.springframework.xd.dirt.core.ResourceDeployer;
//import org.springframework.xd.dirt.stream.AbstractDeployer;
//import org.springframework.xd.dirt.stream.AbstractInstancePersistingDeployer;
import org.springframework.xd.dirt.stream.BaseInstance;
import org.springframework.xd.dirt.stream.NoSuchDefinitionException;
import org.springframework.xd.dirt.util.DeploymentPropertiesUtility;
import org.springframework.xd.rest.domain.DeployableResource;
import org.springframework.xd.rest.domain.NamedResource;

/**
 * Base Class for XD Controllers.
 *
 * @param <D> the kind of definition entity this controller deals with
 * @param <A> a resource assembler that knows how to build Ts out of Ds
 * @param <R> the resource class for D
 *
 * @author Glenn Renfro
 * @author Ilayaperumal Gopinathan
 * @author David Turanski
 */

public abstract class XDController<D extends BaseDefinition, A extends ResourceAssemblerSupport<D, R>, R extends NamedResource> {

	private ResourceDeployer<D> deployer;

	private A resourceAssemblerSupport;

	/**
	 * Data holder class for controlling how the list methods should behave.
	 *
	 * @author Eric Bottard
	 */
	public static class QueryOptions {

		public static final QueryOptions NONE = new QueryOptions();

		private boolean deployments;

		/**
		 * Whether to also return deployment status when listing definitions.
		 */
		public boolean isDeployments() {
			return deployments;
		}

		public void setDeployments(boolean deployments) {
			this.deployments = deployments;
		}
	}

//	protected XDController(AbstractDeployer<D> deployer, A resourceAssemblerSupport) {
//		this.deployer = deployer;
//		this.resourceAssemblerSupport = resourceAssemblerSupport;
//	}

	protected ResourceDeployer<D> getDeployer() {
		return deployer;
	}

	/**
	 * Request removal of an existing resource definition (stream or job).
	 *
	 * @param name the name of an existing definition (required)
	 */
	@RequestMapping(value = "/definitions/{name}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable("name") String name) {
		deployer.delete(name);
	}

	/**
	 * Request removal of all definitions.
	 */
	@RequestMapping(value = "/definitions", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteAll() {
		deployer.deleteAll();
	}

	/**
	 * Request un-deployment of an existing resource.
	 *
	 * @param name the name of an existing resource (required)
	 */
	@RequestMapping(value = "/deployments/{name}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void undeploy(@PathVariable("name") String name) {
		deployer.undeploy(name);
	}

	/**
	 * Request un-deployment of all resources.
	 */
	@RequestMapping(value = "/deployments", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void undeployAll() {
		deployer.undeployAll();
	}

	/**
	 * Request deployment of an existing definition resource. The definition must exist before deploying and is included 
	 * in the path. A new deployment instance is created.
	 *
	 * @param name the name of an existing definition resource (job or stream) (required)
	 * @param properties the deployment properties for the resource as a comma-delimited list of key=value pairs
	 */
	@RequestMapping(value = "/deployments/{name}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public void deploy(@PathVariable("name") String name, @RequestParam(required = false) String properties) {
		deployer.deploy(name, DeploymentPropertiesUtility.parseDeploymentProperties(properties));
	}

	/**
	 * Retrieve information about a single {@link ResourceSupport}.
	 *
	 * @param name the name of an existing resource (required)
	 */
	@RequestMapping(value = "/definitions/{name}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResourceSupport display(@PathVariable("name") String name) {
		final D definition = deployer.findOne(name);
		if (definition == null) {
			throw new NoSuchDefinitionException(name, "There is no definition named '%s'");
		}
		R resource = resourceAssemblerSupport.toResource(definition);
		return resource; //enhanceWithDeployment(definition, resource);
	}

	/**
	 * List module definitions.
	 */
	// protected and not annotated with @RequestMapping due to the way
	// PagedResourcesAssemblerArgumentResolver works
	// subclasses should override and make public (or delegate)
	protected PagedResources<R> listValues(Pageable pageable, PagedResourcesAssembler<D> assembler) {
		Page<D> page = deployer.findAll(pageable);
		PagedResources<R> result = assembler.toResource(page, resourceAssemblerSupport);
//		if (page.getNumberOfElements() > 0) {
//			enhanceWithDeployments(page, result);
//		}
		return result;
	}

	/**
	 * Queries the deployer about deployed instances and enhances the resources with deployment info. Does nothing if
	 * the operation is not supported.
	 */
//	private void enhanceWithDeployments(Page<D> page, PagedResources<R> result) {
//		if (deployer instanceof AbstractInstancePersistingDeployer) {
//			@SuppressWarnings("unchecked")
//			AbstractInstancePersistingDeployer<D, BaseInstance<D>> ipDeployer = (AbstractInstancePersistingDeployer<D, BaseInstance<D>>) deployer;
//			D first = page.getContent().get(0);
//			D last = page.getContent().get(page.getNumberOfElements() - 1);
//			Iterator<BaseInstance<D>> deployedInstances = ipDeployer.deploymentInfo(first.getName(), last.getName()).iterator();
//			BaseInstance<D> instance = deployedInstances.hasNext() ? deployedInstances.next() : null;
//
//			// There are >= more definitions than there are instances, and they're both sorted
//			for (R definitionResource : result) {
//				String instanceName = (instance != null) ? instance.getDefinition().getName() : null;
//				if (definitionResource.getName().equals(instanceName)) {
//					((DeployableResource) definitionResource).setStatus(instance.getStatus().getState().toString());
//					instance = deployedInstances.hasNext() ? deployedInstances.next() : null;
//				}
//				else {
//					((DeployableResource) definitionResource).setStatus(DeploymentUnitStatus.State.undeployed.toString());
//				}
//			}
//			Assert.state(!deployedInstances.hasNext(), "Not all instances were looked at");
//		}
//	}

	/**
	 * Create a new resource definition.
	 *
	 * @param name The name of the entity to create (required)
	 * @param definition The entity definition, expressed in the XD DSL (required)
	 */
	@RequestMapping(value = "/definitions", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public R save(@RequestParam("name") String name, @RequestParam("definition") String definition,
			@RequestParam(value = "deploy", defaultValue = "true") boolean deploy) {
		final D moduleDefinition = createDefinition(name, definition);
		final D savedModuleDefinition = deployer.save(moduleDefinition);
		if (deploy) {
			deployer.deploy(name, Collections.<String, String> emptyMap());
		}
		final R result = resourceAssemblerSupport.toResource(savedModuleDefinition);
		return result;
	}

//	private ResourceSupport enhanceWithDeployment(D definition, R resource) {
//		if (deployer instanceof AbstractInstancePersistingDeployer) {
//			@SuppressWarnings("unchecked")
//			AbstractInstancePersistingDeployer<D, BaseInstance<D>> ipDeployer = (AbstractInstancePersistingDeployer<D, BaseInstance<D>>) deployer;
//			BaseInstance<D> deployedInstance = ipDeployer.deploymentInfo(definition.getName());
//			String status = (deployedInstance != null) ? deployedInstance.getStatus().getState().toString()
//					: DeploymentUnitStatus.State.undeployed.toString();
//			((DeployableResource) resource).setStatus(status);
//		}
//		return resource;
//	}

	protected abstract D createDefinition(String name, String definition);


}
