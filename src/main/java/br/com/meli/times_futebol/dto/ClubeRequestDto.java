package br.com.meli.times_futebol.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClubeRequestDto(@NotBlank String nome,
                              @NotBlank String estado,
                              @NotNull  LocalDate dataCriacao,
                              @NotNull Boolean status) {

}