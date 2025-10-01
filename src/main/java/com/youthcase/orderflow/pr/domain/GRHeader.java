package com.youthcase.orderflow.pr.domain;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.po.domain.POHeader;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "GR_HEADER")
@Getter
@Setter
public class GRHeader {

    @Id
    @Column(name = "GR_ID", length = 20)
    private String grId;  // PK, 문자열

    @Column(name = "STATUS", length = 15, nullable = false)
    private String status; // 상태

    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // NUMBER(10,2)

    @Column(name = "REMARKS", length = 255)
    private String remarks; // NULL 허용

    @Column(name = "APPROVAL_DATE")
    @Temporal(TemporalType.DATE)
    private Date approvalDate = new Date(); // 기본값 SYSDATE와 매핑

    // PO 테이블과 FK 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PO_ID", nullable = false)
    private POHeader poHeader;

    // USER 테이블과 FK 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
}