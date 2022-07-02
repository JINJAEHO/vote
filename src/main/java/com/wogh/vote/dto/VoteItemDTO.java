package com.wogh.vote.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteItemDTO {
	private Long ino;
	
	private String item;
	
	private String imageurl;
	
	private Integer count;
	
	private MultipartFile image;
	
	private Long board_num;
}
