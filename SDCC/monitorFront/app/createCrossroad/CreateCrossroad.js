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
        $scope.cross = "Loading...";
        $http({
            method: 'GET',
            url: 'http://localhost:8080/semaphoreStatus'
        }).then(function successCallback(response) {
            $scope.cross = null;
            $scope.cross = response.data;
            console.log(response.data);
            nextLoad();

        }, function errorCallback() {
            $scope.cross = null;
            console.log("error contacting server");
            nextLoad();
        });

    };

    var loadTime = 15000, //Load the data every 15 seconds
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
}]);