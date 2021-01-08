package com.blackbox.dao.model;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.MeetingType;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.enumeration.ToolsType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Meeting {

	@Id
	private String id;
	private String roomName;
	private MeetingType meetingType;
	private Day day;
	private Timeslot timeslot;
	private Set<ToolsType> addonTools;
}
