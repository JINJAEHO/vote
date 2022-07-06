package com.wogh.vote.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.FollowDTO;
import com.wogh.vote.service.BoardService;
import com.wogh.vote.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {
	
	private final BoardService boardService;
	private final MemberService memberService;
	
	@Transactional
	@RequestMapping("/")
	public String home(HttpSession session, Model model) {
		log.info("mainpage 요청");
		//투표 마감여부 체크
		boardService.checkClose();
		//인기글 가져오기
		List<BoardDTO> list = boardService.mostPopluar();
		//팔로우 정보 가져오기
		String sessionLogin = (String)session.getAttribute("userLogin");
		if(sessionLogin != null) {
			List<FollowDTO> followList = memberService.getFollow((String)session.getAttribute("userId"));
			model.addAttribute("followList", followList);
			
			List<BoardDTO> boardList = new ArrayList<>();
			for(FollowDTO follow : followList) {
				BoardDTO findBoard = boardService.getFollowerLatest(follow.getMno());
				boardList.add(findBoard);
			}
			model.addAttribute("boadList", boardList);
		}
		model.addAttribute("list", list);
		return "index";
	}
}