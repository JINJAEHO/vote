package com.wogh.vote.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.wogh.vote.dto.FollowDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.model.Member;

public interface MemberService {
	
	//아이디 중복 체크
	public String checkEmail(String email);
	
	//닉네임 중복 체크
	public String checkNickname(String nickname);
	
	//새로운 회원 등록
	public String insertMember(MemberDTO memberDTO);
	
	//로그인
	public int memberLogin(MemberDTO memberDTO);

	public MemberDTO getMember(MemberDTO memberDTO);
	public String updateMember(MemberDTO memberDTO);
	public String deleteMember(MemberDTO memberDTO);
	
	//팔로우 등록
	String followMember(FollowDTO followDTO);
	//팔로우 가져오기
	List<FollowDTO> getFollow(String email);
	
	public default Member dtoToEntity(MemberDTO memberDTO) {
		String password = "";
		if(memberDTO.getPassword() != null) {
			password = BCrypt.hashpw(memberDTO.getPassword(), BCrypt.gensalt());
		}
		Member member = Member.builder().email(memberDTO.getEmail())
										.password(password)
										.name(memberDTO.getName())
										.nickname(memberDTO.getNickname())
										.officialmark(memberDTO.isOfficialmark())
										.build();
		return member;
	}
	
	public default MemberDTO entityToDto(Member member) {
		MemberDTO dto = MemberDTO.builder().email(member.getEmail())
											.password(member.getPassword())
											.name(member.getName())
											.nickname(member.getNickname())
											.officialmark(member.isOfficialmark())
											.build();
		return dto;
	}
}
