package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidaNomeExistente implements ClubeValidator{

    @Autowired
    private ClubeRepository clubeRepository;

    @Override
    public void validar(ClubeRequestDto dto) {
        if (clubeRepository.existsByNomeIgnoreCase(dto.nome().toUpperCase())) {
            throw new GenericExceptionConflict("Nome : " + dto.nome() + " ja cadastrado");
        }
    }
}
