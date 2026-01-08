package com.abt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LabResult extends LabRequestMetadata {
    @JsonProperty("TBLResults")
    private List<TBLResult> TBLResults;

    public List<TBLResult> getTBLResults() {
        return TBLResults;
    }

    public void setTBLResults(List<TBLResult> TBLResults) {
        this.TBLResults = TBLResults;
    }

    public static class TBLResult {
        @JsonProperty("SIValue")
        private int SIValue;

        @JsonProperty("SIUnits")
        private String SIUnits;

        @JsonProperty("SILoRange")
        private String SILoRange;

        @JsonProperty("SIHiRange")
        private String SIHiRange;

        @JsonProperty("DateTimeValue")
        private String DateTimeValue;

        @JsonProperty("CodedValue")
        private String codedValue;

        @JsonProperty("ResultSemiquantitive")
        private String resultSemiquantitive;

        @JsonProperty("Note")
        private boolean note;

        @JsonProperty("LIMSObservationCode")
        private String LIMSObservationCode;

        @JsonProperty("LIMSObservationDesc")
        private String LIMSObservationDesc;

        @JsonProperty("LIMSRptResult")
        private String LIMSRptResult;

        @JsonProperty("LIMSRptUnits")
        private String LIMSRptUnits;

        @JsonProperty("LIMSRptFlag")
        private String LIMSRptFlag;

        @JsonProperty("LIMSRptRange")
        private String LIMSRptRange;

        @JsonProperty("LIMSCodedValue")
        private String LIMSCodedValue;

        @JsonProperty("WorkUnits")
        private String workUnits;

        @JsonProperty("CostUnits")
        private String costUnits;

        public int getSIValue() {
            return SIValue;
        }

        public void setSIValue(int SIValue) {
            this.SIValue = SIValue;
        }

        public String getSIUnits() {
            return SIUnits;
        }

        public void setSIUnits(String SIUnits) {
            this.SIUnits = SIUnits;
        }

        public String getSILoRange() {
            return SILoRange;
        }

        public void setSILoRange(String SILoRange) {
            this.SILoRange = SILoRange;
        }

        public String getSIHiRange() {
            return SIHiRange;
        }

        public void setSIHiRange(String SIHiRange) {
            this.SIHiRange = SIHiRange;
        }

        public String getDateTimeValue() {
            return DateTimeValue;
        }

        public void setDateTimeValue(String dateTimeValue) {
            DateTimeValue = dateTimeValue;
        }

        public String getCodedValue() {
            return codedValue;
        }

        public void setCodedValue(String codedValue) {
            this.codedValue = codedValue;
        }

        public String getResultSemiquantitive() {
            return resultSemiquantitive;
        }

        public void setResultSemiquantitive(String resultSemiquantitive) {
            this.resultSemiquantitive = resultSemiquantitive;
        }

        public boolean isNote() {
            return note;
        }

        public void setNote(boolean note) {
            this.note = note;
        }

        public String getLIMSObservationCode() {
            return LIMSObservationCode;
        }

        public void setLIMSObservationCode(String LIMSObservationCode) {
            this.LIMSObservationCode = LIMSObservationCode;
        }

        public String getLIMSObservationDesc() {
            return LIMSObservationDesc;
        }

        public void setLIMSObservationDesc(String LIMSObservationDesc) {
            this.LIMSObservationDesc = LIMSObservationDesc;
        }

        public String getLIMSRptResult() {
            return LIMSRptResult;
        }

        public void setLIMSRptResult(String LIMSRptResult) {
            this.LIMSRptResult = LIMSRptResult;
        }

        public String getLIMSRptUnits() {
            return LIMSRptUnits;
        }

        public void setLIMSRptUnits(String LIMSRptUnits) {
            this.LIMSRptUnits = LIMSRptUnits;
        }

        public String getLIMSRptFlag() {
            return LIMSRptFlag;
        }

        public void setLIMSRptFlag(String LIMSRptFlag) {
            this.LIMSRptFlag = LIMSRptFlag;
        }

        public String getLIMSRptRange() {
            return LIMSRptRange;
        }

        public void setLIMSRptRange(String LIMSRptRange) {
            this.LIMSRptRange = LIMSRptRange;
        }

        public String getLIMSCodedValue() {
            return LIMSCodedValue;
        }

        public void setLIMSCodedValue(String LIMSCodedValue) {
            this.LIMSCodedValue = LIMSCodedValue;
        }

        public String getWorkUnits() {
            return workUnits;
        }

        public void setWorkUnits(String workUnits) {
            this.workUnits = workUnits;
        }

        public String getCostUnits() {
            return costUnits;
        }

        public void setCostUnits(String costUnits) {
            this.costUnits = costUnits;
        }
    }
}
