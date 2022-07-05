package com.wogh.vote.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.model.Member;
import com.wogh.vote.persistency.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberRepository memberRepository;
	
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
	public int memberLogin(MemberDTO memberDTO) {
		
		List<Member> result = memberRepository.findByEmail(memberDTO.getEmail());
		
		if(result.size() > 0) {
			
			Member member = result.get(0);
			if(BCrypt.checkpw(memberDTO.getPassword(), member.getPassword())) {
				return 1;
			}else {
				return -1;
			}
		}
		
		return 0;
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

}
