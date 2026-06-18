package com.example.optimal.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    /**
     * Tên field cần filter (VD: "name", "category", "stock")
     */
    private String key;

    /**
     * Toán tử: EQUALITY, GREATER_THAN, LESS_THAN
     */
    private SearchOperation operation;

    /**
     * Giá trị cần so sánh (VD: "Deluxe", "500000")
     */
    private Object value;
}
