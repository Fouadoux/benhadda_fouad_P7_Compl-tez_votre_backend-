package com.nnk.springboot.domain;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "curvepoint")
public class CurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "curve_Id", unique = true,nullable = false)
    private Integer curveId;

    @Column(name = "as_of_date")
    private LocalDateTime asOfDate;

    @Column(name = "term",nullable = false)
    private Double term;

    @Column(name = "\"value\"",nullable = false)
    private Double value;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

}
