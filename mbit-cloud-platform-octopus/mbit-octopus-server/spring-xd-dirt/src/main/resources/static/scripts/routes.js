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
 * XD UI Routes configuration
 *
 * @author Gunnar Hillert
 * @author Ilayaperumal Gopinathan
 */
define(['./app'], function (xdAdmin) {
  'use strict';
  xdAdmin.config(function ($stateProvider, $urlRouterProvider, $httpProvider, hljsServiceProvider, growlProvider) {
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.interceptors.push('httpErrorInterceptor');
    $urlRouterProvider.otherwise('/jobs/definitions');

    hljsServiceProvider.setOptions({
      tabReplace: '  '
    });

    growlProvider.globalTimeToLive(5000);

    var jobTemplatesPath = 'scripts/job/views',
        streamTemplatesPath = 'scripts/stream/views',
        authTemplatesPath = 'scripts/auth/views',
        sharedTemplatesPath = 'scripts/shared/views';

    $stateProvider.state('home', {
      url : '/',
      abstract:true,
      templateUrl : sharedTemplatesPath + '/home.html'
    })
    .state('home.jobs', {
      abstract:true,
      template: '<ui-view/>',
      data:{
        authenticate: true
      }
    })
    .state('home.streams', {
      abstract:true,
      template: '<ui-view/>',
      data:{
        authenticate: true
      }
    })
    .state('home.jobs.tabs', {
      url : 'jobs',
      abstract:true,
      data:{
        authenticate: true
      },
      templateUrl : jobTemplatesPath + '/jobs.html'
    })
    .state('home.about', {
      url : 'about',
      templateUrl : sharedTemplatesPath + '/about.html',
      data:{
        authenticate: false
      }
    })
    .state('login', {
      url : '/login',
      controller: 'LoginController',
      templateUrl : authTemplatesPath + '/login.html',
      data:{
        authenticate: false
      }
    })
    .state('logout', {
      url : '/logout',
      controller: 'LogoutController',
      templateUrl : authTemplatesPath + '/login.html',
      data:{
        authenticate: true
      }
    })
    .state('home.streams.tabs', {
      url : 'streams',
      abstract:true,
      data:{
        authenticate: true
      },
      templateUrl : streamTemplatesPath + '/streams.html'
    })
    .state('home.streams.tabs.definitions', {
      url : '/definitions',
      templateUrl : streamTemplatesPath + '/definitions.html',
      controller: 'StreamsDefinitionsController'
    })
    .state('home.jobs.tabs.modules', {
      url : '/modules',
      templateUrl : jobTemplatesPath + '/modules.html',
      controller: 'ModuleController'
    })
    .state('home.jobs.tabs.definitions', {
      url : '/definitions',
      templateUrl : jobTemplatesPath + '/definitions.html',
      controller: 'JobDefinitionsController'
    })
    .state('home.jobs.deployjob', {
      url : 'jobs/definitions/{definitionName}/deploy',
      templateUrl : jobTemplatesPath + '/definition-deploy.html',
      controller: 'DefinitionDeployController',
      data:{
        authenticate: true
      }
    })
    .state('home.jobs.tabs.deployments', {
      url : '/deployments',
      templateUrl : jobTemplatesPath + '/deployments.html',
      controller: 'JobDeploymentsController'
    })
    .state('home.jobs.deploymentdetails', {
      url : 'jobs/deployments/{jobName}',
      templateUrl : jobTemplatesPath + '/deployment-details.html',
      controller: 'JobDeploymentDetailsController',
      data:{
        authenticate: true
      }
    })
    .state('home.jobs.tabs.executions', {
      url : '/executions',
      templateUrl : jobTemplatesPath + '/executions.html',
      controller: 'JobExecutionsController'
    })
    .state('home.jobs.executiondetails', {
      url : 'jobs/executions/{executionId}',
      templateUrl : jobTemplatesPath + '/execution-details.html',
      controller: 'JobExecutionDetailsController'
    })
    .state('home.jobs.stepexecutiondetails', {
      url : 'jobs/executions/{executionId}/{stepExecutionId}',
      templateUrl : jobTemplatesPath + '/stepexecution-details.html',
      controller: 'StepExecutionDetailsController'
    })
    .state('home.jobs.stepexecutionprogress', {
      url : 'jobs/executions/{executionId}/{stepExecutionId}/progress',
      templateUrl : jobTemplatesPath + '/stepexecution-progress.html',
      controller: 'StepExecutionProgressController'
    })
    .state('home.jobs.tabs.deployments.launch', {
      url : '/launch/{jobName}',
      templateUrl : jobTemplatesPath + '/launch.html',
      controller: 'JobLaunchController'
    })
    .state('home.jobs.tabs.deployments.schedule', {
      url : '/schedule/{jobName}',
      templateUrl : jobTemplatesPath + '/schedule.html',
      controller: 'JobScheduleController'
    })
    .state('home.jobs.moduledetails', {
      url : 'jobs/modules/{moduleName}',
      templateUrl : jobTemplatesPath + '/module-details.html',
      controller: 'ModuleDetailsController',
      data:{
        title: 'Module Details',
        authenticate: true
      }
    })
    .state('home.jobs.modulecreatedefinition', {
      url : 'jobs/modules/{moduleName}/create-definition',
      templateUrl : jobTemplatesPath + '/module-create-definition.html',
      controller: 'ModuleCreateDefinitionController',
      data:{
        title: 'Module Create Definition',
        authenticate: true
      }
    });
  });
  xdAdmin.run(function ($rootScope, $state, $stateParams, userService, $log) {
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;
    $rootScope.xdAdminServerUrl = window.location.protocol + '//' + window.location.host;
    $rootScope.authenticationEnabled = false;
    $rootScope.user = userService;
    $rootScope.pageRefreshTime = 5000;

    $rootScope.$on('$stateChangeStart', function(event, toState) {
        $log.info('Need to authenticate? ' + toState.data.authenticate);
        if ($rootScope.authenticationEnabled && toState.data.authenticate && !userService.isAuthenticated){
          // User is not authenticated
          $state.transitionTo('login');
          event.preventDefault();
        }
      });
  });
  return xdAdmin;
});
