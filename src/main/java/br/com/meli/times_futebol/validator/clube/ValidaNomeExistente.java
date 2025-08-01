package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidaNomeExistente implements ClubeValidator {

    @Autowired
    private ClubeRepository clubeRepository;

    @Override
    public void validar(ClubeRequestDto dto, ClubeModel clubeExistente) {

        boolean existe = clubeRepository.existsByNomeIgnoreCase(dto.nome().toUpperCase());

        // nesse caso para criacao de clube e outras situacoes onde clubeExistente é null
        if (existe && clubeExistente == null) {
            throw new GenericExceptionConflict("Nome : " + dto.nome() + " ja cadastrado");
        }

        // nesse caso para atualizacao de clube, onde clubeExistente é o clube que está sendo atualizado
        if (existe && !clubeExistente.getNome().equals(dto.nome())) {
            throw new GenericExceptionConflict("Nome : " + dto.nome() + " ja cadastrado");
        }

    }
}
