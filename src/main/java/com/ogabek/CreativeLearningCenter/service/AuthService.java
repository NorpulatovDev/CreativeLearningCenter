package com.ogabek.CreativeLearningCenter.service;

import com.ogabek.CreativeLearningCenter.dto.request.LoginRequest;
import com.ogabek.CreativeLearningCenter.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
