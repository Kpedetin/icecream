package com.blackbox.dto;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.MeetingType;
import com.blackbox.dao.enumeration.Timeslot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingBookingDto {

	Day day;
	Timeslot timeslot;
	Long numberOfAttendant;
	MeetingType meetingType;

}
