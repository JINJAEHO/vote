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
	
	//글 상세 정보 조회
	@EntityGraph(attributePaths = {"member", "items"})
	@Query(value =  "select b from Board b where b.bno = :bno",
			countQuery = "select count(b) from Board b where b.bno = :bno")
	public Optional<Board> findByIdWithJoin(@Param("bno") Long id);
	
	//특정 회원의 글 조회
	public Page<Board> findByMember(Member member, Pageable pageable);
	
	//가장 투표수가 많은 글 조회
	@Query(value="select b.*, "
			+ "sum(i.count) c from board as b left join voteitem as i "
			+ "on b.bno=i.board_num "
			+ "where b.dead = false "
			+ "group by i.board_num order by c desc limit 3", nativeQuery = true)
	List<Board> findTop3();
	
	//마감체크
	@Query("select b from Board b where b.closetime < :now")
	List<Board> checkClose(@Param("now") LocalDateTime now);
	
	//팔로워 글 중 가장 최신글 3개 조회
	@Query("select b from Board b join fetch b.member m join fetch m.follows f where f.me = :me order by b.bno desc")
	List<Board> getLatestFollow(@Param("me") String me, Pageable pageable);
}
