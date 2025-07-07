package br.com.meli.times_futebol.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;


    public record ClubeRequestDto(String nome,
                                  String estado,
                                  LocalDate dataCriacao,
                                  @NotNull Boolean status) {

    }


