package com.even.zaro.service;

import com.even.zaro.entity.User;

public interface EmailService {
    void sendVerificationEmail(User user, String token);
}
