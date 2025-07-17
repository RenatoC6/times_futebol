package br.com.meli.times_futebol.dto;

public record ClubeResponseRetrospectivaDto(String mensagem,
                                            String nome,
                                            String nomeAdversario,
                                            Long vitorias,
                                            Long empates,
                                            Long derrotas,
                                            Long golsMarcados,
                                            Long golsSofridos) {
}
