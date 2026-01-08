# UCS-LAB-MODULE-INTEGRATION-SERVICE


A service used to process received HIV Viral Load and DBS lab results and rejections from DDR into UCS.

## 1. Dev Requirements

 1. Java 17
 2. IntelliJ or Visual Studio Code
 3. Gradle

## 2. Deployment

To build and run the service after performing the above configurations, run the following

```
  ./gradlew clean shadowJar
  java -jar build/libs/ucs-lab-module-integration-service-<version>.jar
```


## 3. Deployment via Docker

First Install docker in your PC by following [this guide](https://docs.docker.com/engine/install/). Secondly, clone this repo to your computer by using git clone and the repo's address:

`git clone https://github.com/Digital-Square-Tanzania/ucs-lab-integration-service.git`

Once you have completed cloning the repo, go inside the repo in your computer: `cd ucs-lab-integration-service`

Update `application.conf` found in `src/main/resources/` with the correct configs and use the following Docker commands for various uses:

### Run/start
`docker build -t ucs-lab-integration-service .`

`docker run -d -p 127.0.0.1:9202:9202 ucs-lab-integration-service`


### Interact With Shell

`docker exec -it ucs-lab-integration-service sh`

### Stop Services

`docker stop ucs-lab-integration-service`

## License

ISC

## Author

Ilakoze Jumanne

## Version

1.0.0
