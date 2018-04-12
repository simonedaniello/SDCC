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
        var chartSem;
        var chartBar;

        var labelsBar = [];
        var dataBar = [];
        var index = 0;
        var green = 0;


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
            intensity = [0,0,0,0,0,0,0,0,0,0];
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

                var ctx = document.getElementById("semaphoreHistory").getContext('2d');
                chartSem = new Chart(ctx, {
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

                crossroads.forEach(function forEachFunction(cross){
                    if(cross.id.localeCompare($scope.dataSelected) === 0){

                        (cross.semaphores).forEach(function forEachSemaphore(sem) {
                            if(sem.light === 1){
                                console.log("è verde " + sem.id);
                                green = index;
                            }
                            dataBar.push(sem.queue.length);
                            labelsBar.push(sem.id);
                            // dataBar.push(sem.queue.length);
                            index = index + 1;
                        })
                    }

                });

                document.getElementById("allSemapohresQueuesID").style.display = "block";
                chartBar = new Chart(document.getElementById("allSemapohresQueues"), {
                    type: 'bar',
                    data: {
                        // labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                        // datasets: [{
                        //     data: [86, 783, 106, 2478, 107, 111, 133, 221, 783, 86],
                        //     label: "Semaphore ID",
                        //     borderColor: "#cdc600",
                        //     fill: false
                        // }
                        // ]
                        labels: labelsBar,
                        datasets: [{
                            data: dataBar,
                            backgroundColor: 'rgba(50,205,50, 0.2)',
                            borderColor: 'rgba(0,100,0, 0.2), ',
                            borderWidth: 1
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

                nextLoad();

            }, function errorCallback() {
                crossroads = null;
                console.log("error contacting server");
                nextLoad();
            });
        };

        var loadTime = 3000, //Load the data every 3 seconds
            loadPromise; //Pointer to the promise created by the Angular $timeout service

        var cancelNextLoad = function() {
            $timeout.cancel(loadPromise);
        };

        var nextLoad = function(mill) {
            mill = mill || loadTime;

            //Always make sure the last timeout is cleared before starting a new one
            cancelNextLoad();
            //loadPromise = $timeout($scope.getSemaphoreStatistics, mill);
            loadPromise = $timeout(updateAll, mill);
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
        };

        var updateAll = function() {


            index = 0;
            green = 0;

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

                chartSem.destroy();
                var ctx = document.getElementById("semaphoreHistory").getContext('2d');
                chartSem = new Chart(ctx, {
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

                labelsBar = [];
                dataBar = [];

                crossroads.forEach(function forEachFunction(cross){
                    if(cross.id.localeCompare($scope.dataSelected) === 0){

                        (cross.semaphores).forEach(function forEachSemaphore(sem) {
                            if(sem.light === 1){
                                console.log("è verde " + sem.id);
                                green = index;
                            }
                            dataBar.push(sem.queue.length);
                            labelsBar.push(sem.id);
                            // dataBar.push(sem.queue.length);
                            index = index + 1;
                        })
                    }

                });

                chartBar.destroy();

                document.getElementById("allSemapohresQueuesID").style.display = "block";
                chartBar = new Chart(document.getElementById("allSemapohresQueues"), {
                    type: 'bar',
                    data: {
                        // labels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                        // datasets: [{
                        //     data: [86, 783, 106, 2478, 107, 111, 133, 221, 783, 86],
                        //     label: "Semaphore ID",
                        //     borderColor: "#cdc600",
                        //     fill: false
                        // }
                        // ]
                        labels: labelsBar,
                        datasets: [{
                            data: dataBar,
                            backgroundColor: 'rgba(50,205,50, 0.2)',
                            borderColor: 'rgba(0,100,0, 0.2), ',
                            borderWidth: 1
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

                nextLoad();

            }, function errorCallback() {
                crossroads = null;
                console.log("error contacting server");
                nextLoad();
            });
        };

        function addData(chart, label, data) {
            chart.data.labels.push(label);
            chart.data.datasets.forEach(function fefunc1(dataset) {
                dataset.data.push(data);
            });
            chart.update();
        }

        function removeData(chart) {
            (chart.data).labels.pop();
            (chart.data).datasets.forEach(function fefunc2(dataset) {
                dataset.data.pop();
            });
            chart.update();
        }

    }]);

