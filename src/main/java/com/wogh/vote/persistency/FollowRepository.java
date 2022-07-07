package com.wogh.vote.persistency;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wogh.vote.model.Follow;
import com.wogh.vote.model.Member;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Optional<Follow> findByMeAndFmember(String me, Member member); 
	
	//팔로잉 목록 가져오기
	@EntityGraph(attributePaths = {"fmember"})
	List<Follow> findByMe(String me);
	
	//팔로워 목록 가져오기
	List<Follow> findByFmember(Member fmember);
}
