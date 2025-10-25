package com.youthcase.orderflow.po.controller;

import com.youthcase.orderflow.po.dto.*;
import com.youthcase.orderflow.po.service.POService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 🧾 POController
 * ----------------------------------------------------------------------
 * • 목적: PR(장바구니/발주 준비) → S(저장) → PO(발주 확정) → GI/입출고 진행
 *   까지의 주요 API 진입점. 프런트에서 장바구니/발주 화면이 호출.
 *
 * • 핵심 동작 요약
 *   1) /items(POST): 공용 장바구니(현재 진행 중 PR) 최신 1건에 아이템을 추가하거나
 *      PR이 없으면 새 헤더를 생성한 뒤 아이템 추가. 동일 GTIN은 수량 합산.
 *   2) /current(GET): 장바구니(현재 진행 중 PR) 헤더의 ID만 반환(없으면 204).
 *   3) /items(GET): 특정 헤더(poId)의 아이템 목록 조회.
 *   4) /update/{itemNo}(PUT): 장바구니(PR) 상태인 아이템 수량 변경.
 *   5) /delete(DELETE): 장바구니에서 선택 아이템 삭제(복수 가능).
 *   6) /save/{poId}(POST): PR → S(저장)로 전환(비고 저장).
 *   7) /saved(GET): 저장된 장바구니(S) 헤더 목록 조회.
 *   8) /savedCart/{poId}(GET): 저장된 장바구니의 아이템 목록 조회.
 *   9) /delete/{poId}(DELETE): 저장된 장바구니 헤더/아이템 삭제.
 *  10) /confirm/{poId}(POST): 발주 확정(PO) 전환.
 *  11) /progress/{poId}(PUT): 입고 진행률 기반으로 헤더 상태 갱신(GI/FULLY_RECEIVED).
 *
 * • 공용 장바구니 정책:
 *   - PR 상태 헤더가 여러 개 존재하면 최신 1건만 유지하고 나머지는 S로 자동 전환.
 *   - 동일 헤더+GTIN 추가 시 라인 추가가 아니라 수량 합산(아이템 스냅샷 단가 갱신).
 *
 * • 예외/검증:
 *   - 모든 메서드의 비즈니스 검증/예외 처리는 Service에서 수행.
 *   - 컨트롤러는 thin controller 원칙을 유지(파라미터 전달/응답 포맷).
 */
@RestController
@RequestMapping("/api/po")
@RequiredArgsConstructor
public class POController {

    private final POService poService;

    // ======================================================================
    // ✅ [1] 장바구니(POHeader + Item) 신규 생성 또는 기존 PR에 아이템 추가
    // ----------------------------------------------------------------------
    // • URL: POST /api/po/items
    // • Request Body (JSON): POAddRequestDTO
    //   {
    //     "userId": "admin01",
    //     "item": {
    //       "gtin": "8801234567890",
    //       "orderQty": 3
    //     }
    //   }
    //   - userId: 로그인 사용자 ID
    //   - item.gtin: 상품 GTIN (필수)
    //   - item.orderQty: 추가 수량(>=1)
    //   - item.unitPrice: 보내지 않아도 됨(서버가 PriceMaster로부터 스냅샷)
    //
    // • Behavior:
    //   - 서버가 "현재 PR 헤더 최신 1건"을 조회:
    //       없으면 새 PR 헤더 생성(externalId = yyyyMMdd + storeId + seq)
    //       있으면 해당 헤더 사용
    //   - 동일 헤더에 동일 GTIN이 이미 존재하면: 새 row 추가가 아니라 수량 합산
    //   - purchasePrice는 PriceMaster에서 스냅샷하여 POItem에 기록/합계 갱신
    //
    // • Response (200, JSON): POItemResponseDTO
    //   {
    //     "itemNo": 101,
    //     "gtin": "8801234567890",
    //     "productName": "...",
    //     "purchasePrice": 1000.00,
    //     "orderQty": 5,            // 합산 후 수량
    //     "pendingQty": 5,
    //     "shippedQty": 0,
    //     "total": 5000.00,
    //     "expectedArrival": "2025-10-29",
    //     "status": "PR",
    //     "poId": 12
    //   }
    //
    // • HTTP Status:
    //   - 200 OK: 정상 추가/합산
    //   - 400 Bad Request: 검증 실패(존재하지 않는 GTIN/수량<1 등)
    //   - 404 Not Found: user/price/product/po not found
    //
    // • Example (cURL):
    //   curl -X POST http://{host}/api/po/items \
    //        -H "Content-Type: application/json" \
    //        -d '{"userId":"admin01","item":{"gtin":"8801234567890","orderQty":3}}'
    // ======================================================================
    @PostMapping("/items")
    public ResponseEntity<POItemResponseDTO> addOrCreateItem(
            @RequestBody POAddRequestDTO request
    ) {
        return ResponseEntity.ok(
                poService.addOrCreatePOItem(request.getUserId(), request.getItem())
        );
    }

