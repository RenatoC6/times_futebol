package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.dto.ClubeResponseRankingDto;
import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import br.com.meli.times_futebol.specification.ClubeSpecification;
import br.com.meli.times_futebol.validator.clube.ValidaDataCriacao;
import br.com.meli.times_futebol.validator.clube.ValidaEstado;
import br.com.meli.times_futebol.validator.clube.ValidaNome;
import br.com.meli.times_futebol.validator.clube.ValidaNomeExistente;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ClubeService {

    @Autowired
    ClubeRepository clubeRepository;

    @Autowired
    PartidaRepository partidaRepository;

    @Autowired
    private ValidaNome validaNome;
    @Autowired
    private ValidaEstado validaEstado;
    @Autowired
    private ValidaDataCriacao validaDataCriacao;
    @Autowired
    private ValidaNomeExistente validaNomeExistente;


    public ClubeModel criarTime(ClubeRequestDto clubeRequestDto) {

        validaNome.validar(clubeRequestDto);
        validaEstado.validar(clubeRequestDto);
        validaDataCriacao.validar(clubeRequestDto);
        validaNomeExistente.validar(clubeRequestDto);

        var clubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }


    public Page<ClubeModel> listarTodosTimes(int page, int size, String[] sort, String nome, String estado, boolean status) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sortOrder = Sort.by(direction, sort[0]);

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Specification<ClubeModel> specs = null;

        if (nome != null && !nome.isEmpty()) {
            specs = ClubeSpecification.porNome(nome);
        }
        if (estado != null && !estado.isEmpty()) {
            specs = specs == null ? ClubeSpecification.porEstado(estado) : specs.or(ClubeSpecification.porEstado(estado));
        }
        if (!status) {
            specs = specs == null ? ClubeSpecification.porStatus(status) : specs.or(ClubeSpecification.porStatus(status));
        }

        return clubeRepository.findAll(specs, pageable);

    }


    public ClubeModel acharTime(Long idValor) {

        return clubeRepository.findById(idValor)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Time: " + idValor + " nao encontrado"));

    }

    public ClubeModel atualizarTime(Long idValor, ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModel = acharTime(idValor);

        validaNome.validar(clubeRequestDto);
        validaEstado.validar(clubeRequestDto);
        validaDataCriacao.validar(clubeRequestDto);

        // valida se ja existe esse nome de clube na base, exceto se o nome nao foi alterado
        if (!clubeModel.getNome().equals(clubeRequestDto.nome())) {
            validaNomeExistente.validar(clubeRequestDto);
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

        String mensagem;
        Long vitorias = 0L;
        Long empates = 0L;
        Long derrotas = 0L;
        Long golsMarcados = 0L;
        Long golsSofridos = 0L;
        String nomeAdversario;

        ClubeModel clubeModel = acharTime(idValor);

        List<PartidaModel> listaPartidas = partidaRepository.findByClubeMandanteOrClubeVisitante(clubeModel, clubeModel);

        if (listaPartidas.isEmpty()) {
            mensagem = "Nenhuma partida encontrada para o clube " + clubeModel.getNome();
        } else {
            mensagem = "Retrospectiva do Clube: " + clubeModel.getNome();
            for (PartidaModel partida : listaPartidas) {

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

        nomeAdversario = "qualquer time";
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

        if (clubeModel1.getId().equals(clubeModel2.getId())) {
            throw new GenericExceptionConflict("Os clubes nao podem ser iguais");
        }
        if (clubeModel1.getStatus() || clubeModel2.getStatus()) {
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

        if (listaPartidas.isEmpty()) {
            mensagem = "Nenhuma partida entre o clube " + clubeModel1.getNome() + " contra o clube: " + clubeModel2.getNome();
        } else {
            mensagem = "Retrospectiva do Clube " + clubeModel1.getNome() + " contra o clube: " + clubeModel2.getNome();
            for (PartidaModel partida : listaPartidas) {
                if (partida.getClubeMandante().getId().equals(clubeModel1.getId())) {
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
                clubeModel1.getNome(),
                clubeModel2.getNome(),
                vitorias,
                empates,
                derrotas,
                golsMarcados,
                golsSofridos);
    }

    public List<ClubeResponseRankingDto> listarRankingClubes(String tipo) {

        if (!tipo.equalsIgnoreCase("vitorias") && !tipo.equalsIgnoreCase("gols")
                && !tipo.equalsIgnoreCase("jogos")) {
            throw new GenericException("Tipo inválido. O Total de pontos é default e Use: 'gols' / vitorias ou Jogos, para compor o segundo processo de classificacao");
        }

        String mensagem = "Ranking de clubes por pontos e: " + tipo;
        long pontos;
        long gols;
        long vitorias;
        long jogos;

        List<ClubeModel> listaClubes = clubeRepository.findAll();

        List<ClubeResponseRankingDto> rankingClubes = new ArrayList<>();

        if (listaClubes.isEmpty()) {
            throw new GenericExceptionConflict("Nenhum clube cadastrado para calcular o ranking");
        }

        for (ClubeModel clube : listaClubes) {

            ClubeModel clubeModel = acharTime(clube.getId());
            List<PartidaModel> listaPartidas = partidaRepository.findByClubeMandanteOrClubeVisitante(clubeModel, clubeModel);
            pontos = 0L;
            vitorias = 0L;
            gols = 0L;
            jogos = 0L;
            if (!listaPartidas.isEmpty()) {

                for (PartidaModel partida : listaPartidas) {

                    if (partida.getClubeMandante().getId().equals(clubeModel.getId())) {
                        gols += partida.getGolsMandante();
                        if (partida.getGolsMandante() > partida.getGolsVisitante()) {
                            pontos += 3; // vitoria
                            vitorias++;
                        } else if (partida.getGolsMandante().equals(partida.getGolsVisitante())) {
                            pontos += 1;  // empate
                        }

                    } else if (partida.getClubeVisitante().getId().equals(clubeModel.getId())) {
                        gols += partida.getGolsVisitante();
                        if (partida.getGolsVisitante() > partida.getGolsMandante()) {
                            pontos += 3; // vitoria
                            vitorias++;
                        } else if (partida.getGolsVisitante().equals(partida.getGolsMandante())) {
                            pontos += 1; // empate
                        }

                    }

                    jogos++;
                }

            }

            rankingClubes.add(new ClubeResponseRankingDto(mensagem, clubeModel.getNome(), pontos, gols, vitorias, jogos));
        }


        Comparator<ClubeResponseRankingDto> primary = Comparator.comparingLong(ClubeResponseRankingDto::pontos).reversed();

        Comparator<ClubeResponseRankingDto> secondary;

        secondary = switch (tipo.toLowerCase()) {
            case "gols" -> Comparator.comparingLong(ClubeResponseRankingDto::gols).reversed();
            case "vitorias" -> Comparator.comparingLong(ClubeResponseRankingDto::vitorias).reversed();
            case "jogos" -> Comparator.comparingLong(ClubeResponseRankingDto::jogos).reversed();
            default ->
                    throw new GenericException("Tipo inválido. Use: 'gols' / vitorias ou Jogos, para compor o segundo processo de classificacao");
        };

        rankingClubes.sort(primary.thenComparing(secondary));

        return rankingClubes;
    }

    // metodos validacao

//    public void validaNome(ClubeRequestDto clubeRequestDto) {
//
//        if (clubeRequestDto.nome().trim().length() < 3) {
//            throw new GenericException("nome deve ter no minimo 3 caracteres");
//        }
//
//    }
//
//    public void validaEstado(ClubeRequestDto clubeRequestDto) {
//        if (!EstadoBr.validaEstadoBr(clubeRequestDto.estado())) {
//            throw new GenericException("Estado: " + clubeRequestDto.estado() + " invalido");
//        }
//    }
//
//    public void validaDataCriacao(ClubeRequestDto clubeRequestDto) {
//
//        LocalDate dataCriacao = clubeRequestDto.dataCriacao();
//
//        if (dataCriacao == null || dataCriacao.isAfter(LocalDate.now())) {
//            throw new GenericException("data de criacao invalido ou no futuro");
//        }
//    }
//
//    public void validaNomeExistente(ClubeRequestDto clubeRequestDto) {
//        if (clubeRepository.existsByNomeIgnoreCase(clubeRequestDto.nome().toUpperCase())) {
//            throw new GenericExceptionConflict("Nome : " + clubeRequestDto.nome() + " ja cadastrado");
//        }
//    }


}


