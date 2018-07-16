# SDCC traffic lights controller

## Introduction

This project takes part in the CINI SMART CITIES UNIVERSITY CHALLENGE, which purpose was to build a distributed mechanism to control traffic lights temporization while gathering useful infos regarding the most dangerous intersections

## Content

In the directory SDCC all modules with related code can be found. The project report is in the main root

## Prerequisites

In order to run the project in localhost Apache Kafka, Apache Flink and MongoDB are needed

## Run

The project is composed by several modules. In order to run each section in localhost...
* __SemaphoreController__ 
    * open the project as maven file
    * Run the project using the class Runner (Kafka, Mongo and Spring are needed)
* __MonitorBackEnd__
    * open the project as maven file
    * Run the project using the class Runner (Kafka and Spring are needed)
* __FrontEnd__
    * go to app/
    * download the libraries in the head of index.html (the scripts)
    * open index.html in a browser
* __Semaphore__
    * open the project as maven file
    * Run the project using the class Runner (Kafka and Spring are needed)
* __Flink Dispatcher__ 
    * open the project as maven file
    * Run the project using the class Runner (Kafka and Flink are needed)
* __Monitor__
    * open the project as maven file
    * Run the project using the class Runner (Kafka and Mongo are needed)

## Technologies

* __IDE__
    * IntelliJ
* __Messages__
    * Apache Kafka
    * Spring
* __Front End__
    * AngularJS
* __Data Stream__ 
    * Flink
* __Storage__
    * MongoDB

## Authors

* __Simone D'Aniello - Master student of Computer Science in the University of Rome Tor Vergata__
* __Marius Ionita - Master student of Computer Science in the University of Rome Tor Vergata__
* __Federico Ronci - Master student of Computer Science in the University of Rome Tor Vergata__
