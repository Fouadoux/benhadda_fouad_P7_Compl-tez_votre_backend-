package com.nnk.springboot.dto;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.validation.annotation.UniqueValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CurveDTO {

    private long id;


    @NotNull(message="Must not be null")
  //  @UniqueValue(entityClass = CurvePoint.class, columnName = "curveId", message = "Ce CurveId est déjà utilisé")
    @Positive(message = "Id must be positive")
    private Integer curveId;

    @NotNull(message="Must not be null")
    @Positive(message = "Term must be positive")
    private Double term;

    @NotNull(message="Must not be null")
    private Double value;
}
