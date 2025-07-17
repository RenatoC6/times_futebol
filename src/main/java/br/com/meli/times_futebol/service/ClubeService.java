package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
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

    public ClubeResponseRetrospectivaDto buscaRetrospectivaClube(Long idValor) {

        String mensagem = "";
        Long vitorias = 0L;
        Long empates = 0L;
        Long derrotas = 0L;
        Long golsMarcados = 0L;
        Long golsSofridos = 0L;
        String nomeAdversario = "";

        ClubeModel clubeModel = acharTime(idValor);

       List<PartidaModel> listaPartidas =  partidaRepository.findByClubeMandanteOrClubeVisitante(clubeModel, clubeModel);

        if(listaPartidas.isEmpty()) {
            mensagem = "Nenhuma partida encontrada para o clube " + clubeModel.getNome() +  "\n" + "\n";
        }
        else {
            for (PartidaModel partida : listaPartidas) {
                mensagem = "Retrospectiva do Clube: " + clubeModel.getNome() + "\n" + "\n";
                if (partida.getClubeMandante().getId().equals(clubeModel.getId())) {
                    golsMarcados += partida.getGolsMandante();
                    golsSofridos += partida.getGolsVisitante();

                    if (partida.getGolsMandante() > partida.getGolsVisitante()) {
                        vitorias++;
                    } else if (partida.getGolsMandante() < partida.getGolsVisitante()) {
                        derrotas++;
                    } else {
                        empates++;
                    }
                } else {
                    golsMarcados += partida.getGolsVisitante();
                    golsSofridos += partida.getGolsMandante();

                    if (partida.getGolsVisitante() > partida.getGolsMandante()) {
                        vitorias++;
                    } else if (partida.getGolsVisitante() < partida.getGolsMandante()) {
                        derrotas++;
                    } else {
                        empates++;
                    }
                }
            }
        }

        return new ClubeResponseRetrospectivaDto(mensagem,
                                                clubeModel.getNome(),
                                               nomeAdversario,
                                               vitorias,
                                               empates,
                                               derrotas,
                                               golsMarcados,
                                               golsSofridos);

    }

    public ClubeResponseRetrospectivaDto buscaRetrospectivaClubesContraAdversario(Long clube1, Long clube2) {

        ClubeModel clubeModel1 = acharTime(clube1);
        ClubeModel clubeModel2 = acharTime(clube2);

        if(clubeModel1.getId().equals(clubeModel2.getId())) {
            throw new GenericExceptionConflict("Os clubes nao podem ser iguais");
        }
        if(clubeModel1.getStatus() || clubeModel2.getStatus()) {
            throw new GenericExceptionConflict("Um dos clubes esta inativo");
        }
        String mensagem;
        Long vitorias = 0L;
        Long empates = 0L;
        Long derrotas = 0L;
        Long golsMarcados = 0L;
        Long golsSofridos = 0L;

        List<PartidaModel> listaPartidas = partidaRepository.findByClubeMandanteAndClubeVisitante(clubeModel1, clubeModel2);

        List<PartidaModel> listapartidas1 = partidaRepository.findByClubeMandanteAndClubeVisitante(clubeModel2, clubeModel1);

        listaPartidas.addAll(listapartidas1);

        if(listaPartidas.isEmpty()) {
            mensagem = "Nenhuma partida entre o clube " + clubeModel1.getNome() + " contra o clube: " + clubeModel2.getNome() + "\n" + "\n";
        }
        else {
            mensagem = "Retrospectiva do Clube " + clubeModel1.getNome() + " contra o clube: " + clubeModel2.getNome() + "\n" + "\n";
            for(PartidaModel partida : listaPartidas) {
                if(partida.getClubeMandante().getId().equals(clubeModel1.getId())) {
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

        }

        return new ClubeResponseRetrospectivaDto(mensagem,
                                                clubeModel1.getNome(),
                                               clubeModel2.getNome(),
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


