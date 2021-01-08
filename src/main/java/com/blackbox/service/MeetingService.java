package com.blackbox.service;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.MeetingType;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.enumeration.ToolsType;
import com.blackbox.dao.model.Meeting;
import com.blackbox.dao.model.Room;
import com.blackbox.dao.repository.MeetingRepository;
import com.blackbox.dao.repository.RoomRepository;
import com.blackbox.util.PropertiesReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingService {

	@Autowired
	private PropertiesReader propertiesReader;
	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private ToolService toolService;

	@Autowired
	private RoomService roomService;

	/**
	 * This method computes availables rooms at the specified day and timeslot. It takes in consideration the COVID
	 * percentile for each room,the sanitary rule of one hour cleaning and the screenshot of current meeting planned.
	 *
	 * @param day enumeration {@link Day}
	 * @param timeslot enumeration {@link Timeslot}
	 * @param numberOfAttendant Number of Attendants or participants to a meeting
	 * @return Set of Rooms availables
	 */
	public Set<Room> readAvailableRoomMeeting(Day day, Timeslot timeslot, long numberOfAttendant) throws Exception {
		//Is that necessary  to synchronize this method?
		synchronized (this) {
			Set<String> unavailableRoom = meetingRepository.findByDayAndTimeslotIn(day,
					List.of(Timeslot.getPreviousTimeslot(timeslot), timeslot))
			                                               .stream()
			                                               .map(Meeting::getRoomName)
			                                               .collect(Collectors.toSet());

			Set<Room> eligibleRoomByAttendanceCapacity = roomService.filterRoomAboveCapacity(roomRepository.findAll(),
					numberOfAttendant, propertiesReader
					.getCovidPercentageAttendance());

			return eligibleRoomByAttendanceCapacity.stream()
			                                       .filter(room -> !unavailableRoom.contains(room.getName()))
			                                       .collect(Collectors.toSet());
		}
	}

	/**
	 * This method is quite central to the project.
	 *
	 * @param availableRooms Set of Rooms{@link Room} that are availables
	 * @param meetingType Meeting type {@link MeetingType}
	 * @param day abstraction of day {@link Day}
	 * @param timeslot abstraction of time {@link Timeslot}
	 * @return the best room for the meeting
	 */
	//TODO Refacto to return a Room
	public Meeting computeAndBookedBestRoomForMeeting(Set<Room> availableRooms, MeetingType meetingType, Day day,
			Timeslot timeslot) {
		Optional<Room> proposedRoom;
		Meeting bookedMeeting = null;
		Map<Set<ToolsType>, Room> toolsToReservedByRoom = new HashMap<>();
		Optional<Room> roomWithSmallCapacity;

		if (MeetingType.readNeededToolsByMeetingType(meetingType).isEmpty()) {
			// if the room with no tool will be prioritized regardless the capacity.
			// AS example :
			// room 1 { capacity : 50, tools : null}
			// room 2 { capacity : 7, tools : SCREEN}
			// the room 1 will be prioritized
			// Is that what we want ?
			roomWithSmallCapacity =
					getEmptyRoomWithSmallCapacity(availableRooms).or(() -> getRoomWithSmallCapacity(availableRooms));
			if (roomWithSmallCapacity.isPresent()) {
				bookedMeeting = Meeting.builder()
				                       .day(day)
				                       .timeslot(timeslot)
				                       .roomName(roomWithSmallCapacity.get().getName())
				                       .meetingType(meetingType)
				                       .build();
			}
		} else {
			for (Room room : availableRooms) {
				Set<ToolsType> toolsTypes = roomService.makeDiffBetweenRoomToolsAndMeetingType(room, meetingType);
				toolsToReservedByRoom.computeIfPresent(toolsTypes,
						(tool, prevRoom) -> getRoomWithSmallCapacity(List.of(prevRoom, room))
						.get());

			}
			proposedRoom = findRoomWithSmallTools(toolsToReservedByRoom, day, timeslot);
			if (proposedRoom.isPresent()) {
				bookedMeeting = Meeting.builder()
				                       .day(day)
				                       .timeslot(timeslot)
				                       .addonTools(roomService.makeDiffBetweenRoomToolsAndMeetingType(proposedRoom.get(),
						                       meetingType))
				                       .roomName(proposedRoom.get().getName())
				                       .meetingType(meetingType)
				                       .build();
			}
		}
		if (bookedMeeting != null) {
			meetingRepository.insert(bookedMeeting);
		}
		return bookedMeeting;
	}

	/**
	 * This method retrieves the room with the minimum capacity among a list
	 *
	 * @param rooms Collection {@link Collection} of rooms
	 * @return The room with the smallest capacity or empty is the collection entered is null.
	 */
	public Optional<Room> getRoomWithSmallCapacity(Collection<Room> rooms) {
		if (rooms != null) {
			return rooms.stream().min(Comparator.comparing(Room::getCapacity));
		}
		return Optional.empty();
	}

	/**
	 * This method allowed to find the smallest room that does not have tools in it.
	 *
	 * @param rooms rooms Collection {@link Collection} of rooms
	 * @return The room with the smallest capacity and no tools or empty is the collection entered is null.
	 */
	public Optional<Room> getEmptyRoomWithSmallCapacity(Collection<Room> rooms) {
		if (rooms != null) {
			return rooms.stream()
			            .filter(room -> room.getTools() == null || room.getTools().isEmpty())
			            .min(Comparator.comparing(Room::getCapacity));

		}
		return Optional.empty();
	}

	/**
	 * This method helps to find effectives configurations according room and stock capacity
	 *
	 * @param toolsAndRooms Map of configuration (set of add-ons tools) according to a room and Meeting type
	 * @param day abstraction of day {@link Day}
	 * @param timeslot abstraction of timeslot {@link Timeslot}
	 * @return List of effective Configuration
	 */
	public List<Set<ToolsType>> listOfConfigurationsPossibleAfterCheckingStock(Map<Set<ToolsType>, Room> toolsAndRooms,
			Day day, Timeslot timeslot) {
		List<Set<ToolsType>> setsOfRoomWithLessToolsToReserved = getSetsWithSmallAmoutOfTools(toolsAndRooms);
		return computeSetOfToolsEligibleFromRemovableStock(day, timeslot, setsOfRoomWithLessToolsToReserved);
	}

	/**
	 * This method finds the rooms that required the minimum number of tools as add-ons. It may have several room that
	 * meets that minimum.
	 *
	 * @param toolsAndRooms Map with set of add-ons tools (tools that need to be added to the room) as key and the
	 * room{@link Room} as value
	 * @return List of configuration that have a minimun size of add-ons tools
	 */
	public List<Set<ToolsType>> getSetsWithSmallAmoutOfTools(Map<Set<ToolsType>, Room> toolsAndRooms) {
		Map<Integer, List<Set<ToolsType>>> nbMissingTools = new HashMap<>();
		var ref = new Object() {
			int min = Integer.MAX_VALUE;
		};
		Set<Set<ToolsType>> sets = toolsAndRooms.keySet();
		for (Set<ToolsType> set : sets) {
			if (nbMissingTools.get(set.size()) == null) {
				nbMissingTools.put(set.size(), List.of(set));
			} else {
				//Immutable set
				List<Set<ToolsType>> sets1 = new ArrayList(nbMissingTools.get(set.size()));
				sets1.add(set);
				nbMissingTools.put(set.size(), sets1);
			}
			if (set.size() != 0 && set.size() < ref.min) {
				ref.min = set.size();
			}
		}
		return nbMissingTools.get(ref.min);
	}

	/**
	 * This method take in consideration the stock of removable tools for room that need add-ons tools
	 *
	 * @param day abstraction of day {@link Day}
	 * @param timeslot abstraction of time {@link Timeslot}
	 * @param setsOfRoomWithLessToolsToReserved Set of tools {@link ToolsType}
	 * @return list of sets(configurations) that can be fullfilled according to stock capacity
	 */
	public List<Set<ToolsType>> computeSetOfToolsEligibleFromRemovableStock(Day day, Timeslot timeslot,
			List<Set<ToolsType>> setsOfRoomWithLessToolsToReserved) {
		List<Set<ToolsType>> resultOfChecks = new ArrayList<>();
		Map<ToolsType, Integer> stockOfRemovableTools = toolService.readAvailableRemovableTools(day, timeslot);
		if (!setsOfRoomWithLessToolsToReserved.isEmpty()) {
			for (Set<ToolsType> theSet : setsOfRoomWithLessToolsToReserved) {
				AtomicBoolean available = new AtomicBoolean(true);
				for (ToolsType s : theSet) {
					if (stockOfRemovableTools.get(s) == 0) {
						available.set(false);
					}
				}
				if (available.get()) {
					resultOfChecks.add(theSet);
				}
			}
		}
		return resultOfChecks;
	}


	public Optional<Room> findRoomWithSmallTools(Map<Set<ToolsType>, Room> toolsAndRooms, Day day, Timeslot timeslot) {
		List<Set<ToolsType>> listOfkeysOfAvailablesRooms = listOfConfigurationsPossibleAfterCheckingStock(toolsAndRooms,
				day, timeslot);
		if (!listOfkeysOfAvailablesRooms.isEmpty()) {
			return Optional.of(toolsAndRooms.get(listOfkeysOfAvailablesRooms.get(0)));
		}
		return Optional.empty();
	}

}
