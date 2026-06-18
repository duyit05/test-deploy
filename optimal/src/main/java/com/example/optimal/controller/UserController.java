package com.example.optimal.controller;

import com.example.optimal.dto.ApiResponse;
import com.example.optimal.dto.PageResponse;
import com.example.optimal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<?> getAllUsers (@RequestParam(defaultValue = "1") int pageNo,
                                       @RequestParam(defaultValue = "20") int pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String... sorts) {
        return ApiResponse.<PageResponse<?>>builder()
                .code(HttpStatus.OK.value())
                .message("Get users")
                .data(userService.getUsers(pageNo,pageSize,keyword,sorts))
                .build();
    }

}
