package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "AUTHORITY")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK

    // TODO: authorityName 등 추가

    public Authority() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
