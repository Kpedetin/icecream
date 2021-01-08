package com.blackbox.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.enumeration.ToolsType;
import com.blackbox.dao.model.Room;
import com.blackbox.dao.repository.MeetingRepository;
import com.blackbox.dao.repository.RoomRepository;
import com.blackbox.util.PropertiesReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "application.properties", properties = "app.covidPercentageAttendance=0.7")
class MeetingServiceTest {

	@Mock
	private PropertiesReader propertiesReader;

	@InjectMocks
	private MeetingService meetingService;

	@Mock
	private ToolService toolService;

	@Mock
	private MeetingRepository meetingRepository;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private RoomService roomService;


	Map<ToolsType, Integer> toolsInventory = new HashMap<>();
	Map<Set<ToolsType>, Room> toolsAndRooms = new HashMap<>();
	Set<ToolsType> setNeededForRoom1 = Set.of(ToolsType.BOARD, ToolsType.SCREEN);
	Set<ToolsType> setNeededForRoom2 = Set.of(ToolsType.SPEAKER, ToolsType.SCREEN);
	Set<ToolsType> setNeededForRoom3 = Set.of(ToolsType.SPEAKER);
	Room room1;
	Room room2;
	Room room3;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		toolsInventory.put(ToolsType.BOARD, 3);
		toolsInventory.put(ToolsType.SCREEN, 2);
		toolsInventory.put(ToolsType.WEBCAM, 4);
		toolsInventory.put(ToolsType.SPEAKER, 2);
		room1 = Room.builder().name("E1001").capacity(10L).build();
		room2 = Room.builder().name("E1002").capacity(20L).build();
		room3 = Room.builder().name("E1003").capacity(8L).build();
		toolsAndRooms.put(setNeededForRoom1, room1);
		toolsAndRooms.put(setNeededForRoom2, room2);
		toolsAndRooms.put(setNeededForRoom3, room3);

	}

	@Test
	void getRoomWithSmallCapacity_Success() {
		Room room_0 = Room.builder().name("E1000").capacity(8L).tools(Set.of(ToolsType.SPEAKER, ToolsType.SCREEN)).build();
		Room room_1 = Room.builder().name("E1001").capacity(10L).build();
		Room room_2 = Room.builder().name("E1002").capacity(13L).build();
		Optional<Room> roomWithSmallCapacity = meetingService.getRoomWithSmallCapacity(List.of(room_0, room_1, room_2));
		assertFalse(roomWithSmallCapacity.isEmpty());
		assertEquals(room_0, roomWithSmallCapacity.get());
	}

	@Test
	void getEmptyRoomWithSmallCapacity_Success() {
		Room room_0 = Room.builder().name("E1000").capacity(8L).tools(Set.of(ToolsType.SPEAKER, ToolsType.SCREEN)).build();
		Room room_1 = Room.builder().name("E1001").capacity(10L).build();
		Room room_2 = Room.builder().name("E1002").capacity(13L).build();
		Optional<Room> emptyRoomWithSmallCapacity = meetingService.getEmptyRoomWithSmallCapacity(List.of(room_0, room_1,
				room_2));
		assertFalse(emptyRoomWithSmallCapacity.isEmpty());
		assertEquals(room_1, emptyRoomWithSmallCapacity.get());
	}

	@Test
	void readAvailableRoomFindRoom() {
		Room room_1 = Room.builder().name("E1001").capacity(10L).build();
		Room room_2 = Room.builder().name("E1002").capacity(13L).build();
		Room room_3 = Room.builder().name("E1003").capacity(8L).build();
		Room room_4 = Room.builder().name("E1004").capacity(20L).build();

		when(meetingRepository.findByDayAndTimeslotIn(any(Day.class), any(ArrayList.class))).thenReturn(List.of(room_1,
				room_3));
		when(roomRepository.findAll()).thenReturn(List.of(room_1, room_2, room_3, room_4));
		when(propertiesReader.getCovidPercentageAttendance()).thenReturn(0.5);
		when(roomService.filterRoomAboveCapacity(anyCollection(), anyLong(), anyDouble())).thenCallRealMethod();

		Set<Room> rooms = assertDoesNotThrow(() -> meetingService.readAvailableRoomMeeting(Day.MONDAY, Timeslot.H2, 7));
		assertFalse(rooms.isEmpty());
		assertEquals(Set.of(room_4), rooms);
	}

	@Test
	void getSetsWithSmallAmoutOfTools() {
		Set<ToolsType> toBeReservedRoom1 = Set.of(ToolsType.BOARD, ToolsType.SCREEN);
		Set<ToolsType> toBeReservedRoom2 = Set.of(ToolsType.SCREEN, ToolsType.WEBCAM);
		Set<ToolsType> toBeReservedRoom3 = Set.of(ToolsType.SCREEN, ToolsType.WEBCAM, ToolsType.BOARD);

		Map<Set<ToolsType>, Room> missingToolsByRoom = new HashMap<>();
		missingToolsByRoom.put(toBeReservedRoom1, new Room());
		missingToolsByRoom.put(toBeReservedRoom2, new Room());
		missingToolsByRoom.put(toBeReservedRoom3, new Room());

		List<Set<ToolsType>> setsWithSmallAmoutOfTools = meetingService.getSetsWithSmallAmoutOfTools(missingToolsByRoom);
		assertEquals(2, setsWithSmallAmoutOfTools.size());
		assertTrue(setsWithSmallAmoutOfTools.containsAll(List.of(toBeReservedRoom1, toBeReservedRoom2)));

	}

	@Test
	void computeSetOfToolsFromStock_NoSetEligible() {
		Set<ToolsType> toBeReservedRoom1 = Set.of(ToolsType.BOARD, ToolsType.SCREEN);
		Set<ToolsType> toBeReservedRoom2 = Set.of(ToolsType.SCREEN, ToolsType.WEBCAM);
		List<Set<ToolsType>> listOfToolsToReserved = List.of(toBeReservedRoom1, toBeReservedRoom2);

		Map<ToolsType, Integer> removableStock = new HashMap<>();
		removableStock.put(ToolsType.BOARD, 1);
		removableStock.put(ToolsType.SPEAKER, 3);
		removableStock.put(ToolsType.SCREEN, 0);
		removableStock.put(ToolsType.WEBCAM, 0);
		when(toolService.readAvailableRemovableTools(any(Day.class), any(Timeslot.class))).thenReturn(removableStock);
		List<Set<ToolsType>> eligibleSets = meetingService.computeSetOfToolsEligibleFromRemovableStock(Day.MONDAY,
				Timeslot.H1, listOfToolsToReserved);
		assertTrue(eligibleSets.isEmpty());

	}

	@Test
	void computeSetOfToolsFromStock_foundOneSetEligible() {
		Set<ToolsType> toBeReservedRoom1 = Set.of(ToolsType.BOARD, ToolsType.SCREEN);
		Set<ToolsType> toBeReservedRoom2 = Set.of(ToolsType.SCREEN, ToolsType.WEBCAM);
		List<Set<ToolsType>> listOfToolsToReserved = List.of(toBeReservedRoom1, toBeReservedRoom2);

		Map<ToolsType, Integer> removableStock = new HashMap<>();
		removableStock.put(ToolsType.BOARD, 1);
		removableStock.put(ToolsType.SPEAKER, 3);
		removableStock.put(ToolsType.SCREEN, 1);
		removableStock.put(ToolsType.WEBCAM, 0);
		when(toolService.readAvailableRemovableTools(any(Day.class), any(Timeslot.class))).thenReturn(removableStock);
		List<Set<ToolsType>> eligibleSets = meetingService.computeSetOfToolsEligibleFromRemovableStock(Day.MONDAY,
				Timeslot.H1, listOfToolsToReserved);
		assertFalse(eligibleSets.isEmpty());
		assertEquals(toBeReservedRoom1, eligibleSets.get(0));

	}

	@Test
	void getRoomThatDoesNotNeedToolsTestOk() {
		when(toolService.readAvailableRemovableTools(any(Day.class), any(Timeslot.class))).thenReturn(toolsInventory);

		Optional<Room> roomWithSmallTools = meetingService.findRoomWithSmallTools(toolsAndRooms, Day.FRIDAY, Timeslot.H2);
	}
}