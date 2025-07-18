package br.com.meli.times_futebol.dto;

import br.com.meli.times_futebol.model.PartidaModel;

import java.util.List;

public record PartidaResponseConfrontoDiretoDto(String mensagem,
                                                List<PartidaModel> listaPartidas,
                                                ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto1,
                                                ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto2) {}
