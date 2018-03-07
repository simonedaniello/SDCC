# SDCC traffic lights controller

## Introduction

>todo

## Installation

The project is composed by several parts. In order to run each section download 
and install [RabbitMQ](https://www.rabbitmq.com/download.html) and...
* __CrossroadWR__ 
    * open the project as maven file
    * Include the jars in the libs folder
    * Include the dependency with the module __entities__
    * Run the project using the spring configuration and the JRE 9.0
* __MonitorBackEnd__
    * open the project as maven file
    * Include the jars in the libs folder
    * Include the dependency with the module __entities__
    * Run the project using the spring configuration and the JRE 9.0
* __MonitorFront__
    * go to app/
    * download the libraries in the head of index.html (the scripts)
    * open index.html in a browser
* __Semaphore__
    * todo


## Messages

Code | From | To | Other listening | Meaning
--- | --- | --- | --- | --- 
1 | Semaphore | Crossroad | Semaphores | Message sent to add a semaphore to the crossroad. Every other semaphore listening on the crossroad channel is informed and can add the semaphore to its own list
-1 | Semaphore | Crossroad | Semaphores| Message sent to remove a semaphore from a crossroad.  Every other semaphore listening on the crossroad channel is informed and can remove the semaphore to its own list
400 | Semaphore | MonitorController | / | Message sent to the monitor controller in order to retrieve the semaphore status (that will be send to the monitor back end controller) 
401 | MonitorController | Semaphores | / | Message sent by the monitor controller to each semaphore in order to request its status
10 | Crossroad | Semaphores | / | Message sent periodically by the crossroad to inform each semaphore associated of its status (the semaphores binded)
500 | MonitorController | MonitorBE | / | Message sent to the monitor back end controller by the monitor controller after the retrieving of each semaphore’s status


##Technologies

* IDE: 
    * IntelliJ
* Messages:
    * RabbitMQ
    * Spring
* Front End:
    * AngularJS
* Data Stream: 
    * Flink

##How it works

>todo


## Authors

* Simone D'Aniello - Master student of Computer Science in the University of Rome Tor Vergata
* Marius Ionita - Master student of Computer Science in the University of Rome Tor Vergata
* Federico Ronci - Master student of Computer Science in the University of Rome Tor Vergata
