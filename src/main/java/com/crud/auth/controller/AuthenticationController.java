package com.crud.auth.controller;

import com.crud.auth.exceptions.ErrorMessage;
import com.crud.auth.model.dto.AuthenticationRequest;
import com.crud.auth.model.dto.AuthenticationResponse;
import com.crud.auth.model.dto.RegisterRequest;
import com.crud.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest
            ) {
        try {
            var register = service.register(request);
            return ResponseEntity.ok(new AuthenticationResponse(register.getName(), register.getToken()));
        } catch (DataIntegrityViolationException ex) {
            log.info("The email: {} already exists", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage
                    (httpServletRequest, HttpStatus.CONFLICT,
                            new Date(), "The email already exists!!"));
        } catch (Exception ex) {
            log.error("API Error - ", ex);
        }
        return ResponseEntity.badRequest().body(new ErrorMessage
                (httpServletRequest, HttpStatus.BAD_REQUEST,
                        new Date(), "Try again later!!"));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpServletRequest
    ) {
        try {
            var login = service.login(request);
            return ResponseEntity.ok(new AuthenticationResponse(login.getName(), login.getToken()));
        } catch (AuthenticationException ex) {
            log.warn("Bad credentials from username: {}", request.getEmail());
        } catch (Exception ex) {
            log.error("API Error - ", ex);
        }
        return ResponseEntity.badRequest().body(new ErrorMessage
                (httpServletRequest, HttpStatus.BAD_REQUEST,
                        new Date(), "Username or password incorrect! Try again!"));
    }

}
