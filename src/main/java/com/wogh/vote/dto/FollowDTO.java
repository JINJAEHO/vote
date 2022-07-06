package com.wogh.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {
	private Long fno;
	
	private String you;
	
	private String myEmail;
	
	private Long mno;
}
