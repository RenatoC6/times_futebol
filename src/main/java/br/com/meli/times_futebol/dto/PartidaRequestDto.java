package br.com.meli.times_futebol.dto;


import java.time.LocalDate;

public record PartidaRequestDto(Long clubeMandante,
                                Long clubeVisitante,
                                Long estadioPartida,
                                Long golsMandante,
                                Long golsVisitante,
                                LocalDate dataPartida
                                ) {}



