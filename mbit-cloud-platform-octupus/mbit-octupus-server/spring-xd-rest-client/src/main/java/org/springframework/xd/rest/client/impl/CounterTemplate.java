/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.xd.rest.client.impl;

import org.springframework.hateoas.PagedResources;
import org.springframework.xd.rest.client.CounterOperations;
import org.springframework.xd.rest.domain.metrics.CounterResource;
import org.springframework.xd.rest.domain.metrics.MetricResource;

/**
 * Implements the {@link CounterOperations} part of the API.
 * 
 * @author Eric Bottard
 */
public class CounterTemplate extends AbstractTemplate implements CounterOperations {

	public CounterTemplate(AbstractTemplate abstractTemplate) {
		super(abstractTemplate);
	}

	@Override
	public CounterResource retrieve(String name) {
		String url = resources.get("counters").toString() + "/{name}";
		return restTemplate.getForObject(url, CounterResource.class, name);
	}

	@Override
	public PagedResources<MetricResource> list() {
		String url = resources.get("counters").toString() + "?size=10000";
		return restTemplate.getForObject(url, MetricResource.Page.class);
	}

	@Override
	public void delete(String name) {
		String url = resources.get("counters").toString() + "/{name}";
		restTemplate.delete(url, name);
	}

}
