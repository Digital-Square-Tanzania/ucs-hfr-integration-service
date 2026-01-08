package com.moh.go.tz.service;

import com.moh.go.tz.domain.HfrFacilityPayload;
import com.moh.go.tz.domain.Location;
import com.moh.go.tz.domain.SyncResponse;
import com.moh.go.tz.util.CapitalizeUtil;
import com.moh.go.tz.util.CustomJacksonObjectMapper;
import com.moh.go.tz.util.FetchLocationsHelper;
import com.moh.go.tz.util.OpenmrsClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Coordinates syncing HFR facility payloads into OpenMRS.
 */
public class LocationSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationSyncService.class);
    private static final int DEFAULT_MAX_ATTEMPTS = 5;

    private final OpenmrsClient openmrsClient;
    private final FetchLocationsHelper fetchLocationsHelper;
    private final HfrClient hfrClient;
    private final String openmrsBaseUrl;
    private final String codeLocationAttributeUuid;
    private final String hfrCodeLocationAttributeUuid;

    private final Map<String, Location> locationCache = new ConcurrentHashMap<>();
    private final Map<String, Location> codeCache = new ConcurrentHashMap<>();
    private List<Location> allLocations = new CopyOnWriteArrayList<>();

    public LocationSyncService() {
        this(ConfigFactory.load());
    }

    public LocationSyncService(Config config) {
        this.openmrsBaseUrl = config.getString("openmrs.base-url");
        this.codeLocationAttributeUuid = config.getString("openmrs.code-location-attribute-uuid");
        this.hfrCodeLocationAttributeUuid = config.getString("openmrs.hfr-code-location-attribute-uuid");
        this.openmrsClient = new OpenmrsClient(
                openmrsBaseUrl,
                config.getString("openmrs.user"),
                config.getString("openmrs.password"));
        this.fetchLocationsHelper = new FetchLocationsHelper(openmrsClient);
        this.hfrClient = new HfrClient(config);
        refreshLocationCache();
    }

    public synchronized SyncResponse syncFacility(HfrFacilityPayload payload) {
        if (payload == null) {
            return new SyncResponse("error", "Empty payload");
        }

        try {
            Location regionLoc = ensureLocationExists(
                    null,
                    CapitalizeUtil.capitalizeWords(payload.getRegion()),
                    payload.getRegionCode(),
                    "Region");

            Location districtLoc = ensureLocationExists(
                    regionLoc,
                    CapitalizeUtil.capitalizeWords(payload.getDistrict()),
                    payload.getDistrictCode(),
                    "District");

            Location councilLoc = ensureLocationExists(
                    districtLoc,
                    CapitalizeUtil.capitalizeWords(payload.getCouncil()),
                    payload.getCouncilCode(),
                    "Council");

            String wardCode = resolveWardCode(payload);
            Location wardLoc = ensureLocationExists(
                    councilLoc,
                    buildWardName(payload),
                    wardCode,
                    "Ward");

            Location facilityLoc = ensureLocationExists(
                    wardLoc,
                    buildFacilityName(payload),
                    payload.getFacIdNumber(),
                    "Facility");

            retireOrUnretireIfNeeded(payload, facilityLoc);

            ensureLocationExists(
                    wardLoc,
                    buildVillageName(payload),
                    payload.getVillageCode(),
                    "Village");

            String message = String.format("Processed facility %s (%s)", payload.getName(), payload.getFacIdNumber());
            String status = (facilityLoc != null) ? "success" : "warning";
            return new SyncResponse(status, message);
        } catch (Exception e) {
            LOGGER.error("Failed to sync facility", e);
            return new SyncResponse("error", "Failed to sync facility: " + e.getMessage());
        }
    }

    public synchronized SyncResponse refreshFromHfr() {
        try {
            int adminCount = hfrClient.fetchAdminHierarchyData(this::processAdminHierarchyData);
            int facilityCount = hfrClient.fetchHealthFacilityData(this::processHfrResponse);
            return new SyncResponse("success",
                    String.format("Refreshed %d admin hierarchy entries and %d facilities from HFR", adminCount, facilityCount));
        } catch (Exception e) {
            LOGGER.error("Failed to refresh hierarchy from HFR", e);
            return new SyncResponse("error", "Failed to refresh hierarchy: " + e.getMessage());
        }
    }

    private synchronized Location ensureLocationExists(Location parentLocation, String name, String code, String tag) throws Exception {
        if (code == null || code.isEmpty()) {
            LOGGER.warn("Skipping {} creation because code is missing for {}", tag, name);
            return null;
        }

        Location existing = findLocationByCode(code);
        if (existing != null) {
            String desiredName = name != null ? name.trim() : "";
            String existingName = existing.getName() != null ? existing.getName().trim() : "";
            if (!existingName.equalsIgnoreCase(desiredName) && !desiredName.isEmpty()) {
                updateLocationName(existing, desiredName);
                existing.setName(desiredName);
            }

            if (existing.getParentLocation() == null && parentLocation != null) {
                LOGGER.warn("Location {} has no parent, setting to {}", existing.getName(), parentLocation.getName());
                updateChildLocationParent(existing, parentLocation.getLocationId());
                existing.setParentLocation(parentLocation);
            } else if (existing.getParentLocation() != null && parentLocation != null &&
                    !existing.getParentLocation().getLocationId().equals(parentLocation.getLocationId())) {
                LOGGER.warn("Location {} parent mismatch. Updating parent to {}", existing.getName(), parentLocation.getName());
                updateChildLocationParent(existing, parentLocation.getLocationId());
                existing.setParentLocation(parentLocation);
            }
            return existing;
        }

        if (parentLocation == null && !"Region".equalsIgnoreCase(tag)) {
            LOGGER.warn("Parent location missing for {} with name {}", tag, name);
            return null;
        }

        Map<String, String> attributes = new HashMap<>();
        if ("facility".equalsIgnoreCase(tag)) {
            attributes.put(hfrCodeLocationAttributeUuid, code);
        } else {
            attributes.put(codeLocationAttributeUuid, code);
        }

        Set<String> tags = new HashSet<>(Collections.singletonList(tag));
        String parentUuid = parentLocation != null ? parentLocation.getLocationId() : null;
        Location newLoc = createNewLocation(name, parentUuid, tags, attributes);

        if (newLoc != null) {
            LOGGER.info("Created new {}: {}", tag, name);
            allLocations.add(newLoc);
            addToCaches(newLoc);
        } else {
            LOGGER.warn("Failed creating new {}: {}", tag, name);
        }
        return newLoc;
    }

    private Location createNewLocation(String name, String parentUuid, Set<String> tags, Map<String, String> attributes) throws Exception {
        String url = OpenmrsClient.stripEndingSlash(openmrsBaseUrl) + "/ws/rest/v1/location";
        for (int attempt = 1; attempt <= DEFAULT_MAX_ATTEMPTS; attempt++) {
            HttpURLConnection conn = null;
            try {
                conn = openmrsClient.createConnection(url, "POST");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("description", "Created via HFR integration");
                if (parentUuid != null) {
                    json.put("parentLocation", parentUuid);
                }
                if (tags != null && !tags.isEmpty()) {
                    JSONArray tagArray = new JSONArray();
                    for (String tag : tags) {
                        tagArray.put(new JSONObject().put("name", tag));
                    }
                    json.put("tags", tagArray);
                }
                if (attributes != null && !attributes.isEmpty()) {
                    JSONArray attributesArray = new JSONArray();
                    for (Map.Entry<String, String> entry : attributes.entrySet()) {
                        JSONObject attrJson = new JSONObject();
                        attrJson.put("attributeType", entry.getKey());
                        attrJson.put("value", entry.getValue());
                        attributesArray.put(attrJson);
                    }
                    json.put("attributes", attributesArray);
                }

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                }
                int responseCode = conn.getResponseCode();
                LOGGER.info("Create location {} response code {}", name, responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        JSONObject createdLocation = new JSONObject(response.toString());
                        Location newLocation = new Location();
                        newLocation.setLocationId(createdLocation.getString("uuid"));
                        newLocation.setName(createdLocation.getString("name"));

                        Map<String, String> locAttributes = new HashMap<>();
                        if (attributes != null && !attributes.isEmpty()) {
                            if (attributes.get(codeLocationAttributeUuid) != null) {
                                locAttributes.put("Code", attributes.get(codeLocationAttributeUuid));
                            } else if (attributes.get(hfrCodeLocationAttributeUuid) != null) {
                                locAttributes.put("HFR Code", attributes.get(hfrCodeLocationAttributeUuid));
                            } else {
                                locAttributes.putAll(attributes);
                            }
                        }

                        newLocation.setAttributes(locAttributes);
                        newLocation.setTags(tags);
                        if (parentUuid != null) {
                            newLocation.setParentLocation(findLocationByUuid(parentUuid));
                        }
                        return newLocation;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error creating new location {}", name, e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        return null;
    }

    private void updateChildLocationParent(Location child, String newParentUuid) {
        String url = OpenmrsClient.stripEndingSlash(openmrsBaseUrl) + "/ws/rest/v1/location/" + child.getLocationId();
        for (int attempt = 1; attempt <= DEFAULT_MAX_ATTEMPTS; attempt++) {
            HttpURLConnection conn = null;
            try {
                conn = openmrsClient.createConnection(url, "POST");
                conn.setRequestProperty("Content-Type", "application/json");
                JSONObject parentLocationJson = new JSONObject();
                parentLocationJson.put("uuid", newParentUuid);
                JSONObject requestJson = new JSONObject();
                requestJson.put("parentLocation", parentLocationJson);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestJson.toString().getBytes(StandardCharsets.UTF_8));
                }
                int responseCode = conn.getResponseCode();
                LOGGER.info("Update child parent response code {}", responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Location newParent = findLocationByUuid(newParentUuid);
                    child.setParentLocation(newParent);
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("Error updating child parent for {}", child.getName(), e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private int updateLocationName(Location location, String newName) {
        String url = OpenmrsClient.stripEndingSlash(openmrsBaseUrl) + "/ws/rest/v1/location/" + location.getLocationId();
        for (int attempt = 1; attempt <= DEFAULT_MAX_ATTEMPTS; attempt++) {
            HttpURLConnection conn = null;
            try {
                conn = openmrsClient.createConnection(url, "POST");
                conn.setRequestProperty("Content-Type", "application/json");
                JSONObject requestJson = new JSONObject();
                requestJson.put("name", newName);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestJson.toString().getBytes(StandardCharsets.UTF_8));
                }
                int responseCode = conn.getResponseCode();
                LOGGER.info("Update location name response code {}", responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    location.setName(newName);
                    return responseCode;
                }
            } catch (Exception e) {
                LOGGER.error("Error updating location name for {}", location.getName(), e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        return -1;
    }

    private void retireOrUnretireIfNeeded(HfrFacilityPayload payload, Location facilityLoc) {
        if (facilityLoc == null) {
            return;
        }
        String status = payload.getOperatingStatus();
        if (status == null) {
            return;
        }
        String trimmed = status.trim();
        if (!trimmed.equalsIgnoreCase("Operating")) {
            String reason = "Operating status: " + trimmed;
            openmrsClient.retireLocation(facilityLoc.getLocationId(), reason);
        } else {
            openmrsClient.unretireLocation(facilityLoc.getLocationId());
        }
    }

    private void addToCaches(Location location) {
        if (location == null || location.getLocationId() == null) {
            return;
        }
        locationCache.put(location.getLocationId().toLowerCase(), location);
        if (location.getAttributes() != null) {
            if (location.getAttributes().get("Code") != null) {
                codeCache.put(location.getAttributes().get("Code").toLowerCase(), location);
            } else if (location.getAttributes().get("HFR Code") != null) {
                codeCache.put(location.getAttributes().get("HFR Code").toLowerCase(), location);
            }
        }
    }

    public synchronized void refreshLocationCache() {
        allLocations = fetchLocationsHelper.getAllOpenMRSlocations();
        locationCache.clear();
        codeCache.clear();
        for (Location loc : allLocations) {
            addToCaches(loc);
        }
        LOGGER.info("Loaded {} locations from OpenMRS", allLocations.size());
    }

    private Location findLocationByCode(String code) {
        if (code == null) return null;
        return codeCache.get(code.toLowerCase());
    }

    private Location findLocationByUuid(String uuid) {
        if (uuid == null) return null;
        return locationCache.get(uuid.toLowerCase());
    }

    private String buildWardName(HfrFacilityPayload payload) {
        if (payload.getWard() == null) {
            return null;
        }
        return CapitalizeUtil.capitalizeWords(String.format("%s - %s", payload.getWard(), valueOrEmpty(payload.getCouncil())));
    }

    private String buildVillageName(HfrFacilityPayload payload) {
        if (payload.getVillage() == null) {
            return null;
        }
        return CapitalizeUtil.capitalizeWords(String.format("%s - %s - %s",
                payload.getVillage(),
                valueOrEmpty(payload.getWard()),
                valueOrEmpty(payload.getCouncil())));
    }

    private String buildFacilityName(HfrFacilityPayload payload) {
        return CapitalizeUtil.capitalizeWords(String.format("%s - %s", payload.getName(), payload.getFacIdNumber()));
    }

    private String resolveWardCode(HfrFacilityPayload payload) {
        if (payload.getWardCode() != null && !payload.getWardCode().isBlank()) {
            return payload.getWardCode();
        }
        if (payload.getVillageCode() != null && payload.getVillageCode().contains(".")) {
            int idx = payload.getVillageCode().lastIndexOf(".");
            if (idx > 0) {
                return payload.getVillageCode().substring(0, idx);
            }
        }
        return payload.getCouncilCode();
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

    public void processAdminHierarchyData(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject facilityJson = response.getJSONObject(i);
            try {
                Location countryLoc = ensureLocationExists(null,
                        CapitalizeUtil.capitalizeWords(facilityJson.optString("country")),
                        "TZ", "Country");
                Location zoneLoc = ensureLocationExists(countryLoc,
                        CapitalizeUtil.capitalizeWords(facilityJson.optString("zone")),
                        facilityJson.optString("zone_code"),
                        "Zone");
                Location regionLoc = ensureLocationExists(zoneLoc,
                        CapitalizeUtil.capitalizeWords(facilityJson.optString("region")),
                        facilityJson.optString("region_code"),
                        "Region");
                Location wardLoc = ensureLocationExists(regionLoc,
                        CapitalizeUtil.capitalizeWords(facilityJson.optString("ward") + " - " + facilityJson.optString("council")),
                        facilityJson.optString("ward_code"),
                        "Ward");
                ensureLocationExists(wardLoc,
                        CapitalizeUtil.capitalizeWords(facilityJson.optString("village_mtaa") + " - " + facilityJson.optString("ward") + " - " + facilityJson.optString("council")),
                        facilityJson.optString("village_mtaa_code"),
                        "Village");
            } catch (Exception e) {
                LOGGER.error("Error processing admin hierarchy {}", e.getMessage());
            }
        }
    }

    public void processHfrResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject facilityJson = response.getJSONObject(i);
            try {
                HfrFacilityPayload payload = CustomJacksonObjectMapper.mapper.readValue(
                        facilityJson.toString(),
                        HfrFacilityPayload.class);
                syncFacility(payload);
            } catch (Exception e) {
                LOGGER.error("Error processing HFR facility {}", e.getMessage());
            }
        }
    }
}
