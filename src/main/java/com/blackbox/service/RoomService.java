package com.blackbox.service;

import com.blackbox.dao.enumeration.MeetingType;
import com.blackbox.dao.enumeration.ToolsType;
import com.blackbox.dao.model.Room;
import com.blackbox.dao.repository.RoomRepository;
import com.blackbox.dto.RoomDto;
import com.blackbox.util.Mapping;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;

	public void addRoom(RoomDto room) {
		roomRepository.insert(Mapping.roomDtoToModel(room));
	}

	/**
	 * This method filters and gets rooms that meet the room capacity regarding covid percentile.
	 *
	 * @param rooms collection of rooms
	 * @param nbOfAttendant Number of participants or attendants
	 * @param covidRate percentile of attendance due to COVID sanitary rules
	 * @return Set of rooms that matches number of attendant regarding the covid rate of percentile
	 */
	public Set<Room> filterRoomAboveCapacity(Collection<Room> rooms, long nbOfAttendant, double covidRate) {
		return rooms.stream().filter(room -> room.getCapacity() * covidRate >= nbOfAttendant).collect(Collectors.toSet());
	}

	/**
	 * This method computes set of tools that are missing by room for a specific type of meeting.
	 *
	 * @param room A specified room
	 * @param meetingType meeting type {@link MeetingType}
	 * @return Set of tools that are missing or need to be reserved (configuration) to book a meeting regarding the room
	 * specified
	 */
	public Set<ToolsType> makeDiffBetweenRoomToolsAndMeetingType(Room room, MeetingType meetingType) {
		Set<ToolsType> toolsTypeNeeded = MeetingType.readNeededToolsByMeetingType(meetingType);
		Set<ToolsType> toolsTypeInTheRoom = room.getTools();
		return toolsTypeNeeded.stream()
		                      .filter(Objects::nonNull)
		                      .filter(tools -> !toolsTypeInTheRoom.contains(tools))
		                      .collect(Collectors.toUnmodifiableSet());
	}

}
