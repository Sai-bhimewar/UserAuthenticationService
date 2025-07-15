package com.scaler.userauthservice.controllers;

import com.scaler.userauthservice.dtos.UserDto;
import com.scaler.userauthservice.exceptions.UserNotRegisteredException;
import com.scaler.userauthservice.models.User;
import com.scaler.userauthservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserDetails(@PathVariable Long id) {
        User user=userService.getUserDetails(id);
        if(user==null){
            System.out.println("User not found");
            return null;
        }
        System.out.println(user.getEmail());
        return from(user);
    }

    public UserDto from(User user) {
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }
}
