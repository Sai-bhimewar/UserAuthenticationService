package com.scaler.userauthservice.services;

import com.scaler.userauthservice.models.User;
import com.scaler.userauthservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserDetails(Long id){
        Optional<User> optionalUser=userRepository.findById(id);
        if(optionalUser.isEmpty()){
            System.out.println("User not found");
            return null;
        }
        System.out.println(optionalUser.get().getEmail());
        return optionalUser.get();
    }
}
