package com.wogh.vote.persistency;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wogh.vote.model.VoteDetail;
import com.wogh.vote.model.VoteItem;

public interface VoteDetailRepository extends JpaRepository<VoteDetail, Long> {
	Integer countByVoteitem(VoteItem voteitem);
}
