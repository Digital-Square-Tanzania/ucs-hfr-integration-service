package com.moh.go.tz.service;

import com.moh.go.tz.domain.HfrFacilityPayload;
import com.moh.go.tz.util.CustomJacksonObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocationSyncServiceTest {

    @Test
    public void resolveWardCodeDoesNotFallbackToCouncilCode() throws Exception {
        HfrFacilityPayload payload = CustomJacksonObjectMapper.mapper.readValue(
                "{"
                        + "\"Fac_IDNumber\":\"124746-9\","
                        + "\"Name\":\"Morogoro DC\","
                        + "\"District_Code\":\"TZ.ET.MO.MG\","
                        + "\"Council_Code\":\"TZ.ET.MO.MG.2\","
                        + "\"Ward_Code\":null,"
                        + "\"Ward\":null,"
                        + "\"Village_Code\":null,"
                        + "\"Village\":null"
                        + "}",
                HfrFacilityPayload.class);

        assertNull(LocationSyncService.resolveWardCode(payload));
    }

    @Test
    public void resolveWardCodeDerivesFromVillageCodeWhenWardCodeIsMissing() throws Exception {
        HfrFacilityPayload payload = CustomJacksonObjectMapper.mapper.readValue(
                "{"
                        + "\"Council_Code\":\"TZ.ET.MO.MG.2\","
                        + "\"Ward_Code\":null,"
                        + "\"Village_Code\":\"TZ.ET.MO.MG.2.3.1\""
                        + "}",
                HfrFacilityPayload.class);

        assertEquals("TZ.ET.MO.MG.2.3", LocationSyncService.resolveWardCode(payload));
    }

    @Test
    public void resolveWardCodeUsesExplicitWardCode() throws Exception {
        HfrFacilityPayload payload = CustomJacksonObjectMapper.mapper.readValue(
                "{"
                        + "\"Council_Code\":\"TZ.ET.MO.MG.2\","
                        + "\"Ward_Code\":\" TZ.ET.MO.MG.2.3 \","
                        + "\"Village_Code\":\"TZ.ET.MO.MG.2.3.1\""
                        + "}",
                HfrFacilityPayload.class);

        assertEquals("TZ.ET.MO.MG.2.3", LocationSyncService.resolveWardCode(payload));
    }
}
