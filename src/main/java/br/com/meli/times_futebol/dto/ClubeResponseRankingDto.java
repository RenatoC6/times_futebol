package br.com.meli.times_futebol.dto;


public record ClubeResponseRankingDto(String mensagem,
                                      String nome,
                                      Long pontos,
                                      Long gols,
                                      Long vitorias,
                                      Long jogos) {}
