package com.example.optimal.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class SearchCriteriaConsumer<T> implements Consumer<SearchCriteria> {
    private CriteriaBuilder builder;
    private Root<T> root;
    private Predicate predicate;

    @Override
    public void accept(SearchCriteria criteria) {
        // Validate field tồn tại trong entity
        try {
            root.get(criteria.getKey());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Field '" + criteria.getKey() + "' không tồn tại trong entity");
        }
        Predicate newCondition = switch (criteria.getOperation()) {
            case GREATER_THAN -> builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            // EQUALITY: LIKE nếu String, equals nếu type khác
            case EQUALITY -> {
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    yield builder.like(
                            builder.upper(root.get(criteria.getKey())),
                            "%" + criteria.getValue().toString().toUpperCase() + "%"
                    );
                } else {
                    yield builder.equal(
                            root.get(criteria.getKey()),
                            criteria.getValue()
                    );
                }
            }
        };
        predicate = builder.and(predicate, newCondition);
    }
}
