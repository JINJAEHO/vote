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
	
	private String me;
	
	private String youEmail;
	
	private Long mno;
}
