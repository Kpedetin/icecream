package com.blackbox.controller;

import com.blackbox.dao.model.Meeting;
import com.blackbox.dao.model.Room;
import com.blackbox.dto.MeetingBookingDto;
import com.blackbox.service.MeetingService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/meeting")
public class MeetingManager {


	@Autowired
	private MeetingService meetingService;


	@PostMapping(value = "/booking")
	public Meeting bookedRoomForMeeting(@RequestBody MeetingBookingDto meetingBookingDto) throws Exception {
		Set<Room> rooms = meetingService.readAvailableRoomMeeting(meetingBookingDto.getDay(),
				meetingBookingDto.getTimeslot(), meetingBookingDto
				.getNumberOfAttendant());
		if (rooms.isEmpty()) {
			throw new Exception("No Room Available according to the meeting configuration");
		} else {
			return meetingService.computeAndBookedBestRoomForMeeting(rooms, meetingBookingDto.getMeetingType(),
					meetingBookingDto
					.getDay(), meetingBookingDto.getTimeslot());

		}
	}
}
