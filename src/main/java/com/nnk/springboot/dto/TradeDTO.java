package com.nnk.springboot.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TradeDTO {

    private int id;

    @NotBlank(message = "Account is mandatory")
    private String account;

    @NotBlank(message = "Type is mandatory")
    private String type;

    @NotNull(message = "Buy Quantity is mandatory")
    @Positive(message = "Buy Quantity must be positive")
    private Double buyQuantity;
}
