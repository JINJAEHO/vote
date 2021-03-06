package com.wogh.vote.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wogh.vote.dto.FollowDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.PageResponseBoardDTO;
import com.wogh.vote.service.BoardService;
import com.wogh.vote.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;
	private final BoardService boardService;
	
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
		Long result = memberService.memberLogin(dto);
		if(result != -1 && result != 0) { //로그인 성공 시
			session.setAttribute("userLogin", "login");
			session.setAttribute("userId", dto.getEmail());
			session.setAttribute("userNum", result);
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
		session.removeAttribute("userNum");
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
	
	//팔로우 처리
	@ResponseBody
	@PostMapping("/follow")
	public JSONObject followPro(@RequestBody FollowDTO dto) {
		String result = memberService.followMember(dto);
		
		Map<String, String> map = new HashMap<>();
		map.put("result", result);
		JSONObject json = new JSONObject(map);
		
		return json;
	}
	
	//메뉴이동
	@GetMapping("/menu")
	public void menu() {
		log.info("menu 이동");
	}
	
	//팔로우 메뉴
	@GetMapping("/follow")
	public void follow(HttpSession session, Model model) {
		log.info("follow 목록 요청");
		String email = (String)session.getAttribute("userId");
		
		List<FollowDTO> follower = memberService.getFollower(email);
		List<FollowDTO> following = memberService.getFollowing(email);
		
		model.addAttribute("followerList", follower);
		model.addAttribute("followingList", following);
	}
	
	//작성한 글
	@GetMapping("/myboard")
	public void myBoard(PageRequestBoardDTO dto, HttpSession session, Model model) {
		log.info("작성한 글 요청");
		int page = dto.getPage()-1;
		int size = dto.getSize();
		
		Sort sort = Sort.by("bno").descending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		Long mno = (Long)session.getAttribute("userNum");
		
		PageResponseBoardDTO res = boardService.getListByMember(mno, pageable);
		model.addAttribute("result", res);
	}
	
	//참여한 투표들
	@GetMapping("/attendvote")
	public void attVote(PageRequestBoardDTO dto, HttpSession session, Model model) {
		log.info("참여한 투표 요청");
		Sort sort = Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		
		String email = (String)session.getAttribute("userId");
		
		PageResponseBoardDTO res = boardService.attendBoard(pageable, email);
		model.addAttribute("result", res);
	}
}
