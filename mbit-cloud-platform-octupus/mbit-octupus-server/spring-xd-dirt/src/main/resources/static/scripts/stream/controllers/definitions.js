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
 * Stream Definition controller
 *
 * @author Ilayaperumal Gopinathan
 */
define([], function () {
  'use strict';
  return ['$scope', 'StreamService', 'XDUtils', '$timeout', '$rootScope',
    function ($scope, streamService, utils, $timeout, $rootScope) {

      (function loadStreamDefinitions() {
        streamService.getDefinitions().get(
            function (result) {
              utils.$log.info(result);
              $scope.streamDefinitions = result.content;
              var getStreamDefinitions = $timeout(loadStreamDefinitions, $rootScope.pageRefreshTime);
              $scope.$on('$destroy', function(){
                $timeout.cancel(getStreamDefinitions);
              });
            }
        );
      })();
      $scope.deployStream = function (streamDefinition) {
        utils.$log.info('Deploying Stream ' + streamDefinition.name);
        utils.$log.info(streamService);
        streamService.deploy(streamDefinition).$promise.then(
            function () {
              utils.growl.addSuccessMessage('Deployment Request Sent.');
            },
            function () {
              utils.growl.addErrorMessage('Error Deploying Stream.');
            }
        );
      };
      $scope.undeployStream = function (streamDefinition) {
        utils.$log.info('Undeploying Stream ' + streamDefinition.name);
        utils.$log.info(streamService);
        streamService.undeploy(streamDefinition).$promise.then(
            function () {
              utils.growl.addSuccessMessage('Undeployment Request Sent.');
            },
            function () {
              utils.growl.addErrorMessage('Error Undeploying Stream.');
            }
        );
      };
      $scope.clickModal = function (streamDefinition) {
        $scope.destroyItem = streamDefinition;
      };
      $scope.destroyStream = function (streamDefinition) {
        utils.$log.info('Destroying Stream ' + streamDefinition.name);
        utils.$log.info(streamService);

        streamService.destroy(streamDefinition).$promise.then(
            function () {
              utils.growl.addSuccessMessage('Destroy Request Sent.');
              streamDefinition.inactive = true;
              $scope.closeModal();
            },
            function () {
              utils.growl.addErrorMessage('Error Destroying Stream.');
              $scope.closeModal();
            }
        );
      };

      $scope.$on('$destroy', function() {
        // Remove deployment status tooltip
        angular.element('.popover').hide();
      });

    }];
});
