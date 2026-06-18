package com.example.optimal.controller;

import com.example.optimal.dto.ApiResponse;
import com.example.optimal.service.CriteriaSearchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final CriteriaSearchService productService;

    @GetMapping
    public ApiResponse<?> searchWithCriteria(@RequestParam(defaultValue = "1") int pageNo,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String sortBy,
                                             @RequestParam(required = false) Double minPrice,
                                             @RequestParam(required = false) String... search
    ) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get products")
                .data(productService.searchWithCriteria(pageNo, pageSize, sortBy, minPrice, search))
                .build();
    }
}
