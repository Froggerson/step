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
  public Collection < TimeRange > query(Collection < Event > events, MeetingRequest request) {
    ArrayList < Event > eventsList = new ArrayList < >(events);
    ArrayList < TimeRange > times = new ArrayList < TimeRange > ();

    HashSet < String > attendees = new HashSet < String > (request.getAttendees());
    ArrayList < Event > sortedEvents = sortEvents(eventsList, attendees);
    int requestDuration = (int) request.getDuration();
    int eventStartTime = 0;
    int index = 0;
    TimeRange priorEventTime = null;
    System.out.println("Adding events now!");
    for (Event event: sortedEvents) {
      TimeRange currentEvent = event.getWhen();
      int eventEndTime = currentEvent.end();

      /**
        * If this is is the first event or if this and the previous event
        * don't overlap, then add a new event to times 
        */
      if (priorEventTime == null || !priorEventTime.overlaps(currentEvent)) {
        TimeRange validEventTime = TimeRange.fromStartEnd(eventStartTime, currentEvent.start(), false);
        if (validEventTime.duration() >= requestDuration) {
          times.add(validEventTime);
        }
        eventStartTime = eventEndTime;
        priorEventTime = currentEvent;
      } else if (priorEventTime != null && priorEventTime.contains(currentEvent)) {
        eventStartTime = priorEventTime.end();
      } else {
        eventStartTime = eventEndTime;
        priorEventTime = currentEvent;
      }
    }
    /* Add in event at end of day */
    if (eventStartTime < 60 * 24 && 60 * 24 - requestDuration >= eventStartTime) {
      TimeRange validEventTime = TimeRange.fromStartEnd(eventStartTime, 60 * 24, false);
      times.add(validEventTime);
    }
    return times;
  }

  /**
    * Returns list of events that have at least one attendee in it sorted by earliest time.
    */
  public ArrayList < Event > sortEvents(ArrayList < Event > events, HashSet < String > attendees) {
    sort(events);
    Boolean eventAttended = false;
    ArrayList < Event > sortedEvents = new ArrayList < >(events);

    for (Event event: events) {
      HashSet < String > eventAttendees = new HashSet < String > (event.getAttendees());
      eventAttended = false;
      for (String attendee: attendees) {
        if (eventAttendees.contains(attendee)) {
          eventAttended = true;
        }
      }
      if (eventAttended) {
        sortedEvents.add(event);
      }
    }
    return sortedEvents;
  }

  /**
    * Splits a given ArrayList in half, sorts it, and returns the sorted ArrayList
    */
  public void merge(ArrayList < Event > events, int left, int middle, int right) {
    ArrayList < Event > leftEvents = new ArrayList < >(events);
    ArrayList < Event > righttEvents = new ArrayList < >(events);

    // Make temporary arrayLists for the left and right
    for (int i = 0; i < events.size(); i++) {
      if (i < middle - left) {
        leftEvents.add(events.get(left + i));
      } else {
        rightEvents.add(events.get(left + i));
      }
    }

    int leftIndex = 0,
    rightIndex = 0;
    int mergeIndex = left;
    while (leftIndex < leftEvents.size() && rightIndex < rightEvents.size()) {
      if (leftEvents.get(leftIndex).getWhen().start() <= rightEvents.get(j).getWhen().start()) {
        events.set(mergeIndex, leftEvents.get(leftIndex));
        leftIndex++;
      }
      else {
        events.set(mergeIndex, rightEvents.get(rightIndex));
        rightIndex++;
      }
      mergeIndex++;
    }
  }

  /**
    * Sorts a given ArrayList of event objects.
   */
  public void sort(ArrayList < Event > events, int left, int right) {
    if (left < right) {
      int middle = (left + right) / 2;
      sort(events, left, middle);
      sort(events, middle + 1, right);
      merge(events, left, middle, right);
    }
  }
}