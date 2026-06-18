package com.example.optimal.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PageResponse <T> implements Serializable {
    private int pageNo;
    private int pageSize;
    private int totalPage;
    private T items;
}
