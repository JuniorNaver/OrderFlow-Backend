package com.youthcase.orderflow.pr.mock;

import com.youthcase.orderflow.pr.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile({"dev", "local"})
@Order(3)
@RequiredArgsConstructor
public class InventorySeeder implements CommandLineRunner {

    private final InventoryService inventoryService;

    @Override
    @Transactional
    public void run(String... args) {
        // GTIN은 ProductSeeder에 있는 것과 맞춰서
        inventoryService.receive("8801115115809", 30);
        inventoryService.receive("8809929360583", 25);
        inventoryService.receive("8801128244077", 50);
        inventoryService.receive("3155250001592", 40);
        inventoryService.receive("8801115641001", 15);
        inventoryService.receive("8809456641346", 30);
        inventoryService.receive("8801114161791", 25);
        inventoryService.receive("8802259023357", 50);
        inventoryService.receive("8802259024514", 40);
        inventoryService.receive("8802259024781", 15);
        inventoryService.receive("8801005000772", 30);
        inventoryService.receive("8809495078011", 25);
        inventoryService.receive("8809585581445", 50);
        inventoryService.receive("8803694140623", 40);
        inventoryService.receive("8809544616232", 15);
        inventoryService.receive("8809926821889", 30);
        inventoryService.receive("8801114176924", 25);
        inventoryService.receive("8809990198504", 50);
        inventoryService.receive("8809776951811", 40);
        inventoryService.receive("8801117664008", 15);
        inventoryService.receive("8809456642756", 30);
        inventoryService.receive("8801114178768", 25);
        inventoryService.receive("8809778497508", 50);
        inventoryService.receive("8809778498253", 40);
        inventoryService.receive("8801073216624", 15);
        inventoryService.receive("8801019613159", 30);
        inventoryService.receive("8801117584016", 25);
        inventoryService.receive("8801725002643", 50);
        inventoryService.receive("8809486993118", 40);
        inventoryService.receive("8809971931106", 15);
        inventoryService.receive("8809482998971", 30);
        inventoryService.receive("8801019209253", 25);
        inventoryService.receive("8801094000264", 50);
        inventoryService.receive("8801056245948", 40);
        inventoryService.receive("8801056249113", 15);
        inventoryService.receive("8801043030465", 30);
        inventoryService.receive("8801114174555", 25);
        inventoryService.receive("8801115339243", 50);
        inventoryService.receive("8801114177907", 40);
        inventoryService.receive("8801155745165", 15);
        inventoryService.receive("8800282210010", 30);
        inventoryService.receive("8809309398823", 25);
        inventoryService.receive("3068320127545", 50);
        inventoryService.receive("8801094000288", 40);
        inventoryService.receive("8806004801467", 15);
        inventoryService.receive("8806004000266", 30);
        inventoryService.receive("8801115118961", 25);
        inventoryService.receive("8809330477054", 50);
        inventoryService.receive("8800261382448", 40);
        inventoryService.receive("8809964250023", 15);
        inventoryService.receive("8801753112932", 40);
        inventoryService.receive("8800277750200", 15);
        inventoryService.receive("8802259022435", 30);
        inventoryService.receive("8809275200472", 25);
        inventoryService.receive("6970689221048", 50);
        inventoryService.receive("8800320510003", 40);
        inventoryService.receive("8801028001701", 15);
        inventoryService.receive("080432402191", 30);
        inventoryService.receive("4901004057044", 25);
        inventoryService.receive("8809972310030", 50);
        inventoryService.receive("8809097170045", 40);
        inventoryService.receive("8801030955054", 15);
        inventoryService.receive("8809516450086", 30);
        inventoryService.receive("080480006662", 25);
        inventoryService.receive("8801028000216", 50);
        inventoryService.receive("8805396903551", 40);
        inventoryService.receive("8800321910086", 40);
        inventoryService.receive("8801046426593", 40);
        inventoryService.receive("8801441021089", 40);
        inventoryService.receive("8809949565159", 40);

        log.info("InventorySeeder done.");
    }
}
