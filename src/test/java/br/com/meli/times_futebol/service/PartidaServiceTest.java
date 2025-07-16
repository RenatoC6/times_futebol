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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class PartidaServiceTest {

    @Mock
    private PartidaRepository partidaRepository;
    @Mock
    private ClubeRepository clubeRepository;
    @Mock
    private EstadioRepository estadioRepository;

    @InjectMocks
    private PartidaService partidaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testeDeveCriarPartidaComDadosValidados(){

        PartidaRequestDto dto = new PartidaRequestDto(7L,10L, 22L, 5L, 0L, LocalDateTime.now());
        ClubeModel mandante = new ClubeModel();
        mandante.setId(7L); mandante.setNome("Mandante"); mandante.setEstado("SP"); mandante.setStatus(false); mandante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(7L)).thenReturn(Optional.of(mandante));

        ClubeModel visitante = new ClubeModel();
        visitante.setId(10L); visitante.setNome("Visitante"); visitante.setEstado("RJ"); visitante.setStatus(false);  visitante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(visitante));

        EstadioModel estadio = new EstadioModel();
        estadio.setId(22L); estadio.setNomeEstadio("estadio 22");
        when(estadioRepository.findById(22L)).thenReturn(Optional.of(estadio));

        PartidaModel retornoPartida = partidaService.criarPartida(dto);

        assertNotNull(retornoPartida);
        assertEquals(mandante, retornoPartida.getClubeMandante());
        assertEquals(visitante, retornoPartida.getClubeVisitante());
        assertEquals(estadio, retornoPartida.getEstadioPartida());
        verify(partidaRepository).save(any());
    }

    @Test
    public void testAcharPartidaQuandoPartidaExiste() {
        // Arrange
        Long idValor = 1L;
        PartidaModel partidaEsperada = new PartidaModel();
        partidaEsperada.setId(idValor);
        when(partidaRepository.findById(idValor)).thenReturn(Optional.of(partidaEsperada));

        // Act
        PartidaModel partidaResultado = partidaService.acharPartida(idValor);

        // Assert
        assertEquals(partidaEsperada, partidaResultado);
        verify(partidaRepository).findById(idValor);
    }

    @Test
    public void testDeveGerarExececaoQuandoPartidaNaoExiste() {
        // Arrange
        Long idValor = 1L;
        when(partidaRepository.findById(idValor)).thenReturn(Optional.empty());

        // Act e Assert
        Exception ex = assertThrows(EntidadeNaoEncontradaException.class,
                () -> partidaService.acharPartida(idValor));

        assertTrue(ex.getMessage().contains("nao encontrada"));
    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeClubesIguais() {
        PartidaRequestDto dto = new PartidaRequestDto(7L, 7L, 23L, 5L, 1L, LocalDateTime.now());

        Exception ex = assertThrows(GenericException.class, () -> partidaService.validaClubesIguais(dto));

        assertTrue(ex.getMessage().contains("não podem ser o mesmo"));

    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeGolsMenorZeroMandante() {
        PartidaRequestDto dto = new PartidaRequestDto(7L, 10L, 23L, -5L, 1L, LocalDateTime.now());

        Exception ex = assertThrows(GenericException.class, () -> partidaService.validaGols(dto));

        assertTrue(ex.getMessage().contains("gols não pode ser negativo"));

    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeGolsMenorZeroVisitante() {
        PartidaRequestDto dto = new PartidaRequestDto(7L, 10L, 23L, 1L, -1L, LocalDateTime.now());

        Exception ex = assertThrows(GenericException.class, () -> partidaService.validaGols(dto));

        assertTrue(ex.getMessage().contains("gols não pode ser negativo"));

    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeClubeInativo() {

        ClubeModel mandante = new ClubeModel();
        mandante.setId(7L); mandante.setNome("Mandante"); mandante.setEstado("SP"); mandante.setStatus(false); mandante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(mandante));

        ClubeModel visitante = new ClubeModel();
        visitante.setId(10L); visitante.setNome("Visitante"); visitante.setEstado("RJ"); visitante.setStatus(true);  visitante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(visitante));

        Exception ex = assertThrows(GenericExceptionConflict.class, () -> partidaService.validaClubeAtivo(mandante, visitante));

        assertTrue(ex.getMessage().contains("não está ativo"));

    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeDataPartidaFutura() {
        PartidaRequestDto dto = new PartidaRequestDto(7L,10L, 22L, 5L, 0L, LocalDateTime.now().plusDays(1));

        Exception ex = assertThrows(GenericException.class, () -> partidaService.validaDataPatidaFutura(dto));

        assertTrue(ex.getMessage().contains("futuro"));
    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSeEstadioOcupadoNaDataPartida() {
        PartidaRequestDto dto = new PartidaRequestDto(7L,10L, 22L, 5L, 0L, LocalDateTime.now());
        EstadioModel estadio = new EstadioModel();
        estadio.setId(22L); estadio.setNomeEstadio("estadio 22");
        when(estadioRepository.findById(22L)).thenReturn(Optional.of(estadio));
        when(partidaRepository.existsByEstadioPartidaAndDataPartida(estadio, dto.dataPartida())).thenReturn(true);

        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> partidaService.validaEstadioOcupadonaDataPartida(estadio, dto));

        assertTrue(ex.getMessage().contains("ocupado"));
    }

    @Test
    public void testCriarPartidaDeveLancarExceptionSePartidaDuplicada() {

        ClubeModel mandante = new ClubeModel();
        mandante.setId(7L); mandante.setNome("Mandante"); mandante.setEstado("SP"); mandante.setStatus(false); mandante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(7L)).thenReturn(Optional.of(mandante));

        ClubeModel visitante = new ClubeModel();
        visitante.setId(10L); visitante.setNome("Visitante"); visitante.setEstado("RJ"); visitante.setStatus(false);  visitante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(visitante));

        EstadioModel estadio = new EstadioModel();
        estadio.setId(22L); estadio.setNomeEstadio("estadio 22");
        when(estadioRepository.findById(22L)).thenReturn(Optional.of(estadio));

        when(partidaRepository.existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(mandante, visitante, estadio)).
                thenReturn(true);

        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> partidaService.validarPartidaDuplicada(mandante, visitante, estadio));

        assertTrue(ex.getMessage().contains("Já existe uma partida"));
    }

    @Test
    public void testeDeveAlterarPartidaComDadosValidados(){

        PartidaRequestDto dto = new PartidaRequestDto(7L,10L, 22L, 5L, 0L, LocalDateTime.now());
        when(partidaRepository.existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(any(),any(),any())).thenReturn(false);
        when(partidaRepository.existsByEstadioPartidaAndDataPartida(any(),any())).thenReturn(false);
        when(partidaRepository.existsByClubeMandante_AndDataPartidaBetween(any(),any(),any())).thenReturn(false);
        when(partidaRepository.existsByClubeVisitante_AndDataPartidaBetween(any(),any(),any())).thenReturn(false);

        ClubeModel mandante = new ClubeModel();
        mandante.setId(7L); mandante.setNome("Mandante"); mandante.setEstado("SP"); mandante.setStatus(false); mandante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(7L)).thenReturn(Optional.of(mandante));

        ClubeModel visitante = new ClubeModel();
        visitante.setId(10L); visitante.setNome("Visitante"); visitante.setEstado("RJ"); visitante.setStatus(false);  visitante.setDataCriacao(LocalDate.now());
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(visitante));

        EstadioModel estadio = new EstadioModel();
        estadio.setId(22L); estadio.setNomeEstadio("estadio 22");
        when(estadioRepository.findById(22L)).thenReturn(Optional.of(estadio));

        PartidaModel partidaExistente = new PartidaModel();
        BeanUtils.copyProperties(dto, partidaExistente);
        Long idValor = 1L;
        partidaExistente.setId(idValor);

        when(partidaRepository.findById(idValor)).thenReturn(Optional.of(partidaExistente));


        PartidaModel retornoPartidaAtlz = partidaService.atualizarPartida(dto, idValor);

        assertEquals(dto.golsMandante(), retornoPartidaAtlz.getGolsMandante());
        verify(partidaRepository).save(any());
    }

    @Test
    public void testDeveLancarExececaoQuandoPartidaNaoExisteParaAtualizar() {
        PartidaRequestDto dto = new PartidaRequestDto(7L,10L, 22L, 5L, 0L, LocalDateTime.now());
        Long idValor = 1L;
        when(partidaRepository.findById(idValor)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntidadeNaoEncontradaException.class,
                () -> partidaService.atualizarPartida(dto, idValor));

        assertTrue(ex.getMessage().contains("nao encontrada"));
    }

    @Test
    public void testDeveLancarExececaoQuandoPartidaNaoExisteParaBuscar() {
        Long idValor = 1L;
        when(partidaRepository.findById(idValor)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntidadeNaoEncontradaException.class,
                () -> partidaService.acharPartida(idValor));

        assertTrue(ex.getMessage().contains("nao encontrada"));
    }


}
