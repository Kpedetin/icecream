package com.blackbox.dao.model;

import com.blackbox.dao.enumeration.ToolsType;
import lombok.AllArgsConstructor;
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
public class Tools {

	@Id
	private String id;
	private ToolsType type;
}
