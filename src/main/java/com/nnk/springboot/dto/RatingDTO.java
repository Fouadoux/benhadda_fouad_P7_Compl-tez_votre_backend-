package com.nnk.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RatingDTO {


    private int id;

    @NotBlank(message = "Moodys Rating is mandatory")
    private String moodysRating;

    @NotBlank(message = "SandP Rating is mandatory")
    private String sandPRating;

    @NotBlank(message = "FitchRating is mandatory")
    private String fitchRating;

    @NotNull(message = "Order Number quantity is mandatory")
    @Positive(message = "Order Number must be positive")
    private Integer orderNumber;
}
