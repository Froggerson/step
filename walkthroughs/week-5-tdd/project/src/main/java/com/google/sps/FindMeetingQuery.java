// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");
    // Create a bunch 
    ArrayList<Event> eventsList = new ArrayList<>(events);
    ArrayList<TimeRange> times = new ArrayList<TimeRange>();

    HashSet<String> attendees = new HashSet<String>(request.getAttendees());
    ArrayList<Event> sortedEvents = sortEvents(eventsList, attendees);
    int requestDuration = (int) request.getDuration();
    int eventStartTime = 0;
    int index = 0;
    TimeRange priorEventTime = null;
    System.out.println("Adding events now!");
    for (Event event : sortedEvents){
        TimeRange currentEvent = event.getWhen();
        int eventEndTime = currentEvent.end();

        /* If this is is the first event or if this and the previous event
            don't overlap, then add a new event to times */
        if (priorEventTime == null || !priorEventTime.overlaps(currentEvent)){
            TimeRange validEventTime =TimeRange.fromStartEnd(eventStartTime,currentEvent.start(), false);
            if(validEventTime.duration() >= requestDuration){
                times.add(validEventTime);
            }
            eventStartTime= eventEndTime;
            priorEventTime = currentEvent;
        } else if(priorEventTime != null && priorEventTime.contains(currentEvent)){
            eventStartTime = priorEventTime.end(); 
        } else{
            eventStartTime = eventEndTime;
            priorEventTime = currentEvent;
        }
    }
    /* Add in event at end of day */
    if(eventStartTime < 60*24 && 60*24 - requestDuration >= eventStartTime){
        TimeRange validEventTime = TimeRange.fromStartEnd(eventStartTime,60*24, false);
        times.add(validEventTime);
    }
    return times;
  }

    /**
        Returns sorted list of events that attendees are attending.
    */
  public ArrayList<Event> sortEvents(ArrayList<Event> events, HashSet<String>attendees){
    ArrayList<Event> sortedEvents = new ArrayList<Event>();
    while(events.size()>0){
        int minIndex = 0;
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).getWhen().start() < events.get(minIndex).getWhen().start()){
                minIndex=i;
            }
        }
        Event earliestEvent = events.get(minIndex);
        events.remove(minIndex);
        HashSet<String> eventAttendees= new HashSet<String>(earliestEvent.getAttendees());
        Boolean eventAttended = false;
        for(String attendee : attendees){
            if (eventAttendees.contains(attendee)){
                eventAttended=true;
            }
        }
        if(eventAttended){
            sortedEvents.add(earliestEvent);
        }
        
    }
   System.out.println("SORTED EVENTS:");
   System.out.println(sortedEvents);
    return sortedEvents;  
  }
}