    // ======================================================================
    // ✅ [2] 현재 PR 상태(=진행 중 장바구니)의 Header ID 조회
    // ----------------------------------------------------------------------
    // • URL: GET /api/po/current
    //
    // • Behavior:
    //   - PR 상태 헤더를 actionDate DESC로 정렬하여 최신 1건의 poId 반환
    //   - 다중 PR 존재 시: 최신 1건을 제외한 나머지는 자동으로 S(저장)로 전환
    //
    // • Response:
    //   - 200 OK + Long (poId) : 현재 진행 중 장바구니 ID
    //   - 204 No Content       : PR 상태가 하나도 없을 때
    //
    // • Example:
    //   curl -X GET http://{host}/api/po/current
    // ======================================================================
    @GetMapping("/current")
    public ResponseEntity<Long> getCurrentCartId() {
        Long currentPoId = poService.getCurrentCartId();
        if (currentPoId == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentPoId);
    }

    // ======================================================================
    // ✅ [3] 특정 장바구니 아이템 목록 조회
    // ----------------------------------------------------------------------
    // • URL: GET /api/po/items?poId={id}
    //
    // • Query:
    //   - poId: 대상 헤더 ID(필수). PR/S/PO 무엇이든 조회 가능(화면 용도에 따라).
    //
    // • Response (200, JSON): List<POItemResponseDTO>
    //   [
    //     {...}, {...}
    //   ]
    //
    // • Example:
    //   curl -G http://{host}/api/po/items --data-urlencode "poId=12"
    // ======================================================================
    @GetMapping("/items")
    public ResponseEntity<List<POItemResponseDTO>> getAllItems(@RequestParam Long poId) {
        return ResponseEntity.ok(poService.getAllItems(poId));
    }

    // ======================================================================
    // ✅ [4] 수량 변경
    // ----------------------------------------------------------------------
    // • URL: PUT /api/po/update/{itemNo}
    //
    // • Path:
    //   - itemNo: 변경할 아이템 번호(장바구니 PR 상태인 아이템만 허용)
    //
    // • Request Body (JSON): POItemRequestDTO
    //   { "orderQty": 7 }
    //   - orderQty >= 1
    //   - gtin/unitPrice는 보낼 필요 없음
    //
    // • Behavior:
    //   - PR 상태에서만 수량 변경 가능
    //   - 라인 total 재계산, 헤더 totalAmount는 프론트에서 필요 시 /current → /items 재조회로 반영
    //
    // • Response (200, JSON): POItemResponseDTO (변경된 라인)
    //
    // • Example:
    //   curl -X PUT http://{host}/api/po/update/101 \
    //        -H "Content-Type: application/json" \
    //        -d '{"orderQty":7}'
    // ======================================================================
    @PutMapping("/update/{itemNo}")
    public ResponseEntity<POItemResponseDTO> updateItemQuantity(
            @PathVariable Long itemNo,
            @RequestBody POItemRequestDTO dto
    ) {
        return ResponseEntity.ok(poService.updateItemQuantity(itemNo, dto));
    }

