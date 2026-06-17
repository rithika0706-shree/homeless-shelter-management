package com.tn.homeless.service;

import com.tn.homeless.dto.RegisterRequest;
import com.tn.homeless.entity.User;

public interface UserService {
    User registerUser(RegisterRequest request);
    User findByUsername(String username);
}
