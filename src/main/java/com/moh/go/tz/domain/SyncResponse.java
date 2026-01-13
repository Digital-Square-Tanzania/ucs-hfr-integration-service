package com.moh.go.tz.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncResponse {
    private final String status;
    private final String message;

    @JsonCreator
    public SyncResponse(@JsonProperty("status") String status,
                        @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