    // ======================================================================
    // ✅ [5] 선택 상품 삭제 (복수 가능)
    // ----------------------------------------------------------------------
    // • URL: DELETE /api/po/delete?itemIds=101,102
    //
    // • Query:
    //   - itemIds: 삭제할 아이템 번호 목록(콤마 구분)
    //
    // • Behavior:
    //   - 해당 아이템 라인 삭제. (PR/S/PO에 따라 실제 화면에서 버튼 활성화 제어 권장)
    //
    // • Response:
    //   - 204 No Content
    //
    // • Example:
    //   curl -X DELETE "http://{host}/api/po/delete?itemIds=101,102"
    // ======================================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteItem(@RequestParam List<Long> itemIds) {
        poService.deleteItem(itemIds);
        return ResponseEntity.noContent().build();
    }

    // ======================================================================
    // ✅ [6] 현재 장바구니(PR) 저장 → S 상태 전환
    // ----------------------------------------------------------------------
    // • URL: POST /api/po/save/{poId}
    //
    // • Path:
    //   - poId: 저장할 대상 헤더 ID (현 PR)
    //
    // • Request Body (JSON): POHeaderRequestDTO
    //   { "remarks": "장바구니명 또는 메모" }
    //
    // • Behavior:
    //   - 헤더.status = S (저장), remarks 저장
    //
    // • Response:
    //   - 200 OK
    //
    // • Example:
    //   curl -X POST http://{host}/api/po/save/12 \
    //        -H "Content-Type: application/json" \
    //        -d '{"remarks":"10월 행사 장바구니"}'
    // ======================================================================
    @PostMapping("/save/{poId}")
    public ResponseEntity<Void> saveCart(
            @PathVariable Long poId,
            @RequestBody POHeaderRequestDTO request
    ) {
        poService.saveCart(poId, request.getRemarks());
        return ResponseEntity.ok().build();
    }

    // ======================================================================
    // ✅ [7] 저장된 장바구니 목록 조회(S 상태 헤더)
    // ----------------------------------------------------------------------
    // • URL: GET /api/po/saved
    //
    // • Response (200, JSON): List<POHeaderResponseDTO>
    //   [
    //     {
    //       "poId": 13,
    //       "status": "S",
    //       "totalAmount": 12345.67,
    //       "actionDate": "2025-10-26",
    //       "remarks": "10월 행사 장바구니",
    //       "externalId": "20251026BR01..."
    //     }, ...
    //   ]
    //
    // • Example:
    //   curl -X GET http://{host}/api/po/saved
    // ======================================================================
    @GetMapping("/saved")
    public ResponseEntity<List<POHeaderResponseDTO>> getSavedCartList() {
        return ResponseEntity.ok(poService.getSavedCartList());
    }

    // ======================================================================
    // ✅ [8] 저장된 장바구니 아이템 목록 조회
    // ----------------------------------------------------------------------
    // • URL: GET /api/po/savedCart/{poId}
    //
    // • Path:
    //   - poId: 저장된 장바구니 헤더 ID(S 상태)
    //
    // • Response (200, JSON): List<POItemResponseDTO>
    //
    // • Example:
    //   curl -X GET http://{host}/api/po/savedCart/13
    // ======================================================================
    @GetMapping("/savedCart/{poId}")
    public ResponseEntity<List<POItemResponseDTO>> getSavedCart(@PathVariable Long poId) {
        return ResponseEntity.ok(poService.getSavedCartItems(poId));
    }

    // ======================================================================
    // ✅ [9] 저장된 장바구니 삭제
    // ----------------------------------------------------------------------
    // • URL: DELETE /api/po/delete/{poId}
    //
    // • Behavior:
    //   - 헤더 및 하위 아이템 전체 삭제(orphanRemoval=true)
    //
    // • Response:
    //   - 204 No Content
    //
    // • Example:
    //   curl -X DELETE http://{host}/api/po/delete/13
    // ======================================================================
    @DeleteMapping("/delete/{poId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long poId) {
        poService.deletePO(poId);
        return ResponseEntity.noContent().build();
    }

    // ======================================================================
    // ✅ [10] 발주 확정 (PR/S → PO)
    // ----------------------------------------------------------------------
    // • URL: POST /api/po/confirm/{poId}
    //
    // • Behavior:
    //   - 헤더 상태를 PO로 전환(이후 수량 변경/아이템 삭제는 불가하도록 프론트 제어 권장)
    //   - 이후 GR(입고) 단계에서 바코드/LOT/재고 반영 처리
    //
    // • Response:
    //   - 200 OK
    //
    // • Example:
    //   curl -X POST http://{host}/api/po/confirm/12
    // ======================================================================
    @PostMapping("/confirm/{poId}")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long poId) {
        poService.confirmOrder(poId);
        return ResponseEntity.ok().build();
    }

    // ======================================================================
    // ✅ [11] 입고 진행률 업데이트 (GI → FULLY_RECEIVED)
    // ----------------------------------------------------------------------
    // • URL: PUT /api/po/progress/{poId}
    //
    // • Behavior:
    //   - 하위 아이템 pendingQty를 검사하여
    //       모두 0 → 헤더 상태 FULLY_RECEIVED
    //       일부 남음 → 헤더 상태 GI
    //   - GR 확정/취소 시점에 호출하여 PO 진행상태와 싱크 맞춤
    //
    // • Response:
    //   - 200 OK
    //
    // • Example:
    //   curl -X PUT http://{host}/api/po/progress/12
    // ======================================================================
    @PutMapping("/progress/{poId}")
    public ResponseEntity<Void> updateReceiveProgress(@PathVariable Long poId) {
        poService.updateReceiveProgress(poId);
        return ResponseEntity.ok().build();
    }
}
