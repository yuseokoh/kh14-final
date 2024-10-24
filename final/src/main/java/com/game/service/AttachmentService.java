	package com.game.service;
	
	import java.io.File;
	import java.io.IOException;
	import java.nio.charset.StandardCharsets;
	
	import org.apache.commons.io.FileUtils;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.core.io.ByteArrayResource;
	import org.springframework.http.ContentDisposition;
	import org.springframework.http.HttpHeaders;
	import org.springframework.http.MediaType;
	import org.springframework.http.ResponseEntity;
	import org.springframework.stereotype.Service;
	import org.springframework.web.multipart.MultipartFile;
	
	import com.game.configuration.CustomFileuploadProperties;
	import com.game.dao.AttachmentDao;
	import com.game.dto.AttachmentDto;
	import com.game.error.TargetNotFoundException;
	
	import jakarta.annotation.PostConstruct;
	
	@Service
	public class AttachmentService {
	    @Autowired
	    private CustomFileuploadProperties properties;
	    
	    @Autowired
	    private AttachmentDao attachmentDao;
	    
	    private File dir;
	    
	    @PostConstruct
	    public void init() {
	        dir = new File(properties.getPath());
	        dir.mkdirs();
	    }
	    
	    public int save(MultipartFile attach) throws IllegalStateException, IOException {
	        // 시퀀스 번호 생성
	        int attachmentNo = attachmentDao.sequence();
	        
	        // 실제 파일 저장
	        File target = new File(dir, String.valueOf(attachmentNo));
	        attach.transferTo(target);
	        
	        // DB에 파일 정보 저장
	        AttachmentDto attachmentDto = new AttachmentDto();
	        attachmentDto.setAttachmentNo(attachmentNo);
	        attachmentDto.setAttachmentName(attach.getOriginalFilename());
	        attachmentDto.setAttachmentType(attach.getContentType());
	        attachmentDto.setAttachmentSize(attach.getSize());
	        attachmentDao.insert(attachmentDto);
	        
	        return attachmentNo;
	    }
	    
	    public void delete(int attachmentNo) {
	        // 파일 정보 조회
	        AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
	        if(attachmentDto == null) {
	            throw new TargetNotFoundException("존재하지 않는 파일 번호");
	        }
	        
	        // 실제 파일 삭제
	        
	        File target = new File(dir, String.valueOf(attachmentNo));
	        target.delete();
	        
	        // DB 정보 삭제
	        attachmentDao.delete(attachmentNo);
	    }
	    
	    public ResponseEntity<ByteArrayResource> find(int attachmentNo) throws IOException {
	        // 파일 정보 조회
	        AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
	        if(attachmentDto == null) {
	            throw new TargetNotFoundException("존재하지 않는 파일 번호");
	        }
	        
	        // 실제 파일 로드
	        File target = new File(dir, String.valueOf(attachmentNo));
	        byte[] data = FileUtils.readFileToByteArray(target);
	        ByteArrayResource resource = new ByteArrayResource(data);
	        
	        // 다운로드 응답 생성
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .contentLength(attachmentDto.getAttachmentSize())
	                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
	                .header(HttpHeaders.CONTENT_DISPOSITION, 
	                        ContentDisposition.attachment()
	                                .filename(attachmentDto.getAttachmentName(), 
	                                        StandardCharsets.UTF_8)
	                                .build().toString()
	                )
	                .body(resource);
	    }
	}