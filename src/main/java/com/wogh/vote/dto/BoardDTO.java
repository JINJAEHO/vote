package com.wogh.vote.dto;

import java.time.LocalDateTime;
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
	
	private LocalDateTime closetime;
	
	private String title;
	
	private String description;
	
	private boolean anonymous;
	
	private boolean dead;
	
	private Long member_id;
	
	private String member_email;
	
	private String member_nickname;
	
	private LocalDateTime regdate;
	
	private LocalDateTime moddate;
	
	private List<VoteItemDTO> voteItem;
}
