package com.youthcase.orderflow.mockTest;

import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.domain.StorageMethod;
import com.youthcase.orderflow.master.product.domain.Unit;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile({"dev", "local"})      // 운영 배포 제외
@Order(2)                       // CategorySeeder 이후 실행
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private record Seed(
            String gtin, String name,
            Unit unit, String price,
            StorageMethod sm, String kanCode,
            String imageUrl, String desc,
            ExpiryType expiry, Integer shelfLife,
            Integer w, Integer d, Integer h
    ) {
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Seed> seeds = List.of(
                new Seed("8801120000000", "서울우유 저지방 우유 200ml", Unit.ML, "2500", StorageMethod.COLD, "01020101",
                        "https://firebasestorage.googleapis.com/v0/b/steady-copilot-206205.appspot.com/o/goods%2Fd4e91179-7014-4295-be67-a8f4d0d6ff36%2Fd4e91179-7014-4295-be67-a8f4d0d6ff36_front_angle_1000.jpg?alt=media&token=8148e663-1066-43f5-8452-dcf4d19b4434",
                        "서울우유의 저지방 우유 200ml 팩 제품", ExpiryType.USE_BY, 7, 55, 35, 112),

                new Seed("8801111000001", "매일우유 오리지널 900ml", Unit.ML, "2700", StorageMethod.COLD, "01020101",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmaeil_milk_900ml.png?alt=media",
                        "매일유업의 대표 우유 제품 900ml", ExpiryType.USE_BY, 7, 70, 70, 230),

                new Seed("8801111000002", "서울우유 멸균우유 1L", Unit.ML, "2900", StorageMethod.ROOM_TEMP, "01020101",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fseoul_sterilized_1l.png?alt=media",
                        "실온 보관 가능한 멸균우유 1L 팩", ExpiryType.BEST_BEFORE, 180, 70, 70, 240),

                new Seed("8801073216624", "삼양 불닭볶음면 큰컵 105g", Unit.EA, "1500", StorageMethod.ROOM_TEMP, "01120401",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsamyang_firecup_105g.png?alt=media",
                        "매운맛 라면 컵형 불닭볶음면", ExpiryType.BEST_BEFORE, 180, 100, 100, 100),

                new Seed("8801043039001", "농심 신라면 큰사발 114g", Unit.EA, "1600", StorageMethod.ROOM_TEMP, "01120401",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fshinramen_cup_114g.png?alt=media",
                        "농심 신라면 컵라면 114g", ExpiryType.BEST_BEFORE, 180, 105, 105, 110),

                new Seed("8801043042001", "농심 너구리 컵 110g", Unit.EA, "1600", StorageMethod.ROOM_TEMP, "01120401",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fneoguri_cup_110g.png?alt=media",
                        "해물맛 라면 너구리 컵 110g", ExpiryType.BEST_BEFORE, 180, 105, 105, 110),

                new Seed("8801007300476", "빙그레 딸기맛우유 240ml", Unit.ML, "2100", StorageMethod.COLD, "01020203",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_strawberry_240ml.png?alt=media",
                        "빙그레 딸기맛우유 240ml", ExpiryType.USE_BY, 7, 60, 60, 115),

                new Seed("8801007300483", "빙그레 바나나맛우유 240ml", Unit.ML, "2100", StorageMethod.COLD, "01020203",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_banana_240ml.png?alt=media",
                        "국민음료 빙그레 바나나맛우유 240ml", ExpiryType.USE_BY, 7, 60, 60, 115),

                new Seed("8801056085101", "남양 요구르트 85ml", Unit.ML, "1900", StorageMethod.COLD, "01020202",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fnamyang_yogurt_85ml.png?alt=media",
                        "남양 오리지널 요구르트 85ml", ExpiryType.USE_BY, 7, 40, 40, 90),

                new Seed("8809460000000", "굿프렌즈 고추튀김 1kg", Unit.KG, "5400", StorageMethod.FROZEN, "01080107",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fchili_fry_1kg.png?alt=media",
                        "바삭한 고추튀김 냉동식품 1kg", ExpiryType.BEST_BEFORE, 365, 265, 80, 380),

                new Seed("8801110000000", "풀무원 홀아이스 3kg", Unit.KG, "4700", StorageMethod.FROZEN, "01080114",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fwhole_ice_3kg.png?alt=media",
                        "음료 냉각 및 보관용 홀아이스 3kg", ExpiryType.BEST_BEFORE, 365, 3000, 4700, 100),

                new Seed("8802260000000", "롯데 설레임 밀크쉐이크 저당 160ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080301",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsulleim_shake_160ml.png?alt=media",
                        "설레임 밀크쉐이크 저당 버전 160ml", ExpiryType.BEST_BEFORE, 365, 80, 33, 160),

                new Seed("8802260000001", "롯데 미니 스크류바 300ml (25ml x 12개)", Unit.ML, "3200", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmini_screwbar_300ml.png?alt=media",
                        "롯데 미니 스크류바 멀티팩 12개입", ExpiryType.BEST_BEFORE, 365, 212, 47, 167),

                new Seed("8802260000002", "롯데 조이 그릭요거트 땅콩버터 애플 220ml", Unit.ML, "2900", StorageMethod.FROZEN, "01080305",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fjoy_greek_220ml.png?alt=media",
                        "조이 그릭요거트 땅콩버터 애플맛 아이스디저트", ExpiryType.BEST_BEFORE, 365, 60, 100, 92),

                new Seed("8801115111111", "빙그레 메로나 70ml", Unit.ML, "1500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_merona_70ml.png?alt=media",
                        "빙그레 메로나 70ml 바형 아이스크림", ExpiryType.BEST_BEFORE, 365, 38, 25, 155),

                new Seed("8801115222222", "롯데 월드콘 160ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_worldcone_160ml.png?alt=media",
                        "롯데 월드콘 초코아몬드 160ml", ExpiryType.BEST_BEFORE, 365, 75, 75, 160),

                new Seed("8801115333333", "롯데 돼지바 90ml", Unit.ML, "1800", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_pigbar_90ml.png?alt=media",
                        "롯데 돼지바 오리지널 90ml", ExpiryType.BEST_BEFORE, 365, 42, 25, 155),

                new Seed("8801115444444", "롯데 찰떡아이스 120ml", Unit.ML, "1900", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_chaltteok_120ml.png?alt=media",
                        "롯데 찰떡아이스 바닐라 120ml", ExpiryType.BEST_BEFORE, 365, 60, 40, 155),

                new Seed("8801115555555", "빙그레 투게더 바닐라 900ml", Unit.ML, "4800", StorageMethod.FROZEN, "01080303",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_together_900ml.png?alt=media",
                        "빙그레 투게더 바닐라 900ml 대용량", ExpiryType.BEST_BEFORE, 365, 120, 120, 130),

                new Seed("8801115666666", "롯데 나뚜루 녹차 474ml", Unit.ML, "5500", StorageMethod.FROZEN, "01080303",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_natuur_green_474ml.png?alt=media",
                        "롯데 나뚜루 녹차 아이스크림 474ml", ExpiryType.BEST_BEFORE, 365, 110, 110, 110),

                new Seed("8801115777777", "빙그레 쿠앤크콘 160ml", Unit.ML, "2400", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_cookncream_160ml.png?alt=media",
                        "쿠키앤크림 콘타입 아이스크림 160ml", ExpiryType.BEST_BEFORE, 365, 75, 75, 160),

                new Seed("8801115888888", "롯데 죠스바 70ml", Unit.ML, "1500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_jawsbar_70ml.png?alt=media",
                        "죠스바 오리지널 70ml 바형 아이스크림", ExpiryType.BEST_BEFORE, 365, 35, 20, 150),

                new Seed("8801115999999", "빙그레 붕어싸만코 150ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_samanko_150ml.png?alt=media",
                        "붕어싸만코 바닐라 아이스크림 150ml", ExpiryType.BEST_BEFORE, 365, 75, 40, 160),

                new Seed("8801120000003", "롯데 스크류바 70ml", Unit.ML, "1500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_screwbar_70ml.png?alt=media",
                        "스크류바 딸기맛 아이스크림 70ml", ExpiryType.BEST_BEFORE, 365, 38, 25, 150),

                new Seed("8801120111111", "롯데 빵빠레 바닐라 160ml", Unit.ML, "2200", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_bbangpare_160ml.png?alt=media",
                        "롯데 빵빠레 바닐라 아이스크림 160ml", ExpiryType.BEST_BEFORE, 365, 75, 75, 160),

                new Seed("8801120222222", "빙그레 비비빅 90ml", Unit.ML, "1800", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_bibibig_90ml.png?alt=media",
                        "팥 아이스크림 비비빅 90ml", ExpiryType.BEST_BEFORE, 365, 42, 25, 155),

                new Seed("8801120333333", "롯데 설레임 딸기쉐이크 160ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080301",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsulleim_strawberry_160ml.png?alt=media",
                        "설레임 딸기쉐이크 160ml 아이스디저트", ExpiryType.BEST_BEFORE, 365, 80, 33, 160),

                new Seed("8801120444444", "빙그레 요맘때 150ml", Unit.ML, "2400", StorageMethod.FROZEN, "01080303",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_yomam_150ml.png?alt=media",
                        "빙그레 요맘때 요거트맛 아이스크림 150ml", ExpiryType.BEST_BEFORE, 365, 70, 70, 150),

                new Seed("8801120555555", "롯데 월드콘 쿠앤크 160ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_worldcone_cncr_160ml.png?alt=media",
                        "월드콘 쿠키앤크림 콘 160ml", ExpiryType.BEST_BEFORE, 365, 75, 75, 160),

                new Seed("8801120666666", "롯데 나뚜루 바닐라 474ml", Unit.ML, "5500", StorageMethod.FROZEN, "01080303",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_natuur_vanilla_474ml.png?alt=media",
                        "롯데 나뚜루 바닐라 아이스크림 474ml", ExpiryType.BEST_BEFORE, 365, 110, 110, 110),

                new Seed("8801120777777", "빙그레 투게더 초코 900ml", Unit.ML, "4800", StorageMethod.FROZEN, "01080303",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_together_choco_900ml.png?alt=media",
                        "빙그레 투게더 초코맛 900ml 대용량 아이스크림", ExpiryType.BEST_BEFORE, 365, 120, 120, 130),

                new Seed("8801120888888", "빙그레 슈퍼콘 쿠앤크 160ml", Unit.ML, "2500", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_supercone_cncr_160ml.png?alt=media",
                        "빙그레 슈퍼콘 쿠앤크 아이스크림 160ml", ExpiryType.BEST_BEFORE, 365, 75, 75, 160),

                new Seed("8801120999999", "롯데 와일드바디 바닐라 70ml", Unit.ML, "1800", StorageMethod.FROZEN, "01080302",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_wildbody_70ml.png?alt=media",
                        "롯데 와일드바디 바닐라 아이스크림 70ml", ExpiryType.BEST_BEFORE, 365, 35, 20, 150),

                new Seed("8801130000000", "빙그레 구슬아이스 딸기 150ml", Unit.ML, "2700", StorageMethod.FROZEN, "01080305",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_strawbead_150ml.png?alt=media",
                        "빙그레 구슬아이스 딸기맛 150ml", ExpiryType.BEST_BEFORE, 365, 70, 70, 150),

                new Seed("8801130111111", "롯데 빙하수 500ml", Unit.ML, "1000", StorageMethod.ROOM_TEMP, "01010101",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_icewater_500ml.png?alt=media",
                        "롯데 빙하수 생수 500ml", ExpiryType.BEST_BEFORE, 180, 65, 65, 210),

                new Seed("8801130222222", "하이트진로 아이시스 500ml", Unit.ML, "1000", StorageMethod.ROOM_TEMP, "01010101",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fhite_isis_500ml.png?alt=media",
                        "하이트진로 아이시스 8.0 생수 500ml", ExpiryType.BEST_BEFORE, 180, 65, 65, 210),

                new Seed("8801130333333", "제주 삼다수 2L", Unit.ML, "1500", StorageMethod.ROOM_TEMP, "01010101",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsamdasu_2l.png?alt=media",
                        "제주 삼다수 2L 생수", ExpiryType.BEST_BEFORE, 180, 100, 100, 320),

                new Seed("8801130444444", "롯데 펩시콜라 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_pepsi_355ml.png?alt=media",
                        "펩시콜라 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801130555555", "코카콜라 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fcocacola_355ml.png?alt=media",
                        "코카콜라 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801130666666", "칠성사이다 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fchilsung_355ml.png?alt=media",
                        "칠성사이다 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801130777777", "몬스터 에너지 355ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmonster_energy_355ml.png?alt=media",
                        "몬스터 에너지 드링크 355ml 캔", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801130888888", "레드불 250ml", Unit.ML, "2800", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fredbull_250ml.png?alt=media",
                        "레드불 에너지 드링크 250ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801130999999", "코카콜라 제로 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fcocacola_zero_355ml.png?alt=media",
                        "코카콜라 제로 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801140000000", "스프라이트 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsprite_355ml.png?alt=media",
                        "스프라이트 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801140111111", "핫식스 250ml", Unit.ML, "2000", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fhotsix_250ml.png?alt=media",
                        "롯데 핫식스 에너지드링크 250ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801140222222", "롯데 레몬에이드 340ml", Unit.ML, "1800", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_lemonade_340ml.png?alt=media",
                        "롯데 레몬에이드 340ml 캔음료", ExpiryType.BEST_BEFORE, 180, 60, 60, 120),

                new Seed("8801140333333", "칸타타 아메리카노 275ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fcantata_americano_275ml.png?alt=media",
                        "칸타타 아메리카노 블랙 275ml 캔커피", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801140444444", "조지아 오리지널 240ml", Unit.ML, "2200", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fgeorgia_original_240ml.png?alt=media",
                        "조지아 오리지널 캔커피 240ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 115),

                new Seed("8801140555555", "레쓰비 175ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fletsbe_175ml.png?alt=media",
                        "레쓰비 마일드 커피 175ml 캔", ExpiryType.BEST_BEFORE, 180, 50, 50, 100),

                new Seed("8801140666666", "맥스웰하우스 오리지널 240ml", Unit.ML, "2000", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmaxwell_original_240ml.png?alt=media",
                        "맥스웰하우스 오리지널 캔커피 240ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 115),

                new Seed("8801130777777", "몬스터 에너지 355ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmonster_energy_355ml.png?alt=media",
                        "몬스터 에너지 드링크 355ml 캔", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801130888888", "레드불 250ml", Unit.ML, "2800", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fredbull_250ml.png?alt=media",
                        "레드불 에너지 드링크 250ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801130999999", "코카콜라 제로 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fcocacola_zero_355ml.png?alt=media",
                        "코카콜라 제로 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801140000000", "스프라이트 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsprite_355ml.png?alt=media",
                        "스프라이트 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801140111111", "핫식스 250ml", Unit.ML, "2000", StorageMethod.ROOM_TEMP, "01010103",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fhotsix_250ml.png?alt=media",
                        "롯데 핫식스 에너지드링크 250ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801140222222", "롯데 레몬에이드 340ml", Unit.ML, "1800", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Flotte_lemonade_340ml.png?alt=media",
                        "롯데 레몬에이드 340ml 캔음료", ExpiryType.BEST_BEFORE, 180, 60, 60, 120),

                new Seed("8801140333333", "칸타타 아메리카노 275ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fcantata_americano_275ml.png?alt=media",
                        "칸타타 아메리카노 블랙 275ml 캔커피", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801140444444", "조지아 오리지널 240ml", Unit.ML, "2200", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fgeorgia_original_240ml.png?alt=media",
                        "조지아 오리지널 캔커피 240ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 115),

                new Seed("8801140555555", "레쓰비 175ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fletsbe_175ml.png?alt=media",
                        "레쓰비 마일드 커피 175ml 캔", ExpiryType.BEST_BEFORE, 180, 50, 50, 100),

                new Seed("8801140666666", "맥스웰하우스 오리지널 240ml", Unit.ML, "2000", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmaxwell_original_240ml.png?alt=media",
                        "맥스웰하우스 오리지널 캔커피 240ml", ExpiryType.BEST_BEFORE, 180, 55, 55, 115),

                new Seed("8801140777777", "조지아 크래프트 스위트 아메리카노 470ml", Unit.ML, "3200", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fgeorgia_craft_470ml.png?alt=media",
                        "조지아 크래프트 스위트 아메리카노 470ml PET", ExpiryType.BEST_BEFORE, 180, 65, 65, 200),

                new Seed("8801140888888", "TOP 스위트 아메리카노 275ml", Unit.ML, "2700", StorageMethod.ROOM_TEMP, "01010104",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Ftop_sweet_275ml.png?alt=media",
                        "맥심 TOP 스위트 아메리카노 275ml 캔커피", ExpiryType.BEST_BEFORE, 180, 55, 55, 120),

                new Seed("8801140999999", "롯데 밀키스 340ml", Unit.ML, "1800", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fmilkis_340ml.png?alt=media",
                        "롯데 밀키스 340ml 캔음료", ExpiryType.BEST_BEFORE, 180, 60, 60, 120),

                new Seed("8801150000000", "델몬트 오렌지주스 500ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010105",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fdelmonte_orange_500ml.png?alt=media",
                        "델몬트 오렌지주스 500ml", ExpiryType.BEST_BEFORE, 180, 65, 65, 200),

                new Seed("8801150111111", "델몬트 포도주스 500ml", Unit.ML, "2500", StorageMethod.ROOM_TEMP, "01010105",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fdelmonte_grape_500ml.png?alt=media",
                        "델몬트 포도주스 500ml", ExpiryType.BEST_BEFORE, 180, 65, 65, 200),

                new Seed("8801150222222", "스프라이트 제로 355ml", Unit.ML, "1700", StorageMethod.ROOM_TEMP, "01010102",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fsprite_zero_355ml.png?alt=media",
                        "스프라이트 제로 355ml 캔음료", ExpiryType.BEST_BEFORE, 180, 65, 65, 120),

                new Seed("8801150333333", "빙그레 바나나맛 드링크 240ml", Unit.ML, "2100", StorageMethod.COLD, "01020203",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbanana_drink_240ml.png?alt=media",
                        "빙그레 바나나맛 드링크 240ml 병음료", ExpiryType.USE_BY, 7, 60, 60, 115),

                new Seed("8801150444444", "빙그레 요구르트 오리지널 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020202",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_yogurt_150ml.png?alt=media",
                        "빙그레 요구르트 오리지널 150ml", ExpiryType.USE_BY, 7, 50, 50, 110),

                new Seed("8801150555555", "빙그레 요구르트 딸기 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020202",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_yogurt_strawberry_150ml.png?alt=media",
                        "빙그레 요구르트 딸기맛 150ml", ExpiryType.USE_BY, 7, 50, 50, 110),

                new Seed("8801150666666", "빙그레 요구르트 복숭아 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020202",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fbinggrae_yogurt_peach_150ml.png?alt=media",
                        "빙그레 요구르트 복숭아맛 150ml", ExpiryType.USE_BY, 7, 50, 50, 110),

                new Seed("8801150777777", "빙그레 요플레 오리지널 딸기 85g", Unit.EA, "1900", StorageMethod.COLD, "01020204",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fyoplait_strawberry_85g.png?alt=media",
                        "요플레 딸기맛 요구르트 85g 컵", ExpiryType.USE_BY, 7, 75, 75, 70),

                new Seed("8801150888888", "빙그레 요플레 플레인 85g", Unit.EA, "1900", StorageMethod.COLD, "01020204",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fyoplait_plain_85g.png?alt=media",
                        "요플레 플레인 85g 컵요구르트", ExpiryType.USE_BY, 7, 75, 75, 70),

                new Seed("8801150999999", "빙그레 요플레 복숭아 85g", Unit.EA, "1900", StorageMethod.COLD, "01020204",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fyoplait_peach_85g.png?alt=media",
                        "요플레 복숭아맛 요구르트 85g", ExpiryType.USE_BY, 7, 75, 75, 70),

                new Seed("8801160000000", "서울우유 요구르트 포도 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020205",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fseoul_yogurt_grape_150ml.png?alt=media",
                        "서울우유 포도맛 요구르트 150ml", ExpiryType.USE_BY, 7, 50, 50, 110),

                new Seed("8801160111111", "서울우유 요구르트 사과 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020205",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fseoul_yogurt_apple_150ml.png?alt=media",
                        "서울우유 사과맛 요구르트 150ml", ExpiryType.USE_BY, 7, 50, 50, 110),

                new Seed("8801160222222", "서울우유 요구르트 오리지널 150ml", Unit.ML, "1900", StorageMethod.COLD, "01020205",
                        "https://firebasestorage.googleapis.com/v0/b/storeflow.appspot.com/o/img%2Fseoul_yogurt_original_150ml.png?alt=media",
                        "서울우유 요구르트 오리지널 150ml", ExpiryType.USE_BY, 7, 50, 50, 110)

                );

        int inserted = 0, skipped = 0, missingCat = 0;
        List<String> skippedKANList = new ArrayList<>();

        for (Seed s : seeds) {
            if (productRepository.findByGtin(s.gtin()).isPresent()) {
                skipped++;
                continue;
            }

            var cat = categoryRepository.findByKanCode(s.kanCode()).orElse(null);
            if (cat == null) {
                log.warn("⚠️ Skip: category not found for KAN={}", s.kanCode());
                skippedKANList.add(s.kanCode());
                missingCat++;
                continue;
            }

            var p = Product.builder()
                    .gtin(s.gtin())
                    .productName(s.name())
                    .unit(s.unit())
                    .price(new BigDecimal(s.price()))
                    .storageMethod(s.sm())
                    .category(cat)
                    .imageUrl(s.imageUrl())
                    .description(s.desc())
                    .orderable(Boolean.TRUE)
                    .expiryType(s.expiry())
                    .shelfLifeDays(s.shelfLife())
                    .widthMm(s.w())
                    .depthMm(s.d())
                    .heightMm(s.h())
                    .build();

            productRepository.save(p);
            inserted++;
        }

        log.info("✅ ProductSeeder completed. inserted={}, skipped(exists)={}, missingCategory={}",
                inserted, skipped, missingCat);
        log.info("missingCategoryList={}", skippedKANList.toString());
    }
}
