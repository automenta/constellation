/*
 * Constellation - An open source and standard compliant SDI
 *
 *     http://www.constellation-sdi.org
 *
 *     Copyright 2014 Geomatys
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

angular.module('cstl-sensor-import', ['cstl-restapi', 'cstl-services', 'ui.bootstrap.modal'])

    .controller('SensorAddModalController', function ($rootScope, $scope, $modalInstance, sensor, Growl, $cookieStore, cfpLoadingBar) {
        $scope.close = function() {
            $modalInstance.dismiss('close');
        };

        $scope.uploadData = function() {
            var $form = $('#uploadSensor');

            var formData = new FormData($form[0]);

            $.ajax({
                headers: {
                 'X-Auth-Token': $rootScope.authToken
                },
                url: $cookieStore.get('cstlUrl') + "api/1/sensor/upload",
                type: 'POST',
                data: formData,
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                beforeSend: function(){
                    cfpLoadingBar.start();
                    cfpLoadingBar.inc();
                },
                success: function (data) {
                    Growl('success','Success','Sensor correctly imported');
                    $modalInstance.close();
                    cfpLoadingBar.complete();
                },
                error: function (data){
                    Growl('error','Error','Unable to import sensor');
                    $modalInstance.dismiss('close');
                    cfpLoadingBar.complete();
                }
            });
        };
    });