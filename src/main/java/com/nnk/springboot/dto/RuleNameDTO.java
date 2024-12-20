package com.nnk.springboot.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RuleNameDTO {

    private int id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "json is mandatory")
    private String json;

    @NotBlank(message = "Template is mandatory")
    private String template;

    @NotBlank(message = "Sql is mandatory")
    private String sql;

    @NotBlank(message = "sqlPart is mandatory")
    private String sqlPart;
}
