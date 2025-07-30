package br.com.meli.times_futebol.dto;

public record EstadioViaCepDto(String nomeEstadio,
                               String cep,
                               String logradouro,
                               String complemento,
                               String bairro,
                               String localidade,
                               String uf,
                               String ibge,
                               String gia,
                               String ddd,
                               String siafi,
                               boolean erro)
{}



