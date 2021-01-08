package com.blackbox.controller;

import com.blackbox.dto.RoomDto;
import com.blackbox.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/room")
public class RoomManager {

	@Autowired
	private RoomService roomService;

	@PostMapping(value = "/addRoom")
	@ResponseStatus(HttpStatus.CREATED)
	public void addNewRoom(@RequestBody RoomDto roomDto) {
		roomService.addRoom(roomDto);
	}

}
