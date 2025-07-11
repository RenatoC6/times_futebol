package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.enums.EstadoBr;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClubeService {

    @Autowired
    ClubeRepository clubeRepository;

    public ClubeModel criarTime(ClubeRequestDto clubeRequestDto) {


        validaNome(clubeRequestDto);
        validaEstado(clubeRequestDto);
        validaDataCriacao(clubeRequestDto);
        validaNomeExistente(clubeRequestDto);

        var clubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }


    public Page<ClubeModel> listarTodosTimes(Pageable pageable) {

        return clubeRepository.findAll(pageable);

    }

    public ClubeModel acharTime(Long idValor) {

        return clubeRepository.findById(idValor)
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Time: "+ idValor +  " nao encontrado"));

    }

    public ClubeModel atualizarTime(ClubeModel clubeModel,  ClubeRequestDto clubeRequestDto) {


        validaNome(clubeRequestDto);
        validaEstado(clubeRequestDto);
        validaDataCriacao(clubeRequestDto);

        // valida se ja existe esse nome de clube na base, exceto se o nome nao foi alterado
        if(!clubeModel.getNome().equals(clubeRequestDto.nome())) {
            validaNomeExistente(clubeRequestDto);
        }

        clubeModel.setId(clubeModel.getId());
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public void inativaTime(ClubeModel clubeModel) {

        clubeModel.setStatus(false);
        clubeRepository.save(clubeModel);

    }

    // metodos validacao

    public void validaNome(ClubeRequestDto clubeRequestDto) {

        if(clubeRequestDto.nome().trim().length() < 3 || clubeRequestDto.nome().isEmpty()){
            throw new GenericException("nome deve ter no minimo 3 caracteres");
        }

    }

    public void validaEstado(ClubeRequestDto clubeRequestDto) {
        if(!EstadoBr.validaEstadoBr(clubeRequestDto.estado())){
            throw new GenericException("Estado: " + clubeRequestDto.estado() + " invalido");
        }
    }

    public void validaDataCriacao(ClubeRequestDto clubeRequestDto) {

        LocalDate dataCriacao = clubeRequestDto.dataCriacao();

        if(dataCriacao == null || dataCriacao.isAfter(LocalDate.now())) {
            throw new GenericException("data de criacao invalido ou no futuro");
        }
    }

    public void validaNomeExistente(ClubeRequestDto clubeRequestDto) {
        if(clubeRepository.existsByNomeIgnoreCase(clubeRequestDto.nome().toUpperCase())){
            throw new GenericExceptionConflict("Nome : " + clubeRequestDto.nome() + " ja cadastrado");
        }
    }


}


