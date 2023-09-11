package com.app.payloads;

import com.app.user.services.UserDTO;
import lombok.Data;

@Data
public class JWTAuthResponse {
	private String token;
	
	private UserDTO user;
}