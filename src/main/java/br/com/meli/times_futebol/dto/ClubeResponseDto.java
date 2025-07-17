package br.com.meli.times_futebol.dto;

public record ClubeResponseDto(String nome,
                                Long vitorias,
                                Long empates,
                                Long derrotas,
                                Long golsMarcados,
                                Long golsSofridos) {
}
