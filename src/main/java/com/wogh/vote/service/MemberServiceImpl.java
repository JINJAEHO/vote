package com.wogh.vote.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.wogh.vote.dto.FollowDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.model.Follow;
import com.wogh.vote.model.Member;
import com.wogh.vote.persistency.FollowRepository;
import com.wogh.vote.persistency.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;
	
	@Override
	public String checkEmail(String email) {
		List<Member> findMember = memberRepository.findByEmail(email);
		if(findMember.size() > 0) {
			return "사용불가";
		}
		return "사용가능";
	}
	
	@Override
	public String checkNickname(String nickname) {
		List<Member> findMember = memberRepository.findByNickname(nickname);
		if(findMember.size() > 0) {
			return "사용불가";
		}
		return "사용가능";
	}

	@Override //멤버 등록
	public String insertMember(MemberDTO memberDTO) {
		Member member = dtoToEntity(memberDTO);

		memberRepository.save(member);
		return member.getEmail();
	}

	@Override //로그인 처리
	public Long memberLogin(MemberDTO memberDTO) {
		
		List<Member> result = memberRepository.findByEmail(memberDTO.getEmail());
		
		if(result.size() > 0) {
			
			Member member = result.get(0);
			if(BCrypt.checkpw(memberDTO.getPassword(), member.getPassword())) {
				return member.getMno();
			}else {
				return -1L;
			}
		}
		
		return 0L;
	}

	@Override //멤버 조회
	public MemberDTO getMember(MemberDTO memberDTO) {
		
		List<Member> findMember = memberRepository.findByEmail(memberDTO.getEmail());
		
		if(findMember.size() > 0) {
			Member member = findMember.get(0);
			return entityToDto(member);
		}
		
		return null;
	}

	@Override
	public String updateMember(MemberDTO memberDTO) {
		Member findMember = memberRepository.findByEmail(memberDTO.getEmail()).get(0);
		findMember.changeMember(memberDTO.getPassword(), memberDTO.getName(), 
				memberDTO.getNickname(), memberDTO.isOfficialmark());
		
		return findMember.getEmail();
	}

	@Override
	public String deleteMember(MemberDTO memberDTO) {
		memberRepository.deleteByEmail(memberDTO.getEmail());
		return memberDTO.getEmail();
	}

	@Override //팔로우 처리
	public String followMember(FollowDTO followDTO) {
		Member member = memberRepository.findByEmail(followDTO.getYouEmail()).get(0);
		
		Optional<Follow> opt = followRepository.findByMeAndFmember(followDTO.getMe(), member);
		if(opt.isPresent()) {
			return "팔로우중";
		}
		
		Follow follow = Follow.builder().me(followDTO.getMe())
										.fmember(member).build();
		followRepository.save(follow);
		return "팔로우성공";
	}
	
	@Override
	public List<FollowDTO> getFollower(String email) {
		Member member = memberRepository.findByEmail(email).get(0);
		List<Follow> list = followRepository.findByFmember(member);
		
		List<FollowDTO> result = new ArrayList<>();
		for(Follow follow : list) {
			FollowDTO dto = FollowDTO.builder().fno(follow.getFno())
											.me(follow.getMe())
											.youEmail(email).build();
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public List<FollowDTO> getFollowing(String email) {
		List<Follow> list = followRepository.findByMe(email);
		
		List<FollowDTO> result = new ArrayList<>();
		for(Follow follow : list) {
			FollowDTO dto = FollowDTO.builder().fno(follow.getFno())
											.me(email)
											.youEmail(follow.getFmember().getEmail()).build();
			result.add(dto);
		}
		return result;
	}
}
