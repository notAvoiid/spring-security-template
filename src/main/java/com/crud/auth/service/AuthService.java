package com.crud.auth.service;

import com.crud.auth.exceptions.UsernameUniqueViolationException;
import com.crud.auth.model.User;
import com.crud.auth.model.dto.AuthenticationRequest;
import com.crud.auth.model.dto.AuthenticationResponse;
import com.crud.auth.model.dto.RegisterRequest;
import com.crud.auth.model.enums.Role;
import com.crud.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            User user = User.builder()
                    .name(request.getName())
                    .username(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            log.info("Saving an user");
            return AuthenticationResponse.builder()
                    .name(user.getName())
                    .token(jwtToken)
                    .build();
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username '%s' já cadastrado", request.getName()));
        }
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository.findByUsername(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("Username not found!")
        );

        var jwtToken = jwtService.generateToken(user);
        log.info("An user is authenticated");
        return AuthenticationResponse.builder()
                .name(user.getName())
                .token(jwtToken)
                .build();
    }

}
