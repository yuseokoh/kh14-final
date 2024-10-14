package com.game.advice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.game.error.TargetNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;

//스프링 부트에서 발생하는 각종 예외들을 처리하는 간섭 객체

//@RestControllerAdvice(basePackages = {"com.kh.spring12.restcontroller"})
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice {

	//TargetNotFoundException은 404로 처리
	//나머지는 500번으로 처리하되 메세지 제거(서버에만 출력)
	@ExceptionHandler(com.game.error.TargetNotFoundException.class)
	public ResponseEntity<String> error404(Exception e) {
		e.printStackTrace();
		//return ResponseEntity.ok().build();//200
		
//		return ResponseEntity.status(404).build();
		return ResponseEntity.status(404).body("target not found");
//		return ResponseEntity.notFound().build();//404
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<String> errorTokenExpire(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(404).body("token expired");
	}
	
	@ExceptionHandler(Exception.class) 
	public ResponseEntity<String> error500(Exception e) {
		e.printStackTrace();
		//return ResponseEntity.status(500).build();
		//return ResponseEntity.internalServerError().build();
		return ResponseEntity.internalServerError().body("server error");
	}
	
	
}