package br.com.meli.times_futebol.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PartidaRequestDto(Long clubeMandante,
                                Long clubeVisitante,
                                Long estadioPartida,
                                Long golsMandante,
                                Long golsVisitante,
                                @NotBlank LocalDate dataPartida
                                ) {}



