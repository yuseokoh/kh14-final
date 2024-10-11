package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.MemberDao;
import com.game.dao.MemberTokenDao;
import com.game.service.TokenService;

@CrossOrigin
@RestController
@RequestMapping("/")
public class MemberRestController {

	 @Autowired
	 private MemberDao memberDao;
	 @Autowired
	 private TokenService tokenService;
	 @Autowired
	 private MemberTokenDao memberTokenDao;
	 
	 
}
