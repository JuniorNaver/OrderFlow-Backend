package com.youthcase.orderflow.po;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.service.POHeaderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class POHeaderServiceImplTest {

    @Mock
    private POHeaderRepository poHeaderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private POHeaderServiceImpl poHeaderService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .userId("admin01")
                .name("테스트유저")
                .build();
    }

    // ✅ 1️⃣ createNewPO() 테스트
    @Test
    void createNewPO_정상작동() {
        // given
        when(userRepository.findById("admin01")).thenReturn(Optional.of(mockUser));

        // mock 동작: save() 호출 시, 저장된 엔티티에 id 세팅 흉내
        when(poHeaderRepository.save(any(POHeader.class))).thenAnswer(invocation -> {
            POHeader header = invocation.getArgument(0);
            header.setPoId(1L); // DB가 자동으로 생성해주는 것처럼
            return header;
        });

        // when
        Long resultId = poHeaderService.createNewPO();

        // then
        assertNotNull(resultId); // ✅ 이제 통과됨
        assertEquals(1L, resultId);
        verify(poHeaderRepository).save(any(POHeader.class));
    }

    // ✅ 2️⃣ saveCart() 테스트
    @Test
    void saveCart_정상작동() {
        // given
        POHeader poHeader = new POHeader();
        poHeader.setPoId(1L);
        poHeader.setStatus(POStatus.PR);

        when(poHeaderRepository.findById(1L)).thenReturn(Optional.of(poHeader));

        // when
        poHeaderService.saveCart(1L, "비고내용");

        // then
        assertEquals(POStatus.S, poHeader.getStatus());
        assertEquals("비고내용", poHeader.getRemarks());
        verify(poHeaderRepository).save(poHeader);
    }

    // ✅ 3️⃣ getSavedCartList() 테스트
    @Test
    void getSavedCartList_정상조회() {
        // given
        POHeader h1 = new POHeader();
        h1.setPoId(1L);
        h1.setStatus(POStatus.S);

        POHeader h2 = new POHeader();
        h2.setPoId(2L);
        h2.setStatus(POStatus.S);

        when(poHeaderRepository.findByStatus(POStatus.S)).thenReturn(List.of(h1, h2));

        // when
        List<POHeaderResponseDTO> result = poHeaderService.getSavedCartList();

        // then
        assertEquals(2, result.size());
        verify(poHeaderRepository).findByStatus(POStatus.S);
    }

    // ✅ 4️⃣ deletePO() 테스트
    @Test
    void deletePO_정상삭제() {
        // given
        POHeader poHeader = new POHeader();
        poHeader.setPoId(1L);

        when(poHeaderRepository.findById(1L)).thenReturn(Optional.of(poHeader));

        // when
        poHeaderService.deletePO(1L);

        // then
        verify(poHeaderRepository).delete(poHeader);
    }

    // ✅ 5️⃣ deletePO_없는경우_예외()
    @Test
    void deletePO_없는경우_예외() {
        // given
        when(poHeaderRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> poHeaderService.deletePO(999L));
    }
}

