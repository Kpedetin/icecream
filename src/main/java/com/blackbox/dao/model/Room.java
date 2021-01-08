package com.blackbox.dao.model;

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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {

	@Id
	private String name;
	private Long capacity;
	private Set<ToolsType> tools;
}
