package dev.andersonhjp.miniagendamento.dto;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(@NotEmpty(message = "Nome é obrigatório") String nome,
                                  @NotEmpty(message = "E-mail é obrigatório")String email,
                                  @NotEmpty(message = "Senha é obrigatório")String password) {
}
