package com.apply.serviceImpl;

import com.apply.dto.request.UserRequestDTO;
import com.apply.entity.User;
import com.apply.repository.UserRepository;
import com.apply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepository userRepository;

    // ✅ Creating a user from DTO
    @Override
    public User createUser(UserRequestDTO userRequest) {
        User user = new User();
        user.setName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        user.setKeywords(userRequest.getKeywords());

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserKeywords(Long userId, Set<String> keywords) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setKeywords(keywords);
        return userRepository.save(user);
    }
}
