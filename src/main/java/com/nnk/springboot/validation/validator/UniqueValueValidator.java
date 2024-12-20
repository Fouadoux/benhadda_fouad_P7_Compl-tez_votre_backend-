package com.nnk.springboot.validation.validator;

import com.nnk.springboot.validation.annotation.UniqueValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private String columnName;
    private Class<?> entityClass;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.columnName = constraintAnnotation.columnName();
        this.entityClass = constraintAnnotation.entityClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Gérer null comme une validation séparée
        }

        String query = "SELECT COUNT(e) FROM " + entityClass.getName() + " e WHERE e." + columnName + " = :value";
        Long count = (Long) entityManager.createQuery(query)
                .setParameter("value", value)
                .getSingleResult();

        return count == 0;
    }
}
