package com.blackbox.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.blackbox.dao.enumeration.MeetingType;
import com.blackbox.dao.enumeration.ToolsType;
import com.blackbox.dao.model.Room;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class RoomServiceTest {

	@InjectMocks
	private RoomService roomService;

	Set<Room> rooms = new HashSet<>();
	Room room1;
	Room room2;
	Room room3;
	Set<ToolsType> toolsType1 = new HashSet<>();
	Set<ToolsType> toolsType2 = new HashSet<>();

	@BeforeEach
	void setUp() {

		toolsType1.add(ToolsType.SCREEN);
		toolsType1.add(ToolsType.SPEAKER);
		toolsType2.add(ToolsType.BOARD);
		room1 = Room.builder().name("ROOM1").capacity(10L).tools(toolsType1).build();
		room2 = Room.builder().name("ROOM2").capacity(5L).tools(toolsType2).build();
		room3 = Room.builder().name("ROOM3").capacity(5L).build();
		rooms.add(room1);
		rooms.add(room2);

	}

	@Test
	void filterRoomAboveCapacityTest() {
		Set<Room> selectedRooms = roomService.filterRoomAboveCapacity(rooms, 7, 0.7);
		assertEquals(1, selectedRooms.size());
		assertEquals(Set.of(room1), selectedRooms);
	}

	@Test
	void makeToolsDiffBetweenRoomAndMeetingTools_VC() {
		Set<ToolsType> expectedToolsTypeForRoom1 = new HashSet<>();
		expectedToolsTypeForRoom1.add(ToolsType.WEBCAM);
		Set<ToolsType> resultToolsTypeRoom1 = roomService.makeDiffBetweenRoomToolsAndMeetingType(room1,
				MeetingType.VISIOCONFERNCE);
		assertEquals(expectedToolsTypeForRoom1, resultToolsTypeRoom1);
	}

	@Test
	void makeToolsDiffBetweenRoomAndMeetingTools_RM() {

		Set<ToolsType> expectedToolsTypeForRoom2 = new HashSet<>();
		expectedToolsTypeForRoom2.add(ToolsType.SCREEN);
		expectedToolsTypeForRoom2.add(ToolsType.SPEAKER);
		Set<ToolsType> resultToolsTypeRoom2 = roomService.makeDiffBetweenRoomToolsAndMeetingType(room2,
				MeetingType.REMOTEMEETING);
		assertEquals(expectedToolsTypeForRoom2, resultToolsTypeRoom2);
	}

	@Test
	void makeToolsDiffBetweenRoomAndMeetingTools_ReturnEmptySet() {
		Set<ToolsType> resultToolsTypeForMeetingThatDoesNotNeedTools =
				roomService.makeDiffBetweenRoomToolsAndMeetingType(room2, MeetingType.SIMPLEMEETING);
		assertEquals(0, resultToolsTypeForMeetingThatDoesNotNeedTools.size());
		assertEquals(new HashSet<>(), resultToolsTypeForMeetingThatDoesNotNeedTools);

		Set<ToolsType> resultToolsTypeForRoomThatDoesNotHaveTools =
				roomService.makeDiffBetweenRoomToolsAndMeetingType(room3, MeetingType.SIMPLEMEETING);
		assertEquals(0, resultToolsTypeForMeetingThatDoesNotNeedTools.size());
		assertEquals(new HashSet<>(), resultToolsTypeForRoomThatDoesNotHaveTools);

	}

}