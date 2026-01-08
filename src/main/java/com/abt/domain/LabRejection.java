package com.abt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LabRejection extends LabRequestMetadata {
    @JsonProperty("LIMSRejectionArray")
    private List<LIMSRejection> LIMSRejectionArray;

    public List<LIMSRejection> getLIMSRejectionArray() {
        return LIMSRejectionArray;
    }

    public void setLIMSRejectionArray(List<LIMSRejection> LIMSRejectionArray) {
        this.LIMSRejectionArray = LIMSRejectionArray;
    }

    public static class LIMSRejection {
        @JsonProperty("Code")
        private String code;


        @JsonProperty("Description")
        private String description;

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
