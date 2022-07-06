package com.wogh.vote.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String boardRegister(HttpSession session, Model model) {
		log.info("게시글 등록 폼 요청");
		if(loginStat(session)) {
			String email = (String)session.getAttribute("userId");
			model.addAttribute("email", email);
			return "/board/register";
		}else {
			return "redirect:/";
		}
	}
	
	@PostMapping("/register")
	public String boardRegister(BoardDTO boardDTO, HttpServletRequest request) {
		log.info("등록 요청", boardDTO);
		String tempTime = request.getParameter("closetime");
		if(tempTime != null) {
			String str = tempTime + " 00:00:00";
			boardDTO.setClosetime(str);
		}
		
		String[] items = request.getParameterValues("item");
		
		List<VoteItemDTO> list = new ArrayList<>();
		for(String item : items) {
			VoteItemDTO dto = VoteItemDTO.builder().item(item).build();
			list.add(dto);
		}
		
		boardService.registerBoard(boardDTO, list);
		return "redirect:/board/list";
	}
	
	@GetMapping("/list")
	public String boardPage(PageRequestBoardDTO dto, Model model) {
		log.info(dto);
		if(dto == null) {
			dto = new PageRequestBoardDTO();
		}
		
		PageResponseBoardDTO response = boardService.getList(dto);
		model.addAttribute("list", response.getBoardList());
		model.addAttribute("page", response);
		model.addAttribute("search", dto);
		return "board/list";
	}
	
	@GetMapping("/vote")
	public void vote(BoardDTO dto, Model model) {
		log.info("투표글 상세 조회");
		VoteItemDTO itemDTO = VoteItemDTO.builder().board_num(dto.getBno()).build();
		Long votedItem = itemService.votedItem(itemDTO, dto.getMember_email());
		model.addAttribute("votedItem", votedItem!=null ? votedItem : 0);
		BoardDTO result = boardService.getBoard(dto);
		model.addAttribute("result", result);
		model.addAttribute("closetime", result.getClosetime());
	}
	
	@GetMapping("/result")
	public void boardResponse(String result, Long bno, Model model) {
		model.addAttribute("result", result);
		model.addAttribute("bno", bno);
	}
	
	@PostMapping("/result")
	public String votePro(VoteItemDTO itemDTO, PageRequestBoardDTO reqDTO, 
				HttpSession session, Model model) {
		String email = (String)session.getAttribute("userId");
		String result = itemService.throwVote(itemDTO, email);
		model.addAttribute("result", result);
		model.addAttribute("bno", itemDTO.getBoard_num());
		model.addAttribute("email", email);
		model.addAttribute("list", reqDTO);
		return "/board/result";
	}
	
	@GetMapping("/modify")
	public void modify(BoardDTO boardDTO, HttpServletRequest request, Model model) {
		log.info("수정 폼 요청");
		List<Long> inoList = new ArrayList<>();
		String[] inos = request.getParameterValues("ino");
		String[] items = request.getParameterValues("item");
		List<VoteItemDTO> list = new ArrayList<>();
		for(int i=0; i<inos.length; i++) {
			VoteItemDTO itemDTO = VoteItemDTO.builder().ino(Long.parseLong(inos[i]))
														.board_num(boardDTO.getBno())
														.item(items[i]).build();
			list.add(itemDTO);
			inoList.add(Long.parseLong(inos[i]));
		}
		int canMod = itemService.canModify(inoList);
		model.addAttribute("canMod", canMod);
		model.addAttribute("boardDTO", boardDTO);
		model.addAttribute("list", list);
	}
	
	@Transactional
	@PostMapping("/modify")
	public String modifyPro(BoardDTO boardDTO, HttpServletRequest request, RedirectAttributes reAttr) {
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
		
		reAttr.addAttribute("result", "수정성공");
		reAttr.addAttribute("bno", boardDTO.getBno());
		
		return "redirect:/board/result";
	}
	
	@Transactional
	@PostMapping("/delete")
	public String deletePro(BoardDTO boardDTO, RedirectAttributes reAttr) {
		Long res = boardService.deleteBoard(boardDTO);
		if(res != null) {
			reAttr.addAttribute("result", "삭제성공");
		}else {
			reAttr.addAttribute("result", "삭제실패");
			reAttr.addAttribute("bno", boardDTO.getBno());
		}
		return "redirect:/board/result";
	}
	
	@ResponseBody
	@PostMapping("/deleteitem")
	public void deleteItem(@RequestBody VoteItemDTO itemDTO) {
		itemService.deleteItem(itemDTO);
	}
}
