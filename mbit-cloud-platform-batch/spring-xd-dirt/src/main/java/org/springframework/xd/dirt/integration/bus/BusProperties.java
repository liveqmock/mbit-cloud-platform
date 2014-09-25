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

package org.springframework.xd.dirt.integration.bus;


/**
 * Common bus properties.
 * 
 * @author Gary Russell
 */
public interface BusProperties {

	/**
	 * The retry back off initial interval.
	 */
	public static final String BACK_OFF_INITIAL_INTERVAL = "backOffInitialInterval";

	/**
	 * The retry back off max interval.
	 */
	public static final String BACK_OFF_MAX_INTERVAL = "backOffMaxInterval";

	/**
	 * The retry back off multiplier.
	 */
	public static final String BACK_OFF_MULTIPLIER = "backOffMultiplier";

	/**
	 * The minimum number of concurrent deliveries.
	 */
	public static final String CONCURRENCY = "concurrency";

	/**
	 * The maximum delivery attempts when a delivery fails.
	 */
	public static final String MAX_ATTEMPTS = "maxAttempts";

	/**
	 * The maximum number of concurrent deliveries.
	 */
	public static final String MAX_CONCURRENCY = "maxConcurrency";

	/**
	 * The partition count.
	 */
	public static final String PARTITION_COUNT = "partitionCount";

	/**
	 * The consumer's partition index.
	 */
	public static final String PARTITION_INDEX = "partitionIndex";

	/**
	 * The partition key expression.
	 */
	public static final String PARTITION_KEY_EXPRESSION = "partitionKeyExpression";

	/**
	 * The partition key class.
	 */
	public static final String PARTITION_KEY_EXTRACTOR_CLASS = "partitionKeyExtractorClass";

	/**
	 * The partition selector class.
	 */
	public static final String PARTITION_SELECTOR_CLASS = "partitionSelectorClass";

	/**
	 * The partition selector expression.
	 */
	public static final String PARTITION_SELECTOR_EXPRESSION = "partitionSelectorExpression";

	/**
	 * If true, the bus will attempt to create a direct binding between the producer and consumer. 
	 */
	public static final String DIRECT_BINDING_ALLOWED = "directBindingAllowed";

}
