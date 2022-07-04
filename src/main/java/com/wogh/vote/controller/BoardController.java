package com.wogh.vote.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.PageResponseBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.service.BoardService;
import com.wogh.vote.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {

	public final BoardService boardService;
	public final ItemService itemService;
	
	private boolean loginStat(HttpSession session) {
		Object loginSession = session.getAttribute("userLogin");
		if(loginSession != null && loginSession.toString().equals("login")) return true;
		return false;
	}
	
	@GetMapping("/register")
	public void boardRegister(HttpSession session, Model model) {
		log.info("게시글 등록 폼 요청");
		if(loginStat(session)) {
			String email = (String)session.getAttribute("userId");
			model.addAttribute("email", email);
		}
		return;
	}
	
	@PostMapping("/register")
	public String boardRegister(BoardDTO boardDTO, HttpServletRequest request) {
		log.info(boardDTO);
		
		String[] items = request.getParameterValues("item");
		
		List<VoteItemDTO> list = new ArrayList<>();
		for(String item : items) {
			VoteItemDTO dto = VoteItemDTO.builder().item(item).build();
			list.add(dto);
		}
		
		boardService.registerBoard(boardDTO, list);
		return "/board/list";
	}
	
	@GetMapping("/list")
	public String boardPage(@RequestParam(required = false) PageRequestBoardDTO dto, Model model) {
		log.info(dto);
		if(dto == null) {
			dto = new PageRequestBoardDTO();
		}
		
		PageResponseBoardDTO response = boardService.getList(dto);
		model.addAttribute("list", response.getBoardList());
		return "board/list";
	}
	
	@GetMapping("/vote")
	public void vote(BoardDTO dto, Model model) {
		log.info("투표글 상세 조회");
		BoardDTO result = boardService.getBoard(dto);
		model.addAttribute("result", result);
	}
	
	@GetMapping("/modify")
	public void modify(BoardDTO boardDTO, Model model) {
		List<Long> inoList = new ArrayList<>();
		for(VoteItemDTO itemDTO : boardDTO.getVoteItem()) {
			inoList.add(itemDTO.getIno());
		}
		int canMod = itemService.canModify(inoList);
		model.addAttribute("canMod", canMod);
		model.addAttribute("boardDTO", boardDTO);
	}
	
	@Transactional
	@PutMapping("/modify")
	public String modifyPro(BoardDTO boardDTO, HttpServletRequest request) {
		String[] inos = request.getParameterValues("ino");
		String[] items = request.getParameterValues("item");
		
		List<VoteItemDTO> itemList = new ArrayList<>();
		
		for(int i=0; i<items.length; i++) {
			try {
				VoteItemDTO itemDTO = VoteItemDTO.builder().ino(Long.parseLong(inos[i]))
														.item(items[i])
														.board_num(boardDTO.getBno()).build();
				itemList.add(itemDTO);
			} catch (ArrayIndexOutOfBoundsException e) {
				log.info("new item add");
				VoteItemDTO itemDTO = VoteItemDTO.builder()
										.item(items[i])
										.board_num(boardDTO.getBno()).build();
				itemList.add(itemDTO);
			}
		}
		itemService.itemUpdate(itemList);
		boardService.updateBoard(boardDTO);
		return "/board/list";
	}
	
	@Transactional
	@DeleteMapping("/modify")
	public String deletePro(BoardDTO boardDTO) {
		boardService.deleteBoard(boardDTO);
		return "/board/list";
	}
}
