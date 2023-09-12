package com.app.controllers;

import com.app.user.services.UserDTO;
import com.app.user.services.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.config.AppConstants;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class UserController {

    @Value("${com.app.user-service-url}")
    private String userServiceBaseUrl;
    @Autowired
    private RestTemplate restTemplate;

    private <T> ResponseEntity<T> sendGetRequest(String url, Map<String, Object> params, Class<T> tClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                tClass
        );
    }

    @GetMapping("/admin/users")
    public ResponseEntity<UserResponse> getUsers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_USERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        String url = userServiceBaseUrl + "/getAllUsers";

        Map<String, Object> params = new HashMap<>();
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        params.put("sortBy", sortBy);
        params.put("sortOrder", sortOrder);

        ResponseEntity<UserResponse> response = sendGetRequest(url, params, UserResponse.class);

        return new ResponseEntity<>(response.getBody(), HttpStatus.FOUND);
    }

    @GetMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        String url = userServiceBaseUrl + "/getUserById/{userId}";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        ResponseEntity<UserDTO> response = sendGetRequest(url, params, UserDTO.class);

        return new ResponseEntity<>(response.getBody(), HttpStatus.FOUND);
    }

    @PutMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        String url = userServiceBaseUrl + "/updateUser/{userId}";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        HttpEntity<UserDTO> requestEntity = new HttpEntity<>(userDTO);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                UserDTO.class,
                params
        );

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        String url = userServiceBaseUrl + "/deleteUser/{userId}";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                String.class,
                params
        );

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }
}
