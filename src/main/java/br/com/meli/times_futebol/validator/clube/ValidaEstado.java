package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.enums.EstadoBr;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.model.ClubeModel;
import org.springframework.stereotype.Component;

@Component
public class ValidaEstado implements ClubeValidator{
    @Override
    public void validar(ClubeRequestDto dto, ClubeModel clubeExistente) {
        if (!EstadoBr.validaEstadoBr(dto.estado())) {
            throw new GenericException("Estado: " + dto.estado() + " invalido");
        }
    }
}
