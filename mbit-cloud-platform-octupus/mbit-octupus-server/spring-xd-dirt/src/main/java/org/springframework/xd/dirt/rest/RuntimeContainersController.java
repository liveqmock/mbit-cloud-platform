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

package org.springframework.xd.dirt.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.xd.dirt.cluster.Container;
import org.springframework.xd.dirt.container.store.ContainerRepository;
import org.springframework.xd.rest.domain.ContainerAttributesResource;
import org.springframework.xd.rest.domain.ContainerResource;

/**
 * Handles interaction with the runtime containers/and its modules.
 * 
 * @author Ilayaperumal Gopinathan
 * @author Mark Fisher
 */
@Controller
@RequestMapping("/runtime/containers")
@ExposesResourceFor(ContainerAttributesResource.class)
public class RuntimeContainersController {

	private ContainerRepository containerRepository;

	private ResourceAssemblerSupport<Container, ContainerResource> containerResourceAssemblerSupport;

	@Autowired
	public RuntimeContainersController(ContainerRepository containerRepository) {
		this.containerRepository = containerRepository;
		containerResourceAssemblerSupport = new ContainerResourceAssembler();
	}

	/**
	 * List all the available containers
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public PagedResources<ContainerResource> list(Pageable pageable,
			PagedResourcesAssembler<Container> assembler) {
		Page<Container> page = this.containerRepository.findAll(pageable);
		PagedResources<ContainerResource> result = assembler.toResource(page,
				containerResourceAssemblerSupport);
		return result;
	}

}
