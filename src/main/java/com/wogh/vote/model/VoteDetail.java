package com.wogh.vote.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "votedetail")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "voteitem")
public class VoteDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dno;
	
	private String voter;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_num")
	private VoteItem voteitem;
}
