package com.wogh.vote.persistency;

import java.time.LocalDateTime;
import java.util.List;
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
	
	@Query(value="select b.*, "
			+ "sum(i.count) c from board as b left join voteitem as i "
			+ "on b.bno=i.board_num "
			+ "group by i.board_num order by c desc limit 3", nativeQuery = true)
	List<Board> findTop3();
	
	//마감체크
	@Query("select b from Board b where b.closetime < :now")
	List<Board> checkClose(@Param("now") LocalDateTime now);
	
	@Query("select b from Board b join fetch b.member m join fetch m.follows f where f.me = :me order by b.bno desc")
	List<Board> getLatestFollow(@Param("me") String me, Pageable pageable);
}
