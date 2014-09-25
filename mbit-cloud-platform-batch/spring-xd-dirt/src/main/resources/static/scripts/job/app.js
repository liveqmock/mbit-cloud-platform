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

/**
 * Definition of XD jobs admin app module.
 *
 * @author Gunnar Hillert
 * @author Ilayaperumal Gopinathan
 */
define([
  'angular',
  'uiRouter',
  'ngResource',
  'cgBusy',
  'ngGrowl',
  'ngAnimate',
  'angularHighlightjs',
  './controllers',
  './services',
  '../directives',
  '../filters',
  '../shared/services',
  '../shared/interceptors'
], function (angular) {
  'use strict';
  return angular.module('xdJobsAdmin', [
    'xdJobsAdmin.services',
    'xdJobsAdmin.controllers',
    'xdShared.services',
    'xdShared.interceptors',
    'xdAdmin.directives',
    'xdAdmin.filters',
    'ui.router',
    'ngResource',
    'ngAnimate',
    'cgBusy',
    'hljs',
    'angular-growl'
  ]);
});
