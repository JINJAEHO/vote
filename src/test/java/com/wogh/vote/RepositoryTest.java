package com.wogh.vote;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.wogh.vote.model.VoteItem;
import com.wogh.vote.persistency.BoardRepository;
import com.wogh.vote.persistency.VoteDetailRepository;
import com.wogh.vote.persistency.VoteItemRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class RepositoryTest {
	
	@Autowired
	private BoardRepository boardRepository;
	
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
	
	@Test
	public void testVoterAndIno() {
		detailRepository.findByVoterAndIno("wogh3@gmail.com", 2L);
	}
	
}
