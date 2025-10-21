package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class POHeaderServiceImpl implements POHeaderService {
    private final POHeaderRepository poHeaderRepository;
    private final UserRepository userRepository;


    /** '담기' 클릭시 POHeader 추가 */
    @Override
    @Transactional
    public Long createNewPO() {
        POHeader poHeader = new POHeader();
        poHeader.setStatus(POStatus.PR); // 'PR' = 초안 상태
        poHeader.setTotalAmount(0L);                      // 2️⃣ 기본 금액
        poHeader.setActionDate(LocalDate.now());          // 3️⃣ 오늘 날짜

        // 5️⃣ 테스트용 유저 (ID=1) 실제 로그인 기능이 없으면 임시 유저를 지정
        User testUser = userRepository.findById("admin01")
                .orElseThrow(() -> new IllegalArgumentException("테스트 유저가 없습니다."));
        poHeader.setUser(testUser);

        poHeaderRepository.save(poHeader);
        return poHeader.getPoId();
    }

    /** 모든 발주 헤더 조회 */
    @Override
    public List<POHeaderResponseDTO> findAll() {
        return poHeaderRepository.findAll().stream()
                .map(this::toDto) //현재 클래스에 정의된 toDto() 메서드를 가리킴
                .toList();
    }

    /** 장바구니 저장*/
    @Override
    public void saveCart(Long poId, String remarks) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다."));

        header.setStatus(POStatus.S);
        header.setRemarks(remarks);
        poHeaderRepository.save(header);
    }

    /** 저장된 장바구니 불러오기 */
    @Override
    public List<POHeaderResponseDTO> getSavedCartList(){
        List<POHeader> savedHeaders = poHeaderRepository.findByStatus(POStatus.S);

        return savedHeaders.stream()
                .map(POHeaderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    };

    private POHeaderResponseDTO toDto(POHeader poHeader) {
        return POHeaderResponseDTO.builder()
                .poId(poHeader.getPoId())
                .status(poHeader.getStatus())
                .totalAmount(poHeader.getTotalAmount())
                .actionDate(poHeader.getActionDate())
                .remarks(poHeader.getRemarks())
                .build();
    }


    /** 저장한 장바구니 삭제 */
    @Override
    public void deletePO(Long poId) {
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("POHeader not found: " + poId));

        poHeaderRepository.delete(poHeader); // 연관된 POItem은 CascadeType.ALL 설정되어 있으면 자동 삭제됨
    }
}



