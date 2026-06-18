package com.example.optimal.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class ProductResponse {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
    private LocalDateTime createdAt;
}
