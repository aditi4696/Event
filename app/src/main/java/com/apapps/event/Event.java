package com.apapps.event;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Aditi on 3/5/2017.
 */

public class Event {

    private String eId;
    private String name;
    private String eventDate;
    private String venue;
    private String eventDescription;
    private String createdBy;
    private int attendeesCount;
    private HashMap<String, Boolean> attendee;

    public Event(){

    }

    public Event(String eId, String name, String eventDate, String venue, String eventDescription, String createdBy){
        this.eId = eId;
        this.name = name;
        this.eventDate = eventDate;
        this.venue = venue;
        this.eventDescription = eventDescription;
        this.createdBy = createdBy;
        this.attendeesCount = 0;
    }

    public Event(String name, String eventDate, String venue, String eventDescription, String createdBy){
        this.name = name;
        this.eventDate = eventDate;
        this.venue = venue;
        this.eventDescription = eventDescription;
        this.createdBy = createdBy;
        this.attendeesCount = 0;
    }

    public String geteId() { return eId;}

    public String getName(){
        return name;
    }

    public String getEventDate(){
        return eventDate;
    }

    public int getAttendeesCount() { return attendeesCount;}

    public String getVenue() { return venue;}

    public String getEventDescription() { return eventDescription;}

    public void setAttendeesCount(int attendeesCount) { this.attendeesCount = attendeesCount;}

    public String getCreatedBy() { return createdBy;}

    public HashMap<String, Boolean> getAttendee(){
        return attendee;
    }
}
