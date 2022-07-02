package com.wogh.vote.persistency;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
	
	@EntityGraph(attributePaths = {"member", "items"})
	@Query(value =  "select b from Board b where b.bno = :bno",
			countQuery = "select count(b) from Board b where b.bno = :bno")
	public Optional<Board> findByIdWithJoin(@Param("bno") Long id);
	
	public Page<Board> findByAnonymous(boolean anonymous, Pageable pageable);
	
	@EntityGraph(attributePaths = {"member"})
	@Query(value = "select b from Board b where b.member = :member order by b.bno desc", 
			countQuery = "select count(b) from Board b where b.member = :member")
	public Page<Board> findByMember(@Param("member") Member member, Pageable pageable);
}
