package com.blackbox.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Builder
@Getter
public class PropertiesReader {

	@Value("${app.covidPercentageAttendance:0.5}")
	private final Double covidPercentageAttendance;

}
