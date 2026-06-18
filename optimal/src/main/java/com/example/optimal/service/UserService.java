package com.example.optimal.service;

import com.example.optimal.dto.PageResponse;
import com.example.optimal.dto.UserResponse;
import com.example.optimal.mapper.UserMapper;
import com.example.optimal.model.User;
import com.example.optimal.repository.UserRepository;
import com.example.optimal.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public PageResponse<?> getUsers (int pageNo, int pageSize, String keyword, String ...sorts ) {
        int page = pageNo > 0 ? pageNo - 1 : 0;
        List<Sort.Order> orders = new ArrayList<>();
        if(sorts != null){
            for (String sortBy : sorts) {
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    String fieldName = matcher.group(1);
                    String direction = matcher.group(3);
                    orders.add(new Sort.Order(
                            direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                            fieldName
                    ));
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Specification<User> spec = Specification.where(UserSpecification.hasKeyword(keyword));
        Page<User> users = userRepository.findAll(spec, pageable);
        List<UserResponse> responses = users.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(responses)
                .build();
    }

    public User createUser (User user){
        return userRepository.save(user);
    }
}
