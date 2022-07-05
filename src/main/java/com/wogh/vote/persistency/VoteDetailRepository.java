package com.wogh.vote.persistency;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wogh.vote.model.VoteDetail;
import com.wogh.vote.model.VoteItem;

public interface VoteDetailRepository extends JpaRepository<VoteDetail, Long> {
	Integer countByVoteitem(VoteItem voteitem);
	
	@Query(value = "select v.* from votedetail v where voter=:voter and item_num=:ino", nativeQuery = true)
	Optional<VoteDetail> findByVoterAndIno(@Param("voter") String voter, @Param("ino") Long ino);
}
