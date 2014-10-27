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

angular.module('cstl-data-dashboard', ['ngCookies', 'cstl-restapi', 'cstl-services', 'ui.bootstrap.modal'])

    .controller('DataController', function($scope, $routeParams, $location, Dashboard, webService, dataListing, datasetListing, DomainResource,
                                            provider, $window, style, textService, $modal, Growl, StyleSharedService, $cookies, cfpLoadingBar) {
        /**
         * To fix angular bug with nested scope.
         */
        $scope.wrap = {};
        $scope.wrap.matchExactly = true;

        $scope.dataCtrl = {
            cstlUrl : $cookies.cstlUrl,
            cstlSessionId : $cookies.cstlSessionId,
            domainId : $cookies.cstlActiveDomainId,
            advancedDataSearch : false,
            advancedMetadataSearch : false,
            searchTerm : "",
            searchMetadataTerm : "",
            hideScroll : true,
            currentTab : $routeParams.tabindex || 'tabdata',
            alphaPattern : /^([0-9A-Za-z\u00C0-\u017F\*\?]+|\s)*$/,
            published : null,
            observation : null,
            smallMode : false,
            selectedDataSetChild : null
        };
        $scope.search = {};
        $scope.searchMD = {};

        /**
         * Select appropriate tab 'tabdata' or 'tabmetadata'.
         * @param item
         */
        $scope.selectTab = function(item) {
            $scope.dataCtrl.currentTab = item;
        };

        /**
         * Toggle selection of dataSet child item.
         * @param item
         */
        $scope.selectDataSetChild = function(item,parent) {
            if(parent && $scope.selectedDS && parent.id !== $scope.selectedDS.id){
                $scope.selectedDS = null;
            }
            if ($scope.dataCtrl.selectedDataSetChild && item && $scope.dataCtrl.selectedDataSetChild.Id === item.Id) {
                $scope.dataCtrl.selectedDataSetChild = null;
            } else {
                $scope.dataCtrl.selectedDataSetChild = item;
            }
        };

        /**
         * Toggle advanced data search view panel.
         */
        $scope.toggleAdvancedDataSearch = function(){
            if ($scope.dataCtrl.advancedDataSearch){
                $scope.dataCtrl.advancedDataSearch = false;
            }  else {
                $scope.dataCtrl.advancedDataSearch = true;
                $scope.dataCtrl.searchTerm ="";
            }
        };

        /**
         * Toggle advanced metadata search view panel.
         */
        $scope.toggleAdvancedMDSearch = function(){
            if ($scope.dataCtrl.advancedMetadataSearch){
                $scope.dataCtrl.advancedMetadataSearch = false;
            }  else {
                $scope.dataCtrl.advancedMetadataSearch = true;
                $scope.dataCtrl.searchMetadataTerm ="";
            }
        };

        /**
         * Clean advanced search inputs.
         */
        $scope.resetSearch = function(){
            $scope.search = {};
        };

        /**
         * Clean advanced metadata search inputs.
         */
        $scope.resetSearchMD = function(){
            $scope.searchMD = {};
        };

        /**
         * Check the validity against the pattern and display growl error for given validity.
         * @param isInvalid
         */
        $scope.checkIsValid = function(isInvalid){
            if (isInvalid){
                Growl('error','Error','Invalid Chars');
            }
        };

        /**
         * Binding action for search button in data dashboard.
         * the result is stored with Dashboard service.
         */
        $scope.callSearch = function(){
            $scope.wrap.filtertext='';
            if ($scope.dataCtrl.searchTerm){
                datasetListing.findDataset({values: {'search': $scope.dataCtrl.searchTerm+'*'}},
                    function(response) {
                        Dashboard($scope, response, true);
                    },
                    function(response){
                        console.error(response);
                        Growl('error','Error','Search failed:'+ response.data);
                    }
                );
            }else{
                if (!$.isEmptyObject($scope.search)){
                    var searchString = "";
                    if ($scope.search.title){
                        searchString += " title:"+$scope.search.title+'*';
                    }
                    if ($scope.search.abstract){
                        searchString += " abstract:"+$scope.search.abstract+'*';
                    }
                    if ($scope.search.keywords){
                        searchString += " keywords:"+$scope.search.keywords+'*';
                    }
                    if ($scope.search.topic){
                        searchString += " topic:"+$scope.search.topic+'*';
                    }
                    if ($scope.search.data){
                        searchString += " data:"+$scope.search.data+'*';
                    }
                    if ($scope.search.level){
                        searchString += " level:"+$scope.search.level+'*';
                    }
                    if ($scope.search.area){
                        searchString += " area:"+$scope.search.area+'*';
                    }
                    datasetListing.findDataset({values: {'search': searchString}},function(response) {
                        Dashboard($scope, response, true);
                    }, function(response){
                        console.error(response);
                        Growl('error','Error','Search failed:'+ response.data);
                    });
                } else {
                    datasetListing.listAll({}, function(response) {
                        Dashboard($scope, response, true);
                    });
                }
            }
        };

        /**
         * Binding action for search button in metadata dashboard.
         * the result is stored with Dashboard service.
         */
        $scope.callSearchMDForTerm = function(term){
            $scope.wrap.filtertext='';
            if (term){
                datasetListing.findDataset({values: {'search': term+'*'}},
                    function(response) {//success
                        Dashboard($scope, response, true);
                    },
                    function(response){//error
                        console.error(response);
                        Growl('error','Error','Search failed:'+ response.data);
                    });
            }else{
                if (!$.isEmptyObject($scope.searchMD)){
                    var searchString = "";
                    if ($scope.searchMD.title){
                        searchString += " title:"+$scope.searchMD.title+'*';
                    }
                    if ($scope.searchMD.abstract){
                        searchString += " abstract:"+$scope.searchMD.abstract+'*';
                    }
                    if ($scope.searchMD.keywords){
                        searchString += " keywords:"+$scope.searchMD.keywords+'*';
                    }
                    if ($scope.searchMD.topic){
                        searchString += " topic:"+$scope.searchMD.topic+'*';
                    }
                    if ($scope.searchMD.data){
                        searchString += " data:"+$scope.searchMD.data+'*';
                    }
                    if ($scope.searchMD.level){
                        searchString += " level:"+$scope.searchMD.level+'*';
                    }
                    if ($scope.searchMD.area){
                        searchString += " area:"+$scope.searchMD.area+'*';
                    }
                    datasetListing.findDataset({values: {'search': searchString}},
                        function(response) {//success
                            Dashboard($scope, response, true);
                        }, function(response){ //error
                            console.error(response);
                            Growl('error','Error','Search failed:'+ response.data);
                        });
                } else {
                    datasetListing.listAll({}, function(response) {
                        Dashboard($scope, response, true);
                    });
                }
            }
        };
        $scope.callSearchMD = function(){
            $scope.callSearchMDForTerm($scope.dataCtrl.searchMetadataTerm);
        };

        /**
         * main function of dashboard that loads the list of objects from server.
         */
        $scope.init = function() {
            $scope.wrap.fullList = [];
            $scope.wrap.filtertext='';
            $scope.wrap.filtertype = undefined;
            if($scope.dataCtrl.currentTab === 'tabdata'){
                $scope.dataCtrl.searchTerm="";
                datasetListing.listAll({}, function(response) {//success
                    Dashboard($scope, response, true);
                    $scope.wrap.ordertype = "Name";
                }, function() {//error
                    Growl('error','Error','Unable to load list of data!');
                });
            }else if($scope.dataCtrl.currentTab === 'tabmetadata') {
                $scope.dataCtrl.searchMetadataTerm="";
                datasetListing.listAll({}, function(response){//success
                    Dashboard($scope, response, true);
                    $scope.wrap.ordertype = "Name";
                    if($scope.selectedDS || $scope.dataCtrl.selectedDataSetChild){
                        //then we need to highlight the metadata associated in MD dashboard
                        var term = $scope.selectedDS?$scope.selectedDS.Name:$scope.dataCtrl.selectedDataSetChild.Name;
                        $scope.wrap.filtertext = term;
                    }else {
                        //otherwise reset selection
                        $scope.dataCtrl.selectedDataSetChild = null;
                        $scope.selectedDS = null;
                    }
                }, function(response){//error
                    Growl('error','Error','Unable to load list of dataset!');
                });
            }
            //display button that allow to scroll to top of the page from a certain height.
            angular.element($window).bind("scroll", function() {
                $scope.dataCtrl.hideScroll = this.pageYOffset < 220;
                $scope.$apply();
            });
        };

        /**
         * Called only when import wizard finished.
         */
        $scope.initAfterImport = function() {
            $scope.wrap.fullList = [];
            $scope.wrap.filtertext='';
            $scope.wrap.filtertype = undefined;
            $scope.dataCtrl.searchTerm="";
            $scope.dataCtrl.searchMetadataTerm="";
            $scope.dataCtrl.selectedDataSetChild = null;
            $scope.selectedDS = null;
            datasetListing.listAll({}, function(response) {//success
                Dashboard($scope, response, true);
                $scope.wrap.ordertype = "Date";
                $scope.wrap.orderreverse=true;
            }, function() {//error
                Growl('error','Error','Unable to load list of dataset!');
            });
        };

        /**
         * Reset filters for dashboard
         */
        $scope.resetFilters = function(){
            $scope.wrap.filtertext='';
            $scope.wrap.filtertype = undefined;
            if($scope.dataCtrl.currentTab === 'tabdata'){
                $scope.dataCtrl.searchTerm="";
                $scope.dataCtrl.selectedDataSetChild = null;
                $scope.selectedDS = null;
                datasetListing.listAll({}, function(response) {//success
                    Dashboard($scope, response, true);
                    $scope.wrap.ordertype = "Name";
                }, function() {//error
                    Growl('error','Error','Unable to load list of data!');
                });
            }else if($scope.dataCtrl.currentTab === 'tabmetadata') {
                $scope.dataCtrl.searchMetadataTerm="";
                $scope.dataCtrl.selectedDataSetChild = null;
                $scope.selectedDS = null;
                datasetListing.listAll({}, function(response){//success
                    Dashboard($scope, response, true);
                    $scope.wrap.ordertype = "Name";
                }, function(response){//error
                    Growl('error','Error','Unable to load list of dataset!');
                });
            }
        };

        /**
         * Apply filter to show only published data in service depending on given flag.
         * ie: data is linked to services.
         * @param published if true then proceed to show only published data.
         */
        $scope.showPublished = function(published){
            $scope.dataCtrl.published=published;
            dataListing.listPublishedDS({published:published}, function(response) {//success
                Dashboard($scope, response, true);
            }, function() { //error
                Growl('error','Error','Unable to show published data!');
            });
        };

        /**
         * Apply filter to show only sensorable data depending on given flag.
         * ie: data is linked to sensors
         * @param observation if true then proceed to show only sensorable data.
         */
        $scope.showSensorable = function(observation){
            $scope.dataCtrl.observation=observation;
            dataListing.listSensorableDS({observation:observation},
                function(response) {//success
                    Dashboard($scope, response, true);
                }, function() {//error
                    Growl('error','Error','Unable to show sensorable data!');
                });
        };

        /**
         * Returns formatted name of data for given data's provider and data's name.
         * @param providerName given provider name.
         * @param dataName given data name.
         * @returns {*}
         */
        $scope.getDisplayName = function(providerName, dataName) {
            if (providerName === dataName){
                return dataName;
            } else {
                return dataName + ' ( ' + providerName + ' ) ';
            }
        };

        // Map methods
        $scope.showData = function() {
            //clear the map
            if (DataViewer.map) {
                DataViewer.map.setTarget(undefined);
            }
            var viewerData = $('#viewerData');
            viewerData.modal("show");
            viewerData.off('shown.bs.modal');
            viewerData.on('shown.bs.modal', function (e) {
                var layerName;
                if ($scope.dataCtrl.selectedDataSetChild && $scope.dataCtrl.selectedDataSetChild.Namespace) {
                    layerName = '{' + $scope.dataCtrl.selectedDataSetChild.Namespace + '}' + $scope.dataCtrl.selectedDataSetChild.Name;
                } else {
                    layerName = $scope.dataCtrl.selectedDataSetChild.Name;
                }
                var providerId = $scope.dataCtrl.selectedDataSetChild.Provider;
                var layerData;
                if ($scope.dataCtrl.selectedDataSetChild.TargetStyle && $scope.dataCtrl.selectedDataSetChild.TargetStyle.length > 0) {
                    layerData = DataViewer.createLayerWithStyle($scope.dataCtrl.cstlUrl,
                        layerName,
                        providerId,
                        $scope.dataCtrl.selectedDataSetChild.TargetStyle[0].Name);
                } else {
                    layerData = DataViewer.createLayer($scope.dataCtrl.cstlUrl, layerName, providerId);
                }
                //to force the browser cache reloading styled layer.
                layerData.get('params').ts=new Date().getTime();

                //var layerBackground = DataViewer.createLayer($scope.dataCtrl.cstlUrl, "CNTR_BN_60M_2006", "generic_shp");

                //attach event loader in modal map viewer
                layerData.on('precompose',function(){
                    $scope.$apply(function() {
                        window.cfpLoadingBar_parentSelector = '#dataMap';
                        cfpLoadingBar.start();
                        cfpLoadingBar.inc();
                    });
                });
                layerData.on('postcompose',function(){
                    cfpLoadingBar.complete();
                    window.cfpLoadingBar_parentSelector = null;
                });
                DataViewer.layers = [layerData];
                provider.dataDesc({},{values: {'providerId':providerId,'dataId':layerName}},
                    function(response) {//success
                        var bbox = response.boundingBox;
                        if (bbox) {
                            DataViewer.extent = [bbox[0],bbox[1],bbox[2],bbox[3]];
                        }
                        DataViewer.initMap('dataMap');
                    }, function() {//error
                        // failed to find a metadata, just load the full map
                        DataViewer.initMap('dataMap');
                    }
                );
            });
        };

        $scope.deleteData = function() {
            var dlg = $modal.open({
                templateUrl: 'views/modal-confirm.html',
                controller: 'ModalConfirmController',
                resolve: {
                    'keyMsg':function(){return "dialog.message.confirm.delete.data";}
                }
            });
            dlg.result.then(function(cfrm){
                if(cfrm){
                    var layerName = $scope.dataCtrl.selectedDataSetChild.Name;
                    var providerId = $scope.dataCtrl.selectedDataSetChild.Provider;

                    dataListing.hideData({providerid: providerId, dataid: layerName},
                        {value : $scope.dataCtrl.selectedDataSetChild.Namespace},
                        function() {//success
                            Growl('success','Success','Data '+ layerName +' successfully deleted');
                            datasetListing.listAll({}, function(response) {
                                Dashboard($scope, response, true);
                                $scope.dataCtrl.selectedDataSetChild=null;
                            });
                        },
                        function() {//error
                            Growl('error','Error','Data '+ layerName +' deletion failed');
                        }
                    );
                }
            });
        };

        /**
         * Delete selected dataset.
         */
        $scope.deleteDataset = function() {
            var dlg = $modal.open({
                templateUrl: 'views/modal-confirm.html',
                controller: 'ModalConfirmController',
                resolve: {
                    'keyMsg':function(){return "dialog.message.confirm.delete.dataset";}
                }
            });
            dlg.result.then(function(cfrm){
                if(cfrm){
                    datasetListing.deleteDataset({"datasetIdentifier":$scope.selectedDS.Name},function(response){//success
                        Growl('success','Success','Data set '+ $scope.selectedDS.Name +' successfully deleted');
                        datasetListing.listAll({}, function(response) {
                            Dashboard($scope, response, true);
                            $scope.dataCtrl.selectedDataSetChild=null;
                            $scope.selectedDS = null;
                        });
                    },function(response){//error
                        Growl('error','Error','Dataset '+ $scope.selectedDS.Name +' deletion failed');
                    });
                }
            });
        };

        /**
         * Open metadata viewer popup and display metadata
         * in appropriate template depending on data type property.
         * this function is called from data dashboard.
         */
        $scope.displayMetadataFromDD = function() {
            var type = 'import';
            if($scope.dataCtrl.selectedDataSetChild){
                type = $scope.dataCtrl.selectedDataSetChild.Type.toLowerCase();
            }else if($scope.selectedDS && $scope.selectedDS.Type){
                type = $scope.selectedDS.Type.toLowerCase();
            }
            if(type.toLowerCase() === 'coverage'){
                type = 'raster';
            }
            $modal.open({
                templateUrl: 'views/data/modalViewMetadata.html',
                controller: 'ViewMetadataModalController',
                resolve: {
                    'dashboardName':function(){return 'data';},
                    'metadataValues':function(textService){
                        if($scope.dataCtrl.selectedDataSetChild){
                            return textService.metadataJson($scope.dataCtrl.selectedDataSetChild.Provider,
                                                            $scope.dataCtrl.selectedDataSetChild.Name,
                                                            type,true);
                        }else if($scope.selectedDS){
                            return textService.metadataJsonDS($scope.selectedDS.Name,type,true);
                        }
                    }
                }
            });
        };

        /**
         * Open metadata viewer popup and display metadata
         * in appropriate template depending on data type property.
         * this function is called from metadata dashboard.
         */
        $scope.displayMetadataFromMD = function() {
            var type = 'import';
            if($scope.dataCtrl.selectedDataSetChild){
                type = $scope.dataCtrl.selectedDataSetChild.Type.toLowerCase();
            }else if($scope.selectedDS && $scope.selectedDS.Type){
                type = $scope.selectedDS.Type.toLowerCase();
            }
            if(type.toLowerCase() === 'coverage'){
                type = 'raster';
            }
            $modal.open({
                templateUrl: 'views/data/modalViewMetadata.html',
                controller: 'ViewMetadataModalController',
                resolve: {
                    'dashboardName':function(){return 'dataset';},
                    'metadataValues':function(textService){
                        if($scope.dataCtrl.selectedDataSetChild){
                            return textService.metadataJson($scope.dataCtrl.selectedDataSetChild.Provider,
                                $scope.dataCtrl.selectedDataSetChild.Name,
                                type,true);
                        }else if($scope.selectedDS){
                            return textService.metadataJsonDS($scope.selectedDS.Name,type,true);
                        }
                    }
                }
            });
        };

        /**
         * Open metadata editor in modal popup.
         */
        $scope.displayMetadataEditor = function() {
            var type = 'import';
            if($scope.dataCtrl.selectedDataSetChild){
                type = $scope.dataCtrl.selectedDataSetChild.Type.toLowerCase();
            }else if($scope.selectedDS && $scope.selectedDS.Type){
                type = $scope.selectedDS.Type.toLowerCase();
            }
            if(type.toLowerCase() === 'coverage'){
                type = 'raster';
            }
            var template = type;
            if($scope.dataCtrl.selectedDataSetChild){
                openModalEditor($scope.dataCtrl.selectedDataSetChild.Provider,
                                $scope.dataCtrl.selectedDataSetChild.Name,
                                type,template,'csw');
            }else if($scope.selectedDS){
                openModalEditor(null,$scope.selectedDS.Name,type,template,'csw');
            }
        };

        /**
         * Open modal for metadata editor
         * for given provider id, data type and template.
         * @param provider
         * @param identifier
         * @param type
         * @param template
         * @param theme
         */
        function openModalEditor(provider,identifier,type,template,theme){
            $modal.open({
                templateUrl: 'views/data/modalEditMetadata.html',
                controller: 'EditMetadataModalController',
                resolve: {
                    'provider':function(){return provider;},
                    'identifier':function(){return identifier;},
                    'type':function(){return type;},
                    'template':function(){return template;},
                    'theme':function(){return theme;}
                }
            });
        }

        /**
         * Open metadata page for dataset metadata.
         * use $scope.displayMetadataEditor for Constellation SDI.
         * this function will be used in Constellation Enterprise.
         */
        $scope.editMetadata = function() {
            var type = 'import';
            if($scope.selectedDS && $scope.selectedDS.Children && $scope.selectedDS.Children.length >0){
                type = $scope.selectedDS.Children[0].Type.toLowerCase();
            }
            var template = type;
            $location.path('/editmetadata/'+template+'/'+type+'/'+$scope.selectedDS.Name);
        };

        // Style methods
        $scope.showStyleList = function() {
            StyleSharedService.showStyleList($scope, $scope.dataCtrl.selectedDataSetChild);
        };

        $scope.unlinkStyle = function(providerName, styleName, dataProvider, dataId) {
            StyleSharedService.unlinkStyle($scope,providerName, styleName, dataProvider, dataId, style,$scope.dataCtrl.selectedDataSetChild);
        };

        $scope.editLinkedStyle = function(styleProvider, styleName, selectedData) {
            style.get({provider: styleProvider, name: styleName}, function(response) {
                StyleSharedService.editLinkedStyle($scope, response,selectedData);
            });
        };

        $scope.showSensorsList = function() {
            $modal.open({
                templateUrl: 'views/sensor/modalSensorChoose.html',
                controller: 'SensorModalChooseController',
                resolve: {
                    'selectedData': function() { return $scope.dataCtrl.selectedDataSetChild; }
                }
            });
        };

        $scope.unlinkSensor = function(sensorId) {
            dataListing.unlinkSensor({providerId: $scope.dataCtrl.selectedDataSetChild.Provider,
                    dataId: $scope.dataCtrl.selectedDataSetChild.Name,
                    sensorId: sensorId},
                {value: $scope.dataCtrl.selectedDataSetChild.Namespace},
                function(response) {//success
                    $scope.dataCtrl.selectedDataSetChild.TargetSensor.splice(0, 1);
                });
        };

        $scope.toggleUpDownSelected = function() {
            var $header = $('#dataDashboard').find('.selected-item').find('.block-header');
            $header.next().slideToggle(200);
            $header.find('i').toggleClass('fa-chevron-down fa-chevron-up');
        };
        $scope.toggleUpDownSelectedMD = function() {
            var $header = $('#metadataDashboard').find('.selected-item').find('.block-header');
            $header.next().slideToggle(200);
            $header.find('i').toggleClass('fa-chevron-down fa-chevron-up');
        };

        // Data loading
        $scope.showLocalFilePopup = function() {
            var modal = $modal.open({
                templateUrl: 'views/data/modalImportData.html',
                controller: 'ModalImportDataController',
                resolve: {
                    'firstStep': function() { return 'step1DataLocal'; },
                    'importType': function() { return 'local'; }
                }
            });
            modal.result.then(function(result) {
                if(!result){
                    return;
                }
                if(!result.file){
                    return;
                }else {
                    dataListing.setMetadata({}, {values: {"providerId": result.file,
                                                          "dataType": result.type,
                                                          "mergeWithUploadedMD":result.completeMetadata}},
                       function () {//success
                        $scope.initAfterImport();
                        openModalEditor(null,result.file,result.type,"import",'data');
                    }, function () {//error
                        Growl('error', 'Error', 'Unable to prepare metadata for next step!');
                    });
                }
            });
        };

        $scope.showServerFilePopup = function() {
            var modal = $modal.open({
                templateUrl: 'views/data/modalImportData.html',
                controller: 'ModalImportDataController',
                resolve: {
                    'firstStep': function() { return 'step1DataServer'; },
                    'importType': function() { return 'server'; }
                }
            });
            modal.result.then(function(result) {
                if(!result){
                    return;
                }
                if(!result.file){
                    return;
                }else {
                    dataListing.setMetadata({}, {values: {"providerId": result.file,
                                                          "dataType": result.type,
                                                          "mergeWithUploadedMD":result.completeMetadata}},
                       function () {//success
                        $scope.initAfterImport();
                        openModalEditor(null,result.file,result.type,"import",'data');
                    }, function () {//error
                        Growl('error', 'Error', 'Unable to save metadata');
                    });
                }
            });
        };

        $scope.showDatabasePopup = function() {
            var modal = $modal.open({
                templateUrl: 'views/data/modalImportData.html',
                controller: 'ModalImportDataController',
                resolve: {
                    'firstStep': function() { return 'step1Database'; },
                    'importType': function() { return 'database'; }
                }
            });
            modal.result.then(function(result) {
                if(!result){
                    return;
                }
                if(!result.file){
                    return;
                }else {
                    dataListing.setMetadata({}, {values: {"providerId": result.file,
                                                          "dataType": result.type,
                                                          "mergeWithUploadedMD":result.completeMetadata}},
                       function () {//success
                        $scope.initAfterImport();
                        openModalEditor(null,result.file,result.type,"import",'data');
                    }, function () {//error
                        Growl('error', 'Error', 'Unable to save metadata');
                    });
                }
            });
        };

        $scope.showEmptyDataSetPopup = function() {
            var modal = $modal.open({
                templateUrl: 'views/data/modalImportData.html',
                controller: 'ModalImportDataController',
                resolve: {
                    'firstStep': function() { return 'step2Metadata'; },
                    'importType': function() { return 'empty'; }
                }
            });
            modal.result.then(function(result) {
                if(!result){
                    return;
                }
                if(!result.file){
                    return;
                }else {
                    dataListing.setMetadata({}, {values: {"providerId": result.file,
                                                          "dataType": result.type}},
                        function () {//success
                            $scope.initAfterImport();
                            openModalEditor(null,result.file,result.type,"import",'data');
                        }, function () {//error
                            Growl('error', 'Error', 'Unable to save metadata');
                        });
                }
            });
        };

        $scope.showDomains = function(){
            var modal = $modal.open({
                templateUrl: 'views/data/linkedDomains.html',
                controller: 'ModalDataLinkedDomainsController',
                resolve: {
                    'domains': function() {return dataListing.domains({dataId: $scope.dataCtrl.selectedDataSetChild.Id}).$promise;},
                    'dataId': function(){return $scope.dataCtrl.selectedDataSetChild.Id;}
                }
            });
        };

        $scope.truncate = function(small, text){
            if(text) {
                if (window.innerWidth >= 1200) {
                    if (small && text.length > 20) {
                        return text.substr(0, 20) + "...";
                    } else if (!small && text.length > 65) {
                        return text.substr(0, 65) + "...";
                    } else { return text;}
                } else if (window.innerWidth < 1200 && window.innerWidth >= 992) {
                    if (small && text.length > 12) {
                        return text.substr(0, 12) + "...";
                    } else if (!small && text.length > 50) {
                        return text.substr(0, 50) + "...";
                    } else { return text ;}
                } else if (window.innerWidth < 992) {
                    if (text.length > 30) {
                        return text.substr(0, 30) + "...";
                    } else { return text;}
                }
            }
        };
        $scope.truncateTitleBlock = function(text){
            if(text) {
                if (window.innerWidth >= 1200) {
                    if (text.length > 40) {
                        return text.substr(0, 40) + "...";
                    } else { return text;}
                } else if (window.innerWidth < 1200 && window.innerWidth >= 992) {
                    if (text.length > 30) {
                        return text.substr(0, 30) + "...";
                    } else {return text;}
                } else if (window.innerWidth < 992) {
                    if (text.length > 20) {
                        return text.substr(0, 20) + "...";
                    } else { return text; }
                }
            }
        };

    })

    .controller('DataModalController', function($scope, dataListing, webService, sos, sensor, Dashboard, $modalInstance,
                                                 service, exclude, Growl, $modal) {
        /**
         * To fix angular bug with nested scope.
         */
        $scope.wrap = {};

        $scope.service = service;

        $scope.getDefaultFilter = function() {
            if (service.type.toLowerCase() === 'wcs') {
                return 'coverage';
            }
            if (service.type.toLowerCase() === 'wfs') {
                return 'vector';
            }
            return undefined;
        };
        $scope.wrap.nbbypage = 5;
        $scope.exclude = exclude;

        // WMTS params in the last form before closing the popup
        $scope.wmtsParams = false;
        $scope.tileFormat = undefined;
        $scope.crs = undefined;
        $scope.scales = [];
        $scope.upperCornerX = undefined;
        $scope.upperCornerY = undefined;
        $scope.conformPyramid = undefined;

        $scope.init = function() {
            if (service.type.toLowerCase() === 'sos') {
                sensor.list({}, function(response) {
                    Dashboard($scope, response.children, false);
                });
            } else {
                dataListing.listAll({}, function (response) {
                    Dashboard($scope, response, true);
                    $scope.wrap.filtertype = $scope.getDefaultFilter();
                });
            }
        };

        $scope.selectedSensorsChild = null;

        $scope.selectSensorsChild = function(item) {
            if ($scope.selectedSensorsChild === item) {
                $scope.selectedSensorsChild = null;
            } else {
                $scope.selectedSensorsChild = item;
            }
        };

        $scope.dataSelect={all:false};
        $scope.listSelect=[];

        $scope.selectAllData = function() {
            if ($scope.dataSelect.all) {
                $scope.listSelect = $scope.wrap.dataList.slice();
            }else{
                $scope.listSelect=[];
            }
        };
        $scope.dataInArray = function(item){
            if($scope.listSelect.length>0) {
                for (var i = 0; i < $scope.listSelect.length; i++) {
                    if ($scope.listSelect[i].Name === item.Name && $scope.listSelect[i].Provider === item.Provider) {
                        $scope.listSelect.splice(i, 1);
                        break;
                    }
                    if(i===$scope.listSelect.length-1){
                        if ($scope.listSelect[i].Name !== item.Name || $scope.listSelect[i].Provider !== item.Provider){
                            $scope.listSelect.push(item);
                            break;
                        }
                    }
                }
            } else { $scope.listSelect.push(item);}

            if($scope.listSelect.length < $scope.wrap.dataList.length){
                $scope.dataSelect.all=false;
            } else { $scope.dataSelect.all=true; }
        };
        $scope.isInSelected = function(item){
            for(var i=0; i < $scope.listSelect.length; i++){
                if($scope.listSelect[i].Name === item.Name && $scope.listSelect[i].Provider === item.Provider){
                    return true;
                }
            }
            return false;
        };

        $scope.close = function() {
            $modalInstance.dismiss('close');
        };

        function addLayer(tiledProvider) {
            webService.addLayer({type: service.type, id: service.identifier},
                {layerAlias: tiledProvider.dataId, layerId: tiledProvider.dataId, serviceType: service.type, serviceId: service.identifier, providerId: tiledProvider.providerId},
                function () {
                    Growl('success', 'Success', 'Layer ' + tiledProvider.dataId + ' successfully added to service ' + service.name);
                    $modalInstance.close();
                },
                function () {
                    Growl('error', 'Error', 'Layer ' + tiledProvider.dataId + ' failed to be added to service ' + service.name);
                    $modalInstance.dismiss('close');
                }
            );
        }

        function pyramidGenerationError() {
            Growl('error', 'Error', 'Failed to generate pyramid');
            $modalInstance.dismiss('close');
        }

        function setScale(response) {
            $scope.scales = response.Entry[0].split(',');
        }

        function errorOnPyramid() {
               Growl('error', 'Error', 'No scale can automatically be set');
        }

        /**
         * @FIXME rewrite this function to call rest api outside loop
         * the server side must provide method to treat pyramid with an array instead of treating for each data item.
         * @TODO ugly code, the client side should never call rest api inside a loop.
         */
        $scope.choose = function() {
            if ($scope.listSelect.length !== 0) {
                $scope.selected = $scope.listSelect;
            }
            if (!$scope.selected) {
                Growl('warning', 'Warning', 'No data selected');
                $modalInstance.dismiss('close');
                return;
            }
            else{
                if ($scope.service.type.toLowerCase() === 'sos') {
                    var sensorId = ($scope.selectedSensorsChild) ? $scope.selectedSensorsChild.id : $scope.selected.id;
                    sos.importSensor({id: service.identifier}, {values: {"sensorId": sensorId}}, function () {
                        Growl('success', 'Success', 'Sensor ' + sensorId + ' imported in service ' + service.name);
                        $modalInstance.close();
                    }, function () {
                        Growl('error', 'Error', 'Unable to import sensor ' + sensorId + ' in service ' + service.name);
                        $modalInstance.dismiss('close');
                    });
                    return;
                }

                if ($scope.wmtsParams === false) {
                    // just add the data if we are not in the case of the wmts service
                    if (service.type.toLowerCase() !== 'wmts') {
                        angular.forEach($scope.selected, function(value, key){
                            if (service.type.toLowerCase() === 'wms' &&
                                $scope.conformPyramid &&
                                value.Type.toLowerCase() !== 'vector') {
                                // In the case of a wms service and user asked to pyramid the data
                                dataListing.pyramidConform({providerId: value.Provider, dataId: value.Name}, {}, addLayer, pyramidGenerationError);
                            } else {
                                webService.addLayer({type: service.type, id: service.identifier},
                                    {layerAlias: value.Name, layerId: value.Name, serviceType: service.type, serviceId: service.identifier, providerId: value.Provider, layerNamespace: value.Namespace},
                                function(response) {
                                    Growl('success', 'Success', response.message);
                                    $modalInstance.close();
                                },
                                function(response) {
                                    Growl('error', 'Error', response.message);
                                    $modalInstance.dismiss('close');
                                });
                            }
                        });
                        return;
                    }

                    for(var j=0; j<$scope.selected.length; j++) {
                        // WMTS here, prepare form
                        dataListing.pyramidScales({providerId: $scope.selected[j].Provider, dataId: $scope.selected[j].Name}, setScale, errorOnPyramid);
                        $scope.wmtsParams = true;
                    }
                } else {
                    // Finish the WMTS publish process
                    // Pyramid the data to get the new provider to add
                    for(var k=0; k<$scope.selected.length; k++) {
                        dataListing.pyramidData({providerId: $scope.selected[k].Provider, dataId: $scope.selected[k].Name},
                            {tileFormat: $scope.tileFormat, crs: $scope.crs, scales: $scope.scales, upperCornerX: $scope.upperCornerX, upperCornerY: $scope.upperCornerY}, addLayer, pyramidGenerationError);
                    }
                }
            }
        };

        $scope.truncate = function(text){
            if(text) {
                if (text.length > 40) {
                    return text.substr(0, 40) + "...";
                } else { return text; }
            }
        };
    })

    .controller('ModalDataLinkedDomainsController', function($scope, $modalInstance, Growl, dataListing, domains, dataId) {
        $scope.domains = domains;
        $scope.close = function() {
            $modalInstance.dismiss('close');
        };

        $scope.toggleDomain = function(i){
            var pathParams = {domainId: $scope.domains[i].id, dataId:dataId};
            if($scope.domains[i].linked){
                dataListing.unlinkFromDomain(pathParams, function(){
                    $scope.domains[i].linked = !$scope.domains[i].linked;
                    $scope.domains[i].linked = false;
                }, function(response){
                    Growl('error','error', response.data.message );
                    dataListing.domains({dataId:dataId}, function(domains){
                        $scope.domains = domains;
                    });
                });
            }else{
                dataListing.linkToDomain(pathParams, {}, function(){
                    $scope.domains[i].linked = true;
                }, function(){

                });
            }
        };

    });

