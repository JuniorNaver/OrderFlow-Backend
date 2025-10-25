//package com.youthcase.orderflow.gr;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.youthcase.orderflow.gr.controller.GoodsReceiptController;
//import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
//import com.youthcase.orderflow.gr.dto.POForGRDTO;
//import com.youthcase.orderflow.gr.service.GoodsReceiptService;
//import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
//import org.junit.jupiter.api.BeforeEach;
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
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class GoodsReceiptControllerTest {
//
//    @Mock
//    private GoodsReceiptService service;
//
//    @InjectMocks
//    private GoodsReceiptController controller;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    @DisplayName("✅ 전체 조회 - GET /api/gr")
//    void testGetAll() throws Exception {
//        List<GoodsReceiptHeaderDTO> mockList = List.of(
//                GoodsReceiptHeaderDTO.builder().id(1L).status(GoodsReceiptStatus.RECEIVED).build(),
//                GoodsReceiptHeaderDTO.builder().id(2L).status(GoodsReceiptStatus.CONFIRMED).build()
//        );
//
//        when(service.findAll()).thenReturn(mockList);
//
//        mockMvc.perform(get("/api/gr"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
//
//        verify(service, times(1)).findAll();
//    }
//
//    @Test
//    @DisplayName("✅ 단건 조회 - GET /api/gr/{id}")
//    void testGetById() throws Exception {
//        GoodsReceiptHeaderDTO dto = GoodsReceiptHeaderDTO.builder()
//                .id(10L)
//                .warehouseId("W01")
//                .status(GoodsReceiptStatus.RECEIVED)
//                .receiptDate(LocalDate.now())
//                .build();
//
//        when(service.findById(10L)).thenReturn(dto);
//
//        mockMvc.perform(get("/api/gr/10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.warehouseId").value("W01"))
//                .andExpect(jsonPath("$.status").value("RECEIVED"));
//
//        verify(service, times(1)).findById(10L);
//    }
//
//    @Test
//    @DisplayName("✅ 등록 - POST /api/gr")
//    void testCreate() throws Exception {
//        GoodsReceiptHeaderDTO request = GoodsReceiptHeaderDTO.builder()
//                .poId(1L)
//                .userId("100")
//                .warehouseId("W01")
//                .build();
//
//        GoodsReceiptHeaderDTO response = GoodsReceiptHeaderDTO.builder()
//                .id(500L)
//                .poId(1L)
//                .warehouseId("W01")
//                .status(GoodsReceiptStatus.RECEIVED)
//                .build();
//
//        when(service.create(any(GoodsReceiptHeaderDTO.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/gr")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(500L))
//                .andExpect(jsonPath("$.status").value("RECEIVED"));
//
//        verify(service, times(1)).create(any(GoodsReceiptHeaderDTO.class));
//    }
//
//    @Test
//    @DisplayName("✅ 입고 확정 - POST /api/gr/{id}/confirm")
//    void testConfirm() throws Exception {
//        doNothing().when(service).confirmReceipt(77L);
//
//        mockMvc.perform(post("/api/gr/77/confirm"))
//                .andExpect(status().isOk());
//
//        verify(service, times(1)).confirmReceipt(77L);
//    }
//
//    @Test
//    @DisplayName("✅ 입고 확정 취소 - POST /api/gr/{id}/cancel")
//    void testCancel() throws Exception {
//        doNothing().when(service).cancelConfirmedReceipt(88L, "테스트취소");
//
//        mockMvc.perform(post("/api/gr/88/cancel")
//                        .param("reason", "테스트취소"))
//                .andExpect(status().isOk());
//
//        verify(service, times(1)).cancelConfirmedReceipt(88L, "테스트취소");
//    }
//
//    @Test
//    @DisplayName("✅ 바코드로 발주 조회 - GET /api/gr/po-search?barcode=")
//    void testSearchPOForGR() throws Exception {
//        POForGRDTO mockDto = POForGRDTO.builder()
//                .poId(100L)
//                .name("테스터")
//                .status("IN_PROGRESS")
//                .totalAmount(120000L)
//                .build();
//
//        when(service.searchPOForGR("ABC123")).thenReturn(mockDto);
//
//        mockMvc.perform(get("/api/gr/po-search")
//                        .param("barcode", "ABC123"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.poId").value(100L))
//                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
//
//        verify(service, times(1)).searchPOForGR("ABC123");
//    }
//
//    @Test
//    @DisplayName("✅ 발주 기반 입고+확정 - POST /api/gr/scan-confirm")
//    void testCreateAndConfirm() throws Exception {
//        GoodsReceiptHeaderDTO response = GoodsReceiptHeaderDTO.builder()
//                .id(999L)
//                .status(GoodsReceiptStatus.CONFIRMED)
//                .warehouseId("W02")
//                .build();
//
//        when(service.createAndConfirmFromPO(300L)).thenReturn(response);
//
//        mockMvc.perform(post("/api/gr/scan-confirm")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(Map.of("poId", 300L))))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(999L))
//                .andExpect(jsonPath("$.status").value("CONFIRMED"));
//
//        verify(service, times(1)).createAndConfirmFromPO(300L);
//    }
//}
