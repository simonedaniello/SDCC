// 'use strict';

angular.module('myApp.generalInfoPage', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/generalInfoPage', {
    templateUrl: 'generalInfoPage/generalInfoPage.html'
    // controller: 'generalInfoPageCtrl'
  });
}])

.controller('generalInfoPageCtrl', ['$scope', '$http', '$interval', function($scope, $http, $interval) {

    var getGeneralInfos = function() {

        console.log("chiamo get general infos");
        // $scope.general = "Loading...";
        $http({
            method: 'GET',
            url: 'http://localhost:8080/getGeneralInfo'
        }).then(function successCallback(response) {
            $scope.generalInfo = null;
            // console.log(response.data);
            $scope.generalInfo= (response.data);
            console.log($scope.generalInfo);

        }, function errorCallback() {
            $scope.generalInfo = null;
            console.log("error contacting server");
        });
    };

    getGeneralInfos();
    // $interval(getGeneralInfos(), 1000);

    $scope.getSemaphoreInfo = function(item){
        console.log("vengo chiamato");
        $scope.semaphoreinfoaddress = item.address;
        $scope.semaphoreinfoid = item.semId;
        $scope.semaphoreinfolatitude = item.latitude;
        $scope.semaphoreinfolongitude =   item.longitude;
    };



    $scope.$on('$destroy', function() {
        $interval.cancel(getGeneralInfos);
    });


}]);



