package com.abt.service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.function.Consumer;

public class HfrClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HfrClient.class);

    private final String baseUrlGetHealthFacilities;
    private final String baseUrlGetHierarchy;
    private final String username;
    private final String password;

    public HfrClient(Config config) {
        Config hfrConfig = config.hasPath("hfr") ? config.getConfig("hfr") : config;
        try {
            this.baseUrlGetHealthFacilities = hfrConfig.getString("base-url-health-facilities");
            this.baseUrlGetHierarchy = hfrConfig.getString("base-url-hierarchy");
            this.username = hfrConfig.getString("username");
            this.password = hfrConfig.getString("password");
        } catch (ConfigException.Missing e) {
            throw new IllegalStateException("Missing required HFR configuration", e);
        }
    }

    public int fetchHealthFacilityData(Consumer<JSONArray> consumer) throws Exception {
        return fetchPagedData(baseUrlGetHealthFacilities, consumer);
    }

    public int fetchAdminHierarchyData(Consumer<JSONArray> consumer) throws Exception {
        return fetchPagedData(baseUrlGetHierarchy, consumer);
    }

    private int fetchPagedData(String baseUrl, Consumer<JSONArray> consumer) throws Exception {
        int page = 1;
        int totalPageCount;
        int processed = 0;
        do {
            LOGGER.info("Fetching HFR data from {} page {}", baseUrl, page);
            String url = baseUrl + page;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + getBasicAuth());
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    JSONObject rootObject = new JSONObject(response.toString());
                    JSONObject metaDataObject = rootObject.getJSONObject("metaData");
                    totalPageCount = metaDataObject.getInt("pageCount");
                    page = metaDataObject.getInt("currentPage");

                    JSONArray dataArray = rootObject.getJSONArray("data");
                    consumer.accept(dataArray);
                    processed += dataArray.length();
                }

                page++;
            } else {
                throw new Exception("Failed to fetch data. HTTP response code: " + responseCode);
            }

            connection.disconnect();
        } while (page <= totalPageCount);
        return processed;
    }

    private String getBasicAuth() {
        String auth = username + ":" + password;
        return Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
