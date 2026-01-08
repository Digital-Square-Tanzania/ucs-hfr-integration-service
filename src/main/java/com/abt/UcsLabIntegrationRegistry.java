package com.abt;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.abt.actors.SendRejectionsProcessor;
import com.abt.actors.SendResultsProcessor;
import com.abt.domain.LabRejection;
import com.abt.domain.LabResult;

//#UCSLab-registry-actor
public class UcsLabIntegrationRegistry extends AbstractBehavior<UcsLabIntegrationRegistry.Command> {

    private UcsLabIntegrationRegistry(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(UcsLabIntegrationRegistry::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(SendResults.class, this::onSendResults)
                .onMessage(SendRejections.class, this::onSendRejection)
                .build();
    }

    private Behavior<Command> onSendResults(SendResults command) {
        String response = new SendResultsProcessor().sendResults(command.result, command.url, command.username, command.password);
        command.replyTo().tell(new ActionPerformed(String.format(response)));
        return this;
    }

    //#user-case-classes

    private Behavior<Command> onSendRejection
            (SendRejections command) {
        String response = new SendRejectionsProcessor().sendRejection(command.rejection, command.url, command.username, command.password);
        command.replyTo().tell(new ActionPerformed(String.format(response)));
        return this;
    }

    // actor protocol
    sealed interface Command {
    }

    public final static record SendResults(LabResult result,
                                           String url, String username, String password,
                                           ActorRef<ActionPerformed> replyTo) implements Command {
    }

    public final static record SendRejections(LabRejection rejection, String url, String username, String password,
                                              ActorRef<ActionPerformed> replyTo) implements Command {
    }

    public final static record ActionPerformed(String description) implements Command {
    }

}