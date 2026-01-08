package com.moh.go.tz.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HfrFacilityPayload {

    @JsonProperty("Fac_IDNumber")
    private String facIdNumber;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Comm_FacName")
    private String commFacName;
    @JsonProperty("Zone")
    private String zone;
    @JsonProperty("Region_Code")
    private String regionCode;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("District_Code")
    private String districtCode;
    @JsonProperty("District")
    private String district;
    @JsonProperty("Council_Code")
    private String councilCode;
    @JsonProperty("Council")
    private String council;
    @JsonProperty("Ward")
    private String ward;
    @JsonAlias({"Ward_Code", "ward_Code"})
    private String wardCode;
    @JsonProperty("Village")
    private String village;
    @JsonProperty("Village_Code")
    private String villageCode;
    @JsonProperty("FacilityTypeGroupCode")
    private String facilityTypeGroupCode;
    @JsonProperty("FacilityTypeGroup")
    private String facilityTypeGroup;
    @JsonProperty("FacilityTypeCode")
    private String facilityTypeCode;
    @JsonProperty("FacilityType")
    private String facilityType;
    @JsonProperty("OwnershipGroupCode")
    private String ownershipGroupCode;
    @JsonProperty("OwnershipGroup")
    private String ownershipGroup;
    @JsonProperty("OwnershipCode")
    private String ownershipCode;
    @JsonProperty("Ownership")
    private String ownership;
    @JsonProperty("OperatingStatus")
    private String operatingStatus;
    @JsonProperty("Latitude")
    private String latitude;
    @JsonProperty("Longitude")
    private String longitude;
    @JsonProperty("RegistrationStatus")
    private String registrationStatus;
    @JsonProperty("OpenedDate")
    private String openedDate;
    @JsonProperty("CreatedAt")
    private String createdAt;
    @JsonProperty("UpdatedAt")
    private String updatedAt;
    @JsonProperty("Vote")
    private String vote;
    @JsonProperty("IsDesignated")
    private Integer isDesignated;
    @JsonProperty("ClosedDate")
    private String closedDate;
    @JsonProperty("OSchangeOpenedtoClose")
    private String osChangeOpenedToClose;
    @JsonProperty("OSchangeClosedtoOperational")
    private String osChangeClosedToOperational;
    @JsonProperty("PostorUpdate")
    private String postOrUpdate;

    public String getFacIdNumber() {
        return facIdNumber;
    }

    public String getName() {
        return name;
    }

    public String getCommFacName() {
        return commFacName;
    }

    public String getZone() {
        return zone;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public String getDistrict() {
        return district;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public String getCouncil() {
        return council;
    }

    public String getWard() {
        return ward;
    }

    public String getWardCode() {
        return wardCode;
    }

    public String getVillage() {
        return village;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public String getFacilityTypeGroupCode() {
        return facilityTypeGroupCode;
    }

    public String getFacilityTypeGroup() {
        return facilityTypeGroup;
    }

    public String getFacilityTypeCode() {
        return facilityTypeCode;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getOwnershipGroupCode() {
        return ownershipGroupCode;
    }

    public String getOwnershipGroup() {
        return ownershipGroup;
    }

    public String getOwnershipCode() {
        return ownershipCode;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getOperatingStatus() {
        return operatingStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getVote() {
        return vote;
    }

    public Integer getIsDesignated() {
        return isDesignated;
    }

    public String getClosedDate() {
        return closedDate;
    }

    public String getOsChangeOpenedToClose() {
        return osChangeOpenedToClose;
    }

    public String getOsChangeClosedToOperational() {
        return osChangeClosedToOperational;
    }

    public String getPostOrUpdate() {
        return postOrUpdate;
    }
}
