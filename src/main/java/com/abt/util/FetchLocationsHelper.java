package com.abt.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.abt.domain.Location;

import java.util.ArrayList;
import java.util.List;

import static com.abt.util.ConnectorConstants.*;

public class FetchLocationsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchLocationsHelper.class);
    private final OpenmrsClient client;

    public FetchLocationsHelper(OpenmrsClient client) {
        this.client = client;
    }

    public List<Location> getAllOpenMRSlocations() {
        List<Location> allLocationsList = new ArrayList<>();
        return getAllLocations(allLocationsList, 0);
    }

    protected String fetchLocationResponse(int startIndex) throws Exception {
        String url = "ws/rest/v1/location"
                + "?v=custom:(uuid,display,name,attributes,tags:(uuid,display),parentLocation:(uuid,display))"
                + "&limit=10000&startIndex=" + startIndex;
        return client.get(url);
    }

    protected List<Location> parseLocationsFromResponse(String response, List<Location> locationList) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (StringUtils.isNotBlank(response) && jsonObject.has(RESULTS)) {
            JSONArray results = jsonObject.getJSONArray(RESULTS);
            for (int i = 0; i < results.length(); i++) {
                locationList.add(makeLocation(results.getJSONObject(i)));
            }
        }
        return locationList;
    }

    protected boolean hasNextPage(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("links")) {
            JSONArray links = jsonObject.getJSONArray("links");
            for (int i = 0; i < links.length(); i++) {
                if ("next".equalsIgnoreCase(links.getJSONObject(i).optString("rel"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Location> getAllLocations(List<Location> locationList, int startIndex) throws JSONException {
        try {
            LOGGER.info("Fetching locations from OpenMRS starting at index {}", startIndex);
            String response = fetchLocationResponse(startIndex);
            if (StringUtils.isNotBlank(response)) {
                List<Location> updatedLocationList = parseLocationsFromResponse(response, locationList);
                if (hasNextPage(response)) {
                    return getAllLocations(updatedLocationList, startIndex + 10000);
                }
                return updatedLocationList;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching OpenMRS locations, retrying. {}", e.getMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
            return getAllLocations(locationList, startIndex);
        }
        return locationList;
    }

    public Location makeLocation(String locationJson) throws JSONException {
        JSONObject locationsJsonObject = new JSONObject(locationJson);
        Location parentLocation = getParent(locationsJsonObject);
        Location location = new Location(locationsJsonObject.getString(UUID),
                locationsJsonObject.getString(NAME), parentLocation);
        JSONArray tags = locationsJsonObject.getJSONArray(TAGS);

        for (int i = 0; i < tags.length(); i++) {
            location.addTag(tags.getJSONObject(i).getString(DISPLAY));
        }

        if (locationsJsonObject.has(ATTRIBUTES)) {
            JSONArray attributes = locationsJsonObject.getJSONArray(ATTRIBUTES);
            for (int i = 0; i < attributes.length(); i++) {
                JSONObject attribute = attributes.getJSONObject(i);
                boolean voided = attribute.optBoolean(VOIDED);
                if (!voided) {
                    String ad = attribute.getString(DISPLAY);
                    int delimiterIndex = ad.indexOf(":");
                    if (delimiterIndex > 0 && delimiterIndex + 2 <= ad.length()) {
                        location.addAttribute(ad.substring(0, delimiterIndex), ad.substring(delimiterIndex + 2));
                    }
                }
            }
        }

        return location;
    }

    public Location makeLocation(JSONObject location) throws JSONException {
        return makeLocation(location.toString());
    }

    public Location getParent(JSONObject locobj) throws JSONException {
        JSONObject parentL = (locobj.has(PARENT_LOCATION)
                && !locobj.isNull(PARENT_LOCATION))
                ? locobj.getJSONObject(PARENT_LOCATION)
                : null;

        if (parentL != null) {
            return new Location(parentL.getString(UUID), parentL.getString(DISPLAY),
                    getParent(parentL));
        }
        return null;
    }
}
