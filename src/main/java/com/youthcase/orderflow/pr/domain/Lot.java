package com.youthcase.orderflow.pr.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "LOT")
@Getter
@Setter
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "LOT_ID")
    private Long lotId;

    @Column(name = "EXP_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expDate;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "NEAR_EXPIRY_DAYS", nullable = false)
    private Integer nearExpiryDays;

    // FK: GR_HEADER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GR_ID", nullable = false)
    private GRHeader grHeader;
}