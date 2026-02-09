package com.example.routineapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.routineapp.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

@RestController
public class CalendarController {

    private final GoogleCalendarService calendarService;

    public CalendarController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar/events")
    public List<Event> getEvents() throws IOException {
        return calendarService.getUpcomingEvents(10);
    }
}
