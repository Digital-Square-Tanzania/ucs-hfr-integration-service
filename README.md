# UCS-HFR-INTEGRATION-SERVICE


A service used to receive HFR facility updates and refresh location hierarchy into OpenMRS.

## 1. Dev Requirements

 1. Java 17
 2. IntelliJ or Visual Studio Code
 3. Gradle

## 2. Deployment

To build and run the service after performing the above configurations, run the following

```
  ./gradlew clean shadowJar
  java -jar build/libs/ucs-hfr-integration-service-<version>.jar
```

### HFR location endpoints

- `POST /hfr/facility` – accepts a single facility payload from HFR (sample below) and upserts the Region → District → Council → Ward → Facility → Village structure into OpenMRS.
- `POST /hfr/refresh-hierarchy` – pulls the latest facility list and administrative hierarchy from HFR and syncs them into OpenMRS.

Sample facility payload:
```json
{
  "Fac_IDNumber": "124899-6",
  "Name": "GAKALA",
  "Comm_FacName": "GAKALA ",
  "Zone": "Lake",
  "Region_Code": "TZ.LK.GE",
  "Region": "Geita",
  "District_Code": "TZ.LK.GE.MW",
  "District": "Mbogwe",
  "Council_Code": "TZ.LK.GE.MW.3",
  "Council": "Mbogwe DC",
  "Ward": "Bukandwe",
  "Village": "Bukandwe",
  "Village_Code": "TZ.LK.GE.MW.3.13.3",
  "FacilityTypeGroupCode": "HLCTR",
  "FacilityTypeGroup": "Health Center",
  "FacilityTypeCode": "HLCTR",
  "FacilityType": "Health Center",
  "OwnershipGroupCode": "Priv",
  "OwnershipGroup": "Private",
  "OwnershipCode": "comp",
  "Ownership": "Company/Business Name",
  "OperatingStatus": "Operating",
  "Latitude": "-3.66131",
  "Longitude": "32.19689",
  "RegistrationStatus": "Registered",
  "OpenedDate": "2025-12-15 00:00:00",
  "CreatedAt": "2025-07-16 10:31:27",
  "UpdatedAt": "2025-09-14 00:30:20",
  "Vote": null,
  "IsDesignated": 0,
  "ClosedDate": "",
  "OSchangeOpenedtoClose": "N",
  "OSchangeClosedtoOperational": "N",
  "PostorUpdate": "P"
}
```

Configuration for OpenMRS/HFR connection lives in `src/main/resources/application.conf`.


## 3. Deployment via Docker

First Install docker in your PC by following [this guide](https://docs.docker.com/engine/install/). Secondly, clone this repo to your computer by using git clone and the repo's address:

`git clone https://github.com/Digital-Square-Tanzania/ucs-hfr-integration-service.git`

Once you have completed cloning the repo, go inside the repo in your computer: `cd ucs-hfr-integration-service`

Update `application.conf` found in `src/main/resources/` with the correct configs and use the following Docker commands for various uses:

### Run/start
`docker build -t ucs-hfr-integration-service .`

`docker run -d --add-host=host.docker.internal:host-gateway -p 127.0.0.1:9204:8080 ucs-hfr-integration-service`


### Interact With Shell

`docker exec -it ucs-hfr-integration-service sh`

### Stop Services

`docker stop ucs-hfr-integration-service`

## License

ISC

## Author

Ilakoze Jumanne

## Version

1.0.0
