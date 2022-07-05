package com.wogh.vote.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.PageResponseBoardDTO;
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
	
	public PageResponseBoardDTO getList(PageRequestBoardDTO dto);
	
	List<BoardDTO> mostPopluar();
	
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
	
	public default BoardDTO entityToDto(Board board, int check) {
		List<VoteItemDTO> voteItem = new ArrayList<>();
		if(check == 1) {
			for(VoteItem item : board.getItems()) {
				VoteItemDTO itemDTO = VoteItemDTO.builder().ino(item.getIno())
														.item(item.getItem())
														.count(item.getCount()).build();
				voteItem.add(itemDTO);
			}
		}
		Member member = new Member();
		if(check == 0) {
			member = board.getMember();
		}
		BoardDTO dto = BoardDTO.builder().bno(board.getBno())
										.title(board.getTitle())
										.description(board.getDescription())
										.closetime(board.getClosetime())
										.dead(board.isDead())
										.anonymous(board.isAnonymous())
										.member_id(member.getMno())
										.member_email(member.getEmail())
										.member_nickname(member.getNickname())
										.voteItem(voteItem)
										.build();
		return dto;
	}
}
