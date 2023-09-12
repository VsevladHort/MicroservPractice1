package com.app.controllers;

import com.app.cart.CartDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {

    @Value("${com.app.cart-service-url}")
    private String cartService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
        String redirectUrl = cartService + "/public/carts/" + cartId + "/products/" + productId + "/quantity/" + quantity;

        return getCartDTOResponseEntity(redirectUrl);
    }

    @GetMapping("/admin/carts")
    public RedirectView getCarts() {

        String redirectUrl = cartService + "/admin/carts";

        return new RedirectView(redirectUrl);
    }

    //string%40string.com
    @GetMapping("/public/users/{emailId}/carts/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable String emailId, @PathVariable Long cartId) {
        String redirectUrl = cartService + "/public/users/" + emailId + "/carts/" + cartId;
        return getCartDTOResponseEntity(redirectUrl);
    }

    @PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity) {
        String redirectUrl = cartService + "/public/carts/" + cartId + "/products/" + productId + "/quantity/" + quantity;
        return getCartDTOResponseEntity(redirectUrl);
    }

    private ResponseEntity<CartDTO> getCartDTOResponseEntity(String redirectUrl) {
        ResponseEntity<CartDTO> response = restTemplate.postForEntity(redirectUrl, new Object(), CartDTO.class);

        CartDTO cartDTO = response.getBody();

        return new ResponseEntity<>(cartDTO, response.getStatusCode());
    }

    @DeleteMapping("/public/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String redirectUrl = cartService + "/public/carts/" + cartId + "/product/" + productId;
        ResponseEntity<String> response = restTemplate.postForEntity(redirectUrl, new Object(), String.class);

        String responseBody = response.getBody();

        return new ResponseEntity<>(responseBody, response.getStatusCode());
    }
}
