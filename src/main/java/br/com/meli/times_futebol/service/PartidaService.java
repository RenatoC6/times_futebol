package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.PartidaRequestDto;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.EstadioRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.LocalDate;

@Service
public class PartidaService {

    @Autowired
    private ClubeRepository clubeRepository;
    @Autowired
    private EstadioRepository estadioRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    public PartidaModel criarPartida(PartidaRequestDto partidaRequestDto) {

        ClubeModel mandante = buscarClube(partidaRequestDto.clubeMandante(), "mandante");
        ClubeModel visitante = buscarClube(partidaRequestDto.clubeVisitante(), "visitante");
        EstadioModel estadioPartida = buscarEstadio(partidaRequestDto.estadioPartida());

        validaClubeAtivo(mandante, visitante);
        validaGols(partidaRequestDto);
        validaClubesIguais(partidaRequestDto);
        validaDataPatidaFutura(partidaRequestDto);
        validaEstadioOcupadonaDataPartida(estadioPartida, partidaRequestDto);
        validarPartidaDuplicada(mandante, visitante, estadioPartida);
        validarDataPosteriorCriacaoClubes(partidaRequestDto, mandante, visitante);

        var partidaModel = new PartidaModel();
        BeanUtils.copyProperties(partidaRequestDto, partidaModel);
        partidaModel.setClubeMandante(mandante);
        partidaModel.setClubeVisitante(visitante);
        partidaModel.setEstadioPartida(estadioPartida);

        partidaRepository.save(partidaModel);

        return partidaModel;
    }

    public PartidaModel atualizarPartida(PartidaModel partidaExistente, PartidaRequestDto partidaRequestDto,Long idPartida) {

        ClubeModel mandante = buscarClube(partidaRequestDto.clubeMandante(), "mandante");
        ClubeModel visitante = buscarClube(partidaRequestDto.clubeVisitante(), "visitante");
        EstadioModel estadioPartida = buscarEstadio(partidaRequestDto.estadioPartida());

        validaClubeAtivo(mandante, visitante);
        validaGols(partidaRequestDto);
        validaClubesIguais(partidaRequestDto);
        validaDataPatidaFutura(partidaRequestDto);
        validarDataPosteriorCriacaoClubes(partidaRequestDto, mandante, visitante);
       if(!partidaExistente.getId().equals(idPartida)) {
            validaEstadioOcupadonaDataPartida(estadioPartida, partidaRequestDto);
            validarPartidaDuplicada(mandante, visitante, estadioPartida);
       }


        BeanUtils.copyProperties(partidaRequestDto, partidaExistente, "id");
        partidaExistente.setClubeMandante(mandante);
        partidaExistente.setClubeVisitante(visitante);
        partidaExistente.setEstadioPartida(estadioPartida);

        return partidaRepository.save(partidaExistente);
    }


    public PartidaModel acharPartida(Long idValor) {

        return partidaRepository.findById(idValor)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partida: "+ idValor +  " nao encontrada"));

    }

    public Page<PartidaModel> listarTodasPartidas(int page, int size, String[] sort) {

        // Criando objeto Sort
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortOrder = Sort.by(direction, sort[0]);

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        return partidaRepository.findAll(pageable);
    }

// metdodos de validação

    private ClubeModel buscarClube(Long clubeid, String tipo) {
        return clubeRepository.findById(clubeid)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube " + tipo + " não encontrado"));
    }

    private EstadioModel buscarEstadio(Long estadioid) {
        return estadioRepository.findById(estadioid)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Estádio não encontrado"));
    }

    // Verifica se os clubes estão ativos (0 (false) = ativo, 1 (true) = inativo)
    private void validaClubeAtivo(ClubeModel mandante,ClubeModel visitante) {
        if (mandante.isAtivo() || visitante.isAtivo()) {
            throw new GenericExceptionConflict("Clube mandante ou visitante não está ativo");
        }
    }

    private void validaGols(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.golsMandante() < 0 || partidaRequestDto.golsVisitante() < 0) {
            throw new GenericException("Número de gols não pode ser negativo");
        }
    }

    private void validaClubesIguais(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.clubeMandante().equals(partidaRequestDto.clubeVisitante())) {
            throw new GenericException("Clube mandante e visitante não podem ser o mesmo");
        }
    }

    private void validaDataPatidaFutura(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.dataPartida() == null || partidaRequestDto.dataPartida().isAfter(java.time.LocalDate.now())) {
            throw new GenericException("Data da partida inválida ou no futuro");
        }
    }

    private void validaEstadioOcupadonaDataPartida(EstadioModel estadioPartida, PartidaRequestDto partidaRequestDto) {
        if (partidaRepository.existsByEstadioPartidaAndDataPartida(estadioPartida, partidaRequestDto.dataPartida())) {
            throw new GenericExceptionConflict("Estádio já está ocupado na data da partida");
        }
    }

    private void validarPartidaDuplicada(ClubeModel mandante, ClubeModel visitante, EstadioModel estadio) {
        if (partidaRepository.existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(mandante, visitante, estadio)) {
            throw new GenericExceptionConflict("Já existe uma partida com mandante " +
                    mandante.getNome() + ", visitante " + visitante.getNome() +
                    " e estádio " + estadio.getNomeEstadio());
        }
    }

    private void validarDataPosteriorCriacaoClubes(PartidaRequestDto partidaRequestDto, ClubeModel mandante, ClubeModel visitante) {
        LocalDate dataPartida = partidaRequestDto.dataPartida();
        if (dataPartida.isBefore(mandante.getDataCriacao())) {
            throw new GenericExceptionConflict("Data da partida anterior à criação do clube mandante: " + mandante.getNome());
        }
        if (dataPartida.isBefore(visitante.getDataCriacao())) {
            throw new GenericExceptionConflict("Data da partida anterior à criação do clube visitante: " + visitante.getNome());
        }
    }

}
