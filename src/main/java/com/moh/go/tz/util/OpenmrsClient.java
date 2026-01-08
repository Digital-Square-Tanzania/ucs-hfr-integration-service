package com.moh.go.tz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public class OpenmrsClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenmrsClient.class);

    private final String baseUrl;
    private final String username;
    private final String password;

    public OpenmrsClient(String baseUrl, String username, String password) {
        this.baseUrl = stripEndingSlash(baseUrl);
        this.username = username;
        this.password = password;
    }

    public HttpURLConnection createConnection(String url, String method) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + basicAuth);
        return conn;
    }

    public String get(String relativePath) throws IOException {
        String url = stripEndingSlash(baseUrl) + "/" + stripLeadingSlash(relativePath);
        return getAbsolute(url);
    }

    public String getAbsolute(String url) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = createConnection(url, "GET");
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return in.lines().collect(Collectors.joining());
                }
            }
            LOGGER.error("GET {} failed with status {}", url, responseCode);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String stripEndingSlash(String value) {
        if (value == null) return null;
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static String stripLeadingSlash(String value) {
        if (value == null) return null;
        return value.startsWith("/") ? value.substring(1) : value;
    }

    /**
     * Retire an OpenMRS location with a reason.
     */
    public boolean retireLocation(String uuid, String reason) {
        String reasonParam = reason != null ? URLEncoder.encode(reason, StandardCharsets.UTF_8) : "";
        String url = stripEndingSlash(baseUrl) + "/ws/rest/v1/location/" + uuid + "?reason=" + reasonParam;
        HttpURLConnection conn = null;
        try {
            conn = createConnection(url, "DELETE");
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                LOGGER.info("Retired location {} with reason {}", uuid, reason);
                return true;
            }
            LOGGER.error("Failed to retire location {} status {}", uuid, code);
        } catch (Exception e) {
            LOGGER.error("Error retiring location {}", uuid, e);
        } finally {
            if (conn != null) conn.disconnect();
        }
        return false;
    }

    /**
     * Unretire an OpenMRS location.
     */
    public boolean unretireLocation(String uuid) {
        String url = stripEndingSlash(baseUrl) + "/ws/rest/v1/location/" + uuid;
        HttpURLConnection conn = null;
        try {
            conn = createConnection(url, "POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String payload = "{\"retired\":false}";
            conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                LOGGER.info("Unretired location {}", uuid);
                return true;
            }
            LOGGER.error("Failed to unretire location {} status {}", uuid, code);
        } catch (Exception e) {
            LOGGER.error("Error unretiring location {}", uuid, e);
        } finally {
            if (conn != null) conn.disconnect();
        }
        return false;
    }
}
