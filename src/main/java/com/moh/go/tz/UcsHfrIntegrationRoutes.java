package com.moh.go.tz;

import akka.actor.typed.ActorSystem;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.moh.go.tz.domain.HfrFacilityPayload;
import com.moh.go.tz.domain.SyncResponse;
import com.moh.go.tz.service.LocationSyncService;
import com.moh.go.tz.util.CustomJacksonSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static akka.http.javadsl.server.Directives.*;

/**
 * Routes that expose HFR facility sync endpoints.
 */
public class UcsHfrIntegrationRoutes {
    //#routes-class
    private final static Logger log = LoggerFactory.getLogger(UcsHfrIntegrationRoutes.class);
    private final LocationSyncService locationSyncService;
    private final Executor executor;

    public UcsHfrIntegrationRoutes(ActorSystem<?> system, LocationSyncService locationSyncService) {
        this.locationSyncService = locationSyncService;
        executor = system.classicSystem().dispatcher();
    }

    private CompletionStage<SyncResponse> syncFacility(HfrFacilityPayload payload) {
        return CompletableFuture.supplyAsync(() -> locationSyncService.syncFacility(payload), executor);
    }

    private CompletionStage<SyncResponse> refreshHierarchy() {
        return CompletableFuture.supplyAsync(locationSyncService::refreshFromHfr, executor);
    }

    private Route completeResponse(SyncResponse response) {
        var status = "error".equalsIgnoreCase(response.getStatus()) ? StatusCodes.BAD_REQUEST : StatusCodes.OK;
        log.info("HFR sync responded with {} - {}", response.getStatus(), response.getMessage());
        return complete(status, response, CustomJacksonSupport.customJacksonMarshaller());
    }

    /**
     * Routes that expose HFR endpoints.
     */
    public Route routes() {
        return concat(
                pathPrefix("hfr", () ->
                        concat(
                                path("facility", () ->
                                        post(() ->
                                                entity(CustomJacksonSupport.customJacksonUnmarshaller(HfrFacilityPayload.class),
                                                        payload -> onSuccess(syncFacility(payload), this::completeResponse))
                                        )
                                ),
                                path("refresh-hierarchy", () ->
                                        post(() -> onSuccess(refreshHierarchy(), this::completeResponse))
                                )
                        )
                )
        );
    }
}
