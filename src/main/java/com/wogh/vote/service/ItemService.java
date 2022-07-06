package com.wogh.vote.service;

import java.util.List;

import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.VoteItem;

public interface ItemService {

	public Long itemDelete(VoteItemDTO itemDTO);
	
	public int canModify(List<Long> itemIdList);
	
	public void itemUpdate(List<VoteItemDTO> itemDTO);
	
	public String throwVote(VoteItemDTO itemDTO, String email);
	
	Long votedItem(VoteItemDTO itemDTO, String email);
	
	//투표항목 삭제
	void deleteItem(VoteItemDTO itemDTO);
	
	public default VoteItem dtoToEntity(VoteItemDTO dto) {
		VoteItem item = VoteItem.builder().ino(dto.getIno())
										.item(dto.getItem())
										.imageurl(dto.getImageurl())
										.count(dto.getCount())
										.board(Board.builder().bno(dto.getBoard_num()).build())
										.build();
		return item;
	}
	
	public default VoteItemDTO entityToDto(VoteItem item) {
		VoteItemDTO dto = VoteItemDTO.builder().ino(item.getIno())
												.item(item.getItem())
												.imageurl(item.getImageurl())
												.count(item.getCount())
												.board_num(item.getBoard().getBno())
												.build();
		return dto;
	}
}
