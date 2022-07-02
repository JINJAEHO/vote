package com.wogh.vote.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.MemberDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;
import com.wogh.vote.model.VoteItem;
import com.wogh.vote.persistency.BoardRepository;
import com.wogh.vote.persistency.MemberRepository;
import com.wogh.vote.persistency.VoteItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;
	private final VoteItemRepository itemRepository;
	private final MemberRepository memberRepository;

	@Override // 투표글 등록
	public Long registerBoard(BoardDTO boardDTO, List<VoteItemDTO> itemDTO) {
		//마감 시간을 설정하지 않았다면 24시간을 기본값으로 설정
		if(boardDTO.getClosetime() == null) {
			ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
			LocalDateTime currentDateTime = nowUTC.withZoneSameInstant(
											ZoneId.of("Asia/Seoul")).toLocalDateTime();
			LocalDateTime closetime = currentDateTime.plusHours(24);
			boardDTO.setClosetime(closetime);
		}

		// Board 정보 DB에 저장
		Board board = dtoToEntity(boardDTO);
		boardRepository.save(board);

		// 투표 항목들 리스트로 변환 후 한번에 DB에 저장
		List<VoteItem> list = new ArrayList<>();
		for(VoteItemDTO dto : itemDTO) {
			VoteItem item = VoteItem.builder().item(dto.getItem())
												.imageurl(dto.getImageurl())
												.board(board).build();
			list.add(item);
		}
		itemRepository.saveAll(list);

		return board.getBno();
	}

	@Override //글 정보 가져오기
	public BoardDTO getBoard(BoardDTO dto) {
		Long bno = dto.getBno();
		Optional<Board> optional = boardRepository.findByIdWithJoin(bno);
		
		if(optional.isPresent()) {
			return entityToDto(optional.get());
		}
		return null;
	}
	
	@Override //특정 멤버의 전체 글 가져오기
	public Page<Board> getListByMember(MemberDTO memberDTO, Pageable pageable) {
		Member findMember = memberRepository.getById(memberDTO.getMno());
		return boardRepository.findByMember(findMember, pageable);
	}

	@Override //글 수정
	public Long updateBoard(BoardDTO dto) {
		Board findBoard = boardRepository.findById(dto.getBno()).get();
		findBoard.changeBoard(dto.getTitle(), dto.getDescription(), dto.getClosetime());
		return dto.getBno();
	}
	

	@Override //글 삭제
	public Long deleteBoard(BoardDTO dto) {
		Board board = dtoToEntity(dto);
		Long bno = board.getBno();
		boardRepository.deleteById(bno);
		return bno;
	}
	
	@Override
	public Page<Board> getList(PageRequestBoardDTO dto) {
		
		return boardRepository.searchByDynamicQuery(dto);
	}

}
