package com.example.ipl.services;

import com.example.ipl.model.User;
import com.example.ipl.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public Optional<User> login(User user) throws Exception {
         Long id=user.getId();
         Optional<User> user1= userRepository.findById(id);
         if(user1.isEmpty()){
             throw new Exception("Authentication failed ");
         }
         else
             return  user1;
    }
}
