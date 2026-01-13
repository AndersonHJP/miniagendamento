package dev.andersonhjp.miniagendamento.controller;

import dev.andersonhjp.miniagendamento.dto.LoginRequest;
import dev.andersonhjp.miniagendamento.dto.LoginResponse;
import dev.andersonhjp.miniagendamento.dto.RegisterUserRequest;
import dev.andersonhjp.miniagendamento.dto.RegisterUserResponse;
import dev.andersonhjp.miniagendamento.model.LoginUser;
import dev.andersonhjp.miniagendamento.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return null;
    }

    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request){
        LoginUser newUser = new LoginUser();
        newUser.setPassword(request.password());
        newUser.setEmail(request.email());
        newUser.setNome(request.nome());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(newUser.getNome(), newUser.getEmail()));

    }

}
