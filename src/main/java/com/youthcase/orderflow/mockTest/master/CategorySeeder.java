package com.youthcase.orderflow.mockTest.master;

import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class CategorySeeder {
    private final CategoryRepository repo;

    @Transactional
    public void run(String... args) {
        int inserted = 0;
        for (var s : CategoryData.SEED) {
            if (repo.existsById(s.kanCode())) continue;
            var c = new Category();
            c.setKanCode(s.kanCode());
            c.setTotalCategory(s.totalCategory());
            c.setLargeCategory(s.largeCategory());
            c.setMediumCategory(s.mediumCategory());
            c.setSmallCategory(s.smallCategory());
            repo.save(c);
            inserted++;
        }
        System.out.println("[DEV] CategorySeeder inserted=" + inserted);
    }
}