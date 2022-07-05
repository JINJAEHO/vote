package com.wogh.vote.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
		log.info("회원가입 처리 요청");
		String email = memberService.insertMember(dto);
		if(email == null) {
			return null;
		}
		return "/member/login";
	}
	
	//이메일 중복 확인
	@ResponseBody
	@PostMapping("/emailcheck")
	public JSONObject emailCheck(@RequestBody MemberDTO dto){
		log.info("이메일 중복 확인 요청");
		//결과: "사용가능" or "사용불가"
		String result = memberService.checkEmail(dto.getEmail());
		Map<String, String> map = new HashMap<>();
		map.put("result", result);
		
		JSONObject jsonObj = new JSONObject(map);
		
		return jsonObj;
	}
	
	//닉네임 중복 확인
	@ResponseBody
	@PostMapping("/nicknamecheck")
	public JSONObject nicknameCheck(@RequestBody MemberDTO dto){
		log.info("닉네임 중복 확인 요청");
		//결과: "사용가능" or "사용불가"
		String result = memberService.checkNickname(dto.getNickname());
		Map<String, String> map = new HashMap<>();
		map.put("result", result);
		
		JSONObject jsonObj = new JSONObject(map);
		
		return jsonObj;
	}
	
	//로그인 화면 요청
	@GetMapping("/login")
	public String login(HttpSession session) {
		log.info("login 화면 요청");
		if(loginStat(session)) return "redirect:/";
		return "/member/login";
	}
	
	//로그인 처리
	@PostMapping("/login")
	public String loginPro(MemberDTO dto, HttpSession session, Model model) {
		int result = memberService.memberLogin(dto);
		if(result == 1) { //로그인 성공 시
			session.setAttribute("userLogin", "login");
			session.setAttribute("userId", dto.getEmail());
			return "redirect:/";
		}
		model.addAttribute("loginCheck", 0);
		return "/member/login";
	}
	
	//로그아웃
	@GetMapping("/logout")
	public String logoutPro(HttpSession session) {
		session.removeAttribute("userLogin");
		session.removeAttribute("userId");
		return "redirect:/";
	}
	
	//마이 페이지 요청
	@GetMapping("/mypage")
	public void mypage(HttpSession session, Model model) {
		log.info("mypage 요청");
		if(loginStat(session)) {
			MemberDTO dto = MemberDTO.builder().email((String)session.getAttribute("userId")).build();
			MemberDTO findMember = memberService.getMember(dto);
			if(findMember == null) {
				return;
			}
			model.addAttribute("memberDTO", findMember);
		}
	}
	
	//회원 정보 수정 폼 요청
	@GetMapping("/modify")
	public void modify(MemberDTO dto, HttpSession session, Model model) {
		log.info("회원정보 수정 폼 요청");
		if(loginStat(session)) {
			MemberDTO findMember = memberService.getMember(dto);
			model.addAttribute("dto", findMember);
		}
	}
	
	//회원 정보 수정 처리
	@Transactional
	@PostMapping("/modify")
	public String modifyPro(MemberDTO dto) {
		log.info("회원정보 수정 처리");
		memberService.updateMember(dto);
		return "/member/mypage";
	}
	
	//회원 탈퇴 처리
	@Transactional
	@PostMapping("/delete")
	public String deletePro(MemberDTO dto, HttpSession session) {
		log.info("회원 삭제 요청");
		session.removeAttribute("userLogin");
		session.removeAttribute("userId");
		
		memberService.deleteMember(dto);
		return "redirect:/";
	}
}
