// 'use strict';

angular.module('myApp.generalInfoPage', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/generalInfoPage', {
    templateUrl: 'generalInfoPage/generalInfoPage.html'
    // controller: 'generalInfoPageCtrl'
  });
}])

.controller('generalInfoPageCtrl', ['$scope', '$http', '$interval', function($scope, $http) {


    var elemcontrollers = document.getElementById('targetncontrollers');
    var elemcrossroads = document.getElementById('targetncrossroads');
    var elemsemaphores = document.getElementById('targetnsemaphores');
    var elemquery1 = document.getElementById('targetquery1');
    var elemquery2 = document.getElementById('targetquery2');
    var elemquery3 = document.getElementById('targetquery3');
    var mymap;


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

            colorWords(elemcontrollers, $scope.generalInfo.nOfControllers, 0);
            colorWords(elemcrossroads, $scope.generalInfo.nOfCrossroads, 0);
            colorWords(elemsemaphores, $scope.generalInfo.nOfSemaphores, 0);
            colorWords(elemquery1, "Query 1:", 0);
            colorWords(elemquery2, "Query 2:", 0);
            colorWords(elemquery3, "Query 3:", 0);

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
        $scope.malfunctions = item.malfunctions;
        initializeMap(item.latitude, item.longitude)
        // initializeMap(47.31, 5.38)
    };




    var firsttime = 0;




    function initializeMap(latitude, longitude){
        if(firsttime === 0) {
            mymap = L.map('mapid').setView([latitude, longitude], 10);
            L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
                attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
                maxZoom: 18,
                id: 'mapbox.streets',
                accessToken: 'your.mapbox.access.token'
            }).addTo(mymap);
            L.marker([latitude, longitude]).addTo(mymap);
            firsttime = 1;
        }
        else{
           mymap.setView(new L.LatLng(latitude, longitude), 10);
           L.marker([latitude, longitude]).addTo(mymap);
        }
    }


    function colorWords(idname, number, curr){

        var randomChars = document.createElement("span");

        randomChars.textContent = (number).toString().charAt(curr);
        randomChars.style.fontSize = "40px";
        if(idname===elemquery1 || idname===elemquery2 || idname===elemquery3){
            randomChars.style.color=  "rgb(" + 0 + ", " + 0 + ", " + 0 + ")";
        }
        else{
            randomChars.style.color = randomColor();
        }

        idname.appendChild(randomChars);

        if (curr < (number).length)
            setTimeout(function() { colorWords(idname, number, curr+1) },100)
    }



    function randomColor(){
        //get a "red" from 0 - 255
        var r = Math.floor(Math.random() * 256);
        //get a "green" from  0 - 255
        var g = Math.floor(Math.random() * 256);
        //get a "blue" from  0 - 255
        var b = Math.floor(Math.random() * 256);
        return "rgb(" + r + ", " + g + ", " + b + ")";
    }






}]);





