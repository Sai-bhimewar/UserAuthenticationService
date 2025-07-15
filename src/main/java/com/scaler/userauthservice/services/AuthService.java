package com.scaler.userauthservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.userauthservice.clients.KafkaClient;
import com.scaler.userauthservice.dtos.EmailDto;
import com.scaler.userauthservice.exceptions.AccountSuspendedException;
import com.scaler.userauthservice.exceptions.PasswordMismatchException;
import com.scaler.userauthservice.exceptions.UserAlreadySignedException;
import com.scaler.userauthservice.exceptions.UserNotRegisteredException;
import com.scaler.userauthservice.models.Session;
import com.scaler.userauthservice.models.State;
import com.scaler.userauthservice.models.User;
import com.scaler.userauthservice.repositories.SessionRepository;
import com.scaler.userauthservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private KafkaClient kafkaClient;

    @Autowired
    private ObjectMapper objectMapper;

    public User signUp(String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            throw new UserAlreadySignedException("User already exists please try login directly!");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        //We will add logic to send welcome email at this place
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom("saibhimewar788@gmail.com");
        emailDto.setTo(email);
        emailDto.setBody("Welcome to Scaler");
        emailDto.setSubject("Hope youn will have , good learning experience");
        String emailMessageString=null;
        try{
            emailMessageString=objectMapper.writeValueAsString(emailDto);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Something went wrong");
        }
        kafkaClient.sendEmail("signup", emailMessageString);
        return user;
    }

    public Pair<User,String> login(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new UserNotRegisteredException("Please try signup first!");
        }
        User user = optionalUser.get();
        if(!user.getState().equals(State.Active)){
            throw new AccountSuspendedException("Account is temporarily suspended, please try after some days!");
        }
        String storedPassword = user.getPassword();
        if(!bCryptPasswordEncoder.matches(password,storedPassword)){
            throw new PasswordMismatchException("Please type the correct password!");
        }

        //Token Generation
//        String message = "{\n" +
//                "   \"email\": \"saiprasad@gmail.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"student\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"2ndApril2026\"\n" +
//                "}";
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        Long nowInMillis = System.currentTimeMillis();
        claims.put("iat", nowInMillis);
        claims.put("exp",nowInMillis+nowInMillis);
        claims.put("iss","authservice##");
        String token= Jwts.builder().claims(claims).signWith(secretKey).compact();

        //Session Details
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setState(State.Active);
        sessionRepository.save(session);
        Pair<User,String> response=new Pair<>(user,token);
        return response;
    }

    public boolean validateToken(String token,Long userId){
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token,userId);
        if(optionalSession.isEmpty()){
            System.out.println("Passed Token is not valid!");
            return false;
        }
        JwtParser jwtParser=Jwts.parser().verifyWith(secretKey).build();
        Claims claims=jwtParser.parseSignedClaims(token).getPayload();
        Long expiryTime=(long)claims.get("exp");
        System.out.println("Expiry Time: "+expiryTime);
        System.out.println("Current Time: "+System.currentTimeMillis());
        if(expiryTime<System.currentTimeMillis()){
            return false;
        }
        return true;
    }
}
