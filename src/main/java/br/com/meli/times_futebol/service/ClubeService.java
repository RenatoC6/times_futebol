package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeResponseDto;
import br.com.meli.times_futebol.enums.EstadoBr;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClubeService {

    @Autowired
    ClubeRepository clubeRepository;

    @Autowired
    PartidaRepository partidaRepository;

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

    public ClubeModel atualizarTime(Long idValor,  ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModel =  acharTime(idValor);

        validaNome(clubeRequestDto);
        validaEstado(clubeRequestDto);
        validaDataCriacao(clubeRequestDto);

        // valida se ja existe esse nome de clube na base, exceto se o nome nao foi alterado
        if(!clubeModel.getNome().equals(clubeRequestDto.nome())) {
            validaNomeExistente(clubeRequestDto);
        }

        clubeModel.setId(idValor);
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);

        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public ClubeModel inativaTime(Long idValor) {

        ClubeModel clubeModel = acharTime(idValor);
        clubeModel.setStatus(true);

        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public ClubeResponseDto buscaRetrospectivaClube(Long idValor) {

        Long vitorias = 0L;
        Long empates = 0L;
        Long derrotas = 0L;
        Long golsMarcados = 0L;
        Long golsSofridos = 0L;

        ClubeModel clubeModel = acharTime(idValor);

        if(clubeModel.getStatus()) {
            throw new GenericException("Clube: " + clubeModel.getNome() + " esta inativo");
        }

       List<PartidaModel> listaPartidas =  partidaRepository.findByClubeMandanteOrClubeVisitante(clubeModel, clubeModel);

        if(listaPartidas.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Clube: " + clubeModel.getNome() + " nao possui partidas registradas");
        }

        for(PartidaModel partida : listaPartidas) {

            if(partida.getClubeMandante().getId().equals(clubeModel.getId())) {
                golsMarcados += partida.getGolsMandante();
                golsSofridos += partida.getGolsVisitante();

                if(partida.getGolsMandante() > partida.getGolsVisitante()) {
                    vitorias++;
                } else if(partida.getGolsMandante() < partida.getGolsVisitante()) {
                    derrotas++;
                } else {
                    empates++;
                }
            } else {
                golsMarcados += partida.getGolsVisitante();
                golsSofridos += partida.getGolsMandante();

                if(partida.getGolsVisitante() > partida.getGolsMandante()) {
                    vitorias++;
                } else if(partida.getGolsVisitante() < partida.getGolsMandante()) {
                    derrotas++;
                } else {
                    empates++;
                }
            }
        }

        return new ClubeResponseDto(clubeModel.getNome(),
                                               vitorias,
                                               empates,
                                               derrotas,
                                               golsMarcados,
                                               golsSofridos);

    }


    // metodos validacao

    public void validaNome(ClubeRequestDto clubeRequestDto) {

        if(clubeRequestDto.nome().trim().length() < 3){
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


