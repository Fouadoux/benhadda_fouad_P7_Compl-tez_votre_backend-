package com.nnk.springboot.validation.annotation;

import com.nnk.springboot.validation.validator.UniqueValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to ensure a field value is unique in the database.
 *
 * <p>This annotation is validated by {@link UniqueValueValidator}.
 */
@Constraint(validatedBy = UniqueValueValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {
    /**
     * The default validation message.
     *
     * @return the validation message
     */
    String message() default "Cette valeur existe déjà";

    /**
     * Validation groups for categorizing constraints.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Custom payload for additional metadata about the constraint.
     *
     * @return the payload classes
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Specifies the column name in the database that should be checked for uniqueness.
     *
     * @return the column name
     */
    String columnName();

    /**
     * Specifies the entity class in the database that should be checked for uniqueness.
     *
     * @return the entity class
     */
    Class<?> entityClass();
}
