package com.wogh.vote.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "member")
public class Board extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bno;
	
	private String title;
	
	private String description;
	
	private LocalDateTime closetime;
	
	private boolean anonymous;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
	private final List<VoteItem> items = new ArrayList<VoteItem>();
	
	public void changeBoard(String title, String description, LocalDateTime closetime) {
		if(this.title != title && title != null) this.title = title;
		if(this.description != description && description != null) this.description = description;
		if(this.closetime != closetime && closetime != null) this.closetime = closetime;
	}
}
