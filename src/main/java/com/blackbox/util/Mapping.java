package com.blackbox.util;

import com.blackbox.dao.model.Meeting;
import com.blackbox.dao.model.Room;
import com.blackbox.dto.MeetingDto;
import com.blackbox.dto.RoomDto;

public class Mapping {

	public static Room roomDtoToModel(RoomDto roomDto) {
		return Room.builder().name(roomDto.getName()).capacity(roomDto.getCapacity()).tools(roomDto.getTools()).build();
	}

	public static Meeting meetingDtoToModel(MeetingDto meetingDto) {

		return Meeting.builder().build();
	}

}
