package com.wogh.vote.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.VoteDetail;
import com.wogh.vote.model.VoteItem;
import com.wogh.vote.persistency.VoteDetailRepository;
import com.wogh.vote.persistency.VoteItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	
	private final VoteItemRepository itemRepository;
	private final VoteDetailRepository detailRepository;
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Long itemDelete(VoteItemDTO itemDTO) {
		itemRepository.deleteById(itemDTO.getIno());
		return itemDTO.getIno();
	}
	
	@Override // 수정 가능 여부 체크
	public int canModify(List<Long> itemIdList) {
		boolean canModifyItem = false;
		// 투표 진행 여부 체크
		for(Long id : itemIdList) {
			VoteItem voteItem = itemRepository.findById(id).get();
			long count = detailRepository.countByVoteitem(voteItem);
			// 투표에 참여한 사람이 1명이라도 있는 경우 수정 불가
			if(count > 0) {
				canModifyItem = false;
				break;
			// 아직 투표가 진행되지 않은 경우 수정 가능
			}else canModifyItem = true;
		}
		
		if(canModifyItem) return 1;
		
		return 0;
	}
	
	@Override // 투표 항목 수정 처리
	public void itemUpdate(List<VoteItemDTO> itemDTO) {
		List<VoteItem> list = new ArrayList<>();
		for(VoteItemDTO dto : itemDTO) {
			VoteItem item = dtoToEntity(dto);
			list.add(item);
		}
		// 투표 항목은 추가도 가능하기에 save로 처리
		itemRepository.saveAll(list);
	}
	
	@Override //실제 투표 처리
	public String throwVote(VoteItemDTO itemDTO, String email) {
		VoteItem item = itemRepository.findById(itemDTO.getIno()).get();
		
		VoteDetail detail = VoteDetail.builder().voter(email)
											.voteitem(item)
											.build();
		// save를 하면 commit이 되기 때문에 EntityManager의 persist를 이용
		em.persist(detail);
		
		VoteDetail result = null;
		
		Optional<VoteDetail> optional = detailRepository.findById(detail.getDno());
		if(optional.isPresent()) {
			result = optional.get();
		}
		
		// 득표 수 갱신
		Integer itemCount = detailRepository.countByVoteitem(item);
		//commit 전 이므로 더티 체킹을 이용해 업데이트
		item.countVote(itemCount);
		
		if(result != null) return "성공";
		return "실패";
	}
}