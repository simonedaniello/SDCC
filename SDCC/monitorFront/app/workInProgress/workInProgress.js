'use strict';

angular.module('myApp.workInProgress', ['ngRoute'])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/workInProgress', {
            templateUrl: 'workInProgress/workInProgress.html',
            controller: 'workInProgressCtrl'
        });
    }])

    .controller('workInProgressCtrl', [function() {
    }]);