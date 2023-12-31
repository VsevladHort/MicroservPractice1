package com.app.controllers;

import java.util.Collections;
import java.util.Map;

import com.app.cart.APIException;
import com.app.user.services.UserDTO;
import com.app.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.exceptions.UserNotFoundException;
import com.app.payloads.LoginCredentials;
import com.app.security.JWTUtil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class AuthController {

    @Value("${com.app.user-service-url}")
    private String userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerHandler(@Valid @RequestBody UserDTO user) throws UserNotFoundException {
        UserDTO userDTO;
        try {
            String url = userService + "/public/users/register";

            HttpEntity<UserDTO> requestEntity = new HttpEntity<>(user);

            ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, requestEntity, UserDTO.class);

            userDTO = response.getBody();

        } catch (HttpClientErrorException e) {
            throw new APIException(e.getMessage());
        }

        if (userDTO == null) {
            throw new RuntimeException("Internal service error!");
        }

        String token = jwtUtil.generateToken(userDTO.getEmail());

        return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("jwt-token", token),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@Valid @RequestBody LoginCredentials credentials) {

        UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
                credentials.getEmail(), credentials.getPassword());

        authenticationManager.authenticate(authCredentials);

        String token = jwtUtil.generateToken(credentials.getEmail());

        return Collections.singletonMap("jwt-token", token);
    }
}