package com.wogh.vote;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.FollowDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;
import com.wogh.vote.service.BoardService;
import com.wogh.vote.service.ItemService;
import com.wogh.vote.service.MemberService;

@SpringBootTest
@Transactional
@Rollback(false)
public class ServiceTest {
	
	@Autowired
	private BoardService boardService;
	
	//@Test
	public void closeTest() {
		boardService.checkClose();
	}
	
	//@Test
	public void boardRegiTest() {
		BoardDTO boardDTO = BoardDTO.builder().title("제목")
												.description("내용")
												.anonymous(true)
												.member_id(2L)
												.build();
		List<VoteItemDTO> list = new ArrayList<>();
		for(int i=1; i<4; i++){
			VoteItemDTO itemDTO = VoteItemDTO.builder().item("투표 항목"+i)
														.imageurl("asd.png")
														.build();
			list.add(itemDTO);
		}
		boardService.registerBoard(boardDTO, list);
	}
	
	//@Test
	public void getBoardTest() {
		BoardDTO dto = BoardDTO.builder().bno(21L).build();
		BoardDTO result = boardService.getBoard(dto);

	}
	
	//@Test
	public void topTest() {
		List<BoardDTO> list = boardService.mostPopluar();
		for(BoardDTO dto : list) {
			System.out.println(dto.getMember_nickname());
		}
	}
	
	@Autowired
	private ItemService itemService;
	
	//@Test
	public void checkTest() {
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		int result = itemService.canModify(list);
		System.out.println(result);
	}
	
	//@Test
	public void throwVoteTest() {
		VoteItemDTO dto = VoteItemDTO.builder().ino(61L).board_num(31L).build();
		String email = "wogh3@gmail.com";
		String res = itemService.throwVote(dto, email);
		System.out.println(res);
	}
	
	@Autowired
	private MemberService memberService;
	
	//@Test
	public void followTest() {
		FollowDTO dto = FollowDTO.builder().me("wogh8@gmail.com")
										.youEmail("wogh4@gmail.com").build();
		memberService.followMember(dto);
	}
	
	@Test
	public void totalFollow() {
		//memberService.getFollowing("wogh4@gmail.com");
		memberService.getFollower("wogh9@gmail.com");
	}
}
