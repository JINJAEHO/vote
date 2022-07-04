package com.wogh.vote.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {
	
	private final BoardService boardService;
	
	@Transactional
	@RequestMapping("/")
	public String home(Model model) {
		log.info("mainpage 요청");
		List<BoardDTO> list = boardService.mostPopluar();
		model.addAttribute("list", list);
		return "index";
	}
}
