package com.example.optimal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiResponse <T>{
    private int code;
    private String message;
    private T data;
}