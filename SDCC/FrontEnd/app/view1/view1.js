// 'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html'
    // controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl', ['$scope', '$http', '$interval', function($scope, $http, $interval) {



    var getData = $interval(function() {
        console.log("faccio qualcosa");
        $scope.crossroads = "Loading...";
        $http({
            method: 'GET',
            url: 'http://localhost:8080/getAllCrossroads'
        }).then(function successCallback(response) {
            $scope.crossroads = null;
            // console.log(response.data);
            $scope.crossroads = (response.data);
            console.log($scope.crossroads);
            // nextLoad();

        }, function errorCallback() {
            $scope.crossroads = null;
            console.log("error contacting server");
            // nextLoad();
        });
        return "called";
    }, 5000);

    // var loadTime = 5000, //Load the data every 5 seconds
    //     errorCount = 0, //Counter for the server errors
    //     loadPromise; //Pointer to the promise created by the Angular $timeout service
    //
    // var cancelNextLoad = function() {
    //     $timeout.cancel(loadPromise);
    // };
    //
    // var nextLoad = function(mill) {
    //     mill = mill || loadTime;
    //
    //     //Always make sure the last timeout is cleared before starting a new one
    //     cancelNextLoad();
    //     loadPromise = $timeout(getData, mill);
    // };


    //Start polling the data from the server
    // getData();
    // var result;
    // result = $interval(getData(), 1000);


    $scope.$on('$destroy', function() {
        $interval.cancel(getData);
    });

    //Always clear the timeout when the view is destroyed, otherwise it will keep polling
    // $scope.$on('$destroy', function() {
    //     cancelNextLoad();
    // });
}]);