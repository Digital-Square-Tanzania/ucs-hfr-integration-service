package com.abt;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.abt.domain.LabRejection;
import com.abt.domain.LabResult;
import com.abt.util.CustomJacksonSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

/**
 * Routes can be defined in separated classes like shown in here
 */
public class UcsLabIntegrationRoutes {
    //#routes-class
    private final static Logger log = LoggerFactory.getLogger(UcsLabIntegrationRoutes.class);
    private final ActorRef<UcsLabIntegrationRegistry.Command> labIntegrationActor;
    private final Duration askTimeout;
    private final Scheduler scheduler;
    private final String url;
    private final String username;
    private final String password;

    public UcsLabIntegrationRoutes(ActorSystem<?> system, ActorRef<UcsLabIntegrationRegistry.Command> labIntegrationActor) {
        this.labIntegrationActor = labIntegrationActor;
        scheduler = system.scheduler();
        askTimeout = system.settings().config().getDuration("integration-service.routes.ask-timeout");
        url = system.settings().config().getString("integration-service.destination.url");
        username = system.settings().config().getString("integration-service.destination.username");
        password = system.settings().config().getString("integration-service.destination.password");

    }

    private CompletionStage<UcsLabIntegrationRegistry.ActionPerformed> sendResult(LabResult result) {
        return AskPattern.ask(labIntegrationActor, ref -> new UcsLabIntegrationRegistry.SendResults(result, url, username, password, ref), askTimeout, scheduler);
    }

    private CompletionStage<UcsLabIntegrationRegistry.ActionPerformed> sendRejection(LabRejection rejection) {
        return AskPattern.ask(labIntegrationActor, ref -> new UcsLabIntegrationRegistry.SendRejections(rejection, url, username, password, ref), askTimeout, scheduler);
    }

    /**
     * This method creates one route (of possibly many more that will be part of your Web App)
     */
    public Route labResultsRejectionRoutes() {
        return pathPrefix("send", () ->
                concat(
                        //#send-results
                        pathSuffix("-results", () ->
                                concat(
                                        post(() ->
                                                entity(
                                                        CustomJacksonSupport.customJacksonUnmarshaller(LabResult.class),
                                                        result ->
                                                                onSuccess(sendResult(result), performed -> {
                                                                    log.info("Sent Results: {}", performed.description());
                                                                    if (performed.description().toLowerCase().contains("error")) {
                                                                        return complete(StatusCodes.BAD_REQUEST, performed, Jackson.marshaller());
                                                                    } else {
                                                                        return complete(StatusCodes.OK, performed, Jackson.marshaller());
                                                                    }
                                                                })
                                                )
                                        )
                                )
                        ),
                        //#send-rejections
                        pathSuffix("-rejections", () ->
                                concat(
                                        post(() ->
                                                entity(
                                                        Jackson.unmarshaller(LabRejection.class),
                                                        rejection ->
                                                                onSuccess(sendRejection(rejection), performed -> {
                                                                    log.info("Sent Rejections: {}", performed.description());
                                                                    if (performed.description().toLowerCase().contains("error")) {
                                                                        return complete(StatusCodes.BAD_REQUEST, performed, Jackson.marshaller());
                                                                    } else {
                                                                        return complete(StatusCodes.OK, performed, Jackson.marshaller());
                                                                    }
                                                                })
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
