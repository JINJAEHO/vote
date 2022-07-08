package com.wogh.vote.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wogh.vote.dto.BoardDTO;
import com.wogh.vote.dto.PageRequestBoardDTO;
import com.wogh.vote.dto.PageResponseBoardDTO;
import com.wogh.vote.dto.VoteItemDTO;
import com.wogh.vote.model.Board;
import com.wogh.vote.model.Member;
import com.wogh.vote.model.VoteItem;
import com.wogh.vote.persistency.BoardRepository;
import com.wogh.vote.persistency.MemberRepository;
import com.wogh.vote.persistency.VoteItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;
	private final VoteItemRepository itemRepository;
	private final MemberRepository memberRepository;
	
	//application.properties 파일에 작성한 속성 가져오기
	@Value("${com.board.upload.path}")
	private String uploadPath;
	
	//업로드한 날짜 별로 이미지를 저장하기 위해서 날짜별로 디렉토리를 만들어서 경로를 리턴하는 메서드
	private String makeFolder() {
		//오늘 날짜를 문자열로 생성
		String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		//문자열을 치환 - /를 운영체제의 디렉토리 구분자로 치환
		String realUploadPath = str.replace("//", File.separator);
		//디렉토리 생성
		File uploadPathDir = new File(uploadPath, realUploadPath);
		if(uploadPathDir.exists() == false) {
			uploadPathDir.mkdirs();
		}
		return realUploadPath;
	}

	@Override // 투표글 등록
	public Long registerBoard(BoardDTO boardDTO, List<VoteItemDTO> itemDTO) {
		//파일 업로드 처리
		//전송 받은 파일을 가져오기
		for(VoteItemDTO item : itemDTO) {
			//image 파라미터의 값을 가져오기
			MultipartFile uploadFile = item.getImage();
			//잔성된 파일이 있다면
			if(uploadFile != null) {
				//원본 파일의 파일 이름 찾아오기
				String originalName = uploadFile.getOriginalFilename();
				String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
				
				//파일을 업로드할 디렉토리 경로를 생성
				String realUploadPath = makeFolder();
				
				//업로드 할 파일의 경로를 생성
				String uuid = UUID.randomUUID().toString();
				String saveName = uploadPath + File.separator + realUploadPath 
									+ "/" + uuid + fileName;
				Path savePath = Paths.get(saveName);
				try {
					//파일 업로드
					uploadFile.transferTo(savePath);
					//이미지 경로를 DTO에 설정
					item.setImageurl(realUploadPath + "/" + uuid + fileName);
				} catch (Exception e) {
					log.info(e.getLocalizedMessage());
				}
			}
		}
		
		//마감 시간을 설정하지 않았다면 24시간을 기본값으로 설정
		if(boardDTO.getClosetime() == null) {
			ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
			LocalDateTime currentDateTime = nowUTC.withZoneSameInstant(
											ZoneId.of("Asia/Seoul")).toLocalDateTime();
			LocalDateTime closetime = currentDateTime.plusHours(24);
			String cltime = closetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			boardDTO.setClosetime(cltime);
		}

		// Board 정보 DB에 저장
		Long mno = memberRepository.findByEmail(boardDTO.getMember_email()).get(0).getMno();
		boardDTO.setMember_id(mno);
		Board board = dtoToEntity(boardDTO);
		boardRepository.save(board);

		// 투표 항목들 리스트로 변환 후 한번에 DB에 저장
		List<VoteItem> list = new ArrayList<>();
		for(VoteItemDTO dto : itemDTO) {
			VoteItem item = VoteItem.builder().item(dto.getItem())
												.imageurl(dto.getImageurl())
												.board(board).count(0).build();
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
			return entityToDto(optional.get(), 2);
		}
		return null;
	}
	
	@Override //특정 멤버의 전체 글 가져오기
	public PageResponseBoardDTO getListByMember(Long mno, Pageable pageable) {
		Member findMember = Member.builder().mno(mno).build();
		Page<Board> page = boardRepository.findByMember(findMember, pageable);
		
		PageResponseBoardDTO result = new PageResponseBoardDTO();
		result.setTotalPage(page.getTotalPages());
		result.makePageList(pageable);
		List<BoardDTO> list = new ArrayList<>();
		page.get().forEach(item -> {
			list.add(entityToDto(item, 3));
		});
		result.setBoardList(list);
		
		return result;
	}

	@Override //글 수정
	public Long updateBoard(BoardDTO dto) {
		Board findBoard = boardRepository.findById(dto.getBno()).get();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime closetime = LocalDateTime.parse(dto.getClosetime(), formatter);
		findBoard.changeBoard(dto.getTitle(), dto.getDescription(), closetime);
		return dto.getBno();
	}
	
	@Override //글 삭제
	public Long deleteBoard(BoardDTO dto) {
		boardRepository.deleteById(dto.getBno());
		return dto.getBno();
	}
	
	@Override //전체 글 조회
	public PageResponseBoardDTO getList(PageRequestBoardDTO dto) {
		Sort sort= Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		Page<Board> page = boardRepository.searchByDynamicQuery(dto);
		
		PageResponseBoardDTO result = new PageResponseBoardDTO();
		result.setTotalPage(page.getTotalPages());
		result.makePageList(pageable);
		List<BoardDTO> list = new ArrayList<>();
		page.get().forEach(item -> {
			list.add(entityToDto(item, 0));
		});
		result.setBoardList(list);
		
		return result;
	}
	
	@Override //인기 글 조회
	public List<BoardDTO> mostPopluar() {
		List<Board> list = boardRepository.findTop3();
		List<BoardDTO> result = new ArrayList<>();
		
		for(Board board : list) {
			result.add(entityToDto(board, 3));
		}
		
		return result;
	}
	
	@Override
	public void checkClose() {
		LocalDateTime now = LocalDateTime.now();
		List<Board> findList = boardRepository.checkClose(now);
		
		for(Board board : findList) {
			board.closeBoard();
		}
	}
	
	@Override //팔로워 최근 게시글
	public List<BoardDTO> getFollowerLatest(String email) {
		Pageable pageable = PageRequest.of(0, 3);
		List<Board> list = boardRepository.getLatestFollow(email, pageable);
		
		List<BoardDTO> result = new ArrayList<>();
		for(Board board : list) {
			BoardDTO dto = entityToDto(board, 0);
			result.add(dto);
		}
		return result;
	}
	
	@Override
	public PageResponseBoardDTO getListofFollow(PageRequestBoardDTO dto) {
		Sort sort= Sort.by("bno").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		Page<Board> page = boardRepository.boardByFollow(dto);
		
		PageResponseBoardDTO result = new PageResponseBoardDTO();
		result.setTotalPage(page.getTotalPages());
		result.makePageList(pageable);
		List<BoardDTO> list = new ArrayList<>();
		page.get().forEach(item -> {
			list.add(entityToDto(item, 0));
		});
		result.setBoardList(list);
		return result;
	}
	
	@Override
	public PageResponseBoardDTO attendBoard(Pageable pageable, String email) {
		Page<Board> page = boardRepository.boardByAttend(pageable, email);
		
		PageResponseBoardDTO result = new PageResponseBoardDTO();
		result.setTotalPage(page.getTotalPages());
		result.makePageList(pageable);
		List<BoardDTO> list = new ArrayList<>();
		page.get().forEach(item -> {
			list.add(entityToDto(item, 0));
		});
		result.setBoardList(list);
		return result;
	}
}
