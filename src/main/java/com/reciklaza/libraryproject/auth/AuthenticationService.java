package com.reciklaza.libraryproject.auth;

import com.reciklaza.libraryproject.config.JwtService;
import com.reciklaza.libraryproject.entity.user.User;
import com.reciklaza.libraryproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.reciklaza.libraryproject.util.Util.validateNotBlank;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        validateUserDto(request);

        boolean userExists = repository.findByEmail(request.getEmail()).isPresent();
        if (userExists) {
            throw new DataIntegrityViolationException("Email already taken");
        }
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        repository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Registration successful")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );
        User user = repository.findByEmail(request.email())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void validateUserDto(RegisterRequest request) {
        validateNotBlank(request.getFirstname(), "Firstname");
        validateNotBlank(request.getLastname(), "Lastname");
        validateNotBlank(request.getEmail(), "Email");
        validateNotBlank(request.getPassword(), "Password");
    }
}
