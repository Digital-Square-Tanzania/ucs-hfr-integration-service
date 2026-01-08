package com.abt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabRequestMetadata {
    @JsonProperty("base_entity_id")
    private String baseEntityId;

    @JsonProperty("sample_type")
    private String sampleType;

    @JsonProperty("sample_id")
    private String sampleId;

    @JsonProperty("location_id")
    private String locationId;

    @JsonProperty("provider_id")
    private String providerId;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("team")
    private String team;

    @JsonProperty("RequestID")
    private String requestID;

    @JsonProperty("SpecimenDateTime")
    private String specimenDateTime;

    @JsonProperty("ReceivedDateTime")
    private String receivedDateTime;

    @JsonProperty("RegisteredDateTime")
    private String registeredDateTime;

    @JsonProperty("AnalysisDateTime")
    private String analysisDateTime;

    @JsonProperty("AuthorisedDateTime")
    private String authorisedDateTime;

    @JsonProperty("AdmitAttendDateTime")
    private String admitAttendDateTime;

    @JsonProperty("CollectionVolume")
    private String collectionVolume;

    @JsonProperty("ReceivingFacilityCode")
    private String receivingFacilityCode;

    @JsonProperty("RegisteredBy")
    private String registeredBy;

    @JsonProperty("TestedBy")
    private String testedBy;

    @JsonProperty("AuthorisedBy")
    private String authorisedBy;

    @JsonProperty("OrderingNotes")
    private String orderingNotes;

    @JsonProperty("EncryptedPatientID")
    private String encryptedPatientID;

    @JsonProperty("TestingFacilityCode")
    private String testingFacilityCode;

    @JsonProperty("ReferringRequestID")
    private String referringRequestID;

    @JsonProperty("LIMSRejectionCode")
    private String LIMSRejectionCode;

    @JsonProperty("LIMSRejectionDesc")
    private String LIMSRejectionDesc;

    @JsonProperty("LIMSFacilityCode")
    private String LIMSFacilityCode;

    @JsonProperty("Repeated")
    private String repeated;

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getSpecimenDateTime() {
        return specimenDateTime;
    }

    public void setSpecimenDateTime(String specimenDateTime) {
        this.specimenDateTime = specimenDateTime;
    }

    public String getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(String receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }

    public String getRegisteredDateTime() {
        return registeredDateTime;
    }

    public void setRegisteredDateTime(String registeredDateTime) {
        this.registeredDateTime = registeredDateTime;
    }

    public String getAnalysisDateTime() {
        return analysisDateTime;
    }

    public void setAnalysisDateTime(String analysisDateTime) {
        this.analysisDateTime = analysisDateTime;
    }

    public String getAuthorisedDateTime() {
        return authorisedDateTime;
    }

    public void setAuthorisedDateTime(String authorisedDateTime) {
        this.authorisedDateTime = authorisedDateTime;
    }

    public String getAdmitAttendDateTime() {
        return admitAttendDateTime;
    }

    public void setAdmitAttendDateTime(String admitAttendDateTime) {
        this.admitAttendDateTime = admitAttendDateTime;
    }

    public String getCollectionVolume() {
        return collectionVolume;
    }

    public void setCollectionVolume(String collectionVolume) {
        this.collectionVolume = collectionVolume;
    }

    public String getReceivingFacilityCode() {
        return receivingFacilityCode;
    }

    public void setReceivingFacilityCode(String receivingFacilityCode) {
        this.receivingFacilityCode = receivingFacilityCode;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getTestedBy() {
        return testedBy;
    }

    public void setTestedBy(String testedBy) {
        this.testedBy = testedBy;
    }

    public String getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(String authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public String getOrderingNotes() {
        return orderingNotes;
    }

    public void setOrderingNotes(String orderingNotes) {
        this.orderingNotes = orderingNotes;
    }

    public String getEncryptedPatientID() {
        return encryptedPatientID;
    }

    public void setEncryptedPatientID(String encryptedPatientID) {
        this.encryptedPatientID = encryptedPatientID;
    }

    public String getTestingFacilityCode() {
        return testingFacilityCode;
    }

    public void setTestingFacilityCode(String testingFacilityCode) {
        this.testingFacilityCode = testingFacilityCode;
    }

    public String getReferringRequestID() {
        return referringRequestID;
    }

    public void setReferringRequestID(String referringRequestID) {
        this.referringRequestID = referringRequestID;
    }

    public String getLIMSRejectionCode() {
        return LIMSRejectionCode;
    }

    public void setLIMSRejectionCode(String LIMSRejectionCode) {
        this.LIMSRejectionCode = LIMSRejectionCode;
    }

    public String getLIMSRejectionDesc() {
        return LIMSRejectionDesc;
    }

    public void setLIMSRejectionDesc(String LIMSRejectionDesc) {
        this.LIMSRejectionDesc = LIMSRejectionDesc;
    }

    public String getLIMSFacilityCode() {
        return LIMSFacilityCode;
    }

    public void setLIMSFacilityCode(String LIMSFacilityCode) {
        this.LIMSFacilityCode = LIMSFacilityCode;
    }

    public String getRepeated() {
        return repeated;
    }

    public void setRepeated(String repeated) {
        this.repeated = repeated;
    }
}
