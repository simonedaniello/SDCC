'use strict';

angular.module('myApp.createCrossroad', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/createCrossroad', {
    templateUrl: 'createCrossroad/createCrossroad.html',
    controller: 'createCrossroadCtrl'
  });
}])

.controller('createCrossroadCtrl', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

    var getData = function() {
        $http({
            method: 'GET',
            url: 'http://localhost:8080/semaphoreStatus'
        }).then(function successCallback(response) {
            $scope.cross = response.data;
            console.log(response.data);
            nextLoad();

        }, function errorCallback() {
            $scope.cross = null;
            console.log("error contacting server");
            nextLoad();
        });

    };

    var loadTime = 5000, //Load the data every 5 seconds
        errorCount = 0, //Counter for the server errors
        loadPromise; //Pointer to the promise created by the Angular $timeout service

    var cancelNextLoad = function() {
        $timeout.cancel(loadPromise);
    };

    var nextLoad = function(mill) {
        mill = mill || loadTime;

        //Always make sure the last timeout is cleared before starting a new one
        cancelNextLoad();
        loadPromise = $timeout(getData, mill);
    };

    //Start polling the data from the server
    getData();

    //Always clear the timeout when the view is destroyed, otherwise it will keep polling
    $scope.$on('$destroy', function() {
        cancelNextLoad();
    });

    $scope.sendSemaphore = function () {
        $scope.modalMessage = "Loading ...";
        var parameter = JSON.stringify({id: $scope.semID, street: $scope.semStreet, crossroads:$scope.dataMultipleSelect});
        console.log(parameter);
        $http.post('http://localhost:8098/addSemaphore', parameter)
            .success(function () {
                $scope.modalMessage = "semaphore added with success. Note that input control is not been implemented yet"
            })
            .error(function (error, status) {
                $scope.modalMessage = "Error in adding semaphore, error: " + error + ", status: " + status;
            });
        $("#myModal").modal();
    };

    $scope.createCrossroadFunction = function () {

        var parameter = JSON.stringify({id: $scope.crossID, address: $scope.crossStreet});

        function appendTransform(defaults, transform) {

            // We can't guarantee that the default transformation is an array
            defaults = angular.isArray(defaults) ? defaults : [defaults];

            // Append the new transformation to the defaults
            return defaults.concat(transform);
        }

        var req = {
            method: 'POST',
            url: 'http://' + $scope.crossIP + ':8097/createCrossroad',
            data: parameter,
            transformResponse: appendTransform($http.defaults.transformResponse, function(data) {
                return data;
            })
        };


        $scope.modalMessage = "Loading ...";
        $http(req)
        // $http.post('http://localhost:8080/createCrossroad', parameter)
            .success(function (response) {
                $scope.modalMessage = response
            })
            .error(function (error, status) {
                $scope.modalMessage = "Error in adding crossroad, error: " + error + ", status: " + status;
            });

        $("#myModal").modal();
    };
}]);

