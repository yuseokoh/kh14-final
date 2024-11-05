package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.RoomMessageDao;
import com.game.service.TokenService;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/message")
public class WebsocketRestController {

	@Autowired
	private RoomMessageDao roomMessageDao;
	@Autowired
	private TokenService tokenService;
	

}
