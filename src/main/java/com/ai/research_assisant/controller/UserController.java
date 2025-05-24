package com.ai.research_assisant.controller;

import com.ai.research_assisant.entity.LoginRequest;
import com.ai.research_assisant.entity.User;
import com.ai.research_assisant.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5173/","https://smart-research-ai.vercel.app"})
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody LoginRequest loginRequest){

        try {
            String token= userService.signup(loginRequest);
            return ResponseEntity.ok(Map.of("username", loginRequest.getUsername(), "token", token));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error during signup"));
        }
    }
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(Authentication auth) {
        System.out.println(auth.getName());
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Authentication failed!");
        }
        System.out.println("Authenticated user: " + auth.getName());
        return ResponseEntity.ok("User " + auth.getName() + " is authenticated.");
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = userService.getAuthenticatedUser(loginRequest);
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Exception while creating token", e);
            return new ResponseEntity<>("Invalid username or password", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return userService.getdetails(authentication);


    }


}
