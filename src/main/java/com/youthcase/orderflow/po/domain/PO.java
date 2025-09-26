package com.youthcase.orderflow.po.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "PO")
public class PO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK, 나중에 ERD에 맞게 이름/타입 수정 가능

    // TODO: 실제 컬럼은 각자 ERD에 맞게 추가

    public PO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
