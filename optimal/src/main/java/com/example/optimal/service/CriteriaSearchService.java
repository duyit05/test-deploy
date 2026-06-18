package com.example.optimal.service;

import com.example.optimal.criteria.SearchCriteria;
import com.example.optimal.criteria.SearchCriteriaConsumer;
import com.example.optimal.criteria.SearchOperation;
import com.example.optimal.dto.PageResponse;
import com.example.optimal.dto.ProductResponse;
import com.example.optimal.mapper.ProductMapper;
import com.example.optimal.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class CriteriaSearchService {
    @PersistenceContext
    private final EntityManager entityManager;

    private final ProductMapper mapper;

    public PageResponse<?> searchWithCriteria(int pageNo, int pageSize, String sortBy, Double minPrice, String... search) {
        List<SearchCriteria> criteriaList = parseCriteria(search);
        List<ProductResponse> responses = getProducts(pageNo, pageSize, criteriaList,minPrice, sortBy);
        Long totalElements = getTotalElements(criteriaList, minPrice);
        int  totalPage = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(totalPage)
                .items(responses)
                .build();
    }

    // Xử lý danh sách điều kiện search
    public List<SearchCriteria> parseCriteria(String... search) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        if (search == null) return criteriaList;
        // Regex: "name:Iphone" → group1="name", group2=":", group3="Iphone"
        //        "price>500"   → group1="price", group2=">", group3="500"
        Pattern pattern = Pattern.compile("(\\w+?)([:><])(.*)");
        for (String s : search) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                criteriaList.add(new SearchCriteria(
                        matcher.group(1),
                        SearchOperation.fromSymbol(matcher.group(2).charAt(0)),
                        matcher.group(3)
                ));
            }
        }
        return criteriaList;
    }

    // Lay danh sách sản phẩm
    public List<ProductResponse> getProducts(int pageNo, int pageSize, List<SearchCriteria> criteria, Double minPrice, String sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        // WHERE
        Predicate predicate = buildPredicate(cb, root, criteria, minPrice);
        query.where(predicate);

        return mapper.toProductResponses(
                entityManager.createQuery(query)
                        .setFirstResult(pageNo * pageSize)
                        .setMaxResults(pageSize)
                        .getResultList()
        );
    }

    // Build predicate tổng
    private Predicate buildPredicate(CriteriaBuilder cb, Root<Product> root, List<SearchCriteria> criteria, Double minPrice) {
        // Bắt đầu với where 1 = 1 luôn đúng
        Predicate predicate = cb.conjunction();
        // Xử lý criteriaList qua Consumer
        if (!criteria.isEmpty()) {
            SearchCriteriaConsumer<Product> consumer = new SearchCriteriaConsumer<>(cb, root, predicate);
            criteria.forEach(consumer);
            predicate = consumer.getPredicate();
        }
        if(minPrice != null && minPrice > 0){
            Predicate predicatePrice = cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(minPrice));
            predicate = cb.and(predicate, predicatePrice);
        }
        return predicate;
    }

    // Lấy tổng và phân trang
    private Long getTotalElements (List<SearchCriteria> criteriaList, Double minPrice){
        CriteriaBuilder     cb    = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Product>       root  = query.from(Product.class);

        Predicate predicate = buildPredicate(cb, root, criteriaList, minPrice);
        query.select(cb.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }
}
