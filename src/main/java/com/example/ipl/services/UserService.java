package com.example.ipl.services;

import com.example.ipl.model.LoginRequest;
import com.example.ipl.model.User;
import com.example.ipl.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    public Optional<User> login(LoginRequest loginRequest) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty()) {
            throw new Exception("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        // Check if account is locked
        if (isAccountLocked(user)) {
            throw new Exception("Account is locked. Try again later.");
        }
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new Exception("Invalid credentials");
        }
        
        // Reset failed attempts on successful login
        resetFailedAttempts(user);
        return Optional.of(user);
    }
    
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    private boolean isAccountLocked(User user) {
        if (!user.isAccountLocked()) {
            return false;
        }
        
        if (user.getAccountLockedUntil() != null && 
            LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
            // Unlock account after lockout period
            user.setAccountLocked(false);
            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return false;
        }
        
        return user.isAccountLocked();
    }
    
    private void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        user.setLastFailedLogin(LocalDateTime.now());
        
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
        }
        
        userRepository.save(user);
    }
    
    private void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLastFailedLogin(null);
            userRepository.save(user);
        }
    }
}
