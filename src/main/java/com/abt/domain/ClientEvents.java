package com.abt.domain;

import java.util.List;

public class ClientEvents {
    private List<Client> clients;

    private List<Event> events;

    private int no_of_events;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public int getNoOfEvents() {
        return no_of_events;
    }

    public void setNoOfEvents(int no_of_events) {
        this.no_of_events = no_of_events;
    }
}

