package br.com.meli.times_futebol.dto;


import java.time.LocalDateTime;

public record PartidaRequestDto(Long clubeMandante,
                                Long clubeVisitante,
                                Long estadioPartida,
                                Long golsMandante,
                                Long golsVisitante,
                                LocalDateTime dataPartida
                                ) {}



