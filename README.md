# Matchnight support

## before you start
This project is using both mongo as a database repository and redis for caching.
Both dependencies can be provided as docker container
In order to run it locally you should have both installed on your system
- docker 
- docker-compose 

## How to launch

### Start the dependencies
Using the terminal, go to the env folder and run the following command
```
cd env
docker-compose up -d
```
The dependencies should be launched and the containers should be up and running

### Start the spring boot application
Run the following command at the root of the project
```
gradlew bootrun
```

### Api definition
At the following URL you can have access to the swagger with an initial overview of the apis available
http://localhost:8080/swagger-ui.html#/