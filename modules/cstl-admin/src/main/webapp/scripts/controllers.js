'use strict';

/* Controllers */

cstlAdminApp.controller('MainController', ['$scope',
    function ($scope) {
    }]);

cstlAdminApp.controller('LanguageController', ['$scope', '$translate',
    function ($scope, $translate) {
        $scope.changeLanguage = function (languageKey) {
            $translate.uses(languageKey);
        };
    }]);

cstlAdminApp.controller('MenuController', ['$scope',
    function ($scope) {
    }]);

cstlAdminApp.controller('LoginController', ['$scope', '$location', 'AuthenticationSharedService',
    function ($scope, $location, AuthenticationSharedService) {
        $scope.rememberMe = true;
        $scope.login = function () {
            AuthenticationSharedService.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe,
                success: function () {
                    $location.path('');
                }
            });
        };
    }]);

cstlAdminApp.controller('LogoutController', ['$location', 'AuthenticationSharedService',
    function ($location, AuthenticationSharedService) {
        AuthenticationSharedService.logout({
            success: function () {
                $location.path('');
            }
        });
    }]);

cstlAdminApp.controller('SettingsController', ['$scope', 'resolvedAccount', 'Account',
    function ($scope, resolvedAccount, Account) {
        $scope.success = null;
        $scope.error = null;
        $scope.settingsAccount = resolvedAccount;

        $scope.save = function () {
            Account.save($scope.settingsAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                    $scope.settingsAccount = Account.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

cstlAdminApp.controller('PasswordController', ['$scope', 'Password',
    function ($scope, Password) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.changePassword = function () {
            if ($scope.password != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
                $scope.doNotMatch = null;
                Password.save($scope.password,
                    function (value, responseHeaders) {
                        $scope.error = null;
                        $scope.success = 'OK';
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        $scope.error = "ERROR";
                    });
            }
        };
    }]);

cstlAdminApp.controller('SessionsController', ['$scope', 'resolvedSessions', 'Sessions',
    function ($scope, resolvedSessions, Sessions) {
        $scope.success = null;
        $scope.error = null;
        $scope.sessions = resolvedSessions;
        $scope.invalidate = function (series) {
            Sessions.delete({series: encodeURIComponent(series)},
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = "OK";
                    $scope.sessions = Sessions.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

cstlAdminApp.controller('MetricsController', ['$scope', 'resolvedMetrics',
    function ($scope, resolvedMetrics) {
        $scope.metrics = resolvedMetrics;
    }]);

cstlAdminApp.controller('LogsController', ['$scope', 'resolvedLogs', 'LogsService',
    function ($scope, resolvedLogs, LogsService) {
        $scope.loggers = resolvedLogs;

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        };
    }]);

cstlAdminApp.controller('DataController', ['$scope', '$dashboard', 'dataListing', 'style', '$modal',
    function ($scope, $dashboard, dataListing, style, $modal) {

        $scope.filtertype = "VECTOR";

        dataListing.listAll({}, function(response) {
            $dashboard($scope, response);
        });

        // Map methods
        $scope.showData = function() {
            $('#viewerData').modal("show");
            var layerName = $scope.selected.Name;
            var providerId = $scope.selected.Provider;
            var layerData = DataViewer.createLayer(layerName, providerId);
            var layerBackground = DataViewer.createLayer("CNTR_BN_60M_2006", "generic_shp");
            DataViewer.layers = [layerData, layerBackground];
            DataViewer.initMap();
        };

        // Style methods
        $scope.showStyleList = function() {
            var modal = $modal.open({
                templateUrl: 'views/modalStyleChoose.html',
                controller: 'StyleModalController',
                resolve: {
                    exclude: function() { return $scope.selected.TargetStyle }
                }
            });

            modal.result.then(function(item) {
                if (item) {
                    style.link({
                        provider: item.Provider,
                        name: item.Name
                    }, {
                        values: {
                            dataProvider: $scope.selected.Provider,
                            dataNamespace: "", dataId: $scope.selected.Name
                        }
                    }, function() {
                        $scope.selected.TargetStyle.push(item);
                    });
                }
            });
        };

        $scope.unlinkStyle = function(providerName, styleName, dataProvider, dataId) {
            style.unlink({provider: providerName, name: styleName}, {values: {dataProvider: dataProvider, dataNamespace: "", dataId: dataId}});
        };
    }]);

cstlAdminApp.controller('StyleModalController', ['$scope', '$dashboard', '$modalInstance', 'style', 'exclude',
    function ($scope, $dashboard, $modalInstance, style, exclude) {
        $scope.exclude = exclude;
        $scope.filtertype = "";

        style.listAll({}, function(response) {
            $dashboard($scope, response.styles);
        });

        $scope.ok = function() {
            $modalInstance.close($scope.selected);
        };

        $scope.close = function() {
            $modalInstance.dismiss('close');
        };
    }]);

cstlAdminApp.controller('WebServiceController', ['$scope', 'webService',
    function ($scope, webService) {
       $scope.services = webService.listAll();
    }]);

cstlAdminApp.controller('WebServiceEditController', ['$scope','$routeParams', 'webService',
                                                 function ($scope, $routeParams , webService) {
    $scope.service = webService.get({type: $routeParams.type, id:$routeParams.id});
    $scope.metadata = webService.metadata({type: $routeParams.type, id:$routeParams.id});
    $scope.config = webService.config({type: $routeParams.type, id:$routeParams.id});
                                                 }]);
