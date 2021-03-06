package com.wogh.vote.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.PageResponseBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;
import com.wogh.vote.model.VoteItem;


public interface BoardService {
	
	public Long registerBoard(BoardDTO boardDTO, List<VoteItemDTO> itemDTO);

	public BoardDTO getBoard(BoardDTO dto);
	
	public PageResponseBoardDTO getListByMember(Long mno, Pageable pageable);
	
	public Long updateBoard(BoardDTO dto);
	
	public Long deleteBoard(BoardDTO dto);
	
	public PageResponseBoardDTO getList(PageRequestBoardDTO dto);
	//팔로워 최근 게시글 조회
	List<BoardDTO> getFollowerLatest(String email);
	//팔로워들의 전체 글 조회
	PageResponseBoardDTO getListofFollow(PageRequestBoardDTO dto);
	
	//마감 체크
	void checkClose();
	
	List<BoardDTO> mostPopluar();
	
	PageResponseBoardDTO attendBoard(Pageable pageable, String email);
	
	public default Board dtoToEntity(BoardDTO dto) {
		String str = dto.getClosetime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime closetime = LocalDateTime.parse(str, formatter);
		
		Board board = Board.builder().bno(dto.getBno())
									.title(dto.getTitle())
									.description(dto.getDescription())
									.closetime(closetime)
									.anonymous(dto.isAnonymous())
									.member(Member.builder().mno(dto.getMember_id()).build())
									.build();
		return board;
	}
	
	public default BoardDTO entityToDto(Board board, int check) {
		String closetime = board.getClosetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String regdate = board.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		Member member = new Member();
		
		List<VoteItemDTO> voteItem = new ArrayList<>();
		if(check == 1) {
			for(VoteItem item : board.getItems()) {
				VoteItemDTO itemDTO = VoteItemDTO.builder().ino(item.getIno())
														.item(item.getItem())
														.count(item.getCount()).build();
				voteItem.add(itemDTO);
			}
		}else if(check == 0) {
			member = board.getMember();
		}else if(check == 2) {
			for(VoteItem item : board.getItems()) {
				VoteItemDTO itemDTO = VoteItemDTO.builder().ino(item.getIno())
														.item(item.getItem())
														.count(item.getCount()).build();
				voteItem.add(itemDTO);
			}
			member = board.getMember();
		}
		BoardDTO dto = BoardDTO.builder().bno(board.getBno())
										.title(board.getTitle())
										.description(board.getDescription())
										.closetime(closetime)
										.regdate(regdate)
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
