package com.moh.go.tz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
}
