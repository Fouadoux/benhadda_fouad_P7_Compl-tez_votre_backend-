package com.nnk.springboot.validation.validator;

import com.nnk.springboot.validation.annotation.UniqueValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Validator for the {@link UniqueValue} annotation.
 *
 * <p>Checks if a given value is unique in the database for a specified entity and column.
 */
@Component
public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private String columnName;
    private Class<?> entityClass;

    /**
     * Initializes the validator with the {@link UniqueValue} annotation's parameters.
     *
     * @param constraintAnnotation the annotation instance
     */
    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.columnName = constraintAnnotation.columnName();
        this.entityClass = constraintAnnotation.entityClass();
    }

    /**
     * Validates whether the given value is unique for the specified column and entity.
     *
     * @param value   the value to validate
     * @param context the validation context
     * @return {@code true} if the value is unique, {@code false} otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String query = "SELECT COUNT(e) FROM " + entityClass.getName() + " e WHERE e." + columnName + " = :value";
        Long count = (Long) entityManager.createQuery(query)
                .setParameter("value", value)
                .getSingleResult();

        return count == 0;
    }
}
