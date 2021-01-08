package com.blackbox.dto;

import com.blackbox.dao.enumeration.ToolsType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

	private String name;
	private Long capacity;
	private Set<ToolsType> tools;

}
