package com.blackbox.dao.enumeration;

import java.util.HashSet;
import java.util.Set;

public enum MeetingType {

	VISIOCONFERNCE,
	SIMPLEMEETING,
	REMOTEMEETING,
	SHARINGMEETING;

	//TODO Settings need to be read from database
	public static Set<ToolsType> readNeededToolsByMeetingType(MeetingType meetingType) {
		Set<ToolsType> setOfTools = new HashSet<>();
		switch (meetingType) {
			case VISIOCONFERNCE:
				setOfTools.add(ToolsType.SCREEN);
				setOfTools.add(ToolsType.SPEAKER);
				setOfTools.add(ToolsType.WEBCAM);
				break;
			case SHARINGMEETING:
				setOfTools.add(ToolsType.BOARD);
				break;
			case REMOTEMEETING:
				setOfTools.add(ToolsType.BOARD);
				setOfTools.add(ToolsType.SCREEN);
				setOfTools.add(ToolsType.SPEAKER);
				break;
			default:
		}
		return setOfTools;

	}
}
