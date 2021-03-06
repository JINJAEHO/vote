package com.wogh.vote.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
	private Long bno;
	
	private String closetime;
	
	private String title;
	
	private String description;
	
	private boolean anonymous;
	
	private boolean dead;
	
	private Long member_id;
	
	private String member_email;
	
	private String member_nickname;
	
	private String regdate;
	
	private String moddate;
	
	private List<VoteItemDTO> voteItem;
}
