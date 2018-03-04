'use strict';

angular.module('myApp.trafficAnalytics', ['ngRoute'])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/trafficAnalytics', {
            templateUrl: 'trafficAnalytics/trafficAnalytics.html',
            controller: 'trafficAnalyticsCtrl'
        });
    }])

    .controller('trafficAnalyticsCtrl', ['$scope', '$http','$timeout',  function($scope, $http,$timeout) {

        var intensity = [0,0,0,0,0,0,0,0,0,0];
        var labels = [1,2,3,4,5,6,7,8,9,10];


        var getData = function(){

            $http({
                method: 'GET',
                url: 'http://localhost:8080/semaphoreStatus'
            }).then(function successCallback(response) {
                $scope.crossroads = null;
                $scope.crossroads = response.data;
            }, function errorCallback() {
                $scope.crossroads = null;
                console.log("error contacting server");
                nextLoad();
            });
        };

    $scope.showSecondPanel = function(){
        console.log("data selected = " + $scope.dataSelected);
        ($scope.crossroads).forEach(function secondPanelFunction(cross){
            if((cross.id).localeCompare($scope.dataSelected) === 0){
                // console.log("aggiungo i semafori :" + cross.semaphores);
                $scope.semaphores = cross.semaphores;
            }
        });

        document.getElementById("secondForm").style.display = "block";
    };





        $scope.getSemaphoreStatistics = function() {
            var crossroads = null;
            $http({
                method: 'GET',
                url: 'http://localhost:8080/semaphoreStatus'
            }).then(function successCallback(response) {
                crossroads = null;
                crossroads = response.data;


                crossroads.forEach(function forEachFunction(cross){
                    // console.log(cross);
                    (cross.semaphores).forEach(function forEachSemaphore(sem) {
                        // console.log(sem);
                        if(sem.id.localeCompare($scope.semaphoreSelectData) === 0){
                            // data = sem.queue;
                            console.log("length = ", sem.queue.length);
                            rotateArray(sem.queue.length);
                            console.log("array = ", intensity);

                        }
                    })
                });
                // console.log("data = " + data);

                document.getElementById("semaphoreTrafficHistoryID").style.display = "block";
                new Chart(document.getElementById("semaphoreHistory"), {
                    type: 'line',
                    data: {
                        // labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                        // datasets: [{
                        //     data: [86, 783, 106, 2478, 107, 111, 133, 221, 783, 86],
                        //     label: "Semaphore ID",
                        //     borderColor: "#cdc600",
                        //     fill: false
                        // }
                        // ]
                        labels: labels,
                        datasets: [{
                            data: intensity,
                            label: "Semaphore ID",
                            borderColor: "#cdc600",
                            fill: false
                        }
                        ]
                    },
                    options: {
                        animation: {
                            duration: 0
                        },
                        scales: {
                            yAxes: [{
                                display: true,
                                ticks: {
                                    suggestedMin: 0    // minimum will be 0, unless there is a lower value.
                                }
                            }]
                        },
                        title: {
                            display: true
                        }
                    }
                });
                //
                // document.getElementById("numberOfFailuresID").style.display = "block";
                // new Chart(document.getElementById("numberOfFailures"), {
                //     type: 'horizontalBar',
                //     data: {
                //         labels: ["Semaphore", "Average"],
                //         datasets: [
                //             {
                //                 backgroundColor: ["#cdc600", "#0214a2"],
                //                 data: [18,15]
                //             }
                //         ]
                //     },
                //     options: {
                //         scales: {
                //             xAxes: [{
                //                 display: true,
                //                 ticks: {
                //                     suggestedMin: 10    // minimum will be 0, unless there is a lower value.
                //                 }
                //             }]
                //         },
                //         legend: { display: false },
                //         title: {
                //             display: true
                //         }
                //     }
                // });


                nextLoad();

            }, function errorCallback() {
                crossroads = null;
                console.log("error contacting server");
                nextLoad();
            });
        };


        var loadTime = 5000, //Load the data every 15 seconds
            loadPromise; //Pointer to the promise created by the Angular $timeout service

        var cancelNextLoad = function() {
            $timeout.cancel(loadPromise);
        };

        var nextLoad = function(mill) {
            mill = mill || loadTime;

            //Always make sure the last timeout is cleared before starting a new one
            cancelNextLoad();
            loadPromise = $timeout($scope.getSemaphoreStatistics, mill);
        };

        //Always clear the timeout when the view is destroyed, otherwise it will keep polling
        $scope.$on('$destroy', function() {
            cancelNextLoad();
        });

        getData();

        //scopri come si chiama a funzione che effettua la rotazione
        var rotateArray = function(newValue){
            intensity[0] = intensity[1];
            intensity[1] = intensity[2];
            intensity[2] = intensity[3];
            intensity[3] = intensity[4];
            intensity[4] = intensity[5];
            intensity[5] = intensity[6];
            intensity[6] = intensity[7];
            intensity[7] = intensity[8];
            intensity[8] = intensity[9];
            intensity[9] = newValue;
        }

    }]);

