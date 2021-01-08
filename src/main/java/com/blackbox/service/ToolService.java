package com.blackbox.service;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.enumeration.ToolsType;
import com.blackbox.dao.model.Meeting;
import com.blackbox.dao.repository.MeetingRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

	@Autowired
	private MeetingRepository meetingRepository;


	private final int MAX_NB_SPEAKER = 4;
	private final int MAX_NB_BOARD = 2;
	private final int MAX_NB_WEBCAM = 4;
	private final int MAX_NB_SCREEN = 5;

	/**
	 * This method read removable tools that are available at certain moment (Day/Timeslot).
	 *
	 * @param day abstraction of day {@link Day}
	 * @param timeslot abstraction of timeslot {@link Timeslot}
	 * @return Map with Toolstype{@link ToolsType} as key and number of remaining tools as value
	 */
	public Map<ToolsType, Integer> readAvailableRemovableTools(Day day, Timeslot timeslot) {
		synchronized (this) {
			Map<ToolsType, Integer> counterOfRemovableTools = new HashMap<>();
			//Does tools need an hour for cleaning too ?
			List<Meeting> listOfMeetingByDayAndTimeslot = meetingRepository.findByDayAndTimeslot(day, timeslot);
			if (!listOfMeetingByDayAndTimeslot.isEmpty()) {
				for (Meeting meeting : listOfMeetingByDayAndTimeslot) {
					if (meeting.getAddonTools() != null) {
						handleStockOfRemovableTools(counterOfRemovableTools, meeting);
					} else {
						initStockOfRemovableTools(counterOfRemovableTools);
					}
				}
			} else {
				initStockOfRemovableTools(counterOfRemovableTools);
			}
			return counterOfRemovableTools;
		}
	}

	private void initStockOfRemovableTools(Map<ToolsType, Integer> counterOfRemovableTools) {
		counterOfRemovableTools.put(ToolsType.BOARD, MAX_NB_BOARD);
		counterOfRemovableTools.put(ToolsType.SPEAKER, MAX_NB_SPEAKER);
		counterOfRemovableTools.put(ToolsType.SCREEN, MAX_NB_SCREEN);
		counterOfRemovableTools.put(ToolsType.WEBCAM, MAX_NB_WEBCAM);
	}

	private void handleStockOfRemovableTools(Map<ToolsType, Integer> counterOfRemovableTools, Meeting meeting) {
		for (ToolsType tools : meeting.getAddonTools()) {
			if (counterOfRemovableTools.containsKey(tools)) {
				counterOfRemovableTools.put(tools, counterOfRemovableTools.get(tools) - 1);
			} else {
				if (tools == ToolsType.BOARD) {
					counterOfRemovableTools.put(tools, MAX_NB_BOARD - 1);
				}
				if (tools == ToolsType.SCREEN) {
					counterOfRemovableTools.put(tools, MAX_NB_SCREEN - 1);
				}
				if (tools == ToolsType.SPEAKER) {
					counterOfRemovableTools.put(tools, MAX_NB_SPEAKER - 1);
				}
				if (tools == ToolsType.WEBCAM) {
					counterOfRemovableTools.put(tools, MAX_NB_WEBCAM - 1);
				}
			}
		}
	}

}
