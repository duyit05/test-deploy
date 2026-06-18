package com.example.optimal.mapper;

import com.example.optimal.dto.ProductResponse;
import com.example.optimal.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse (Product product);
    List<ProductResponse> toProductResponses(List<Product> products);
}
