package com.abt.actors;

import com.abt.domain.Event;
import com.abt.domain.EventRequest;
import com.abt.domain.LabResult;
import com.abt.util.OpenSrpService;

import java.util.ArrayList;
import java.util.List;

import static com.abt.util.OpenSrpService.sendDataToDestination;

public class SendResultsProcessor {
    public String sendResults(LabResult labResult, String url, String username, String password) {
        try {
            Event resultEvent = OpenSrpService.getLabResultsEvent(labResult);

            List<Event> events = new ArrayList<>();
            events.add(resultEvent);


            return sendDataToDestination(new EventRequest(events), url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Error while processing the payload";
        }
    }
}
