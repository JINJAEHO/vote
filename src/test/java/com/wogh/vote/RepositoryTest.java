package com.wogh.vote;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.wogh.vote.persistency.BoardRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class RepositoryTest {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Test
	public void topTest() {
		boardRepository.findTop3();
	}
}
