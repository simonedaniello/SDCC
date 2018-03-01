'use strict';

angular.module('myApp.trafficAnalytics', ['ngRoute'])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/trafficAnalytics', {
            templateUrl: 'trafficAnalytics/trafficAnalytics.html',
            controller: 'trafficAnalyticsCtrl'
        });
    }])

    .controller('trafficAnalyticsCtrl', ['$scope', function($scope) {

        $scope.getSemaphoreStatistics = function() {

            document.getElementById("semaphoreTrafficHistoryID").style.display = "block";
            new Chart(document.getElementById("semaphoreHistory"), {
                type: 'line',
                data: {
                    labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                    datasets: [{
                        data: [86, 783, 106, 2478, 107, 111, 133, 221, 783, 86],
                        label: "Semaphore ID",
                        borderColor: "#cdc600",
                        fill: false
                    }
                    ]
                },
                options: {
                    title: {
                        display: true
                    }
                }
            });

            document.getElementById("numberOfFailuresID").style.display = "block";
            new Chart(document.getElementById("numberOfFailures"), {
                type: 'horizontalBar',
                data: {
                    labels: ["Semaphore", "Average"],
                    datasets: [
                        {
                            backgroundColor: ["#cdc600", "#0214a2"],
                            data: [18,15]
                        }
                    ]
                },
                options: {
                    scales: {
                        xAxes: [{
                            display: true,
                            ticks: {
                                suggestedMin: 10    // minimum will be 0, unless there is a lower value.
                            }
                        }]
                    },
                    legend: { display: false },
                    title: {
                        display: true
                    }
                }
            });

        }
    }]);

