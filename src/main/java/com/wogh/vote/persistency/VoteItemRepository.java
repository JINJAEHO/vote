package com.wogh.vote.persistency;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wogh.vote.model.VoteItem;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
	@Query(value="select v.* from voteitem v where  board_num=?", nativeQuery = true)
	List<VoteItem> findByBno(Long bno);
}
