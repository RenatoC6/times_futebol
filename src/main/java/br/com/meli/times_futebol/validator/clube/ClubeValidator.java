package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;

public interface ClubeValidator {

    void validar(ClubeRequestDto dto, ClubeModel clubeExistente);

}
