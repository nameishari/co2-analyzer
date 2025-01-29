## Overview
Service to store CO2 Readings from various sensors and Provide metrics.

## Technologies/Libraries used:

<ul>
  <li>Spring Boot</li>
  <li>Java 17</li>
  <li>PostgreSQL</li>
  <li>Junit 5</li>
  <li>Rest Assured - for BDD style API Testing</li>
  <li>Testcontainers - to spin up postgres instant for Integrations tests</li>
</ul>

## Dependencies
- In order to start service locally you need to start a postgreSQL instance, you can do that by executing **"docker compose up postgres"**
- if you are on a older version of docker, you may have to use **"docker-compose up postgres"**
- This service is using maven wrapper, it is not necessary to have maven in the execution environment. Just use maven commands with ./mvnw prefix.

## REST API
* `POST` /sensors - create a sensor
    - example request body
  ```json
    {
      "name": "name",
      "location": "Berlin"
    }
  ```
* `POST` /sensors/{sensorId}/measurement - creates a measurement
    - sensorId is part of POST /sensors api.
    - example request body
  ```json
    {
      "reading": 2000,
      "time": 100
    }
  ```
* `GET` /sensors/{sensorId} - get a sensor details with status
* `GET` /sensors/{sensorId}/metrics - get a sensor metrics for past 30 days

## How to start service locally in intellij directly using Main method
<ul>
  <li><b>docker compose up postgres</b> - to start postgres instance</li>
  <li>Now just run/debug main method inside of Co2AnalyzerApplication.</li>
 </ul>

## How to start service locally using Docker
<ul>
  <li>**./mvnw clean package** - create a executable jar file</li>
  <li><b>docker-compose build --no-cache</b> - build/rebuild the docker image</li>
  <li>**docker-compose up** - starts the service locally (including postgres dependency)</li>
 </ul>

## Future improvements
Extend tests with exceptions, and also do more request validations when adding measurement.