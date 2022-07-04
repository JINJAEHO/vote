package com.wogh.vote.controller;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;
	
	private boolean loginStat(HttpSession session) {
		Object loginSession = session.getAttribute("userLogin");
		if(loginSession != null && loginSession.toString().equals("login")) return true;
		return false;
	}
	
	//회원가입 폼 요청
	@GetMapping("/signup")
	public void signup() {
		log.info("singup form 요청");
	}
	
	//회원가입 처리
	@PostMapping("/signup")
	public String signupPro(MemberDTO dto) {
		log.info(dto);
		String email = memberService.insertMember(dto);
		if(email == null) {
			return null;
		}
		return "/main";
	}
	
	//로그인 화면 요청
	@GetMapping("/login")
	public String login(HttpSession session) {
		log.info("login 화면 요청");
		if(loginStat(session)) return "redirect:/main";
		return "/member/login";
	}
	
	//로그인 처리
	@PostMapping("/login")
	public String loginPro(MemberDTO dto, HttpSession session, Model model) {
		int result = memberService.memberLogin(dto);
		if(result == 1) { //로그인 성공 시
			session.setAttribute("userLogin", "login");
			session.setAttribute("userId", dto.getEmail());
			return "/main";
		}
		model.addAttribute("loginCheck", 0);
		return "/member/login";
	}
	
	//마이 페이지 요청
	@GetMapping("/mypage")
	public void mypage(MemberDTO dto, HttpSession session, Model model) {
		log.info("mypage 요청");
		if(loginStat(session)) {
			MemberDTO findMember = memberService.getMember(dto);
			if(findMember == null) {
				return;
			}
			model.addAttribute("memberDTO", dto);
		}
	}
	
	//회원 정보 수정 폼 요청
	@GetMapping("/modify")
	public void modify(MemberDTO dto, HttpSession session, Model model) {
		log.info("회원정보 수정 폼 요청");
		if(loginStat(session)) {
			model.addAttribute("memberDTO", dto);
		}
	}
	
	//회원 정보 수정 처리
	@Transactional
	@PutMapping("/modify")
	public void modifyPro(MemberDTO dto) {
		log.info("회원정보 수정 처리");
		memberService.updateMember(dto);
		//return "/member/mypage";
	}
	
	//회원 탈퇴 처리
	@Transactional
	@DeleteMapping("/modify")
	public String deletePro(MemberDTO dto, HttpSession session) {
		session.removeAttribute("userLogin");
		session.removeAttribute("userId");
		
		memberService.deleteMember(dto);
		return "/main";
	}
}
