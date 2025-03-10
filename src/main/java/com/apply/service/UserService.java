package com.apply.service;

import com.apply.dto.request.UserRequestDTO;
import com.apply.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User createUser(UserRequestDTO userRequest);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUserKeywords(Long userId, Set<String> keywords);
}
