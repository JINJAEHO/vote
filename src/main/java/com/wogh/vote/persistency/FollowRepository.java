package com.wogh.vote.persistency;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wogh.vote.model.Follow;
import com.wogh.vote.model.Member;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("select f from Follow f where f.member=:member and f.you=:you")
	Optional<Follow> findByMemberAndYou(@Param("member") Member member, @Param("you") String you);
	
	List<Follow> findByMember(Member member);
}
