package com.example.routineapp.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

@Service
public class GoogleCalendarService {

    private final Calendar calendar;

    public GoogleCalendarService(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<Event> getUpcomingEvents(int maxResults) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = calendar.events()
                .list("primary")
                .setTimeMin(now)
                .setMaxResults(maxResults)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
}
