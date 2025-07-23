package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.repository.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClubeServiceTest {

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private ClubeModel clubeModel;
    private PartidaModel partidaModel;
    private List<PartidaModel> listaPartidas;

    @InjectMocks
    private ClubeService clubeService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
    public void testeDeveCriarClubeComDadosValidos(){
    // Arrange
       ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,1,1), false);
       when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);
    // Act
       ClubeModel cluberetornado = clubeService.criarTime(clubeRequestDto);
    // Assertion
       assertEquals("teste", cluberetornado.getNome());
       assertEquals("SP", cluberetornado.getEstado());
       assertEquals(LocalDate.of(2025, 1, 1), cluberetornado.getDataCriacao());
       verify(clubeRepository).save(any());
    }

    @Test
    public void testeDeveAtualizarClubeComDadosValidos(){

        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,1,1), false);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);

        ClubeModel clubeModel = montarClubeModelParaTestes(1L, "teste", "SP", LocalDate.of(2025, 1, 1), false);
        when(clubeRepository.findById(clubeModel.getId())).thenReturn(Optional.of(clubeModel));

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
    public void testeDeveInativarClube(){

        ClubeModel clubeEsperado = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), true);
        when(clubeRepository.findById(clubeEsperado.getId())).thenReturn(Optional.of(clubeEsperado));

        ClubeModel clubeRetornado = clubeService.inativaTime(clubeEsperado.getId());

        assertEquals(true, clubeRetornado.getStatus());
        verify(clubeRepository).save(any());

    }

    @Test
    void listarTodosTimesDeveRetornarPageComTimes() {
        Pageable pageable = PageRequest.of(0, 10);

        ClubeModel clube1 = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);

        ClubeModel clube2 = montarClubeModelParaTestes(2L, "Time2", "SP", LocalDate.of(2025, 1, 1), false);

        List<ClubeModel> clubes = List.of(clube1, clube2);
        Page<ClubeModel> page = new PageImpl<>(clubes, pageable, clubes.size());

        when(clubeRepository.findAll(pageable)).thenReturn(page);

        Page<ClubeModel> resultado = clubeService.listarTodosTimes(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Time1", resultado.getContent().get(0).getNome());
        assertEquals("Time2", resultado.getContent().get(1).getNome());
        verify(clubeRepository).findAll(pageable);
    }


    @Test
    void testeLancarExceptionQuandoNomeMenorQue3Caracteres() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("A", "SP", LocalDate.now(), true);
        Exception ex = assertThrows(GenericException.class, () -> clubeService.validaNome(clubeRequestDto));
        assertTrue(ex.getMessage().contains("no minimo 3 caracteres"));
    }


    @Test
    void testeLancarExceptionQuandoEstadoInvalido() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "xx", LocalDate.now(), false);
        Exception ex = assertThrows(GenericException.class, () -> clubeService.validaEstado(clubeRequestDto));
        assertTrue(ex.getMessage().contains("invalido"));
    }

    @Test
    void testeLancarExceptionQuandoDataCriacaoNoFuturo() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,12,1), true);
        Exception ex = assertThrows(GenericException.class,
                () -> clubeService.validaDataCriacao(clubeRequestDto));
        assertTrue(ex.getMessage().contains("data de criacao"));
    }

    @Test
    void testeLancarExceptionQuandoDataCriacaoNull() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", null, true);
        Exception ex = assertThrows(GenericException.class,
                () -> clubeService.validaDataCriacao(clubeRequestDto));
        assertTrue(ex.getMessage().contains("data de criacao"));
    }

    @Test
    void testeLancarExceptionQuandoNomeJaExistente() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("paulista", "SP", LocalDate.now(), true);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(true);
        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> clubeService.validaNomeExistente(clubeRequestDto));
        assertTrue(ex.getMessage().contains(" ja cadastrado"));
    }
    @Test
   void testeDeveRetornarMensagemQuandoListaPartidasVaziaNaBuscaRestropectiva() {

        ClubeModel clube = montarClubeModelParaTestes(1L, "Time1", "SP", LocalDate.of(2025, 1, 1), false);

        when(clubeRepository.findById(clube.getId())).thenReturn(Optional.of(clube));
        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clube,clube)).thenReturn(Collections.emptyList());

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

        when(partidaRepository.findByClubeMandanteOrClubeVisitante(clubeMandante,clubeVisitante)).thenReturn(Collections.emptyList());

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(), clubeVisitante.getId());

        assertEquals( "Nenhuma partida entre o clube " + clubeMandante.getNome() + " contra o clube: " + clubeVisitante.getNome(), clubeResponseRetrospectivaDto.mensagem());
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

        when(partidaRepository.findByClubeMandanteAndClubeVisitante(clubeMandante, clubeVisitante))
                .thenReturn(new ArrayList<>(Arrays.asList(partida1, partida2)));

        when(partidaRepository.findByClubeMandanteAndClubeVisitante(clubeVisitante, clubeMandante))
                .thenReturn(new ArrayList<>(Arrays.asList(partida3, partida4)));

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClubesContraAdversario(clubeMandante.getId(),clubeVisitante.getId());

        assertEquals("Time1", clubeResponseRetrospectivaDto.nome());
        assertEquals("Time2", clubeResponseRetrospectivaDto.nomeAdversario());
        assertEquals(2L, clubeResponseRetrospectivaDto.vitorias());
        assertEquals(1L, clubeResponseRetrospectivaDto.empates());
        assertEquals(1L, clubeResponseRetrospectivaDto.derrotas());
        assertEquals(7L, clubeResponseRetrospectivaDto.golsMarcados());
        assertEquals(5, clubeResponseRetrospectivaDto.golsSofridos());

    }

    public ClubeModel montarClubeModelParaTestes(Long id, String nome, String estado, LocalDate dataCriacao, boolean status) {

        clubeModel = new ClubeModel();
        clubeModel.setId(id);
        clubeModel.setNome(nome);
        clubeModel.setEstado(estado);
        clubeModel.setDataCriacao(dataCriacao);
        clubeModel.setStatus(status);

        return  clubeModel;
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
