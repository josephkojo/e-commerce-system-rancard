package com.springDevelopers.Backend.Auth;

import com.springDevelopers.Backend.Entities.User;
import com.springDevelopers.Backend.Repositories.UserRepository;
import com.springDevelopers.Backend.Services.UserService;
import com.springDevelopers.Backend.SpringSecurity.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;




    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        AuthenticateResponse authenticateResponse = this.authenticationService.registerUser(registerRequest);
        return new ResponseEntity<>(authenticateResponse, HttpStatus.CREATED);

    }
    @PostMapping("/login")

    public ResponseEntity<AuthenticateResponse> loginUser(@RequestBody AuthenticateRequest authenticateRequest){
        AuthenticateResponse authenticateResponse = this.authenticationService.loginUser(authenticateRequest);
        return new ResponseEntity<>(authenticateResponse, HttpStatus.OK);
   }


}
