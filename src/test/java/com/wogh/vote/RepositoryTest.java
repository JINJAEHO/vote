package com.wogh.vote;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Follow;
import com.wogh.vote.model.Member;
import com.wogh.vote.model.VoteItem;
import com.wogh.vote.persistency.BoardRepository;
import com.wogh.vote.persistency.FollowRepository;
import com.wogh.vote.persistency.MemberRepository;
import com.wogh.vote.persistency.VoteDetailRepository;
import com.wogh.vote.persistency.VoteItemRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class RepositoryTest {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Test
	public void attendTest() {
		Sort sort = Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(1, 10, sort);
		boardRepository.boardByAttend(pageable, "wogh4@gmail.com");
	}
	
	//@Test
	public void memberboard() {
		Member member = Member.builder().mno(7L).build();
		Pageable pageable= PageRequest.of(0, 10);
		boardRepository.findByMember(member, pageable);
	}
	
	//@Test
	public void followCustoemTest() {
		PageRequestBoardDTO dto = PageRequestBoardDTO.builder().page(1)
										.size(8)
										.keyword("wogh4@gmail.com")
										.build();
		Page<Board> page = boardRepository.boardByFollow(dto);
		System.out.println(page.getContent());
	}
	
	//@Test
	public void saveBoard() {
		Board board = Board.builder().title("asd").description("asd").member(Member.builder().mno(10L).build()).build();
		boardRepository.save(board);
	}
	
	//@Test
	public void folloBoardTest() {
		Pageable pageable = PageRequest.of(0, 3);
		List<Board> list = boardRepository.getLatestFollow("wogh4@gmail.com", pageable);
		for(Board board : list) {
			System.out.println("==============================================");
			System.out.println(board);
			System.out.println(board.getMember());
			System.out.println(board.getMember().getFollows());
		}
	}
	
	//@Test
	public void topTest() {
		boardRepository.findTop3();
	}
	
	@Autowired
	private VoteItemRepository itemRepository;
	
	//@Test
	public void itemByBnoTest() {
		List<VoteItem> list = itemRepository.findByBno(21L);
		System.out.println(list);
	}
	
	@Autowired
	private VoteDetailRepository detailRepository;
	
	//@Test
	public void testVoterAndIno() {
		detailRepository.findByVoterAndIno("wogh3@gmail.com", 2L);
	}
}
