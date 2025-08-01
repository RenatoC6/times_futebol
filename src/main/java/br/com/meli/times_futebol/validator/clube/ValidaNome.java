package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.model.ClubeModel;
import org.springframework.stereotype.Component;

@Component
public class ValidaNome implements ClubeValidator {

    @Override
    public void validar(ClubeRequestDto dto, ClubeModel clubeExistente) {
        if (dto.nome().trim().length() < 3) {
            throw new GenericException("nome deve ter no minimo 3 caracteres");
        }
    }
}
