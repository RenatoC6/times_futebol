package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.dto.ClubeResponseRankingDto;
import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import br.com.meli.times_futebol.validator.clube.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ClubeServiceTest {

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private ClubeModel clubeModel;

    @Mock
    private PartidaModel partidaModel;

    @InjectMocks
    private ClubeService clubeService;

    @Mock
    private final ValidaNome validaNome = new ValidaNome();
    @Mock
    private final ValidaEstado validaEstado = new ValidaEstado();
    @Mock
    private final ValidaDataCriacao validaDataCriacao = new ValidaDataCriacao();
    @Mock
    private final ValidaNomeExistente validaNomeExistente = new ValidaNomeExistente();

    @Mock
    private List<ClubeValidator> validators;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validators = Arrays.asList(validaNome, validaEstado, validaDataCriacao, validaNomeExistente);

    }

    @Test
    public void testeDeveCriarClubeComDadosValidos() {

        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);

        validators.forEach(v -> doNothing().when(v).validar(clubeRequestDto, null));

        ClubeModel cluberetornado = clubeService.criarTime(clubeRequestDto);

        assertEquals("teste", cluberetornado.getNome());
        assertEquals("SP", cluberetornado.getEstado());
        assertEquals(LocalDate.of(2025, 1, 1), cluberetornado.getDataCriacao());
        verify(clubeRepository).save(any());
    }

    @Test
    public void testeDeveAtualizarClubeComDadosValidos() {

        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);

        ClubeModel clubeModel = montarClubeModelParaTestes(1L, "teste", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeModel.getId())).thenReturn(Optional.of(clubeModel));

        validators.forEach(v -> doNothing().when(v).validar(clubeRequestDto, null));
        ClubeModel clubeModelAtlz = clubeService.atualizarTime(clubeModel.getId(), clubeRequestDto);

        assertEquals(clubeRequestDto.nome(), clubeModelAtlz.getNome());
        assertEquals(clubeRequestDto.estado(), clubeModelAtlz.getEstado());
        verify(clubeRepository).save(any());
    }

    @Test
    public void testeDeveRetornarClubeQuandoExiste() {
        // Arrange
        ClubeModel clubeEsperado = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);

        when(clubeRepository.findById(clubeEsperado.getId())).thenReturn(Optional.of(clubeEsperado));

        // Act
        ClubeModel clubeRetornado = clubeService.acharTime(clubeEsperado.getId());

        // Assert
        assertEquals(clubeEsperado.getId(), clubeRetornado.getId());
        verify(clubeRepository).findById(clubeRetornado.getId());

    }

    @Test
    public void testeDeveInativarClube() {

        ClubeModel clubeEsperado = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), true);
        when(clubeRepository.findById(clubeEsperado.getId())).thenReturn(Optional.of(clubeEsperado));

        ClubeModel clubeRetornado = clubeService.inativaTime(clubeEsperado.getId());

        assertEquals(true, clubeRetornado.getStatus());
        verify(clubeRepository).save(any());

    }

    @Test
    void listarTodosTimesDeveRetornarPageComTimes() {
        String[] sort = new String[]{"nome", "asc"};
        int page = 0;
        int size = 10;
        String nome = "Time1";
        String estado = "SP";
        boolean status = false;

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortOrder = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        ClubeModel clube1 = montarClubeModelParaTestes(1L, nome, estado, LocalDate.of(2025, 1, 1), false);

        List<ClubeModel> clubes = List.of(clube1);
        Page<ClubeModel> pageResult = new PageImpl<>(clubes, pageable, clubes.size());

        when(clubeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageResult);

        Page<ClubeModel> resultado = clubeService.listarTodosTimes(page, size, sort, nome, estado, status);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Time1", resultado.getContent().get(0).getNome());

    }


    @Test
    void testeDeveRetornarMensagemQuandoListaPartidasVaziaNaBuscaRestropectiva() {

        ClubeModel clube = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);

        when(clubeRepository.findById(clube.getId())).thenReturn(Optional.of(clube));
        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clube, clube)).thenReturn(Collections.emptyList());

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClube(clube.getId());

        assertEquals("Nenhuma partida encontrada para o clube Time1", clubeResponseRetrospectivaDto.mensagem());
        assertEquals(0L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(0L, clubeResponseRetrospectivaDto.empates());
        assertEquals(0L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(0L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(0L, clubeResponseRetrospectivaDto.golsSofridos());
    }

    @Test
    void testeDeveRetornarRetrospectivaClubeMandanteComDadosValidos() {

        ClubeModel clube1 = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clube1.getId())).thenReturn(Optional.of(clube1));

        PartidaModel partida1 = montarPartidaModelParaTestes(1L, clube1, clube1, 3L, 1L);

        PartidaModel partida2 = montarPartidaModelParaTestes(2L, clube1, clube1, 1L, 1L);

        PartidaModel partida3 = montarPartidaModelParaTestes(3L, clube1, clube1, 1L, 2L);

        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clube1, clube1))
                .thenReturn(List.of(partida1, partida2, partida3));

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClube(clube1.getId());

        assertEquals("Time1", clubeResponseRetrospectivaDto.nome());
        assertEquals(1L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(1L, clubeResponseRetrospectivaDto.empates());
        assertEquals(1L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(5L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(4L, clubeResponseRetrospectivaDto.golsSofridos());
    }

    @Test
    void testeDeveRetornarRetrospectivaClubeVisitanteComDadosValidos() {

        ClubeModel clubeMandante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeMandante.getId())).thenReturn(Optional.of(clubeMandante));

        ClubeModel clubeVisitante = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeVisitante.getId())).thenReturn(Optional.of(clubeVisitante));

        PartidaModel partida1 = montarPartidaModelParaTestes(1L, clubeMandante, clubeVisitante, 3L, 1L);

        PartidaModel partida2 = montarPartidaModelParaTestes(2L, clubeMandante, clubeVisitante, 1L, 1L);

        PartidaModel partida3 = montarPartidaModelParaTestes(3L, clubeMandante, clubeVisitante, 1L, 2L);

        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clubeVisitante, clubeVisitante))
                .thenReturn(List.of(partida1, partida2, partida3));

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClube(clubeVisitante.getId());

        assertEquals("Time2", clubeResponseRetrospectivaDto.nome());
        assertEquals(1L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(1L, clubeResponseRetrospectivaDto.empates());
        assertEquals(1L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(4L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(5L, clubeResponseRetrospectivaDto.golsSofridos());
    }

    @Test
    void testeDeveRetornarMensagemQuandoListaPartidasVaziaNaBuscaRestropectivaEntreDoisTimes() {

        ClubeModel clubeMandante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeMandante.getId())).thenReturn(Optional.of(clubeMandante));

        ClubeModel clubeVisitante = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeVisitante.getId())).thenReturn(Optional.of(clubeVisitante));

        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clubeMandante, clubeVisitante)).thenReturn(Collections.emptyList());

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(), clubeVisitante.getId());

        assertEquals("Nenhuma partida entre o clube " + clubeMandante.getNome() + " contra o clube: " + clubeVisitante.getNome(), clubeResponseRetrospectivaDto.mensagem());
        assertEquals(0L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(0L, clubeResponseRetrospectivaDto.empates());
        assertEquals(0L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(0L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(0L, clubeResponseRetrospectivaDto.golsSofridos());
    }

    @Test
    void testeDeveRetornarRetrospectivaEntreDoisClubesComDadosValidos() {

        ClubeModel clubeMandante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeMandante.getId())).thenReturn(Optional.of(clubeMandante));

        ClubeModel clubeVisitante = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeVisitante.getId())).thenReturn(Optional.of(clubeVisitante));

        PartidaModel partida1 = montarPartidaModelParaTestes(1L, clubeMandante, clubeVisitante, 3L, 1L);
        PartidaModel partida2 = montarPartidaModelParaTestes(2L, clubeMandante, clubeVisitante, 1L, 1L);
        PartidaModel partida3 = montarPartidaModelParaTestes(3L, clubeVisitante, clubeMandante, 1L, 2L);
        PartidaModel partida4 = montarPartidaModelParaTestes(4L, clubeVisitante, clubeMandante, 2L, 1L);
        PartidaModel partida5 = montarPartidaModelParaTestes(5L, clubeVisitante, clubeMandante, 1L, 1L);
        PartidaModel partida6 = montarPartidaModelParaTestes(2L, clubeMandante, clubeVisitante, 0L, 1L);

        when(partidaRepository.findByClubeMandanteAndClubeVisitante(clubeMandante, clubeVisitante))
                .thenReturn(new ArrayList<>(Arrays.asList(partida1, partida2, partida6)));

        when(partidaRepository.findByClubeMandanteAndClubeVisitante(clubeVisitante, clubeMandante))
                .thenReturn(new ArrayList<>(Arrays.asList(partida3, partida4, partida5)));

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(), clubeVisitante.getId());

        assertEquals("Time1", clubeResponseRetrospectivaDto.nome());
        assertEquals("Time2", clubeResponseRetrospectivaDto.nomeAdversario());
        assertEquals(2L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(2L, clubeResponseRetrospectivaDto.empates());
        assertEquals(2L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(8L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(7, clubeResponseRetrospectivaDto.golsSofridos());

    }

    @Test
    void testeDeveRetornarExececaoRetrospectivaEntreDoisClubesQuandoClubesIguais() {

        ClubeModel clubeMandante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeMandante.getId())).thenReturn(Optional.of(clubeMandante));

        ClubeModel clubeVisitante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeVisitante.getId())).thenReturn(Optional.of(clubeVisitante));

        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(), clubeVisitante.getId()));
        assertTrue(ex.getMessage().contains("Os clubes nao podem ser iguais"));
    }

    @Test
    void testeDeveRetornarExececaoRetrospectivaEntreDoisClubesQuandoStatusInativoParaQualquerClube() {

        ClubeModel clubeMandante = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeMandante.getId())).thenReturn(Optional.of(clubeMandante));

        ClubeModel clubeVisitante = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), true);
        when(clubeRepository.findById(clubeVisitante.getId())).thenReturn(Optional.of(clubeVisitante));

        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(), clubeVisitante.getId()));
        assertTrue(ex.getMessage().contains("Um dos clubes esta inativo"));
    }


    @Test
    void testeDeveRetornarRankingDeClubes() {

        ClubeModel clube1 = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clube1.getId())).thenReturn(Optional.of(clube1));

        ClubeModel clube2 = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clube2.getId())).thenReturn(Optional.of(clube2));

        List<ClubeModel> clubes = Arrays.asList(clube1, clube2);

        when(clubeRepository.findAll()).thenReturn(clubes);

        PartidaModel partida1 = montarPartidaModelParaTestes(1L, clube1, clube2, 3L, 1L);
        PartidaModel partida2 = montarPartidaModelParaTestes(2L, clube1, clube2, 1L, 1L);
        PartidaModel partida3 = montarPartidaModelParaTestes(3L, clube2, clube1, 1L, 2L);
        PartidaModel partida4 = montarPartidaModelParaTestes(4L, clube2, clube1, 2L, 1L);

        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clube1, clube1))
                .thenReturn(new ArrayList<>(Arrays.asList(partida1, partida2, partida3, partida4)));

        List<ClubeResponseRankingDto> ranking = clubeService.listarRankingClubes("gols");

        assertEquals(2, ranking.size());
        assertEquals("Time1", ranking.get(0).nome());
        assertEquals("Time2", ranking.get(1).nome());

    }

    // Métodos auxiliares para criar objetos de teste

    public ClubeModel montarClubeModelParaTestes(Long id, String nome, String estado, LocalDate dataCriacao, boolean status) {

        clubeModel = new ClubeModel();
        clubeModel.setId(id);
        clubeModel.setNome(nome);
        clubeModel.setEstado(estado);
        clubeModel.setDataCriacao(dataCriacao);
        clubeModel.setStatus(status);

        return clubeModel;
    }

    public PartidaModel montarPartidaModelParaTestes(Long id, ClubeModel clubeMandante, ClubeModel clubeVisitante, Long golsMandante, Long golsVisitante) {
        partidaModel = new PartidaModel();
        partidaModel.setId(id);
        partidaModel.setClubeMandante(clubeMandante);
        partidaModel.setClubeVisitante(clubeVisitante);
        partidaModel.setGolsMandante(golsMandante);
        partidaModel.setGolsVisitante(golsVisitante);

        return partidaModel;
    }
}
