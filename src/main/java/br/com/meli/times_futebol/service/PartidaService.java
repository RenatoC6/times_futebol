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

import java.time.LocalDateTime;

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
        validarDataPosteriorCriacaoClubesEConflitoHoras(partidaRequestDto, mandante, visitante);

        var partidaModel = new PartidaModel();
        BeanUtils.copyProperties(partidaRequestDto, partidaModel);
        partidaModel.setClubeMandante(mandante);
        partidaModel.setClubeVisitante(visitante);
        partidaModel.setEstadioPartida(estadioPartida);

        partidaRepository.save(partidaModel);

        return partidaModel;
    }

    public PartidaModel atualizarPartida(PartidaRequestDto partidaRequestDto,Long idPartida) {

        PartidaModel partidaExistente = acharPartida(idPartida);

        ClubeModel mandante = buscarClube(partidaRequestDto.clubeMandante(), "mandante");
        ClubeModel visitante = buscarClube(partidaRequestDto.clubeVisitante(), "visitante");
        EstadioModel estadioPartida = buscarEstadio(partidaRequestDto.estadioPartida());

        validaClubeAtivo(mandante, visitante);
        validaGols(partidaRequestDto);
        validaClubesIguais(partidaRequestDto);
        validaDataPatidaFutura(partidaRequestDto);

       if(!partidaExistente.getId().equals(idPartida)) {
            validarDataPosteriorCriacaoClubesEConflitoHoras(partidaRequestDto, mandante, visitante);
            validaEstadioOcupadonaDataPartida(estadioPartida, partidaRequestDto);
            validarPartidaDuplicada(mandante, visitante, estadioPartida);
       }

        BeanUtils.copyProperties(partidaRequestDto, partidaExistente);
        partidaExistente.setClubeMandante(mandante);
        partidaExistente.setClubeVisitante(visitante);
        partidaExistente.setEstadioPartida(estadioPartida);
        partidaExistente.setId(idPartida);

        partidaRepository.save(partidaExistente);

        return partidaExistente;
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

    public ClubeModel buscarClube(Long clubeid, String tipo) {
        return clubeRepository.findById(clubeid)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube " + tipo + " não encontrado"));
    }

    public EstadioModel buscarEstadio(Long estadioid) {
        return estadioRepository.findById(estadioid)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Estádio não encontrado"));
    }

    // Verifica se os clubes estão ativos (0 (false) = ativo, 1 (true) = inativo)
    public void validaClubeAtivo(ClubeModel mandante,ClubeModel visitante) {
        if (mandante.isAtivo() || visitante.isAtivo()) {
            throw new GenericExceptionConflict("Clube mandante ou visitante não está ativo");
        }
    }

    public void validaGols(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.golsMandante() < 0 || partidaRequestDto.golsVisitante() < 0) {
            throw new GenericException("Número de gols não pode ser negativo");
        }
    }

    public void validaClubesIguais(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.clubeMandante().equals(partidaRequestDto.clubeVisitante())) {
            throw new GenericException("Clube mandante e visitante não podem ser o mesmo");
        }
    }

    public void validaDataPatidaFutura(PartidaRequestDto partidaRequestDto) {
        if (partidaRequestDto.dataPartida() == null || partidaRequestDto.dataPartida().isAfter(LocalDateTime.now())) {
            throw new GenericException("Data da partida inválida ou no futuro");
        }
    }

    public void validaEstadioOcupadonaDataPartida(EstadioModel estadioPartida, PartidaRequestDto partidaRequestDto) {
        if (partidaRepository.existsByEstadioPartidaAndDataPartida(estadioPartida, partidaRequestDto.dataPartida())) {
            throw new GenericExceptionConflict("Estádio já está ocupado na data da partida");
        }
    }

    public void validarPartidaDuplicada(ClubeModel mandante, ClubeModel visitante, EstadioModel estadio) {
        if (partidaRepository.existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(mandante, visitante, estadio)) {
            throw new GenericExceptionConflict("Já existe uma partida com mandante " +
                    mandante.getNome() + ", visitante " + visitante.getNome() +
                    " e estádio " + estadio.getNomeEstadio());
        }
    }

    public void validarDataPosteriorCriacaoClubesEConflitoHoras(PartidaRequestDto partidaRequestDto, ClubeModel mandante, ClubeModel visitante) {
        LocalDateTime dataPartida = partidaRequestDto.dataPartida();

        if (dataPartida.toLocalDate().isBefore(mandante.getDataCriacao())) {
            throw new GenericExceptionConflict("Data da partida anterior à criação do clube mandante: " + mandante.getNome());
        }
        if (dataPartida.toLocalDate().isBefore(visitante.getDataCriacao())) {
            throw new GenericExceptionConflict("Data da partida anterior à criação do clube visitante: " + visitante.getNome());
        }

        // Validação de partidas com diferença menor que 48 horas para os clubes
        long intervalo = 48; // horas
        LocalDateTime iniciointervalo = dataPartida.minusHours(intervalo);
        LocalDateTime fimIntervalo = dataPartida.plusHours(intervalo);

        boolean existeMandante_ComConflito = partidaRepository.existsByClubeMandante_AndDataPartidaBetween(
                mandante, iniciointervalo, fimIntervalo
        );
        if (existeMandante_ComConflito) {
            throw new GenericExceptionConflict("O clube " + mandante.getNome() +
                    " já possui uma partida marcada em menos de 48h em relação à data informada.");
        }

        boolean existeVisitanteComConflito = partidaRepository.existsByClubeVisitante_AndDataPartidaBetween(
                visitante, iniciointervalo, fimIntervalo
        );
        if (existeVisitanteComConflito) {
            throw new GenericExceptionConflict("O clube " + visitante.getNome() +
                    " já possui uma partida marcada em menos de 48h em relação à data informada.");
        }


    }

}
