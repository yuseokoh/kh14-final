package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.GameDao;
import com.game.dto.GameDto;
import com.game.error.TargetNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@CrossOrigin
@RestController
@RequestMapping("/")
public class GameRestController {

	@Autowired 
	private GameDao gameDao;
	
	//조회매핑 API 문서 설명 추가
	@Operation(
			deprecated = false //비추천 여부(사용 중지 예정인 경우 true로 설정)
			,description = "게임 정보에 대한 조회를 처리합니다"//설명
			,responses = {//예상 가능한 응답코드에 대한 안내
				@ApiResponse(//정상
					responseCode = "200"//상태코드
					,description = "조회 완료"//설명
					,content = @Content(//결과 메세지의 형태와 샘플
						mediaType = "application/json"//결과 메세지의 MIME 타입
						,array = @ArraySchema(//결과가 배열일 경우에 대한 안내
							schema = @Schema(implementation = GameDto.class)//내용물
						)
					)
				),
				@ApiResponse(//서버 오류
					responseCode = "500"//상태코드	
					,description = "서버 오류"//설명
					,content = @Content(//결과 메세지의 형태와 샘플
						mediaType = "text/plain"//결과 메세지의 MIME 타입
						,schema = @Schema(implementation = String.class)//자바의 자료형
						,examples = @ExampleObject("server error")//예시 데이터
					)
				)
			}
	)
	
	@GetMapping("/")//목록
	public List<GameDto> list(){
		return gameDao.selectList();
	}
	
	@GetMapping("/column/{column}/keyword/{keyword}")//검색(조회)
	public List<GameDto> search(
			@Parameter(description = "검색할 항목")
			@PathVariable String column, 
			@Parameter(description = "검색 키워드")
			@PathVariable String keyword){
		return gameDao.selectList(column,keyword);
	}
	
	@PostMapping("/")//등록
	public void insert(@RequestBody GameDto gameDto) {
		gameDao.insert(gameDto);
	}
	
	@PutMapping("/")//수정
	public void update(@RequestBody GameDto gameDto) {
		boolean result = gameDao.update(gameDto);
		if(result==false) {
			throw new TargetNotFoundException("존재하지 않는 게임정보");
		}
	}
	
	@DeleteMapping("/{gameNo}")//삭제
	public void delete(@PathVariable int gameNo) {
		boolean result = gameDao.delete(gameNo);
		if(result == false) {
			throw new TargetNotFoundException("존재하지 않는 게임정보");
		}
	}
}
