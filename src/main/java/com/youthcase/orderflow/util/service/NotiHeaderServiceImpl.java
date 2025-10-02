package com.youthcase.orderflow.util.service.impl;

import com.youthcase.orderflow.util.domain.NotiHeader;
import com.youthcase.orderflow.util.repository.NotiHeaderRepository;
import com.youthcase.orderflow.util.service.NotiHeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service // 스프링 서비스 빈으로 등록
@RequiredArgsConstructor // Repository 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class NotiHeaderServiceImpl implements NotiHeaderService {

    private final NotiHeaderRepository notiHeaderRepository;

    @Override
    @Transactional // 생성은 쓰기 작업이므로 별도로 트랜잭션 설정
    public NotiHeader createHeader(String type, Long stkId, String nav) {
        // Builder를 사용하여 NotiHeader 객체 생성
        NotiHeader newHeader = NotiHeader.builder()
                .type(type)
                .stkId(stkId)
                .nav(nav)
                .build();

        return notiHeaderRepository.save(newHeader);
    }

    @Override
    public Optional<NotiHeader> findById(Long notiId) {
        return notiHeaderRepository.findById(notiId);
    }
}