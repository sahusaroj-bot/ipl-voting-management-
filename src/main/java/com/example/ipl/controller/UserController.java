package com.example.ipl.controller;

import com.example.ipl.model.LoginRequest;
import com.example.ipl.model.RegisterRequest;
import com.example.ipl.model.ResetRequest;
import com.example.ipl.model.User;
import com.example.ipl.repositories.TransactionRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.model.Transaction;
import com.example.ipl.services.UserService;
import com.example.ipl.utils.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;
    
    @Autowired
    JwtUtil jwtUtil;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            Optional<User> userOpt = userService.login(loginRequest);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                log.info("Authentication successful for user: {}", user.getUsername());
                
                String accessToken = jwtUtil.generateToken(user.getUsername());
                String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
                
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, user.getId(), user.getUsername(), user.getRole().toString()));
            }
        } catch (Exception e) {
            log.warn("Authentication failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        try {
            String username = jwtUtil.verifyRefreshToken(refreshToken).getSubject();
            String newAccessToken = jwtUtil.generateToken(username);
            
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        }
    }
    @GetMapping (value="/getMoney")
    public  double getMoney(@RequestParam ("id")Long id){
       Optional<User> user=userRepository.findById(id);
        return user.map(User::getTotalAmount).orElse(null);
    }

    @GetMapping(value = "/getuser")
    public List<User> getUser()
    {
       return userRepository.findAll();
    }

    @GetMapping("/leaderboard")
    public List<User> getLeaderboard() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getTotalAmount() > 0)
                .sorted((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()))
                .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }
            
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }
            
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());

            // Validate transactionId
            String txId = registerRequest.getTransactionId();
            Transaction transaction = transactionRepository.findByTransactionId(txId)
                    .orElse(null);

            if (transaction == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid transaction ID"));
            }
            if (transaction.isUsed()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Transaction ID already used"));
            }

            user.setTransactionId(txId);
            User savedUser = userService.registerUser(user);

            // Mark transaction as used
            transaction.setUsed(true);
            transaction.setUsedBy(savedUser.getUsername());
            transactionRepository.save(transaction);

            log.info("User registered successfully: {}", savedUser.getUsername());
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "userId", savedUser.getId()));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));
        }
    }


    
    // Fetch the transactionId linked to a username (masked for security)
    @PostMapping("/get-transaction-hint")
    public ResponseEntity<?> getTransactionHint(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        String txId = userOpt.get().getTransactionId();
        if (txId == null || txId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No transaction ID linked to this account"));
        }
        // Return masked hint: show last 4 chars only
        String hint = "*".repeat(Math.max(0, txId.length() - 4)) + txId.substring(Math.max(0, txId.length() - 4));
        return ResponseEntity.ok(Map.of("hint", hint, "length", txId.length()));
    }

    // Reset password: verify username + transactionId, then update password only
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String transactionId = body.get("transactionId");
        String newPassword = body.get("newPassword");

        if (username == null || transactionId == null || newPassword == null
                || username.isBlank() || transactionId.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        if (!transactionId.equals(user.getTransactionId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid transaction ID"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        log.info("Password reset for user: {}", username);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private Long userId;
        private String username;
        private String role;
        
        public AuthResponse(String accessToken, String refreshToken, Long userId, String username, String role) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
