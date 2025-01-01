package com.example.ipl.controller;

import com.example.ipl.model.User;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.services.UserService;
import com.example.ipl.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository ;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> responseEntity(@RequestBody User user) {
        try {
            Optional<User> user1 = userService.login(user);
            if (user1.isPresent()) {
                log.info("Authentication Successfull");
                String token = JwtUtil.generateToken(user1.get().getUsername());
                return ResponseEntity.ok(new AuthResponse(token));
            }
            else
            {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping(value = "/getuser")
    public List<User> getUser()
    {
       return userRepository.findAll();
    }

    @PostMapping("/addUsers")
    public User addUser(@RequestBody User user) {
        System.out.println("Received User: " + user);
        return userRepository.save(user);
    }

public static class AuthResponse {
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    } public String getToken()
    {
        return token;
    }
    public void setToken(String token)
    {
        this.token = token;
    }
    }
    }
