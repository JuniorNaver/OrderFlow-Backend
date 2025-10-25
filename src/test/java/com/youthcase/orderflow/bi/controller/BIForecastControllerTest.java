///**
// * BIForecastController 단위 테스트
// * --------------------------------
// * 🌍 /bi/forecast API 요청 및 응답 구조 검증
// */
//
//package com.youthcase.orderflow.bi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.youthcase.orderflow.bi.dto.ForecastDTO;
//import com.youthcase.orderflow.bi.service.forecast.BIForecastService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class BIForecastControllerTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    /** 실제 호출 대상 Controller */
//    @InjectMocks
//    private BIForecastController forecastController;
//
//    /** Controller 내부 의존 서비스 */
//    @Mock
//    private BIForecastService forecastService;
//
//    /** MockMvc는 수동으로 구성 (Spring Context 로딩 없이 동작) */
//    private MockMvc mockMvc;
//
//    @Test
//    @DisplayName("GET /bi/forecast 요청 시 예측 결과 200 OK 응답")
//    void getForecasts_returnsOk() throws Exception {
//        // given
//        ForecastDTO mockResult = ForecastDTO.builder()
//                .productId(1001L)
//                .periodStartKey("20251001")
//                .periodEndKey("20251007")
//                .forecastQty(120.5)
//                .confidenceRate(91.2)
//                .build();
//
//        given(forecastService.getForecasts(anyString(), anyString(), anyString()))
//                .willReturn(List.of(mockResult));
//
//        // MockMvc 설정
//        mockMvc = MockMvcBuilders.standaloneSetup(forecastController).build();
//
//        // when & then
//        mockMvc.perform(get("/bi/forecast")
//                        .param("storeId", "1")
//                        .param("from", "20251001")
//                        .param("to", "20251031")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].productId").value(1001))
//                .andExpect(jsonPath("$[0].forecastQty").value(120.5))
//                .andExpect(jsonPath("$[0].confidenceRate").value(91.2));
//    }
//}
