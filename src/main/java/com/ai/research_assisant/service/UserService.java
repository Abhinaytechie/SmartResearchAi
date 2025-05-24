package com.ai.research_assisant.service;

import com.ai.research_assisant.entity.LoginRequest;
import com.ai.research_assisant.entity.Paper;
import com.ai.research_assisant.entity.User;
import com.ai.research_assisant.repository.UserRepo;

import com.ai.research_assisant.utilities.JwtUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PapersService papersService;
    @Autowired
    private UserDetailServices userDetailServices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtility jwtUtil;
    @Autowired
    private  PasswordEncoder encoder;
    public String signup(LoginRequest loginRequest) {
        if (userRepo.existsByUsername(loginRequest.getUsername())) {
            throw new DuplicateKeyException("Username already exists");
        }

        User user = new User(loginRequest.getUsername());
        user.setPassword(encoder.encode(loginRequest.getPassword()));
        userRepo.save(user);

        UserDetails details = userDetailServices.loadUserByUsername(loginRequest.getUsername());
        return jwtUtil.generateToken(details.getUsername());
    }


    public String getAuthenticatedUser(LoginRequest loginRequest) {
            try{
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
                UserDetails details=userDetailServices.loadUserByUsername(loginRequest.getUsername());
                String jwt=jwtUtil.generateToken(details.getUsername());
                return jwt;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }



    }

    public ResponseEntity<?> getdetails(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepo.findByUsername(username);

        if (user==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Paper> bookmarkedPapers = papersService.getBookmarkedPapers(username);
          // similarly implemented

        Map<String, Object> response = new HashMap<>();
        response.put("bookmarks", bookmarkedPapers);

        return ResponseEntity.ok(response);
    }
}
