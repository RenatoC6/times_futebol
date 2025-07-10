package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.PartidaRequestDto;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.EstadioRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartidaService {

    @Autowired
    private ClubeRepository clubeRepository;
    @Autowired
    private EstadioRepository estadioRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    public PartidaModel criarPartida(PartidaRequestDto partidaRequestDto) {

        //verifica se os clubes e o estádio existem
        ClubeModel clubeModelMandante = clubeRepository.findById(partidaRequestDto.clubeMandante())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube mandante não encontrado"));
        ClubeModel clubeModelVisitante = clubeRepository.findById(partidaRequestDto.clubeVisitante())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube visitante não encontrado"));
        EstadioModel estadioPartida = estadioRepository.findById(partidaRequestDto.estadioPartida())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Estádio não encontrado"));

        // Verifica se já existe uma partida com o mesmo clube mandante, visitante e estádio
        if (partidaRepository.existsByClubeMandante(clubeModelMandante)) {
            throw new GenericException("Já existe uma partida com o clube mandante: " + clubeModelMandante.getNome());
        }
        if (partidaRepository.existsByClubeVisitante(clubeModelVisitante)) {
            throw new GenericException("Já existe uma partida com o clube visitante: " + clubeModelVisitante.getNome());
        }
        if (partidaRepository.existsByEstadioPartida(estadioPartida)) {
            throw new GenericException("Já existe uma partida no estádio: " + estadioPartida.getNomeEstadio());
        }
        var partidaModel = new PartidaModel();
        // Copia as propriedades do DTO para o modelo de partida(gols e dataPatida)
        BeanUtils.copyProperties(partidaRequestDto, partidaModel);
        // Define os clubes e o estádio na partida
        partidaModel.setClubeMandante(clubeModelMandante);
        partidaModel.setClubeVisitante(clubeModelVisitante);
        partidaModel.setEstadioPartida(estadioPartida);

        // Salva a partida no repositório
        partidaRepository.save(partidaModel);

        return partidaModel;
    }




}
