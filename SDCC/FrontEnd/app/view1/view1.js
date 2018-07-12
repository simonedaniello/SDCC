// 'use strict';

angular.module('myApp.view1', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {
    templateUrl: 'view1/view1.html'
    // controller: 'View1Ctrl'
  });
}])

.controller('View1Ctrl', ['$scope', '$http', '$interval', function($scope, $http, $interval) {



    var getData = function() {
        console.log("faccio qualcosa");
        $scope.crossroads = "Loading...";
        $http({
            method: 'GET',
            url: 'http://localhost:8080/getAllCrossroads'
        }).then(function successCallback(response) {
            $scope.crossroads = null;
            $scope.crossroads = (response.data);
            console.log($scope.crossroads);

        }, function errorCallback() {
            $scope.crossroads = null;
            console.log("error contacting server");
        });
        return "called";
    };

    getData();




}]);