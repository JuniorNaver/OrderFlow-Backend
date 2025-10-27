package com.youthcase.orderflow.mockTest.pr;

import com.youthcase.orderflow.pr.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class InventorySeeder {

    private final InventoryService inventoryService;

    @Transactional
    public void run(String... args) {
        // GTIN은 ProductSeeder에 있는 것과 맞춰서
        inventoryService.receive("8801115115809", 30L);
        inventoryService.receive("8809929360583", 25L);
        inventoryService.receive("8801128244077", 50L);
        inventoryService.receive("3155250001592", 40L);
        inventoryService.receive("8801115641001", 15L);
        inventoryService.receive("8809456641346", 30L);
        inventoryService.receive("8801114161791", 25L);
        inventoryService.receive("8802259023357", 50L);
        inventoryService.receive("8802259024514", 40L);
        inventoryService.receive("8802259024781", 15L);
        inventoryService.receive("8801005000772", 30L);
        inventoryService.receive("8809495078011", 25L);
        inventoryService.receive("8809585581445", 50L);
        inventoryService.receive("8803694140623", 40L);
        inventoryService.receive("8809544616232", 15L);
        inventoryService.receive("8809926821889", 30L);
        inventoryService.receive("8801114176924", 25L);
        inventoryService.receive("8809990198504", 50L);
        inventoryService.receive("8809776951811", 40L);
        inventoryService.receive("8801117664008", 15L);
        inventoryService.receive("8809456642756", 30L);
        inventoryService.receive("8801114178768", 25L);
        inventoryService.receive("8809778497508", 50L);
        inventoryService.receive("8809778498253", 40L);
        inventoryService.receive("8801073216624", 15L);
        inventoryService.receive("8801019613159", 30L);
        inventoryService.receive("8801117584016", 25L);
        inventoryService.receive("8801725002643", 50L);
        inventoryService.receive("8809486993118", 40L);
        inventoryService.receive("8809971931106", 15L);
        inventoryService.receive("8809482998971", 30L);
        inventoryService.receive("8801019209253", 25L);
        inventoryService.receive("8801094000264", 50L);
        inventoryService.receive("8801056245948", 40L);
        inventoryService.receive("8801056249113", 15L);
        inventoryService.receive("8801043030465", 30L);
        inventoryService.receive("8801114174555", 25L);
        inventoryService.receive("8801115339243", 50L);
        inventoryService.receive("8801114177907", 40L);
        inventoryService.receive("8801155745165", 15L);
        inventoryService.receive("8800282210010", 30L);
        inventoryService.receive("8809309398823", 25L);
        inventoryService.receive("3068320127545", 50L);
        inventoryService.receive("8801094000288", 40L);
        inventoryService.receive("8806004801467", 15L);
        inventoryService.receive("8806004000266", 30L);
        inventoryService.receive("8801115118961", 25L);
        inventoryService.receive("8809330477054", 50L);
        inventoryService.receive("8800261382448", 40L);
        inventoryService.receive("8809964250023", 15L);
        inventoryService.receive("8801753112932", 40L);
        inventoryService.receive("8800277750200", 15L);
        inventoryService.receive("8802259022435", 30L);
        inventoryService.receive("8809275200472", 25L);
        inventoryService.receive("6970689221048", 50L);
        inventoryService.receive("8800320510003", 40L);
        inventoryService.receive("8801028001701", 15L);
        inventoryService.receive("080432402191", 30L);
        inventoryService.receive("4901004057044", 25L);
        inventoryService.receive("8809972310030", 50L);
        inventoryService.receive("8809097170045", 40L);
        inventoryService.receive("8801030955054", 15L);
        inventoryService.receive("8809516450086", 30L);
        inventoryService.receive("080480006662", 25L);
        inventoryService.receive("8801028000216", 50L);
        inventoryService.receive("8805396903551", 40L);
        inventoryService.receive("8800321910086", 40L);
        inventoryService.receive("8801046426593", 40L);
        inventoryService.receive("8801441021089", 40L);
        inventoryService.receive("8809949565159", 40L);
        inventoryService.receive("8809345050372", 40L);
        inventoryService.receive("8801173603294", 40L);
        inventoryService.receive("8806006548711", 40L);
        inventoryService.receive("4902430223829", 40L);
        inventoryService.receive("8809184002938", 40L);
        inventoryService.receive("8801260710935", 40L);
        inventoryService.receive("8809756292019", 40L);
        inventoryService.receive("4984824343061", 40L);
        inventoryService.receive("8807424241574", 40L);
        inventoryService.receive("8809148128759", 40L);
        inventoryService.receive("8809266600038", 40L);
        inventoryService.receive("8806016320024", 40L);
        inventoryService.receive("8806335610769", 40L);
        inventoryService.receive("8801166099615", 40L);
        inventoryService.receive("8801166092173", 40L);
        inventoryService.receive("8809750663570", 40L);
        inventoryService.receive("8802260000996", 40L);
        inventoryService.receive("8801166260046", 40L);
        inventoryService.receive("8809949557826", 40L);
        inventoryService.receive("8806105404949", 40L);
        inventoryService.receive("8806121702319", 40L);
        inventoryService.receive("8802260010261", 40L);
        inventoryService.receive("8809193960779", 40L);
        inventoryService.receive("8801489115863", 40L);
        inventoryService.receive("4719003016814", 40L);
        inventoryService.receive("662834500585", 40L);
        inventoryService.receive("8809502408916", 40L);
        inventoryService.receive("8801489416212", 40L);
        inventoryService.receive("8801101890437", 40L);
        inventoryService.receive("8801056232153", 40L);
        inventoryService.receive("827854004806", 40L);
        inventoryService.receive("8806105404949", 40L);
        inventoryService.receive("8809552278897", 40L);
        inventoryService.receive("8809878063818", 40L);
        inventoryService.receive("8809425312048", 40L);
        inventoryService.receive("8801392092183", 40L);
        inventoryService.receive("8801104948180", 40L);
        inventoryService.receive("8809226309230", 40L);
        inventoryService.receive("8801558702024", 40L);
        inventoryService.receive("8801814003155", 40L);
        inventoryService.receive("8801166034814", 40L);
        inventoryService.receive("8801019309946", 40L);



        log.info("InventorySeeder done.");
    }
}
