package com.youthcase.orderflow.gr.domain;

import jakarta.persistence.*;
import lombok.Getter; // ✅ Getter 추가

@Entity
@Table(name = "GR")
@Getter // ✅ Getter 추가
public class GR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GR_ID") // ✅ PK 컬럼명을 명시적으로 지정
    private Long grId;  // ✅ PK 필드명을 grId로 수정

    // TODO: 실제 컬럼은 각자 ERD에 맞게 추가

    public GR() {}

    // Lombok @Getter로 대체
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
}
