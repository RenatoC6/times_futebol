package br.com.meli.times_futebol.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadioRequestDto(@NotBlank String nomeEstadio) {
}
