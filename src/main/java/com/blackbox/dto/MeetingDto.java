package com.blackbox.dto;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.enumeration.ToolsType;
import java.util.List;
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
public class MeetingDto {

	private String roomName;
	private Day day;
	private Timeslot timeslot;
	private List<ToolsType> addonTools;

}
