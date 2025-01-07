package com.nnk.springboot.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rulename")
public class RuleName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private int id;

    @Column(name = "name",  nullable = false, length = 125)
    private String name;

    @Column(name = "description",  nullable = false, length = 125)
    private String description;

    @Column(name = "json",  nullable = false, length = 125)
    private String json;

    @Column(name = "template",  nullable = false, length = 512)
    private String template;

    @Column(name = "sqlStr",  nullable = false, length = 125)
    private String sqlStr;

    @Column(name = "sqlPart",  nullable = false, length = 125)
    private String sqlPart;

}
