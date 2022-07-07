package com.wogh.vote.persistency;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.QBoard;
import com.wogh.vote.model.QFollow;
import com.wogh.vote.model.QMember;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
	
	private final JPAQueryFactory query;
	
	private QBoard board = QBoard.board;
	private QMember member = QMember.member;
	private QFollow follow = QFollow.follow;

	@Override 
	public Page<Board> searchByDynamicQuery(PageRequestBoardDTO dto){
		String keyword = dto.getKeyword();
		String type = dto.getType()!=null ? dto.getType() : "";
		
		int page = dto.getPage()-1;
		int size = dto.getSize();
		
		List<Board> list = query.select(board)
					.from(board)
					.join(board.member, member)
					.fetchJoin()
					.where(containTitle(type, keyword), containDescription(type, keyword), 
							containBoth(type, keyword), containNickname(type, keyword))
					.orderBy(board.bno.desc())
					.offset(page*size)
					.limit(size)
					.fetch();
		
		long count = query.selectFrom(board)
				.where(containTitle(type, keyword),containDescription(type, keyword),containBoth(type, keyword))
				.fetchCount();
		
		Sort sort = Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		
		return new PageImpl<>(list, pageable, count);
	}
	
	private BooleanExpression containTitle(String type, String keyword) {
		return type.equals("t") ? board.title.contains(keyword) : null;
	}
	
	private BooleanExpression containDescription(String type, String keyword) {
		return type.equals("c") ? board.description.contains(keyword) : null;
	}
	
	private BooleanExpression containBoth(String type, String keyword) {
		return type.equals("tc") ? board.description.contains(keyword).or(board.title.contains(keyword)) : null;
	}
	
	private BooleanExpression containNickname(String type, String keyword) {
		return type.equals("w") ? member.nickname.contains(keyword) : null;
	}
	
	@Override
	public Page<Board> boardByFollow(PageRequestBoardDTO dto) {
		int page = dto.getPage()-1;
		int size = dto.getSize();
		
		List<Board> list = query.selectFrom(board)
								.join(board.member, member)
								.fetchJoin()
								.join(member.follows, follow)
								.fetchJoin()
								.where(containFollow(dto.getKeyword()))
								.orderBy(board.bno.desc())
								.offset(page*size)
								.limit(size)
								.fetch();
		
		long count = query.selectFrom(board)
						.join(board.member, member)
						.fetchJoin()
						.join(member.follows, follow)
						.fetchJoin()
						.where(containFollow(dto.getKeyword()))
						.orderBy(board.bno.desc())
						.offset(page*size)
						.limit(size)
						.fetchCount();
		
		Sort sort = Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		
		return new PageImpl<>(list, pageable, count);
	}
	
	private BooleanExpression containFollow(String email) {
		return follow.me.eq(email);
	}
}
