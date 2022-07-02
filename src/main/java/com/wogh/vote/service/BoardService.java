package com.wogh.vote.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;
import com.wogh.vote.model.VoteItem;


public interface BoardService {
	
	public Long registerBoard(BoardDTO boardDTO, List<VoteItemDTO> itemDTO);

	public BoardDTO getBoard(BoardDTO dto);
	
	public Page<Board> getListByMember(MemberDTO memberDTO, Pageable pageable);
	
	public Long updateBoard(BoardDTO dto);
	
	public Long deleteBoard(BoardDTO dto);
	
	public Page<Board> getList(PageRequestBoardDTO dto);
	
	public default Board dtoToEntity(BoardDTO dto) {
		Board board = Board.builder().bno(dto.getBno())
									.title(dto.getTitle())
									.description(dto.getDescription())
									.closetime(dto.getClosetime())
									.anonymous(dto.isAnonymous())
									.member(Member.builder().mno(dto.getMember_id()).build())
									.build();
		return board;
	}
	
	public default BoardDTO entityToDto(Board board) {
		List<Long> voteIdList = new ArrayList<>();
		List<Integer> voteCountList = new ArrayList<>();
		for(VoteItem item : board.getItems()) {
			voteIdList.add(item.getIno());
			voteCountList.add(item.getCount());
		}
		
		BoardDTO dto = BoardDTO.builder().bno(board.getBno())
										.title(board.getTitle())
										.description(board.getDescription())
										.closetime(board.getClosetime())
										.anonymous(board.isAnonymous())
										.member_id(board.getMember().getMno())
										.member_nickname(board.getMember().getNickname())
										.voteIdList(voteIdList)
										.voteCountList(voteCountList)
										.build();
		return dto;
	}
}
