package com.nnk.springboot.validation.annotation;

import com.nnk.springboot.validation.validator.UniqueValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueValueValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {
    String message() default "Cette valeur existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String columnName(); // Nom de la colonne
    Class<?> entityClass(); // Classe de l'entité
}
