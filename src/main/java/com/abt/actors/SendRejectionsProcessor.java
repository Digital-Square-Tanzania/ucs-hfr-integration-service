package com.abt.actors;

import com.abt.UcsLabIntegrationRoutes;
import com.abt.domain.Event;
import com.abt.domain.EventRequest;
import com.abt.domain.LabRejection;
import com.abt.util.OpenSrpService;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.abt.util.OpenSrpService.sendDataToDestination;

public class SendRejectionsProcessor {
    public String sendRejection(LabRejection labRejection, String url, String username, String password) {
        Event rejectionEvent = null;
        try {
            rejectionEvent = OpenSrpService.getLabRejectionEvent(labRejection);
            List<Event> events = new ArrayList<>();
            events.add(rejectionEvent);
            return sendDataToDestination(new EventRequest(events), url, username, password);
        } catch (Exception e) {
            LoggerFactory.getLogger(UcsLabIntegrationRoutes.class).error(e.getMessage(), e);
            return "Internal Error while processing the payload";
        }
    }
}
