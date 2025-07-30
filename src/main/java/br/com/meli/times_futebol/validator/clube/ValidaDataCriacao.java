package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValidaDataCriacao implements ClubeValidator {

    @Override
    public void validar(ClubeRequestDto dto) {
        LocalDate dataCriacao = dto.dataCriacao();

        if (dataCriacao == null || dataCriacao.isAfter(LocalDate.now())) {
            throw new GenericException("data de criacao invalido ou no futuro");
        }
    }

}

