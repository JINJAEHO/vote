/**
 * Querydsl濡� �옉�꽦�븷 荑쇰━�뒗 �씠 怨녹뿉 �떆洹몃땲泥섎�� �꽑�뼵�븯怨� `~RepositoryImpl`�뿉�꽌 援ы쁽�븳�떎.
 */

package com.wogh.vote.persistency;

import org.springframework.data.domain.Page;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.model.Board;

public interface BoardRepositoryCustom {
	public Page<Board> searchByDynamicQuery(PageRequestBoardDTO dto);
}
