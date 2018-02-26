'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html',
    controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl', ['$scope', '$http', '$timeout', function($scope, $http, $timeout) {

    var getData = function() {
        $scope.crossroads = "Loading...";
        $http({
            method: 'GET',
            url: 'http://localhost:8080/semaphoreStatus'
        }).then(function successCallback(response) {
            console.log(response.data);
            $scope.crossroads = response.data;
            nextLoad();

        }, function errorCallback() {
            $scope.crossroads = "error contacting server";
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