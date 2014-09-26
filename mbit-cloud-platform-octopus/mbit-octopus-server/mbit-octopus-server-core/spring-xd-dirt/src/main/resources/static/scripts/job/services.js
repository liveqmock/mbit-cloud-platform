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
 * Definition of xdAdmin services.
 *
 * @author Gunnar Hillert
 * @author Ilayaperumal Gopinathan
 */
define(['angular'], function (angular) {
  'use strict';

  return angular.module('xdJobsAdmin.services', [])
      .factory('JobDefinitions', function ($resource, $rootScope, $log, $http) {
        return {
          getSingleJobDefinition: function (jobname) {
            $log.info('Getting single job definiton for job named ' + jobname);
            return $http({
              method: 'GET',
              url: $rootScope.xdAdminServerUrl + '/jobs/definitions/' + jobname
            });
          },
          getAllJobDefinitions: function () {
            $log.info('Getting all job definitions.');
            return $resource($rootScope.xdAdminServerUrl + '/jobs/definitions.json', {}, {
              query: {
                method: 'GET',
                isArray: true
              }
            }).get();
          }
        };
      })
      .factory('JobModules', function ($resource, $rootScope) {
        return $resource($rootScope.xdAdminServerUrl + '/modules.json?type=job', {}, {
          query: {
            method: 'GET',
            isArray: true
          }
        }).query();
      })
      .factory('ModuleMetaData', function ($resource, $log, $rootScope) {
        return {
          getModuleMetaDataForJob: function (jobName) {
              $log.info('Getting ModuleMetaData for job ' + jobName);
              return $resource($rootScope.xdAdminServerUrl + '/runtime/modules',
              {'jobname' : jobName}, {
                getModuleMetaDataForJob: {
                  method: 'GET',
                  isArray: true
                }
              }).getModuleMetaDataForJob();
            }
        };
      })
      .factory('JobModuleService', function ($resource, $http, $log, $rootScope) {
        return {
          getAllModules: function () {
            $log.info('Getting all job modules.');
            return $resource($rootScope.xdAdminServerUrl + '/modules.json', { 'type': 'job' }).get();
          },
          getSingleModule: function (moduleName) {
            $log.info('Getting details for module ' + moduleName);
            return $resource($rootScope.xdAdminServerUrl + '/modules/job/' + moduleName + '.json').get();
          },
          createDefinition: function (name, definition, deploy) {
            $log.info('Creating definition ' + definition);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/definitions', {}, {
              createDefinition: {
                method: 'POST',
                params: {
                  name: name,
                  definition: definition,
                  deploy: deploy
                }
              }
            }).createDefinition();
          },
          getModuleDefinition: function (moduleName) {
            $log.info('Getting module definition file for module ' + moduleName);
            return $http({
              method: 'GET',
              url: $rootScope.xdAdminServerUrl + '/modules/job/' + moduleName + '/definition'
            });
          }
        };
      })
      .factory('JobDefinitionService', function ($resource, $log, $rootScope) {
        return {
          deploy: function (jobDefinition, properties) {
            $log.info('Deploy Job ' + jobDefinition.name);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/deployments/:jobDefinitionName',
            {
              jobDefinitionName: jobDefinition.name
            },
            {
              deploy: {
                method: 'POST',
                params: {
                  properties: properties
                }
              }
            }).deploy();
          },
          undeploy: function (jobDefinition) {
            $log.info('Undeploy Job ' + jobDefinition.name);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/deployments/' + jobDefinition.name, null, {
              undeploy: { method: 'DELETE' }
            }).undeploy();
          },
          destroy: function (jobDefinition) {
            $log.info('Undeploy Job ' + jobDefinition.name);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/definitions/' + jobDefinition.name, null, {
              destroy: { method: 'DELETE' }
            }).destroy();
          }
        };
      })
      .factory('JobDeployments', function ($resource, $rootScope) {
        return $resource($rootScope.xdAdminServerUrl + '/jobs/configurations.json', {}, {
          getArray: {method: 'GET', isArray: true}
        });
      })
      .factory('JobExecutions', function ($resource, $rootScope, $log) {
        return {
          getArray: function () {
            $log.info('Get Job Executions ');
            return $resource($rootScope.xdAdminServerUrl + '/jobs/executions', {}, {
              getArray: {method: 'GET', isArray: true}
            }).getArray();
          },
          getSingleJobExecution: function (jobExecutionId) {
            $log.info('Getting details for Job Execution with Id ' + jobExecutionId);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/executions/' + jobExecutionId + '.json').get();
          },
          restart: function (jobExecution) {
            $log.info('Restart Job Execution' + jobExecution.executionId);
            return $resource($rootScope.xdAdminServerUrl + '/jobs/executions/' + jobExecution.executionId, { 'restart': true }, {
              restart: { method: 'PUT' }
            }).restart();
          },
          stop: function (jobExecution) {
              $log.info('Stop Job Execution' + jobExecution.executionId);
              return $resource($rootScope.xdAdminServerUrl + '/jobs/executions/' + jobExecution.executionId, { 'stop': true }, {
                stop: { method: 'PUT' }
              }).stop();
            }
        };
      })
      .factory('StepExecutions', function ($resource, $rootScope, $log) {
        return {
          getSingleStepExecution: function (jobExecutionId, stepExecutionId) {
            $log.info('Getting details for Step Execution with Id ' + stepExecutionId + '(Job Execution Id ' + jobExecutionId + ')');
            return $resource($rootScope.xdAdminServerUrl + '/jobs/executions/' + jobExecutionId +  '/steps/' + stepExecutionId + '.json').get();
          },
          getStepExecutionProgress: function (jobExecutionId, stepExecutionId) {
            $log.info('Getting progress details for Step Execution with Id ' + stepExecutionId + '(Job Execution Id ' + jobExecutionId + ')');
            return $resource($rootScope.xdAdminServerUrl + '/jobs/executions/' + jobExecutionId +  '/steps/' + stepExecutionId + '/progress.json').get();
          }
        };
      })
      .factory('JobLaunchService', function ($resource, growl, $rootScope) {
        return {
          convertToJsonAndSend: function (jobLaunchRequest) {
            var jsonData = {};
            jobLaunchRequest.jobParameters.forEach(function (jobParameter) {

              var key = jobParameter.key;
              var value = jobParameter.value;
              var isIdentifying = jobParameter.isIdentifying;
              var dataType = jobParameter.type;
              var dataTypeToUse = '';

              if (typeof dataType !== 'undefined') {
                dataTypeToUse = '(' + dataType + ')';
              }

              if (isIdentifying) {
                jsonData['+' + key + dataTypeToUse] = value;
              }
              else {
                jsonData['-' + key + dataTypeToUse] = value;
              }
            });

            var jsonDataAsString = JSON.stringify(jsonData);

            console.log(jsonDataAsString);

            this.launch(jobLaunchRequest.jobName, jsonDataAsString);
          },
          launch: function (jobName, jsonDataAsString) {
            console.log('Do actual Launch...');
            $resource($rootScope.xdAdminServerUrl + '/jobs/executions', { 'jobname': jobName, 'jobParameters': jsonDataAsString }, {
              launch: { method: 'POST' }
            }).launch().$promise.then(
                function () {
                  growl.addSuccessMessage('Job ' + jobName + ' launched.');
                },
                function (data) {
                  console.error(data);
                  growl.addErrorMessage('Yikes, something bad happened while launching job ' + jobName);
                  growl.addErrorMessage(data.data[0].message);
                }
            );
          }
        };
      })
      .factory('JobScheduleService', function ($rootScope, $resource, growl) {
        return {
          scheduleJob: function (jobScheduleRequest) {

            var streamDefinition = 'trigger  '+ jobScheduleRequest.triggerOption + ' > queue:job:' + jobScheduleRequest.jobName;
            $resource($rootScope.xdAdminServerUrl + '/streams/definitions/',{},
                { createSteam: { method: 'POST' , params: { 'name': jobScheduleRequest.schedulerName, 'definition': streamDefinition, 'deploy': 'true' } }
                }).createSteam().$promise.then(
                function () {
                  growl.addSuccessMessage('Scheduler stream deployed');
                },
                function (data) {
                  console.error(data);
                  growl.addErrorMessage('Yikes, something bad happened while deploying scheduler stream');
                  growl.addErrorMessage(data.data[0].message);
                }
            );
          }

        };
      });
});
