package com.apapps.event;

/**
 * Created by Aditi on 4/16/2017.
 */

public class Attendees {
    private String eventId;
    private String userId;
    private String attendeeId;

    public Attendees() {

    }

    public Attendees(String attendeeId, String eventId, String userId){
        this.attendeeId = attendeeId;
        this.eventId = eventId;
        this.userId = userId;
    }


    public String getEventId(){ return eventId; }

    public String getUserId(){ return userId; }

    public String getAttendeeId(){ return attendeeId;}
}
