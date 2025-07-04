package com.scaler.userauthservice.controllers;

import com.scaler.userauthservice.dtos.LoginRequestDto;
import com.scaler.userauthservice.dtos.SignUpRequestDto;
import com.scaler.userauthservice.dtos.UserDto;
import com.scaler.userauthservice.dtos.ValidateTokenRequestDto;
import com.scaler.userauthservice.models.User;
import com.scaler.userauthservice.services.AuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signUp")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        User user=authService.signUp(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
        return from(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto){
        Pair<User,String> userWithToken =authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        UserDto userDto=from(userWithToken.a);
        String token=userWithToken.b;
        MultiValueMap<String,String> headers=new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE,token);
        return new ResponseEntity<>(userDto,headers, HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequestDto  validateTokenRequestDto){
        return authService.validateToken(validateTokenRequestDto.getToken(),validateTokenRequestDto.getUserId());
    }

    public UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
